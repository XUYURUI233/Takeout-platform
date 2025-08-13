# ��������ɱϵͳ����ʵ���ܽ�

## ��Ŀ����

����ĿΪ������ϵͳʵ������������ɱ���ܣ������̼Ҷ˹����̨��΢��С����ͻ��ˡ�ϵͳ����ǰ��˷���ܹ����ṩ��ɱ��Ĵ�������������Ͷ���������������ܡ�

## ϵͳ�ܹ�

### ����ջ
- **���**: Spring Boot + MyBatis + MySQL + Redis
- **�̼Ҷ�**: Vue.js + Element UI + Nginx
- **С�����**: ΢��С����ԭ������
- **����**: Nginx�������

### ϵͳ�ܹ�ͼ
```
��������������������������������������    ��������������������������������������    ��������������������������������������
��   ΢��С����     ��    ��   �̼ҹ����̨   ��    ��   ��˷���      ��
��   (�ͻ���)      ��    ��   (Vue.js)      ��    ��   (Spring Boot) ��
��������������������������������������    ��������������������������������������    ��������������������������������������
         ��                       ��                       ��
         �������������������������������������������������੤����������������������������������������������
                                 ��
                    ��������������������������������������
                    ��   Nginx����     ��
                    ��������������������������������������
                                 ��
                    ��������������������������������������
                    ��   MySQL���ݿ�   ��
                    ��   Redis����     ��
                    ��������������������������������������
```

## ����ģ��

### 1. �̼Ҷ˹����̨

#### 1.1 ϵͳ����ҳ�� (`seckill-admin.html`)
- **����**: ϵͳ���������ٲ���������ͳ��
- **����**: 
  - ʵʱ����չʾ
  - ���ٵ�����������ģ��
  - ϵͳ���Խ���
  - ���ݷ���Ԥ��

#### 1.2 ��ɱ����� (`seckill.html`)
- **����**: ��ɱ��������������ڹ���
- **����**:
  - ������ɱ�
  - �༭���Ϣ
  - ɾ���
  - �鿴������ͳ������
  - �״̬����
  - ��Ʒ������ӡ��༭��ɾ����ɱ��Ʒ��

#### 1.3 ������ (`seckill-stock.html`)
- **����**: ��ɱ��Ʒ����ʵʱ��غ͹���
- **����**:
  - ʵʱ�����
  - �����²��������ӡ����١����ã�
  - ���ع�����
  - ������¼�鿴
  - ���Ԥ�����Ϳ�桢ȱ�����ѣ�
  - ������ͳ��

#### 1.4 �������� (`seckill-orders.html`)
- **����**: ��ɱ�����Ĳ鿴�ʹ���
- **����**:
  - �����б�鿴
  - ����״̬����
  - ��������鿴
  - ������������
  - ����ͳ�Ƹ���
  - ��ά��ɸѡ�����״̬��ʱ�䡢�ؼ��ʣ�

### 2. ΢��С����ͻ���

#### 2.1 ��ɱ�б�ҳ�� (`/pages/seckill/seckill`)
- **����**: չʾ��ɱ�����Ʒ
- **����**:
  - �ֲ����չʾ
  - ��Ʒ�б�չʾ
  - ��ҳ���ع���
  - ʵʱ����ʱ��ʾ
  - ���״̬���
  - ����ˢ�º��������ظ���

#### 2.2 ��ɱ����ҳ�� (`/pages/seckill/purchase`)
- **����**: �û�������ɱ����
- **����**:
  - ��Ʒ��Ϣչʾ
  - ����ѡ��֧�ּӼ���ť��
  - ��ַѡ����
  - ��ע����
  - �ܼۼ���
  - ����ȷ��

#### 2.3 ��ɱ����ҳ�� (`/pages/seckill/orders`)
- **����**: �û��鿴��ɱ����
- **����**:
  - �����б�չʾ
  - ����״̬�鿴
  - ��������鿴
  - ����״̬ɸѡ

## ���ݿ����

### ���ı�ṹ

#### 1. ��ɱ��� (seckill)
```sql
CREATE TABLE `seckill` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seckill_name` varchar(100) NOT NULL COMMENT '�����',
  `seckill_description` varchar(500) COMMENT '�����',
  `start_time` datetime NOT NULL COMMENT '��ʼʱ��',
  `end_time` datetime NOT NULL COMMENT '����ʱ��',
  `banner_image` varchar(255) COMMENT '���ͼƬ',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '״̬',
  `create_time` datetime NOT NULL COMMENT '����ʱ��',
  `update_time` datetime COMMENT '����ʱ��',
  `create_user` bigint COMMENT '������',
  `update_user` bigint COMMENT '������',
  PRIMARY KEY (`id`)
);
```

#### 2. ��ɱ��Ʒ�� (seckill_item)
```sql
CREATE TABLE `seckill_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seckill_id` bigint NOT NULL COMMENT '�ID',
  `dish_id` bigint NOT NULL COMMENT '��ƷID',
  `dish_name` varchar(100) NOT NULL COMMENT '��Ʒ����',
  `original_price` decimal(10,2) NOT NULL COMMENT 'ԭ��',
  `seckill_price` decimal(10,2) NOT NULL COMMENT '��ɱ��',
  `seckill_stock` int NOT NULL COMMENT '�ܿ��',
  `current_stock` int NOT NULL COMMENT '��ǰ���',
  `limit_per_user` int NOT NULL DEFAULT '1' COMMENT '�޹�����',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '״̬',
  `create_time` datetime NOT NULL,
  `update_time` datetime,
  PRIMARY KEY (`id`)
);
```

#### 3. ��ɱ������ (seckill_order)
```sql
CREATE TABLE `seckill_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_number` varchar(50) NOT NULL COMMENT '������',
  `seckill_id` bigint NOT NULL COMMENT '�ID',
  `item_id` bigint NOT NULL COMMENT '��ƷID',
  `user_id` bigint NOT NULL COMMENT '�û�ID',
  `dish_id` bigint NOT NULL COMMENT '��ƷID',
  `dish_name` varchar(100) NOT NULL COMMENT '��Ʒ����',
  `quantity` int NOT NULL COMMENT '����',
  `seckill_price` decimal(10,2) NOT NULL COMMENT '��ɱ��',
  `total_amount` decimal(10,2) NOT NULL COMMENT '�ܽ��',
  `address_id` bigint NOT NULL COMMENT '��ַID',
  `remark` varchar(500) COMMENT '��ע',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '����״̬',
  `pay_method` tinyint COMMENT '֧����ʽ',
  `pay_time` datetime COMMENT '֧��ʱ��',
  `pay_deadline` datetime NOT NULL COMMENT '֧����ֹʱ��',
  `create_time` datetime NOT NULL,
  `update_time` datetime,
  PRIMARY KEY (`id`)
);
```

#### 4. ��������¼�� (seckill_stock_log)
```sql
CREATE TABLE `seckill_stock_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `item_id` bigint NOT NULL COMMENT '��ƷID',
  `operation_type` varchar(20) NOT NULL COMMENT '��������',
  `stock_change` int NOT NULL COMMENT '���仯��',
  `before_stock` int NOT NULL COMMENT '����ǰ���',
  `after_stock` int NOT NULL COMMENT '��������',
  `order_id` varchar(50) COMMENT '��������ID',
  `operator` varchar(50) COMMENT '������',
  `remark` varchar(500) COMMENT '��ע',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);
```

## API�ӿ����

### �̼Ҷ�API

#### 1. ��ɱ�����
```
POST   /admin/seckill/create              # �����
GET    /admin/seckill/list                # ��б�
PUT    /admin/seckill/update/{id}         # ���»
DELETE /admin/seckill/delete/{id}         # ɾ���
GET    /admin/seckill/statistics/{id}     # �ͳ��
```

#### 2. ������
```
GET    /admin/seckill/stock/list          # ����б�
POST   /admin/seckill/stock/update        # ���¿��
POST   /admin/seckill/stock/rollback      # ���ع�
GET    /admin/seckill/stock/log/{id}      # ������¼
GET    /admin/seckill/stock/overview      # ������
```

#### 3. ��������
```
GET    /admin/seckill/orders/list         # �����б�
GET    /admin/seckill/orders/{id}         # ��������
POST   /admin/seckill/orders/cancel/{id}  # ȡ������
GET    /admin/seckill/orders/export       # ��������
GET    /admin/seckill/orders/overview     # ��������
```

#### 4. ����ͳ��
```
GET    /admin/seckill/dashboard/stats     # �Ǳ��ͳ��
GET    /admin/seckill/analytics           # ���ݷ���
```

### С�����API

#### 1. ��ɱ�
```
GET    /user/seckill/banner/list          # ����б�
GET    /user/seckill/goods/list           # ��Ʒ�б�
```

#### 2. ��ɱ����
```
POST   /user/seckill/purchase              # ������ɱ
GET    /user/seckill/orders/list          # �����б�
```

## ���Ĺ���ʵ��

### 1. ��ɱ�����
- **�����**: ֧�����ûʱ�䡢��Ʒ��Ϣ���۸����
- **��༭**: ���޸Ļ������Ϣ�����ѿ�ʼ�Ļ���Ʊ༭
- **�ɾ��**: ֻ��ɾ��δ��ʼ�Ļ
- **״̬����**: ����/���û״̬

### 2. ������
- **ʵʱ���**: ʵʱ��ʾ���״̬��֧�ֿ��Ԥ��
- **������**: ֧�����ӡ����١����ÿ�����
- **���ع�**: ����ȡ��ʱ�Զ��ع����
- **������¼**: ��¼���п��������������

### 3. ��������
- **��������**: �û�������ɱʱ��������
- **����״̬**: ��֧������֧������ȡ�����ѹ���
- **֧����ʱ**: �Զ�ȡ����ʱδ֧���Ķ���
- **��������**: ֧�ְ�����������������

### 4. ����ͳ��
- **ʵʱͳ��**: ������������������������۶��
- **���Ʒ���**: �������ơ��û���Ϊ����
- **Ч������**: ת���ʡ��͵��۵ȹؼ�ָ��

## ��������

### 1. �߲�������
- **Redis����**: ʹ��Redis�����ȵ�����
- **���Ԥ��**: �µ�ʱԤ�ۿ�棬֧��ʧ��ʱ�ع�
- **�ֲ�ʽ��**: ��ֹ�������ظ��µ�

### 2. ����һ����
- **�������**: ʹ�����ݿ�����ȷ������һ����
- **���ԭ����**: ������ʹ��ԭ�Ӳ���
- **����״̬**: ����״̬�����ԭ���Ա�֤

### 3. �û�����
- **ʵʱ����**: ���͵���ʱʵʱ����
- **������**: ���ƵĴ�����ʾ�ʹ������
- **��Ӧʽ���**: ���䲻ͬ�豸��Ļ

### 4. ��ȫ��
- **Ȩ�޿���**: ����JWT�������֤
- **����У��**: �ϸ���������У��
- **SQLע�����**: ʹ��MyBatis��������ѯ

## ����ܹ�

### 1. ǰ�˲���
```
Nginx (80�˿�)
������ �̼Ҷ˹����̨ (Vue.js)
��   ������ seckill-admin.html
��   ������ seckill.html
��   ������ seckill-stock.html
��   ������ seckill-orders.html
������ ��̬��Դ
    ������ CSS�ļ�
    ������ JS�ļ�
    ������ ͼƬ��Դ
```

### 2. ��˲���
```
Spring BootӦ�� (8074�˿�)
������ �̼Ҷ�API (/admin/*)
������ С�����API (/user/*)
������ ���ݿ����ӳ�
������ Redis����
```

### 3. ���ݿⲿ��
```
MySQL���ݿ�
������ ��ɱ��ر�
������ �û���ر�
������ ������ر�

Redis����
������ ��滺��
������ �����
������ �û��Ự
```

## �����Ż�

### 1. ǰ���Ż�
- **CDN����**: ʹ��CDN���ص�������
- **����ָ�**: ������������ҳ��
- **�������**: �����������������

### 2. ����Ż�
- **���ݿ�����**: Ϊ��ѯ�ֶ���Ӻ��ʵ�����
- **���ӳ�**: ʹ�����ݿ����ӳ��������
- **�������**: ����ʹ��Redis����

### 3. ϵͳ�Ż�
- **���ؾ���**: ֧�ֶ�ʵ������
- **��ظ澯**: ϵͳ���ܼ�غ͸澯
- **��־����**: ���Ƶ���־��¼�ͷ���

## ���Բ���

### 1. ���ܲ���
- **��Ԫ����**: ����ҵ���߼��ĵ�Ԫ����
- **���ɲ���**: API�ӿڵļ��ɲ���
- **�˵��˲���**: ����ҵ�����̲���

### 2. ���ܲ���
- **ѹ������**: �߲��������µ����ܲ���
- **���ز���**: ϵͳ������������
- **�ȶ��Բ���**: ��ʱ�����е��ȶ��Բ���

### 3. ��ȫ����
- **Ȩ�޲���**: Ȩ�޿��Ƶ���Ч�Բ���
- **ע�����**: SQLע���XSS��������
- **���ݰ�ȫ**: �������ݵİ�ȫ�Բ���

## ��غ�ά��

### 1. ϵͳ���
- **Ӧ�ü��**: Ӧ�����ܺʹ�����
- **���ݿ���**: ���ݿ����ܺ����Ӽ��
- **������**: Redis���ܺ������ʼ��

### 2. ��־����
- **������־**: �û�������Ϊ��־
- **������־**: ϵͳ������쳣��־
- **ҵ����־**: �ؼ�ҵ�������־

### 3. ���ݲ���
- **���ݱ���**: �������ݿⱸ��
- **���뱸��**: �汾���ƺʹ��뱸��
- **���ñ���**: ϵͳ���ñ���

## ��Ŀ�ܽ�

### 1. ��������
- **������ǰ��˷���ܹ�**
- **�߲�����ɱ�������Ż�����**
- **ʵʱ���ݼ�غ�ͳ��**
- **���Ƶ�Ȩ�޿��ƺͰ�ȫ��**
- **���õ��û��������**

### 2. ҵ���ֵ
- **�����̼���ӪЧ��**: ��ݵ���ɱ�����
- **��ǿ�û�����**: ��������ɱ��������
- **���ϵͳ�ȶ���**: ���ƵĴ�����ͼ��
- **֧��ҵ����չ**: ģ�黯��Ʊ��ڹ�����չ

### 3. ����չ��
- **֧�ָ�����ɱģʽ**: ����չΪԤԼ��ɱ��������ɱ��
- **֧�ָ�����Ʒ����**: ����չΪ�ײ���ɱ�������ɱ��
- **֧�ָ���Ӫ���**: ����չΪƴ�š����۵Ȼ
- **֧�ֶ��̻�ģʽ**: ����չΪ���̻���ɱƽ̨

## �����Ż�����

### 1. ������չ
- **��ɱԤ��**: ��ǰԤ����ɱ�
- **��ɱ����**: �û���ɱ���ѹ���
- **��ɱ����**: �罻������
- **��ɱ����**: �û����ۺͷ���

### 2. �����Ż�
- **΢����ܹ�**: ���Ϊ������΢����
- **��Ϣ����**: ʹ����Ϣ���д����첽����
- **�ֲ�ʽ����**: ʹ�÷ֲ�ʽ�����������
- **����������**: ʹ��Docker����������

### 3. ��Ӫ�Ż�
- **���ݷ���**: ����������ݷ�������
- **�����Ƽ�**: �����û���Ϊ�������Ƽ�
- **���Ի�**: ���Ի�����ɱ��Ƽ�
- **Ӫ������**: �����Ӫ�����ߺͲ���

---

**��������ɱϵͳ** - Ϊ�̼Һ��û��ṩ��������ɱ���������

