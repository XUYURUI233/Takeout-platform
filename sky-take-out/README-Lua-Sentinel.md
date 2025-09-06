## ���� Lua �ű��� Sentinel ��������˵��

���ĵ�˵���ں�� `sky-take-out/sky-server` �������ı��� Lua �ű�ִ���� Sentinel ������ʵ�ַ�ʽ������λ�á�ʹ�÷�������֤���衣

### 1. �������
- ���� Lua �ű�
  - �����ű��ļ���`src/main/resources/lua/stock_deduct.lua`
  - ��������`com.sky.service.SeckillLuaService`
  - ʵ���ࣺ`com.sky.service.impl.SeckillLuaServiceImpl`
  - �û��� Lua �µ��ӿڣ�`POST /user/seckill/order/submit-lua`
- Sentinel ����
  - ������`sentinel-core`��`sentinel-annotation-aspectj`
  - ���ã�`com.sky.config.SentinelConfiguration`
  - ע����룺`com.sky.controller.user.SeckillController` ���Ľӿ�

---

### 2. ���� Lua �ű�
- λ�ã�`sky-server/src/main/resources/lua/stock_deduct.lua`
- ���ܣ����ۼ� + ������ + һ��һ����ԭ���ԣ�
- KEYS �� ARGV
  - `KEYS[1]`��`seckill:stock:{goodsId}` ��ǰ���ÿ��
  - `KEYS[2]`��`seckill:user:limit:{userId}:{goodsId}` �û��ѹ�����
  - `KEYS[3]`��`seckill:sold:{goodsId}` ���ۼ���
  - `ARGV[1]`��`quantity` ��������
  - `ARGV[2]`��`limitCount` �޹�����
  - `ARGV[3]`��`userId` �û� ID���ű���δֱ��ʹ�ã���������չ��
  - `ARGV[4]`��`expireTime` �û������¼��������
- ���أ�JSON �ַ��������� Java �˽�����

ʾ�������߼���
```lua
local stock = redis.call('GET', KEYS[1])
local userPurchased = redis.call('GET', KEYS[2]) or '0'
local quantity = tonumber(ARGV[1])
local limitCount = tonumber(ARGV[2])
local expireTime = tonumber(ARGV[4])

if (not stock) or (tonumber(stock) < quantity) then
    return cjson.encode({ code = 50006, msg = '��治��' })
end
if (tonumber(userPurchased) + quantity) > limitCount then
    return cjson.encode({ code = 50008, msg = '�û��Ѵﹺ������' })
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

### 3. Java ִ�нű�����
- �ӿڣ�`com.sky.service.SeckillLuaService`
- ʵ�֣�`com.sky.service.impl.SeckillLuaServiceImpl`
  - ʹ�� `StringRedisTemplate` + `DefaultRedisScript<String>` ִ�нű�
  - �� `classpath:lua/stock_deduct.lua` ��ȡ�ű�Դ��
  - �� `List<String> keys` + `Object... args` ��ʽ����
  - ����ֵΪ JSON �ַ�����ʹ�� `ObjectMapper` ����Ϊ `Map<String,Object>`

�ؼ�������
```java
Map<String, Object> executeStockDeduct(Long goodsId, Long userId, Integer quantity,
                                       Integer limitCount, Integer expireSeconds);
```
�������壺
- goodsId����ɱ��Ʒ ID
- userId���û� ID�������޹� Key��
- quantity����������
- limitCount���޹�����
- expireSeconds���û������¼����ʱ�䣨�룩

---

### 4. Lua �µ��ӿڽ���
- ��������`com.sky.controller.user.SeckillController`
- �����ӿڣ�`POST /user/seckill/order/submit-lua`
- ����ʵ�֣�`com.sky.service.impl.SeckillOrderServiceImpl#submitOrderWithLua`

�������̣���������
1) �����������ַ���Ϸ���У�飨����ͨ�µ�һ�£�
2) ���� `SeckillLuaService.executeStockDeduct(...)` ִ�� Lua ԭ�ӿۼ� + �޹�У��
3) �־û��û������¼����ű������������һ���ԣ�
4) ������ͨ���� `orders`����ɱ���� `seckill_order`��������ϸ `order_detail`
5) ���� `SeckillOrderSubmitVO`�������š�֧����ʱ���ܽ�֧��ʱ�ޣ�

�ӿ�ʾ����
```http
POST /user/seckill/order/submit-lua
Content-Type: application/json

{
  "seckillGoodsId": 1,
  "quantity": 1,
  "addressBookId": 1,
  "remark": "��������"
}
```

---

### 5. Sentinel ��������
#### 5.1 ����
�� `sky-server/pom.xml` ������
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

#### 5.2 ����
- �ࣺ`com.sky.config.SentinelConfiguration`
  - ע�� `SentinelResourceAspect` ������ `@SentinelResource`
  - �� `@PostConstruct` �м��ػ��� QPS ����
    - `user_seckill_activity_active`��QPS 50
    - `user_seckill_goods_detail`��QPS 100
    - `user_seckill_order_submit`��QPS 20

#### 5.3 ������ע����ͳһ����
- λ�ã�`com.sky.controller.user.SeckillController`
- �����·��������� `@SentinelResource(value = ��Դ��, blockHandler = "handleBlock")`��
  - `GET /user/seckill/activity/active` �� `user_seckill_activity_active`
  - `GET /user/seckill/goods/{id}` �� `user_seckill_goods_detail`
  - `POST /user/seckill/order/submit` �� `user_seckill_order_submit`
  - `POST /user/seckill/order/submit-lua` �� `user_seckill_order_submit`
- ͳһ������������
```java
public <T> Result<T> handleBlock(Object arg1, Object arg2, Object arg3, Throwable ex) {
    return Result.error(50010, "ϵͳ��æ�����Ժ�����");
}
```

---

### 6. ʹ������֤����
1) ��ʼ�� Redis Key���ֶ���ͨ��Ԥ���߼���
   - `seckill:stock:{goodsId}`������Ϊ��Ʒ���ÿ�棨��������
   - �û��״ι���ʱ��`seckill:user:limit:{userId}:{goodsId}` �ᱻ�Զ��������ۼ�
2) ���� Lua �µ��ӿ�
   - ����治�����ʱ��Lua ���� `50006/50008`���ӿ��׳�ҵ���쳣
3) ��֤ Sentinel ����
   - �� `activity/active`��`goods/{id}`��`order/submit(-lua)` ���и�Ƶ����
   - ��������ʱ���أ�`{"code":50010,"msg":"ϵͳ��æ�����Ժ�����"}`

---

### 7. �������⣨FAQ��
- ����������Ч����
  - ȷ�� `SentinelConfiguration` �ѱ����أ�ȷ����Դ����ע�� `value` һ�£������� QPS �ﵽ������ֵ��
- Lua �µ����δ��д DB��
  - ��ǰ�� Redis Ϊ׼ȷ��������ȷ�ԣ�DB ͬ����ͨ����ʱ������µ��ɹ���ˢ�»���/�첽�������ơ�
- ��Ҫ�����û���������
  - ����չ�ȵ����������ParamFlowRule�������������н� `userId` ע����Դ��ά�Ƚ���������

---

### 8. ������չ����
- ���� `activity_check.lua`��`user_eligibility.lua` ���� Java ���װִ�з���
- ���� Sentinel ����̨��֧�ֶ�̬�����·�
- ���Ӳ����������ȵ������ʵ�֡����û�/����Ʒ����ϸ���ȵ�����
- ���� DB �� Redis �Ŀ��˫������������



