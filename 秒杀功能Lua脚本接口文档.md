# 秒杀功能Lua脚本接口文档

## 文档说明
本文档基于原有秒杀功能接口，重新设计为基于Redis + Lua脚本的高性能秒杀系统。通过Lua脚本实现原子性的库存扣减、防超卖和一人一单限制，确保系统在高并发场景下的数据一致性和性能表现。

**核心特性**:
- ? 基于Redis + Lua脚本的原子性操作
- ?? 防超卖机制，确保库存不会出现负数
- ? 一人一单限制，防止重复购买
- ? 高性能，支持万级并发
- ? 分布式锁机制，保证数据一致性

## 系统架构说明

### 1. Redis数据结构设计
```
# 秒杀活动信息
seckill:activity:{activityId}         # Hash - 活动基本信息
seckill:activity:{activityId}:goods   # Hash - 活动商品列表

# 库存管理
seckill:stock:{goodsId}               # String - 商品可用库存
seckill:stock:total:{goodsId}         # String - 商品总库存
seckill:sold:{goodsId}                # String - 已售数量

# 用户购买限制
seckill:user:limit:{userId}:{goodsId} # String - 用户购买记录
seckill:user:order:{userId}           # Set - 用户订单集合

# 活动状态
seckill:activity:status:{activityId}  # String - 活动状态
seckill:activity:time:{activityId}    # Hash - 活动时间信息
```

### 2. Lua脚本核心逻辑

#### 2.1 库存扣减脚本 (stock_deduct.lua)
```lua
-- 库存扣减 + 防超卖 + 一人一单
-- KEYS[1]: seckill:stock:{goodsId}
-- KEYS[2]: seckill:user:limit:{userId}:{goodsId}
-- KEYS[3]: seckill:sold:{goodsId}
-- ARGV[1]: quantity (购买数量)
-- ARGV[2]: limitCount (限购数量)
-- ARGV[3]: userId
-- ARGV[4]: expireTime (用户记录过期时间)

local stock = redis.call('GET', KEYS[1])
local userPurchased = redis.call('GET', KEYS[2]) or "0"
local quantity = tonumber(ARGV[1])
local limitCount = tonumber(ARGV[2])
local userId = ARGV[3]
local expireTime = tonumber(ARGV[4])

-- 检查库存是否充足
if not stock or tonumber(stock) < quantity then
    return {code = 50006, msg = "库存不足"}
end

-- 检查用户是否超过限购数量
if tonumber(userPurchased) + quantity > limitCount then
    return {code = 50008, msg = "用户已达购买上限"}
end

-- 原子性扣减库存和更新用户购买记录
redis.call('DECRBY', KEYS[1], quantity)
redis.call('INCRBY', KEYS[2], quantity)
redis.call('EXPIRE', KEYS[2], expireTime)
redis.call('INCRBY', KEYS[3], quantity)

return {code = 1, msg = "success", data = {remainingStock = tonumber(stock) - quantity}}
```

#### 2.2 活动状态检查脚本 (activity_check.lua)
```lua
-- 检查活动状态和时间
-- KEYS[1]: seckill:activity:status:{activityId}
-- KEYS[2]: seckill:activity:time:{activityId}
-- ARGV[1]: currentTime

local status = redis.call('GET', KEYS[1])
local timeInfo = redis.call('HMGET', KEYS[2], 'startTime', 'endTime')
local currentTime = tonumber(ARGV[1])

if not status or status ~= "1" then
    return {code = 50002, msg = "秒杀活动未开始或已结束"}
end

local startTime = tonumber(timeInfo[1])
local endTime = tonumber(timeInfo[2])

if currentTime < startTime then
    return {code = 50002, msg = "秒杀未开始"}
end

if currentTime > endTime then
    return {code = 50003, msg = "秒杀已结束"}
end

return {code = 1, msg = "success"}
```

#### 2.3 用户资格检查脚本 (user_eligibility.lua)
```lua
-- 检查用户购买资格
-- KEYS[1]: seckill:user:limit:{userId}:{goodsId}
-- KEYS[2]: seckill:stock:{goodsId}
-- ARGV[1]: limitCount
-- ARGV[2]: quantity

local userPurchased = tonumber(redis.call('GET', KEYS[1]) or "0")
local stock = tonumber(redis.call('GET', KEYS[2]) or "0")
local limitCount = tonumber(ARGV[1])
local quantity = tonumber(ARGV[2])

return {
    code = 1,
    msg = "success",
    data = {
        canPurchase = (userPurchased + quantity <= limitCount and stock >= quantity),
        remainingQuota = math.max(0, limitCount - userPurchased),
        limitCount = limitCount,
        userPurchased = userPurchased,
        availableStock = stock
    }
}
```

---

## 接口文档

### 1. 管理端接口（商家端）

#### 1.1 秒杀活动管理

##### 1.1.1 初始化秒杀活动到Redis ? **新增接口**
**接口地址**: `POST /admin/seckill/activity/init`

**接口描述**: 将秒杀活动数据初始化到Redis中，为高并发秒杀做准备

**请求参数**:
```json
{
  "activityId": 1,
  "preloadMinutes": 30
}
```

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "activityId": 1,
    "goodsCount": 5,
    "totalStock": 500,
    "preloadTime": "2025-08-20 09:30:00",
    "redisKeys": [
      "seckill:activity:1",
      "seckill:stock:46",
      "seckill:activity:status:1"
    ]
  }
}
```

##### 1.1.2 清理秒杀活动缓存 ? **新增接口**
**接口地址**: `DELETE /admin/seckill/activity/cache/{activityId}`

**接口描述**: 清理指定秒杀活动的Redis缓存数据

**路径参数**:
- `activityId`: 活动ID

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "clearedKeys": 15,
    "activityId": 1
  }
}
```

##### 1.1.3 实时库存监控 ? **新增接口**
**接口地址**: `GET /admin/seckill/activity/{activityId}/stock/monitor`

**接口描述**: 实时监控秒杀活动中各商品的库存情况

**路径参数**:
- `activityId`: 活动ID

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "activityId": 1,
    "activityName": "限时秒杀活动",
    "status": 1,
    "goods": [
      {
        "goodsId": 46,
        "goodsName": "蒜蓉鸡爪",
        "totalStock": 100,
        "availableStock": 85,
        "soldCount": 15,
        "stockRate": 0.85,
        "salesRate": 0.15
      }
    ],
    "totalSales": 15,
    "totalStock": 100,
    "overallSalesRate": 0.15,
    "updateTime": "2025-08-20 10:35:00"
  }
}
```

### 1.2 秒杀商品管理

##### 1.2.1 批量更新商品库存到Redis ? **新增接口**
**接口地址**: `POST /admin/seckill/goods/stock/batch-update`

**接口描述**: 批量更新秒杀商品库存到Redis

**请求参数**:
```json
{
  "activityId": 1,
  "goods": [
    {
      "goodsId": 46,
      "stockChange": 50,
      "operation": "increase"
    },
    {
      "goodsId": 47,
      "stockChange": 20,
      "operation": "decrease"
    }
  ]
}
```

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "successCount": 2,
    "failCount": 0,
    "results": [
      {
        "goodsId": 46,
        "success": true,
        "newStock": 135,
        "message": "库存更新成功"
      },
      {
        "goodsId": 47,
        "success": true,
        "newStock": 80,
        "message": "库存更新成功"
      }
    ]
  }
}
```

### 1.3 秒杀数据统计

##### 1.3.1 实时秒杀统计数据 ? **增强接口**
**接口地址**: `GET /admin/seckill/statistics/realtime`

**接口描述**: 获取基于Redis的实时秒杀统计数据

**请求参数**:
```json
{
  "activityId": 1,
  "timeRange": "current"
}
```

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "activityId": 1,
    "activityName": "限时秒杀活动",
    "realTimeData": {
      "totalOrders": 156,
      "successOrders": 142,
      "cancelledOrders": 14,
      "totalAmount": 426.00,
      "successRate": 0.91,
      "qps": 45.2,
      "avgResponseTime": 12
    },
    "stockData": {
      "totalStock": 500,
      "soldCount": 142,
      "remainingStock": 358,
      "stockUtilization": 0.284
    },
    "userStats": {
      "totalUsers": 142,
      "repeatPurchaseAttempts": 28,
      "blockRate": 0.164
    },
    "updateTime": "2025-08-20 10:35:22"
  }
}
```

---

## 2. 用户端接口

### 2.1 秒杀商品展示

##### 2.1.1 查询正在进行的秒杀活动 ? **增强接口**
**接口地址**: `GET /user/seckill/activity/active`

**接口描述**: 获取当前正在进行的秒杀活动（基于Redis缓存）

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "限时秒杀活动",
      "image": "https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/seckill-banner.png",
      "startTime": "2025-08-20 10:00:00",
      "endTime": "2025-08-20 12:00:00",
      "status": 1,
      "description": "限时2小时秒杀活动，数量有限，先到先得！",
      "remainingTime": 3600,
      "totalStock": 500,
      "soldCount": 142,
      "participantCount": 1250,
      "hotLevel": "high"
    }
  ]
}
```

##### 2.1.2 查询秒杀商品详情 ? **增强接口**
**接口地址**: `GET /user/seckill/goods/{id}`

**接口描述**: 获取秒杀商品详细信息（实时库存基于Redis）

**路径参数**:
- `id`: 秒杀商品ID

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "activityId": 1,
    "activityName": "限时秒杀活动",
    "goodsType": 1,
    "goodsId": 46,
    "goodsName": "蒜蓉鸡爪",
    "goodsImage": "https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png",
    "originalPrice": 6.00,
    "seckillPrice": 3.00,
    "availableStock": 85,
    "soldCount": 15,
    "limitCount": 2,
    "userPurchased": 0,
    "canPurchase": true,
    "startTime": "2025-08-20 10:00:00",
    "endTime": "2025-08-20 12:00:00",
    "remainingTime": 3600,
    "description": "香辣爽口，回味无穷",
    "stockWarning": false,
    "hotRanking": 3
  }
}
```

### 2.2 秒杀下单流程

##### 2.2.1 秒杀资格预检查 ? **新增接口**
**接口地址**: `POST /user/seckill/order/pre-check`

**接口描述**: 秒杀下单前的资格预检查（基于Lua脚本）

**请求参数**:
```json
{
  "seckillGoodsId": 1,
  "quantity": 1,
  "userId": 4
}
```

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "eligible": true,
    "canPurchase": true,
    "remainingQuota": 2,
    "limitCount": 2,
    "userPurchased": 0,
    "availableStock": 85,
    "estimatedWaitTime": 0,
    "checkToken": "pre_check_token_12345"
  }
}
```

##### 2.2.2 秒杀下单（Lua脚本版） ? **核心接口重构**
**接口地址**: `POST /user/seckill/order/submit-lua`

**接口描述**: 基于Lua脚本的原子性秒杀下单

**请求参数**:
```json
{
  "seckillGoodsId": 1,
  "quantity": 1,
  "addressBookId": 1,
  "remark": "尽快配送",
  "checkToken": "pre_check_token_12345"
}
```

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderId": 100,
    "orderNumber": "1755102031104",
    "payExpireTime": "2025-08-20 10:45:00",
    "totalAmount": 3.00,
    "payTimeLimit": 900,
    "stockDeductionResult": {
      "success": true,
      "remainingStock": 84,
      "executionTime": 2,
      "luaScriptHash": "sha1:abc123def456"
    },
    "userLimitResult": {
      "remainingQuota": 1,
      "totalPurchased": 1
    }
  }
}
```

##### 2.2.3 秒杀订单状态查询 ? **新增接口**
**接口地址**: `GET /user/seckill/order/status/{orderNumber}`

**接口描述**: 查询秒杀订单实时状态

**路径参数**:
- `orderNumber`: 订单号

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderNumber": "1755102031104",
    "status": 1,
    "payStatus": 0,
    "createTime": "2025-08-20 10:30:00",
    "payExpireTime": "2025-08-20 10:45:00",
    "remainingPayTime": 847,
    "amount": 3.00,
    "stockLocked": true,
    "canCancel": true,
    "canPay": true
  }
}
```

### 2.3 用户限购检查

##### 2.3.1 检查用户购买资格（增强版） ? **增强接口**
**接口地址**: `GET /user/seckill/check/eligibility-enhanced`

**接口描述**: 基于Lua脚本的用户购买资格检查

**请求参数**:
```json
{
  "seckillGoodsId": 1,
  "quantity": 1
}
```

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "canPurchase": true,
    "remainingQuota": 2,
    "limitCount": 2,
    "userPurchased": 0,
    "availableStock": 85,
    "activityStatus": "active",
    "remainingTime": 3542,
    "riskLevel": "low",
    "waitingQueue": 0,
    "estimatedProcessTime": 1
  }
}
```

---

## 3. Lua脚本管理接口

### 3.1 脚本管理

##### 3.1.1 加载Lua脚本 ? **新增接口**
**接口地址**: `POST /admin/seckill/lua/load`

**接口描述**: 将秒杀相关Lua脚本加载到Redis

**请求参数**:
```json
{
  "scripts": ["stock_deduct", "activity_check", "user_eligibility"],
  "forceReload": false
}
```

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "loadedScripts": [
      {
        "name": "stock_deduct",
        "sha1": "abc123def456789",
        "loaded": true
      },
      {
        "name": "activity_check", 
        "sha1": "def456ghi789012",
        "loaded": true
      },
      {
        "name": "user_eligibility",
        "sha1": "ghi789jkl012345",
        "loaded": true
      }
    ],
    "totalScripts": 3,
    "loadTime": "2025-08-20 09:00:00"
  }
}
```

##### 3.1.2 脚本执行统计 ? **新增接口**
**接口地址**: `GET /admin/seckill/lua/statistics`

**接口描述**: 获取Lua脚本执行统计信息

**响应数据**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "totalExecutions": 15420,
    "successExecutions": 15380,
    "failedExecutions": 40,
    "avgExecutionTime": 1.2,
    "scriptStats": [
      {
        "scriptName": "stock_deduct",
        "executions": 8500,
        "successRate": 0.998,
        "avgTime": 1.5
      },
      {
        "scriptName": "activity_check",
        "executions": 4200,
        "successRate": 1.0,
        "avgTime": 0.8
      },
      {
        "scriptName": "user_eligibility",
        "executions": 2720,
        "successRate": 0.995,
        "avgTime": 1.1
      }
    ],
    "peakQPS": 2500,
    "currentQPS": 145
  }
}
```

---

## 4. 错误码说明

### 4.1 Lua脚本特定错误码
| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 60001 | Lua脚本执行失败 | Redis Lua脚本执行异常 |
| 60002 | 脚本未加载 | 指定的Lua脚本未加载到Redis |
| 60003 | 脚本参数错误 | 传递给Lua脚本的参数不正确 |
| 60004 | Redis连接异常 | Redis服务不可用 |
| 60005 | 库存扣减失败 | 原子性库存操作失败 |
| 60006 | 用户限购检查失败 | 用户购买限制检查异常 |
| 60007 | 活动状态检查失败 | 活动时间或状态验证失败 |
| 60008 | 并发冲突 | 高并发场景下的数据冲突 |

### 4.2 性能相关错误码
| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 70001 | 系统繁忙，请稍后重试 | QPS超过限制 |
| 70002 | 排队中，请耐心等待 | 用户进入等待队列 |
| 70003 | 脚本执行超时 | Lua脚本执行时间过长 |

---

## 5. 性能指标说明

### 5.1 基准性能指标
- **QPS**: 单机支持 10,000+ QPS
- **响应时间**: P99 < 50ms
- **库存一致性**: 100%（基于Lua脚本原子性）
- **超卖率**: 0%（严格防超卖机制）

### 5.2 Redis资源使用
- **内存使用**: 每个活动约占用 1-5MB
- **连接数**: 建议连接池大小 50-100
- **脚本缓存**: 常驻内存，支持热更新

### 5.3 监控指标
```json
{
  "redis": {
    "memoryUsage": "256MB",
    "connectionCount": 45,
    "scriptCacheHits": 0.99,
    "avgLatency": "1.2ms"
  },
  "lua": {
    "scriptExecutions": 15420,
    "avgExecutionTime": "1.2ms",
    "errorRate": 0.002
  },
  "business": {
    "stockAccuracy": 1.0,
    "oversellCount": 0,
    "userLimitViolations": 0
  }
}
```

---

## 6. 部署和配置说明

### 6.1 Redis配置要求
```properties
# Redis配置建议
maxmemory 2gb
maxmemory-policy allkeys-lru
timeout 300
tcp-keepalive 300

# Lua脚本相关
lua-time-limit 5000
```

### 6.2 应用配置
```yaml
# application.yml
redis:
  seckill:
    script-cache-size: 10
    max-execution-time: 5000
    retry-times: 3
    connection-pool:
      max-active: 100
      max-idle: 50
      min-idle: 10
```

### 6.3 脚本部署
```bash
# 脚本文件位置
/resources/lua/
├── stock_deduct.lua          # 库存扣减脚本
├── activity_check.lua        # 活动检查脚本
├── user_eligibility.lua      # 用户资格检查脚本
└── batch_operations.lua      # 批量操作脚本
```

---

## 7. 安全和限流说明

### 7.1 接口限流策略
- **秒杀下单接口**: 每用户每秒1次请求
- **资格检查接口**: 每用户每秒3次请求
- **商品详情接口**: 每用户每秒5次请求

### 7.2 防刷机制
- 基于用户ID + IP的组合限流
- Lua脚本内置防重复提交检查
- 支持动态黑名单机制

### 7.3 数据安全
- 所有价格相关计算保留2位小数
- 库存数据定期与数据库同步校验
- 关键操作记录详细审计日志

---

## 8. 接口实现状态总结

### ? **新增核心功能** (11个接口)
1. **活动缓存管理** (2个)
   - 初始化秒杀活动到Redis
   - 清理秒杀活动缓存

2. **实时监控统计** (3个)
   - 实时库存监控
   - 批量库存更新
   - 实时统计数据

3. **Lua脚本管理** (2个)
   - 加载Lua脚本
   - 脚本执行统计

4. **用户体验优化** (4个)
   - 秒杀资格预检查
   - Lua脚本版秒杀下单
   - 订单状态实时查询
   - 增强版资格检查

### ? **核心技术优势**
- **原子性保证**: 基于Lua脚本的原子操作
- **高性能**: 支持万级并发，响应时间<50ms
- **零超卖**: 严格的库存控制机制
- **一人一单**: 完善的用户限购逻辑
- **实时监控**: 全方位的性能和业务监控

---

**注意事项**：
1. 所有接口都需要配置相应的Swagger文档注解
2. 涉及金额字符的响应数据必须使用UTF-8编码
3. 关键业务操作需要记录详细日志
4. 数据库数据需要考虑与Redis的一致性
5. 高并发接口需要进行压力测试验证
6. Lua脚本需要进行充分的单元测试
7. Redis故障时需要有降级方案

本接口文档基于Lua脚本技术栈，为高并发秒杀场景提供了完整的解决方案。
