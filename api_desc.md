# 秒杀系统API接口文档

## 基础信息
- **基础URL**: `http://localhost:8074`
- **认证方式**: JWT Token
- **数据格式**: JSON

---

## 1. 商家端秒杀管理

### 1.1 创建秒杀活动
- **路径**: `/api/seckill/create`
- **方法**: `POST`
- **权限**: 商家管理员权限

**请求体**:
```json
{
  "seckillName": "限时秒杀活动",
  "seckillDescription": "精选菜品限时特价",
  "startTime": "2024-01-20 10:00:00",
  "endTime": "2024-01-20 12:00:00",
  "seckillItems": [
    {
      "dishId": 1,
      "dishName": "宫保鸡丁",
      "originalPrice": 28.00,
      "seckillPrice": 19.90,
      "seckillStock": 100,
      "limitPerUser": 2
    }
  ],
  "bannerImage": "https://example.com/banner.jpg",
  "status": 1
}
```

**字段说明**:
- `seckillName`: 秒杀活动名称
- `startTime/endTime`: 活动时间段
- `seckillItems`: 秒杀商品列表
- `seckillStock`: 秒杀库存数量
- `limitPerUser`: 每人限购数量

### 1.2 获取秒杀活动列表
- **路径**: `/api/seckill/admin/list`
- **方法**: `GET`
- **权限**: 商家管理员权限

**请求参数**:
- `page`: 页码，默认1
- `pageSize`: 每页大小，默认10
- `status`: 状态筛选（1-进行中，2-已结束，3-未开始）

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 5,
    "records": [
      {
        "seckillId": 1,
        "seckillName": "限时秒杀活动",
        "startTime": "2024-01-20 10:00:00",
        "endTime": "2024-01-20 12:00:00",
        "status": 1,
        "totalItems": 2,
        "totalSales": 150,
        "totalRevenue": 2985.00
      }
    ]
  }
}
```

### 1.3 更新秒杀活动
- **路径**: `/api/seckill/update/{seckillId}`
- **方法**: `PUT`
- **权限**: 商家管理员权限

### 1.4 删除秒杀活动
- **路径**: `/api/seckill/delete/{seckillId}`
- **方法**: `DELETE`
- **权限**: 商家管理员权限

---

## 2. 小程序端秒杀接口

### 2.1 获取秒杀横幅列表
- **路径**: `/api/seckill/banner/list`
- **方法**: `GET`
- **权限**: 登录权限

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "seckillId": 1,
      "seckillName": "限时秒杀活动",
      "bannerImage": "https://example.com/banner.jpg",
      "startTime": "2024-01-20 10:00:00",
      "endTime": "2024-01-20 12:00:00",
      "status": 1,
      "remainingTime": 3600
    }
  ]
}
```

### 2.2 获取秒杀商品列表
- **路径**: `/api/seckill/goods/list`
- **方法**: `GET`
- **权限**: 登录权限

**请求参数**:
- `seckillId`: 秒杀活动ID（可选）
- `page`: 页码，默认1
- `pageSize`: 每页大小，默认10

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 2,
    "records": [
      {
        "seckillId": 1,
        "itemId": 1,
        "dishId": 1,
        "dishName": "宫保鸡丁",
        "originalPrice": 28.00,
        "seckillPrice": 19.90,
        "seckillStock": 100,
        "currentStock": 30,
        "limitPerUser": 2,
        "userBoughtCount": 0,
        "remainingTime": 3600
      }
    ]
  }
}
```

### 2.3 参与秒杀购买
- **路径**: `/api/seckill/purchase`
- **方法**: `POST`
- **权限**: 登录权限

**请求体**:
```json
{
  "itemId": 1,
  "quantity": 1,
  "addressId": 1,
  "remark": "请尽快配送"
}
```

**响应示例**:
```json
{
  "code": 1,
  "msg": "秒杀成功",
  "data": {
    "orderId": "SK202401151435001",
    "seckillId": 1,
    "dishName": "宫保鸡丁",
    "quantity": 1,
    "seckillPrice": 19.90,
    "totalAmount": 19.90,
    "payDeadline": "2024-01-15 15:05:00"
  }
}
```

### 2.4 获取用户秒杀订单
- **路径**: `/api/seckill/orders/list`
- **方法**: `GET`
- **权限**: 登录权限

**请求参数**:
- `page`: 页码，默认1
- `pageSize`: 每页大小，默认10
- `status`: 订单状态（1-待支付，2-已支付，3-已取消）

---

## 3. 秒杀库存管理

### 3.1 更新秒杀库存
- **路径**: `/api/seckill/stock/update`
- **方法**: `POST`
- **权限**: 商家管理员权限

**请求体**:
```json
{
  "itemId": 1,
  "stockChange": -1,
  "operationType": "DECREASE",
  "orderId": "SK202401151435001"
}
```

### 3.2 回滚秒杀库存
- **路径**: `/api/seckill/stock/rollback`
- **方法**: `POST`
- **权限**: 商家管理员权限

**请求体**:
```json
{
  "orderId": "SK202401151435001",
  "itemId": 1,
  "rollbackQuantity": 1,
  "reason": "订单取消，回滚库存"
}
```

---

## 4. 秒杀数据统计

### 4.1 获取秒杀统计
- **路径**: `/api/seckill/statistics/{seckillId}`
- **方法**: `GET`
- **权限**: 商家管理员权限

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "seckillId": 1,
    "seckillName": "限时秒杀活动",
    "totalOrders": 150,
    "totalSales": 2985.00,
    "totalItems": 2,
    "conversionRate": 0.15
  }
}
```

---

## 5. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 1 | 成功 |
| 0 | 失败 |
| 2001 | 秒杀活动不存在 |
| 2002 | 秒杀活动未开始 |
| 2003 | 秒杀活动已结束 |
| 2004 | 秒杀商品库存不足 |
| 2005 | 超出用户购买限制 |
| 2006 | 用户已购买过该商品 |
| 2007 | 秒杀活动已暂停 |
| 2008 | 订单创建失败 |
| 2009 | 库存扣减失败 |

---

## 6. 注意事项

1. **时间控制**: 秒杀活动严格按照设定时间进行
2. **库存控制**: 使用Redis + Lua脚本确保库存扣减原子性
3. **用户限制**: 每个用户在同一活动中有限购数量限制
4. **并发控制**: 使用分布式锁防止超卖
5. **订单超时**: 秒杀订单有支付时间限制，超时自动取消
6. **数据一致性**: 使用事务确保订单创建和库存扣减一致性
