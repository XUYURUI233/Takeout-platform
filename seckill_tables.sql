-- 秒杀系统数据库表结构

-- 1. 秒杀活动表
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
  PRIMARY KEY (`id`),
  KEY `idx_status_time` (`status`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';

-- 2. 秒杀商品项表
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
  KEY `idx_dish_id` (`dish_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀商品项表';

-- 3. 秒杀订单表
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
  KEY `idx_item_id` (`item_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀订单表';

-- 4. 秒杀库存操作记录表（可选，用于审计）
CREATE TABLE `seckill_stock_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `item_id` bigint NOT NULL COMMENT '秒杀商品项ID',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型：INCREASE/DECREASE/SET',
  `stock_change` int NOT NULL COMMENT '库存变化量',
  `before_stock` int NOT NULL COMMENT '操作前库存',
  `after_stock` int NOT NULL COMMENT '操作后库存',
  `order_id` varchar(50) DEFAULT NULL COMMENT '关联订单ID',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀库存操作记录表';

-- 插入测试数据
INSERT INTO `seckill` (`seckill_name`, `seckill_description`, `start_time`, `end_time`, `banner_image`, `status`, `create_time`, `create_user`) VALUES
('限时秒杀活动', '精选菜品限时特价', '2024-01-20 10:00:00', '2024-01-20 12:00:00', 'https://example.com/banner.jpg', 1, NOW(), 1);

INSERT INTO `seckill_item` (`seckill_id`, `dish_id`, `dish_name`, `original_price`, `seckill_price`, `seckill_stock`, `current_stock`, `limit_per_user`, `status`, `create_time`) VALUES
(1, 1, '宫保鸡丁', 28.00, 19.90, 100, 100, 2, 1, NOW()),
(1, 2, '麻婆豆腐', 18.00, 12.90, 80, 80, 3, 1, NOW());

