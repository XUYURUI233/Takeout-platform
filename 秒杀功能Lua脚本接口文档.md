# ��ɱ����Lua�ű��ӿ��ĵ�

## �ĵ�˵��
���ĵ�����ԭ����ɱ���ܽӿڣ��������Ϊ����Redis + Lua�ű��ĸ�������ɱϵͳ��ͨ��Lua�ű�ʵ��ԭ���ԵĿ��ۼ�����������һ��һ�����ƣ�ȷ��ϵͳ�ڸ߲��������µ�����һ���Ժ����ܱ��֡�

**��������**:
- ? ����Redis + Lua�ű���ԭ���Բ���
- ?? ���������ƣ�ȷ����治����ָ���
- ? һ��һ�����ƣ���ֹ�ظ�����
- ? �����ܣ�֧���򼶲���
- ? �ֲ�ʽ�����ƣ���֤����һ����

## ϵͳ�ܹ�˵��

### 1. Redis���ݽṹ���
```
# ��ɱ���Ϣ
seckill:activity:{activityId}         # Hash - �������Ϣ
seckill:activity:{activityId}:goods   # Hash - ���Ʒ�б�

# ������
seckill:stock:{goodsId}               # String - ��Ʒ���ÿ��
seckill:stock:total:{goodsId}         # String - ��Ʒ�ܿ��
seckill:sold:{goodsId}                # String - ��������

# �û���������
seckill:user:limit:{userId}:{goodsId} # String - �û������¼
seckill:user:order:{userId}           # Set - �û���������

# �״̬
seckill:activity:status:{activityId}  # String - �״̬
seckill:activity:time:{activityId}    # Hash - �ʱ����Ϣ
```

### 2. Lua�ű������߼�

#### 2.1 ���ۼ��ű� (stock_deduct.lua)
```lua
-- ���ۼ� + ������ + һ��һ��
-- KEYS[1]: seckill:stock:{goodsId}
-- KEYS[2]: seckill:user:limit:{userId}:{goodsId}
-- KEYS[3]: seckill:sold:{goodsId}
-- ARGV[1]: quantity (��������)
-- ARGV[2]: limitCount (�޹�����)
-- ARGV[3]: userId
-- ARGV[4]: expireTime (�û���¼����ʱ��)

local stock = redis.call('GET', KEYS[1])
local userPurchased = redis.call('GET', KEYS[2]) or "0"
local quantity = tonumber(ARGV[1])
local limitCount = tonumber(ARGV[2])
local userId = ARGV[3]
local expireTime = tonumber(ARGV[4])

-- ������Ƿ����
if not stock or tonumber(stock) < quantity then
    return {code = 50006, msg = "��治��"}
end

-- ����û��Ƿ񳬹��޹�����
if tonumber(userPurchased) + quantity > limitCount then
    return {code = 50008, msg = "�û��Ѵﹺ������"}
end

-- ԭ���Կۼ����͸����û������¼
redis.call('DECRBY', KEYS[1], quantity)
redis.call('INCRBY', KEYS[2], quantity)
redis.call('EXPIRE', KEYS[2], expireTime)
redis.call('INCRBY', KEYS[3], quantity)

return {code = 1, msg = "success", data = {remainingStock = tonumber(stock) - quantity}}
```

#### 2.2 �״̬���ű� (activity_check.lua)
```lua
-- ���״̬��ʱ��
-- KEYS[1]: seckill:activity:status:{activityId}
-- KEYS[2]: seckill:activity:time:{activityId}
-- ARGV[1]: currentTime

local status = redis.call('GET', KEYS[1])
local timeInfo = redis.call('HMGET', KEYS[2], 'startTime', 'endTime')
local currentTime = tonumber(ARGV[1])

if not status or status ~= "1" then
    return {code = 50002, msg = "��ɱ�δ��ʼ���ѽ���"}
end

local startTime = tonumber(timeInfo[1])
local endTime = tonumber(timeInfo[2])

if currentTime < startTime then
    return {code = 50002, msg = "��ɱδ��ʼ"}
end

if currentTime > endTime then
    return {code = 50003, msg = "��ɱ�ѽ���"}
end

return {code = 1, msg = "success"}
```

#### 2.3 �û��ʸ���ű� (user_eligibility.lua)
```lua
-- ����û������ʸ�
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

## �ӿ��ĵ�

### 1. ����˽ӿڣ��̼Ҷˣ�

#### 1.1 ��ɱ�����

##### 1.1.1 ��ʼ����ɱ���Redis ? **�����ӿ�**
**�ӿڵ�ַ**: `POST /admin/seckill/activity/init`

**�ӿ�����**: ����ɱ����ݳ�ʼ����Redis�У�Ϊ�߲�����ɱ��׼��

**�������**:
```json
{
  "activityId": 1,
  "preloadMinutes": 30
}
```

**��Ӧ����**:
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

##### 1.1.2 ������ɱ����� ? **�����ӿ�**
**�ӿڵ�ַ**: `DELETE /admin/seckill/activity/cache/{activityId}`

**�ӿ�����**: ����ָ����ɱ���Redis��������

**·������**:
- `activityId`: �ID

**��Ӧ����**:
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

##### 1.1.3 ʵʱ����� ? **�����ӿ�**
**�ӿڵ�ַ**: `GET /admin/seckill/activity/{activityId}/stock/monitor`

**�ӿ�����**: ʵʱ�����ɱ��и���Ʒ�Ŀ�����

**·������**:
- `activityId`: �ID

**��Ӧ����**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "activityId": 1,
    "activityName": "��ʱ��ɱ�",
    "status": 1,
    "goods": [
      {
        "goodsId": 46,
        "goodsName": "���ؼ�צ",
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

### 1.2 ��ɱ��Ʒ����

##### 1.2.1 ����������Ʒ��浽Redis ? **�����ӿ�**
**�ӿڵ�ַ**: `POST /admin/seckill/goods/stock/batch-update`

**�ӿ�����**: ����������ɱ��Ʒ��浽Redis

**�������**:
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

**��Ӧ����**:
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
        "message": "�����³ɹ�"
      },
      {
        "goodsId": 47,
        "success": true,
        "newStock": 80,
        "message": "�����³ɹ�"
      }
    ]
  }
}
```

### 1.3 ��ɱ����ͳ��

##### 1.3.1 ʵʱ��ɱͳ������ ? **��ǿ�ӿ�**
**�ӿڵ�ַ**: `GET /admin/seckill/statistics/realtime`

**�ӿ�����**: ��ȡ����Redis��ʵʱ��ɱͳ������

**�������**:
```json
{
  "activityId": 1,
  "timeRange": "current"
}
```

**��Ӧ����**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "activityId": 1,
    "activityName": "��ʱ��ɱ�",
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

## 2. �û��˽ӿ�

### 2.1 ��ɱ��Ʒչʾ

##### 2.1.1 ��ѯ���ڽ��е���ɱ� ? **��ǿ�ӿ�**
**�ӿڵ�ַ**: `GET /user/seckill/activity/active`

**�ӿ�����**: ��ȡ��ǰ���ڽ��е���ɱ�������Redis���棩

**��Ӧ����**:
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "��ʱ��ɱ�",
      "image": "https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/seckill-banner.png",
      "startTime": "2025-08-20 10:00:00",
      "endTime": "2025-08-20 12:00:00",
      "status": 1,
      "description": "��ʱ2Сʱ��ɱ����������ޣ��ȵ��ȵã�",
      "remainingTime": 3600,
      "totalStock": 500,
      "soldCount": 142,
      "participantCount": 1250,
      "hotLevel": "high"
    }
  ]
}
```

##### 2.1.2 ��ѯ��ɱ��Ʒ���� ? **��ǿ�ӿ�**
**�ӿڵ�ַ**: `GET /user/seckill/goods/{id}`

**�ӿ�����**: ��ȡ��ɱ��Ʒ��ϸ��Ϣ��ʵʱ������Redis��

**·������**:
- `id`: ��ɱ��ƷID

**��Ӧ����**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "activityId": 1,
    "activityName": "��ʱ��ɱ�",
    "goodsType": 1,
    "goodsId": 46,
    "goodsName": "���ؼ�צ",
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
    "description": "����ˬ�ڣ���ζ����",
    "stockWarning": false,
    "hotRanking": 3
  }
}
```

### 2.2 ��ɱ�µ�����

##### 2.2.1 ��ɱ�ʸ�Ԥ��� ? **�����ӿ�**
**�ӿڵ�ַ**: `POST /user/seckill/order/pre-check`

**�ӿ�����**: ��ɱ�µ�ǰ���ʸ�Ԥ��飨����Lua�ű���

**�������**:
```json
{
  "seckillGoodsId": 1,
  "quantity": 1,
  "userId": 4
}
```

**��Ӧ����**:
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

##### 2.2.2 ��ɱ�µ���Lua�ű��棩 ? **���Ľӿ��ع�**
**�ӿڵ�ַ**: `POST /user/seckill/order/submit-lua`

**�ӿ�����**: ����Lua�ű���ԭ������ɱ�µ�

**�������**:
```json
{
  "seckillGoodsId": 1,
  "quantity": 1,
  "addressBookId": 1,
  "remark": "��������",
  "checkToken": "pre_check_token_12345"
}
```

**��Ӧ����**:
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

##### 2.2.3 ��ɱ����״̬��ѯ ? **�����ӿ�**
**�ӿڵ�ַ**: `GET /user/seckill/order/status/{orderNumber}`

**�ӿ�����**: ��ѯ��ɱ����ʵʱ״̬

**·������**:
- `orderNumber`: ������

**��Ӧ����**:
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

### 2.3 �û��޹����

##### 2.3.1 ����û������ʸ���ǿ�棩 ? **��ǿ�ӿ�**
**�ӿڵ�ַ**: `GET /user/seckill/check/eligibility-enhanced`

**�ӿ�����**: ����Lua�ű����û������ʸ���

**�������**:
```json
{
  "seckillGoodsId": 1,
  "quantity": 1
}
```

**��Ӧ����**:
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

## 3. Lua�ű�����ӿ�

### 3.1 �ű�����

##### 3.1.1 ����Lua�ű� ? **�����ӿ�**
**�ӿڵ�ַ**: `POST /admin/seckill/lua/load`

**�ӿ�����**: ����ɱ���Lua�ű����ص�Redis

**�������**:
```json
{
  "scripts": ["stock_deduct", "activity_check", "user_eligibility"],
  "forceReload": false
}
```

**��Ӧ����**:
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

##### 3.1.2 �ű�ִ��ͳ�� ? **�����ӿ�**
**�ӿڵ�ַ**: `GET /admin/seckill/lua/statistics`

**�ӿ�����**: ��ȡLua�ű�ִ��ͳ����Ϣ

**��Ӧ����**:
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

## 4. ������˵��

### 4.1 Lua�ű��ض�������
| ������ | ������Ϣ | ˵�� |
|--------|----------|------|
| 60001 | Lua�ű�ִ��ʧ�� | Redis Lua�ű�ִ���쳣 |
| 60002 | �ű�δ���� | ָ����Lua�ű�δ���ص�Redis |
| 60003 | �ű��������� | ���ݸ�Lua�ű��Ĳ�������ȷ |
| 60004 | Redis�����쳣 | Redis���񲻿��� |
| 60005 | ���ۼ�ʧ�� | ԭ���Կ�����ʧ�� |
| 60006 | �û��޹����ʧ�� | �û��������Ƽ���쳣 |
| 60007 | �״̬���ʧ�� | �ʱ���״̬��֤ʧ�� |
| 60008 | ������ͻ | �߲��������µ����ݳ�ͻ |

### 4.2 ������ش�����
| ������ | ������Ϣ | ˵�� |
|--------|----------|------|
| 70001 | ϵͳ��æ�����Ժ����� | QPS�������� |
| 70002 | �Ŷ��У������ĵȴ� | �û�����ȴ����� |
| 70003 | �ű�ִ�г�ʱ | Lua�ű�ִ��ʱ����� |

---

## 5. ����ָ��˵��

### 5.1 ��׼����ָ��
- **QPS**: ����֧�� 10,000+ QPS
- **��Ӧʱ��**: P99 < 50ms
- **���һ����**: 100%������Lua�ű�ԭ���ԣ�
- **������**: 0%���ϸ���������ƣ�

### 5.2 Redis��Դʹ��
- **�ڴ�ʹ��**: ÿ���Լռ�� 1-5MB
- **������**: �������ӳش�С 50-100
- **�ű�����**: ��פ�ڴ棬֧���ȸ���

### 5.3 ���ָ��
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

## 6. ���������˵��

### 6.1 Redis����Ҫ��
```properties
# Redis���ý���
maxmemory 2gb
maxmemory-policy allkeys-lru
timeout 300
tcp-keepalive 300

# Lua�ű����
lua-time-limit 5000
```

### 6.2 Ӧ������
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

### 6.3 �ű�����
```bash
# �ű��ļ�λ��
/resources/lua/
������ stock_deduct.lua          # ���ۼ��ű�
������ activity_check.lua        # ����ű�
������ user_eligibility.lua      # �û��ʸ���ű�
������ batch_operations.lua      # ���������ű�
```

---

## 7. ��ȫ������˵��

### 7.1 �ӿ���������
- **��ɱ�µ��ӿ�**: ÿ�û�ÿ��1������
- **�ʸ���ӿ�**: ÿ�û�ÿ��3������
- **��Ʒ����ӿ�**: ÿ�û�ÿ��5������

### 7.2 ��ˢ����
- �����û�ID + IP���������
- Lua�ű����÷��ظ��ύ���
- ֧�ֶ�̬����������

### 7.3 ���ݰ�ȫ
- ���м۸���ؼ��㱣��2λС��
- ������ݶ��������ݿ�ͬ��У��
- �ؼ�������¼��ϸ�����־

---

## 8. �ӿ�ʵ��״̬�ܽ�

### ? **�������Ĺ���** (11���ӿ�)
1. **��������** (2��)
   - ��ʼ����ɱ���Redis
   - ������ɱ�����

2. **ʵʱ���ͳ��** (3��)
   - ʵʱ�����
   - ����������
   - ʵʱͳ������

3. **Lua�ű�����** (2��)
   - ����Lua�ű�
   - �ű�ִ��ͳ��

4. **�û������Ż�** (4��)
   - ��ɱ�ʸ�Ԥ���
   - Lua�ű�����ɱ�µ�
   - ����״̬ʵʱ��ѯ
   - ��ǿ���ʸ���

### ? **���ļ�������**
- **ԭ���Ա�֤**: ����Lua�ű���ԭ�Ӳ���
- **������**: ֧���򼶲�������Ӧʱ��<50ms
- **�㳬��**: �ϸ�Ŀ����ƻ���
- **һ��һ��**: ���Ƶ��û��޹��߼�
- **ʵʱ���**: ȫ��λ�����ܺ�ҵ����

---

**ע������**��
1. ���нӿڶ���Ҫ������Ӧ��Swagger�ĵ�ע��
2. �漰����ַ�����Ӧ���ݱ���ʹ��UTF-8����
3. �ؼ�ҵ�������Ҫ��¼��ϸ��־
4. ���ݿ�������Ҫ������Redis��һ����
5. �߲����ӿ���Ҫ����ѹ��������֤
6. Lua�ű���Ҫ���г�ֵĵ�Ԫ����
7. Redis����ʱ��Ҫ�н�������

���ӿ��ĵ�����Lua�ű�����ջ��Ϊ�߲�����ɱ�����ṩ�������Ľ��������
