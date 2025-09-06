## 本地 Lua 脚本与 Sentinel 限流接入说明

本文档说明在后端 `sky-take-out/sky-server` 中新增的本地 Lua 脚本执行与 Sentinel 限流的实现方式、代码位置、使用方法与验证步骤。

### 1. 变更概览
- 本地 Lua 脚本
  - 新增脚本文件：`src/main/resources/lua/stock_deduct.lua`
  - 新增服务：`com.sky.service.SeckillLuaService`
  - 实现类：`com.sky.service.impl.SeckillLuaServiceImpl`
  - 用户端 Lua 下单接口：`POST /user/seckill/order/submit-lua`
- Sentinel 限流
  - 依赖：`sentinel-core`、`sentinel-annotation-aspectj`
  - 配置：`com.sky.config.SentinelConfiguration`
  - 注解接入：`com.sky.controller.user.SeckillController` 核心接口

---

### 2. 本地 Lua 脚本
- 位置：`sky-server/src/main/resources/lua/stock_deduct.lua`
- 功能：库存扣减 + 防超卖 + 一人一单（原子性）
- KEYS 与 ARGV
  - `KEYS[1]`：`seckill:stock:{goodsId}` 当前可用库存
  - `KEYS[2]`：`seckill:user:limit:{userId}:{goodsId}` 用户已购数量
  - `KEYS[3]`：`seckill:sold:{goodsId}` 已售计数
  - `ARGV[1]`：`quantity` 购买数量
  - `ARGV[2]`：`limitCount` 限购数量
  - `ARGV[3]`：`userId` 用户 ID（脚本内未直接使用，仅保留扩展）
  - `ARGV[4]`：`expireTime` 用户购买记录过期秒数
- 返回：JSON 字符串（便于 Java 端解析）

示例核心逻辑：
```lua
local stock = redis.call('GET', KEYS[1])
local userPurchased = redis.call('GET', KEYS[2]) or '0'
local quantity = tonumber(ARGV[1])
local limitCount = tonumber(ARGV[2])
local expireTime = tonumber(ARGV[4])

if (not stock) or (tonumber(stock) < quantity) then
    return cjson.encode({ code = 50006, msg = '库存不足' })
end
if (tonumber(userPurchased) + quantity) > limitCount then
    return cjson.encode({ code = 50008, msg = '用户已达购买上限' })
end

redis.call('DECRBY', KEYS[1], quantity)
redis.call('INCRBY', KEYS[2], quantity)
if expireTime and expireTime > 0 then
    redis.call('EXPIRE', KEYS[2], expireTime)
end
redis.call('INCRBY', KEYS[3], quantity)

local remaining = tonumber(stock) - quantity
return cjson.encode({ code = 1, msg = 'success', data = { remainingStock = remaining } })
```

---

### 3. Java 执行脚本服务
- 接口：`com.sky.service.SeckillLuaService`
- 实现：`com.sky.service.impl.SeckillLuaServiceImpl`
  - 使用 `StringRedisTemplate` + `DefaultRedisScript<String>` 执行脚本
  - 从 `classpath:lua/stock_deduct.lua` 读取脚本源码
  - 以 `List<String> keys` + `Object... args` 形式传参
  - 返回值为 JSON 字符串，使用 `ObjectMapper` 解析为 `Map<String,Object>`

关键方法：
```java
Map<String, Object> executeStockDeduct(Long goodsId, Long userId, Integer quantity,
                                       Integer limitCount, Integer expireSeconds);
```
参数含义：
- goodsId：秒杀商品 ID
- userId：用户 ID（用于限购 Key）
- quantity：购买数量
- limitCount：限购数量
- expireSeconds：用户购买记录过期时间（秒）

---

### 4. Lua 下单接口接入
- 控制器：`com.sky.controller.user.SeckillController`
- 新增接口：`POST /user/seckill/order/submit-lua`
- 服务实现：`com.sky.service.impl.SeckillOrderServiceImpl#submitOrderWithLua`

处理流程（概述）：
1) 基础参数与地址簿合法性校验（与普通下单一致）
2) 调用 `SeckillLuaService.executeStockDeduct(...)` 执行 Lua 原子扣减 + 限购校验
3) 持久化用户购买记录（与脚本结果保持最终一致性）
4) 创建普通订单 `orders`、秒杀订单 `seckill_order`、订单明细 `order_detail`
5) 返回 `SeckillOrderSubmitVO`（订单号、支付超时、总金额、支付时限）

接口示例：
```http
POST /user/seckill/order/submit-lua
Content-Type: application/json

{
  "seckillGoodsId": 1,
  "quantity": 1,
  "addressBookId": 1,
  "remark": "尽快配送"
}
```

---

### 5. Sentinel 限流接入
#### 5.1 依赖
在 `sky-server/pom.xml` 新增：
```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-core</artifactId>
    <version>1.8.6</version>
</dependency>
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-annotation-aspectj</artifactId>
    <version>1.8.6</version>
</dependency>
```

#### 5.2 配置
- 类：`com.sky.config.SentinelConfiguration`
  - 注册 `SentinelResourceAspect` 以启用 `@SentinelResource`
  - 在 `@PostConstruct` 中加载基础 QPS 规则：
    - `user_seckill_activity_active`：QPS 50
    - `user_seckill_goods_detail`：QPS 100
    - `user_seckill_order_submit`：QPS 20

#### 5.3 控制器注解与统一处理
- 位置：`com.sky.controller.user.SeckillController`
- 在以下方法上增加 `@SentinelResource(value = 资源名, blockHandler = "handleBlock")`：
  - `GET /user/seckill/activity/active` → `user_seckill_activity_active`
  - `GET /user/seckill/goods/{id}` → `user_seckill_goods_detail`
  - `POST /user/seckill/order/submit` → `user_seckill_order_submit`
  - `POST /user/seckill/order/submit-lua` → `user_seckill_order_submit`
- 统一限流处理方法：
```java
public <T> Result<T> handleBlock(Object arg1, Object arg2, Object arg3, Throwable ex) {
    return Result.error(50010, "系统繁忙，请稍后重试");
}
```

---

### 6. 使用与验证步骤
1) 初始化 Redis Key（手动或通过预热逻辑）
   - `seckill:stock:{goodsId}`：设置为商品可用库存（正整数）
   - 用户首次购买时，`seckill:user:limit:{userId}:{goodsId}` 会被自动创建并累加
2) 调用 Lua 下单接口
   - 当库存不足或超限时，Lua 返回 `50006/50008`，接口抛出业务异常
3) 验证 Sentinel 限流
   - 对 `activity/active`、`goods/{id}`、`order/submit(-lua)` 进行高频调用
   - 触发限流时返回：`{"code":50010,"msg":"系统繁忙，请稍后重试"}`

---

### 7. 常见问题（FAQ）
- 看不到限流效果？
  - 确认 `SentinelConfiguration` 已被加载；确认资源名与注解 `value` 一致；并发或 QPS 达到规则阈值。
- Lua 下单库存未回写 DB？
  - 当前以 Redis 为准确保并发正确性，DB 同步可通过定时任务或下单成功后刷新缓存/异步对账完善。
- 需要“按用户”限流？
  - 可扩展热点参数限流（ParamFlowRule）或在拦截器中将 `userId` 注入资源的维度进行限流。

---

### 8. 后续扩展建议
- 增加 `activity_check.lua`、`user_eligibility.lua` 并在 Java 侧封装执行方法
- 接入 Sentinel 控制台，支持动态规则下发
- 增加参数限流（热点参数）实现“按用户/按商品”更细粒度的限流
- 完善 DB 与 Redis 的库存双向对账与监控面板



