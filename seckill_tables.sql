-- ��ɱϵͳ���ݿ��ṹ

-- 1. ��ɱ���
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
  PRIMARY KEY (`id`),
  KEY `idx_status_time` (`status`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='��ɱ���';

-- 2. ��ɱ��Ʒ���
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
  KEY `idx_dish_id` (`dish_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='��ɱ��Ʒ���';

-- 3. ��ɱ������
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
  KEY `idx_item_id` (`item_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='��ɱ������';

-- 4. ��ɱ��������¼����ѡ��������ƣ�
CREATE TABLE `seckill_stock_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '����',
  `item_id` bigint NOT NULL COMMENT '��ɱ��Ʒ��ID',
  `operation_type` varchar(20) NOT NULL COMMENT '�������ͣ�INCREASE/DECREASE/SET',
  `stock_change` int NOT NULL COMMENT '���仯��',
  `before_stock` int NOT NULL COMMENT '����ǰ���',
  `after_stock` int NOT NULL COMMENT '��������',
  `order_id` varchar(50) DEFAULT NULL COMMENT '��������ID',
  `operator` varchar(50) DEFAULT NULL COMMENT '������',
  `remark` varchar(500) DEFAULT NULL COMMENT '��ע',
  `create_time` datetime NOT NULL COMMENT '����ʱ��',
  PRIMARY KEY (`id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='��ɱ��������¼��';

-- �����������
INSERT INTO `seckill` (`seckill_name`, `seckill_description`, `start_time`, `end_time`, `banner_image`, `status`, `create_time`, `create_user`) VALUES
('��ʱ��ɱ�', '��ѡ��Ʒ��ʱ�ؼ�', '2024-01-20 10:00:00', '2024-01-20 12:00:00', 'https://example.com/banner.jpg', 1, NOW(), 1);

INSERT INTO `seckill_item` (`seckill_id`, `dish_id`, `dish_name`, `original_price`, `seckill_price`, `seckill_stock`, `current_stock`, `limit_per_user`, `status`, `create_time`) VALUES
(1, 1, '��������', 28.00, 19.90, 100, 100, 2, 1, NOW()),
(1, 2, '���Ŷ���', 18.00, 12.90, 80, 80, 3, 1, NOW());

