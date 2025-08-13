# ��ɱϵͳAPI�ӿ��ĵ�

## ������Ϣ
- **����URL**: `http://localhost:8074`
- **��֤��ʽ**: JWT Token
- **���ݸ�ʽ**: JSON

---

## 1. �̼Ҷ���ɱ����

### 1.1 ������ɱ�
- **·��**: `/api/seckill/create`
- **����**: `POST`
- **Ȩ��**: �̼ҹ���ԱȨ��

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
    }
  ],
  "bannerImage": "https://example.com/banner.jpg",
  "status": 1
}
```

**�ֶ�˵��**:
- `seckillName`: ��ɱ�����
- `startTime/endTime`: �ʱ���
- `seckillItems`: ��ɱ��Ʒ�б�
- `seckillStock`: ��ɱ�������
- `limitPerUser`: ÿ���޹�����

### 1.2 ��ȡ��ɱ��б�
- **·��**: `/api/seckill/admin/list`
- **����**: `GET`
- **Ȩ��**: �̼ҹ���ԱȨ��

**�������**:
- `page`: ҳ�룬Ĭ��1
- `pageSize`: ÿҳ��С��Ĭ��10
- `status`: ״̬ɸѡ��1-�����У�2-�ѽ�����3-δ��ʼ��

**��Ӧʾ��**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 5,
    "records": [
      {
        "seckillId": 1,
        "seckillName": "��ʱ��ɱ�",
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

### 1.3 ������ɱ�
- **·��**: `/api/seckill/update/{seckillId}`
- **����**: `PUT`
- **Ȩ��**: �̼ҹ���ԱȨ��

### 1.4 ɾ����ɱ�
- **·��**: `/api/seckill/delete/{seckillId}`
- **����**: `DELETE`
- **Ȩ��**: �̼ҹ���ԱȨ��

---

## 2. С�������ɱ�ӿ�

### 2.1 ��ȡ��ɱ����б�
- **·��**: `/api/seckill/banner/list`
- **����**: `GET`
- **Ȩ��**: ��¼Ȩ��

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
- **·��**: `/api/seckill/goods/list`
- **����**: `GET`
- **Ȩ��**: ��¼Ȩ��

**�������**:
- `seckillId`: ��ɱ�ID����ѡ��
- `page`: ҳ�룬Ĭ��1
- `pageSize`: ÿҳ��С��Ĭ��10

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
- **·��**: `/api/seckill/purchase`
- **����**: `POST`
- **Ȩ��**: ��¼Ȩ��

**������**:
```json
{
  "itemId": 1,
  "quantity": 1,
  "addressId": 1,
  "remark": "�뾡������"
}
```

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

### 2.4 ��ȡ�û���ɱ����
- **·��**: `/api/seckill/orders/list`
- **����**: `GET`
- **Ȩ��**: ��¼Ȩ��

**�������**:
- `page`: ҳ�룬Ĭ��1
- `pageSize`: ÿҳ��С��Ĭ��10
- `status`: ����״̬��1-��֧����2-��֧����3-��ȡ����

---

## 3. ��ɱ������

### 3.1 ������ɱ���
- **·��**: `/api/seckill/stock/update`
- **����**: `POST`
- **Ȩ��**: �̼ҹ���ԱȨ��

**������**:
```json
{
  "itemId": 1,
  "stockChange": -1,
  "operationType": "DECREASE",
  "orderId": "SK202401151435001"
}
```

### 3.2 �ع���ɱ���
- **·��**: `/api/seckill/stock/rollback`
- **����**: `POST`
- **Ȩ��**: �̼ҹ���ԱȨ��

**������**:
```json
{
  "orderId": "SK202401151435001",
  "itemId": 1,
  "rollbackQuantity": 1,
  "reason": "����ȡ�����ع����"
}
```

---

## 4. ��ɱ����ͳ��

### 4.1 ��ȡ��ɱͳ��
- **·��**: `/api/seckill/statistics/{seckillId}`
- **����**: `GET`
- **Ȩ��**: �̼ҹ���ԱȨ��

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

## 5. ������˵��

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

---

## 6. ע������

1. **ʱ�����**: ��ɱ��ϸ����趨ʱ�����
2. **������**: ʹ��Redis + Lua�ű�ȷ�����ۼ�ԭ����
3. **�û�����**: ÿ���û���ͬһ������޹���������
4. **��������**: ʹ�÷ֲ�ʽ����ֹ����
5. **������ʱ**: ��ɱ������֧��ʱ�����ƣ���ʱ�Զ�ȡ��
6. **����һ����**: ʹ������ȷ�����������Ϳ��ۼ�һ����
