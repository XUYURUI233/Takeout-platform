# 秒杀系统API文档

## 基础信息
- **基础URL**: `http://localhost:8074`
- **认证方式**: JWT Token
- **数据格式**: JSON
- **字符编码**: UTF-8

---

## 1. 商家端秒杀管理

### 1.1 创建秒杀活动
- **路径**: `/admin/seckill/create`
- **方法**: `POST`
- **权限**: 商家管理员权限
- **描述**: 商家创建新的秒杀活动

**请求头**:
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

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
    },
    {
      "dishId": 2,
      "dishName": "麻婆豆腐",
      "originalPrice": 18.00,
      "seckillPrice": 12.90,
      "seckillStock": 80,
      "limitPerUser": 3
    }
  ],
  "bannerImage": "https://example.com/banner.jpg",
  "status": 1
}
```

**字段说明**:
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| seckillName | String | 是 | 秒杀活动名称 |
| seckillDescription | String | 否 | 活动描述 |
| startTime | String | 是 | 开始时间 (yyyy-MM-dd HH:mm:ss) |
| endTime | String | 是 | 结束时间 (yyyy-MM-dd HH:mm:ss) |
| seckillItems | Array | 是 | 秒杀商品列表 |
| bannerImage | String | 否 | 横幅图片URL |
| status | Integer | 是 | 状态：1-启用，0-禁用 |

**响应示例**:
```json
{
  "code": 1,
  "msg": "秒杀活动创建成功",
  "data": "秒杀活动创建成功"
}
```

### 1.2 获取秒杀活动列表
- **路径**: `/admin/seckill/list`
- **方法**: `GET`
- **权限**: 商家管理员权限
- **描述**: 商家查看所有秒杀活动

**请求头**:
```
Authorization: Bearer {admin_token}
```

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页大小，默认10 |
| status | Integer | 否 | 状态筛选：1-进行中，2-已结束，3-未开始 |

**请求示例**:
```
GET /admin/seckill/list?page=1&pageSize=10&status=1
```

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 5,
    "records": [
      {
        "id": 1,
        "seckillName": "限时秒杀活动",
        "seckillDescription": "精选菜品限时特价",
        "startTime": "2024-01-20 10:00:00",
        "endTime": "2024-01-20 12:00:00",
        "bannerImage": "https://example.com/banner.jpg",
        "status": 1,
        "createTime": "2024-01-15 14:30:00",
        "createUser": 1
      }
    ]
  }
}
```

### 1.3 更新秒杀活动
- **路径**: `/admin/seckill/update/{seckillId}`
- **方法**: `PUT`
- **权限**: 商家管理员权限
- **描述**: 更新秒杀活动信息

**请求头**:
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| seckillId | Long | 是 | 秒杀活动ID |

**请求体**:
```json
{
  "seckillName": "限时秒杀活动-更新版",
  "seckillDescription": "精选菜品限时特价-更新描述",
  "startTime": "2024-01-21 10:00:00",
  "endTime": "2024-01-21 12:00:00",
  "bannerImage": "https://example.com/new-banner.jpg",
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 1,
  "msg": "秒杀活动更新成功",
  "data": "秒杀活动更新成功"
}
```

### 1.4 删除秒杀活动
- **路径**: `/admin/seckill/delete/{seckillId}`
- **方法**: `DELETE`
- **权限**: 商家管理员权限
- **描述**: 删除秒杀活动（只能删除未开始的活动）

**请求头**:
```
Authorization: Bearer {admin_token}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| seckillId | Long | 是 | 秒杀活动ID |

**响应示例**:
```json
{
  "code": 1,
  "msg": "秒杀活动删除成功",
  "data": "秒杀活动删除成功"
}
```

### 1.5 更新秒杀库存
- **路径**: `/admin/seckill/stock/update`
- **方法**: `POST`
- **权限**: 商家管理员权限
- **描述**: 更新秒杀商品的库存

**请求头**:
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**请求体**:
```json
{
  "itemId": 1,
  "stockChange": -1,
  "operationType": "DECREASE",
  "orderId": "SK202401151435001",
  "remark": "用户秒杀扣减库存"
}
```

**字段说明**:
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| itemId | Long | 是 | 秒杀商品项ID |
| stockChange | Integer | 是 | 库存变化量（正数增加，负数减少） |
| operationType | String | 是 | 操作类型：INCREASE(增加)、DECREASE(减少)、SET(设置) |
| orderId | String | 否 | 订单ID |
| remark | String | 否 | 操作备注 |

**响应示例**:
```json
{
  "code": 1,
  "msg": "库存更新成功",
  "data": "库存更新成功"
}
```

### 1.6 回滚秒杀库存
- **路径**: `/admin/seckill/stock/rollback`
- **方法**: `POST`
- **权限**: 商家管理员权限
- **描述**: 回滚秒杀库存（订单取消时）

**请求头**:
```
Authorization: Bearer {admin_token}
Content-Type: application/x-www-form-urlencoded
```

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 是 | 订单ID |
| itemId | Long | 是 | 秒杀商品项ID |
| rollbackQuantity | Integer | 是 | 回滚数量 |

**请求示例**:
```
POST /admin/seckill/stock/rollback
Content-Type: application/x-www-form-urlencoded

orderId=SK202401151435001&itemId=1&rollbackQuantity=1
```

**响应示例**:
```json
{
  "code": 1,
  "msg": "库存回滚成功",
  "data": "库存回滚成功"
}
```

### 1.7 获取秒杀统计数据
- **路径**: `/admin/seckill/statistics/{seckillId}`
- **方法**: `GET`
- **权限**: 商家管理员权限
- **描述**: 获取秒杀活动的统计数据

**请求头**:
```
Authorization: Bearer {admin_token}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| seckillId | Long | 是 | 秒杀活动ID |

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

## 2. 小程序端秒杀接口

### 2.1 获取秒杀横幅列表
- **路径**: `/user/seckill/banner/list`
- **方法**: `GET`
- **权限**: 登录权限
- **描述**: 获取当前可显示的秒杀活动横幅

**请求头**:
```
Authorization: Bearer {user_token}
```

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
- **路径**: `/user/seckill/goods/list`
- **方法**: `GET`
- **权限**: 登录权限
- **描述**: 获取秒杀商品列表

**请求头**:
```
Authorization: Bearer {user_token}
```

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| seckillId | Long | 否 | 秒杀活动ID（不传则获取所有活动） |
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页大小，默认10 |

**请求示例**:
```
GET /user/seckill/goods/list?seckillId=1&page=1&pageSize=10
```

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
- **路径**: `/user/seckill/purchase`
- **方法**: `POST`
- **权限**: 登录权限
- **描述**: 用户参与秒杀购买

**请求头**:
```
Authorization: Bearer {user_token}
Content-Type: application/json
```

**请求体**:
```json
{
  "itemId": 1,
  "quantity": 1,
  "addressId": 1,
  "remark": "请尽快配送"
}
```

**字段说明**:
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| itemId | Long | 是 | 秒杀商品项ID |
| quantity | Integer | 是 | 购买数量 |
| addressId | Long | 是 | 收货地址ID |
| remark | String | 否 | 订单备注 |

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

### 2.4 获取用户秒杀订单列表
- **路径**: `/user/seckill/orders/list`
- **方法**: `GET`
- **权限**: 登录权限
- **描述**: 获取用户的秒杀订单列表

**请求头**:
```
Authorization: Bearer {user_token}
```

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页大小，默认10 |
| status | Integer | 否 | 订单状态：1-待支付，2-已支付，3-已取消 |

**请求示例**:
```
GET /user/seckill/orders/list?page=1&pageSize=10&status=1
```

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 3,
    "records": [
      {
        "orderId": "SK202401151435001",
        "seckillId": 1,
        "seckillName": "限时秒杀活动",
        "dishName": "宫保鸡丁",
        "quantity": 1,
        "seckillPrice": 19.90,
        "totalAmount": 19.90,
        "status": 1,
        "createTime": "2024-01-15 14:35:00",
        "payDeadline": "2024-01-15 15:05:00"
      }
    ]
  }
}
```

---

## 3. 错误码说明

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
| 2010 | 权限不足 |

---

## 4. 数据库表结构

### 4.1 秒杀活动表 (seckill)
```sql
CREATE TABLE `seckill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `seckill_name` varchar(100) NOT NULL COMMENT '秒杀活动名称',
  `seckill_description` varchar(500) DEFAULT NULL COMMENT '秒杀活动描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `banner_image` varchar(255) DEFAULT NULL COMMENT '横幅图片',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';
```

### 4.2 秒杀商品项表 (seckill_item)
```sql
CREATE TABLE `seckill_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `seckill_id` bigint NOT NULL COMMENT '秒杀活动ID',
  `dish_id` bigint NOT NULL COMMENT '菜品ID',
  `dish_name` varchar(100) NOT NULL COMMENT '菜品名称',
  `original_price` decimal(10,2) NOT NULL COMMENT '原价',
  `seckill_price` decimal(10,2) NOT NULL COMMENT '秒杀价',
  `seckill_stock` int NOT NULL COMMENT '秒杀库存',
  `current_stock` int NOT NULL COMMENT '当前库存',
  `limit_per_user` int NOT NULL DEFAULT '1' COMMENT '每人限购数量',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_seckill_id` (`seckill_id`),
  KEY `idx_dish_id` (`dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀商品项表';
```

### 4.3 秒杀订单表 (seckill_order)
```sql
CREATE TABLE `seckill_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_number` varchar(50) NOT NULL COMMENT '订单号',
  `seckill_id` bigint NOT NULL COMMENT '秒杀活动ID',
  `item_id` bigint NOT NULL COMMENT '秒杀商品项ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `dish_id` bigint NOT NULL COMMENT '菜品ID',
  `dish_name` varchar(100) NOT NULL COMMENT '菜品名称',
  `quantity` int NOT NULL COMMENT '购买数量',
  `seckill_price` decimal(10,2) NOT NULL COMMENT '秒杀价格',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `address_id` bigint NOT NULL COMMENT '地址ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '订单状态 1-待支付 2-已支付 3-已取消',
  `pay_method` tinyint DEFAULT NULL COMMENT '支付方式 1-微信 2-支付宝',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `pay_deadline` datetime NOT NULL COMMENT '支付截止时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_seckill_id` (`seckill_id`),
  KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀订单表';
```

---

## 5. 注意事项

1. **时间控制**: 秒杀活动严格按照设定时间进行，超时自动结束
2. **库存控制**: 使用Redis + Lua脚本确保库存扣减的原子性
3. **用户限制**: 每个用户在同一活动中有限购数量限制
4. **并发控制**: 高并发场景下使用分布式锁防止超卖
5. **订单超时**: 秒杀订单有支付时间限制，超时自动取消并回滚库存
6. **数据一致性**: 使用事务确保订单创建和库存扣减的一致性
7. **权限验证**: 所有接口都需要验证用户权限
8. **参数校验**: 所有请求参数都需要进行合法性校验

