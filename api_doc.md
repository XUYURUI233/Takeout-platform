# ��ɱϵͳAPI�ĵ�

## ������Ϣ
- **����URL**: `http://localhost:8074`
- **��֤��ʽ**: JWT Token
- **���ݸ�ʽ**: JSON
- **�ַ�����**: UTF-8

---

## 1. �̼Ҷ���ɱ����

### 1.1 ������ɱ�
- **·��**: `/admin/seckill/create`
- **����**: `POST`
- **Ȩ��**: �̼ҹ���ԱȨ��
- **����**: �̼Ҵ����µ���ɱ�

**����ͷ**:
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**������**:
```json
{
  "seckillName": "��ʱ��ɱ�",
  "seckillDescription": "��ѡ��Ʒ��ʱ�ؼ�",
  "startTime": "2024-01-20 10:00:00",
  "endTime": "2024-01-20 12:00:00",
  "seckillItems": [
    {
      "dishId": 1,
      "dishName": "��������",
      "originalPrice": 28.00,
      "seckillPrice": 19.90,
      "seckillStock": 100,
      "limitPerUser": 2
    },
    {
      "dishId": 2,
      "dishName": "���Ŷ���",
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

**�ֶ�˵��**:
| �ֶ��� | ���� | ���� | ˵�� |
|--------|------|------|------|
| seckillName | String | �� | ��ɱ����� |
| seckillDescription | String | �� | ����� |
| startTime | String | �� | ��ʼʱ�� (yyyy-MM-dd HH:mm:ss) |
| endTime | String | �� | ����ʱ�� (yyyy-MM-dd HH:mm:ss) |
| seckillItems | Array | �� | ��ɱ��Ʒ�б� |
| bannerImage | String | �� | ���ͼƬURL |
| status | Integer | �� | ״̬��1-���ã�0-���� |

**��Ӧʾ��**:
```json
{
  "code": 1,
  "msg": "��ɱ������ɹ�",
  "data": "��ɱ������ɹ�"
}
```

### 1.2 ��ȡ��ɱ��б�
- **·��**: `/admin/seckill/list`
- **����**: `GET`
- **Ȩ��**: �̼ҹ���ԱȨ��
- **����**: �̼Ҳ鿴������ɱ�

**����ͷ**:
```
Authorization: Bearer {admin_token}
```

**�������**:
| ������ | ���� | ���� | ˵�� |
|--------|------|------|------|
| page | Integer | �� | ҳ�룬Ĭ��1 |
| pageSize | Integer | �� | ÿҳ��С��Ĭ��10 |
| status | Integer | �� | ״̬ɸѡ��1-�����У�2-�ѽ�����3-δ��ʼ |

**����ʾ��**:
```
GET /admin/seckill/list?page=1&pageSize=10&status=1
```

**��Ӧʾ��**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 5,
    "records": [
      {
        "id": 1,
        "seckillName": "��ʱ��ɱ�",
        "seckillDescription": "��ѡ��Ʒ��ʱ�ؼ�",
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

### 1.3 ������ɱ�
- **·��**: `/admin/seckill/update/{seckillId}`
- **����**: `PUT`
- **Ȩ��**: �̼ҹ���ԱȨ��
- **����**: ������ɱ���Ϣ

**����ͷ**:
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**·������**:
| ������ | ���� | ���� | ˵�� |
|--------|------|------|------|
| seckillId | Long | �� | ��ɱ�ID |

**������**:
```json
{
  "seckillName": "��ʱ��ɱ�-���°�",
  "seckillDescription": "��ѡ��Ʒ��ʱ�ؼ�-��������",
  "startTime": "2024-01-21 10:00:00",
  "endTime": "2024-01-21 12:00:00",
  "bannerImage": "https://example.com/new-banner.jpg",
  "status": 1
}
```

**��Ӧʾ��**:
```json
{
  "code": 1,
  "msg": "��ɱ����³ɹ�",
  "data": "��ɱ����³ɹ�"
}
```

### 1.4 ɾ����ɱ�
- **·��**: `/admin/seckill/delete/{seckillId}`
- **����**: `DELETE`
- **Ȩ��**: �̼ҹ���ԱȨ��
- **����**: ɾ����ɱ���ֻ��ɾ��δ��ʼ�Ļ��

**����ͷ**:
```
Authorization: Bearer {admin_token}
```

**·������**:
| ������ | ���� | ���� | ˵�� |
|--------|------|------|------|
| seckillId | Long | �� | ��ɱ�ID |

**��Ӧʾ��**:
```json
{
  "code": 1,
  "msg": "��ɱ�ɾ���ɹ�",
  "data": "��ɱ�ɾ���ɹ�"
}
```

### 1.5 ������ɱ���
- **·��**: `/admin/seckill/stock/update`
- **����**: `POST`
- **Ȩ��**: �̼ҹ���ԱȨ��
- **����**: ������ɱ��Ʒ�Ŀ��

**����ͷ**:
```
Authorization: Bearer {admin_token}
Content-Type: application/json
```

**������**:
```json
{
  "itemId": 1,
  "stockChange": -1,
  "operationType": "DECREASE",
  "orderId": "SK202401151435001",
  "remark": "�û���ɱ�ۼ����"
}
```

**�ֶ�˵��**:
| �ֶ��� | ���� | ���� | ˵�� |
|--------|------|------|------|
| itemId | Long | �� | ��ɱ��Ʒ��ID |
| stockChange | Integer | �� | ���仯�����������ӣ��������٣� |
| operationType | String | �� | �������ͣ�INCREASE(����)��DECREASE(����)��SET(����) |
| orderId | String | �� | ����ID |
| remark | String | �� | ������ע |

**��Ӧʾ��**:
```json
{
  "code": 1,
  "msg": "�����³ɹ�",
  "data": "�����³ɹ�"
}
```

### 1.6 �ع���ɱ���
- **·��**: `/admin/seckill/stock/rollback`
- **����**: `POST`
- **Ȩ��**: �̼ҹ���ԱȨ��
- **����**: �ع���ɱ��棨����ȡ��ʱ��

**����ͷ**:
```
Authorization: Bearer {admin_token}
Content-Type: application/x-www-form-urlencoded
```

**�������**:
| ������ | ���� | ���� | ˵�� |
|--------|------|------|------|
| orderId | String | �� | ����ID |
| itemId | Long | �� | ��ɱ��Ʒ��ID |
| rollbackQuantity | Integer | �� | �ع����� |

**����ʾ��**:
```
POST /admin/seckill/stock/rollback
Content-Type: application/x-www-form-urlencoded

orderId=SK202401151435001&itemId=1&rollbackQuantity=1
```

**��Ӧʾ��**:
```json
{
  "code": 1,
  "msg": "���ع��ɹ�",
  "data": "���ع��ɹ�"
}
```

### 1.7 ��ȡ��ɱͳ������
- **·��**: `/admin/seckill/statistics/{seckillId}`
- **����**: `GET`
- **Ȩ��**: �̼ҹ���ԱȨ��
- **����**: ��ȡ��ɱ���ͳ������

**����ͷ**:
```
Authorization: Bearer {admin_token}
```

**·������**:
| ������ | ���� | ���� | ˵�� |
|--------|------|------|------|
| seckillId | Long | �� | ��ɱ�ID |

**��Ӧʾ��**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "seckillId": 1,
    "seckillName": "��ʱ��ɱ�",
    "totalOrders": 150,
    "totalSales": 2985.00,
    "totalItems": 2,
    "conversionRate": 0.15
  }
}
```

---

## 2. С�������ɱ�ӿ�

### 2.1 ��ȡ��ɱ����б�
- **·��**: `/user/seckill/banner/list`
- **����**: `GET`
- **Ȩ��**: ��¼Ȩ��
- **����**: ��ȡ��ǰ����ʾ����ɱ����

**����ͷ**:
```
Authorization: Bearer {user_token}
```

**��Ӧʾ��**:
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "seckillId": 1,
      "seckillName": "��ʱ��ɱ�",
      "bannerImage": "https://example.com/banner.jpg",
      "startTime": "2024-01-20 10:00:00",
      "endTime": "2024-01-20 12:00:00",
      "status": 1,
      "remainingTime": 3600
    }
  ]
}
```

### 2.2 ��ȡ��ɱ��Ʒ�б�
- **·��**: `/user/seckill/goods/list`
- **����**: `GET`
- **Ȩ��**: ��¼Ȩ��
- **����**: ��ȡ��ɱ��Ʒ�б�

**����ͷ**:
```
Authorization: Bearer {user_token}
```

**�������**:
| ������ | ���� | ���� | ˵�� |
|--------|------|------|------|
| seckillId | Long | �� | ��ɱ�ID���������ȡ���л�� |
| page | Integer | �� | ҳ�룬Ĭ��1 |
| pageSize | Integer | �� | ÿҳ��С��Ĭ��10 |

**����ʾ��**:
```
GET /user/seckill/goods/list?seckillId=1&page=1&pageSize=10
```

**��Ӧʾ��**:
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
        "dishName": "��������",
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

### 2.3 ������ɱ����
- **·��**: `/user/seckill/purchase`
- **����**: `POST`
- **Ȩ��**: ��¼Ȩ��
- **����**: �û�������ɱ����

**����ͷ**:
```
Authorization: Bearer {user_token}
Content-Type: application/json
```

**������**:
```json
{
  "itemId": 1,
  "quantity": 1,
  "addressId": 1,
  "remark": "�뾡������"
}
```

**�ֶ�˵��**:
| �ֶ��� | ���� | ���� | ˵�� |
|--------|------|------|------|
| itemId | Long | �� | ��ɱ��Ʒ��ID |
| quantity | Integer | �� | �������� |
| addressId | Long | �� | �ջ���ַID |
| remark | String | �� | ������ע |

**��Ӧʾ��**:
```json
{
  "code": 1,
  "msg": "��ɱ�ɹ�",
  "data": {
    "orderId": "SK202401151435001",
    "seckillId": 1,
    "dishName": "��������",
    "quantity": 1,
    "seckillPrice": 19.90,
    "totalAmount": 19.90,
    "payDeadline": "2024-01-15 15:05:00"
  }
}
```

### 2.4 ��ȡ�û���ɱ�����б�
- **·��**: `/user/seckill/orders/list`
- **����**: `GET`
- **Ȩ��**: ��¼Ȩ��
- **����**: ��ȡ�û�����ɱ�����б�

**����ͷ**:
```
Authorization: Bearer {user_token}
```

**�������**:
| ������ | ���� | ���� | ˵�� |
|--------|------|------|------|
| page | Integer | �� | ҳ�룬Ĭ��1 |
| pageSize | Integer | �� | ÿҳ��С��Ĭ��10 |
| status | Integer | �� | ����״̬��1-��֧����2-��֧����3-��ȡ�� |

**����ʾ��**:
```
GET /user/seckill/orders/list?page=1&pageSize=10&status=1
```

**��Ӧʾ��**:
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
        "seckillName": "��ʱ��ɱ�",
        "dishName": "��������",
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

## 3. ������˵��

| ������ | ˵�� |
|--------|------|
| 1 | �ɹ� |
| 0 | ʧ�� |
| 2001 | ��ɱ������� |
| 2002 | ��ɱ�δ��ʼ |
| 2003 | ��ɱ��ѽ��� |
| 2004 | ��ɱ��Ʒ��治�� |
| 2005 | �����û��������� |
| 2006 | �û��ѹ��������Ʒ |
| 2007 | ��ɱ�����ͣ |
| 2008 | ��������ʧ�� |
| 2009 | ���ۼ�ʧ�� |
| 2010 | Ȩ�޲��� |

---

## 4. ���ݿ��ṹ

### 4.1 ��ɱ��� (seckill)
```sql
CREATE TABLE `seckill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '����',
  `seckill_name` varchar(100) NOT NULL COMMENT '��ɱ�����',
  `seckill_description` varchar(500) DEFAULT NULL COMMENT '��ɱ�����',
  `start_time` datetime NOT NULL COMMENT '��ʼʱ��',
  `end_time` datetime NOT NULL COMMENT '����ʱ��',
  `banner_image` varchar(255) DEFAULT NULL COMMENT '���ͼƬ',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '״̬ 0-���� 1-����',
  `create_time` datetime NOT NULL COMMENT '����ʱ��',
  `update_time` datetime DEFAULT NULL COMMENT '����ʱ��',
  `create_user` bigint DEFAULT NULL COMMENT '������',
  `update_user` bigint DEFAULT NULL COMMENT '������',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='��ɱ���';
```

### 4.2 ��ɱ��Ʒ��� (seckill_item)
```sql
CREATE TABLE `seckill_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '����',
  `seckill_id` bigint NOT NULL COMMENT '��ɱ�ID',
  `dish_id` bigint NOT NULL COMMENT '��ƷID',
  `dish_name` varchar(100) NOT NULL COMMENT '��Ʒ����',
  `original_price` decimal(10,2) NOT NULL COMMENT 'ԭ��',
  `seckill_price` decimal(10,2) NOT NULL COMMENT '��ɱ��',
  `seckill_stock` int NOT NULL COMMENT '��ɱ���',
  `current_stock` int NOT NULL COMMENT '��ǰ���',
  `limit_per_user` int NOT NULL DEFAULT '1' COMMENT 'ÿ���޹�����',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '״̬ 0-���� 1-����',
  `create_time` datetime NOT NULL COMMENT '����ʱ��',
  `update_time` datetime DEFAULT NULL COMMENT '����ʱ��',
  PRIMARY KEY (`id`),
  KEY `idx_seckill_id` (`seckill_id`),
  KEY `idx_dish_id` (`dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='��ɱ��Ʒ���';
```

### 4.3 ��ɱ������ (seckill_order)
```sql
CREATE TABLE `seckill_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '����',
  `order_number` varchar(50) NOT NULL COMMENT '������',
  `seckill_id` bigint NOT NULL COMMENT '��ɱ�ID',
  `item_id` bigint NOT NULL COMMENT '��ɱ��Ʒ��ID',
  `user_id` bigint NOT NULL COMMENT '�û�ID',
  `dish_id` bigint NOT NULL COMMENT '��ƷID',
  `dish_name` varchar(100) NOT NULL COMMENT '��Ʒ����',
  `quantity` int NOT NULL COMMENT '��������',
  `seckill_price` decimal(10,2) NOT NULL COMMENT '��ɱ�۸�',
  `total_amount` decimal(10,2) NOT NULL COMMENT '�ܽ��',
  `address_id` bigint NOT NULL COMMENT '��ַID',
  `remark` varchar(500) DEFAULT NULL COMMENT '��ע',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '����״̬ 1-��֧�� 2-��֧�� 3-��ȡ��',
  `pay_method` tinyint DEFAULT NULL COMMENT '֧����ʽ 1-΢�� 2-֧����',
  `pay_time` datetime DEFAULT NULL COMMENT '֧��ʱ��',
  `pay_deadline` datetime NOT NULL COMMENT '֧����ֹʱ��',
  `create_time` datetime NOT NULL COMMENT '����ʱ��',
  `update_time` datetime DEFAULT NULL COMMENT '����ʱ��',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_seckill_id` (`seckill_id`),
  KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='��ɱ������';
```

---

## 5. ע������

1. **ʱ�����**: ��ɱ��ϸ����趨ʱ����У���ʱ�Զ�����
2. **������**: ʹ��Redis + Lua�ű�ȷ�����ۼ���ԭ����
3. **�û�����**: ÿ���û���ͬһ������޹���������
4. **��������**: �߲���������ʹ�÷ֲ�ʽ����ֹ����
5. **������ʱ**: ��ɱ������֧��ʱ�����ƣ���ʱ�Զ�ȡ�����ع����
6. **����һ����**: ʹ������ȷ�����������Ϳ��ۼ���һ����
7. **Ȩ����֤**: ���нӿڶ���Ҫ��֤�û�Ȩ��
8. **����У��**: ���������������Ҫ���кϷ���У��

