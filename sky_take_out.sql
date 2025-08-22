/*
 Navicat Premium Data Transfer

 Source Server         : 1111
 Source Server Type    : MySQL
 Source Server Version : 50536
 Source Host           : localhost:3306
 Source Schema         : sky_take_out

 Target Server Type    : MySQL
 Target Server Version : 50536
 File Encoding         : 65001

 Date: 19/08/2025 23:43:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address_book
-- ----------------------------
DROP TABLE IF EXISTS `address_book`;
CREATE TABLE `address_book`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `consignee` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '收货人',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '手机号',
  `province_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省级区划编号',
  `province_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省级名称',
  `city_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '市级区划编号',
  `city_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '市级名称',
  `district_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区级区划编号',
  `district_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区级名称',
  `detail` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '详细地址',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '默认 0 否 1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '地址簿' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of address_book
-- ----------------------------
INSERT INTO `address_book` VALUES (1, 4, '徐徐', '0', '18026714983', '32', '江苏省', '3209', '盐城市', '320922', '滨海县', '八栋7b503房间', '1', 1);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型   1 菜品分类 2 套餐分类',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '分类名称',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '顺序',
  `status` int(11) NULL DEFAULT NULL COMMENT '分类状态 0:禁用，1:启用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_category_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '菜品及套餐分类' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (11, 1, '酒水饮料', 10, 1, '2022-06-09 22:09:18', '2022-06-09 22:09:18', 1, 1);
INSERT INTO `category` VALUES (12, 1, '传统主食', 9, 1, '2022-06-09 22:09:32', '2022-06-09 22:18:53', 1, 1);
INSERT INTO `category` VALUES (13, 2, '人气套餐', 12, 1, '2022-06-09 22:11:38', '2022-06-10 11:04:40', 1, 1);
INSERT INTO `category` VALUES (15, 2, '商务套餐', 13, 1, '2022-06-09 22:14:10', '2022-06-10 11:04:48', 1, 1);
INSERT INTO `category` VALUES (16, 1, '蜀味烤鱼', 4, 1, '2022-06-09 22:15:37', '2022-08-31 14:27:25', 1, 1);
INSERT INTO `category` VALUES (17, 1, '蜀味牛蛙', 5, 1, '2022-06-09 22:16:14', '2022-08-31 14:39:44', 1, 1);
INSERT INTO `category` VALUES (18, 1, '特色蒸菜', 6, 1, '2022-06-09 22:17:42', '2022-06-09 22:17:42', 1, 1);
INSERT INTO `category` VALUES (19, 1, '新鲜时蔬', 7, 1, '2022-06-09 22:18:12', '2022-06-09 22:18:28', 1, 1);
INSERT INTO `category` VALUES (20, 1, '水煮鱼', 8, 1, '2022-06-09 22:22:29', '2022-06-09 22:23:45', 1, 1);
INSERT INTO `category` VALUES (21, 1, '汤类', 11, 1, '2022-06-10 10:51:47', '2022-06-10 10:51:47', 1, 1);
INSERT INTO `category` VALUES (22, 1, '请问', 1, 0, '2025-08-13 22:31:50', '2025-08-13 22:31:50', 1, 1);
INSERT INTO `category` VALUES (23, 1, '阿德撒法鱼', 0, 1, '2025-08-13 22:40:40', '2025-08-14 01:27:16', 1, 1);
INSERT INTO `category` VALUES (24, 2, '为全球', 1, 1, '2025-08-13 22:41:00', '2025-08-14 01:27:18', 1, 1);

-- ----------------------------
-- Table structure for dish
-- ----------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '菜品名称',
  `category_id` bigint(20) NOT NULL COMMENT '菜品分类id',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '菜品价格',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '图片',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '描述信息',
  `status` int(11) NULL DEFAULT 1 COMMENT '0 停售 1 起售',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_dish_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 73 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '菜品' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of dish
-- ----------------------------
INSERT INTO `dish` VALUES (46, '王老吉', 11, 6.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', '', 1, '2022-06-09 22:40:47', '2022-06-09 22:40:47', 1, 1);
INSERT INTO `dish` VALUES (47, '北冰洋', 11, 4.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/4451d4be-89a2-4939-9c69-3a87151cb979.png', '还是小时候的味道', 1, '2022-06-10 09:18:49', '2022-06-10 09:18:49', 1, 1);
INSERT INTO `dish` VALUES (48, '雪花啤酒', 11, 4.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/bf8cbfc1-04d2-40e8-9826-061ee41ab87c.png', '', 1, '2022-06-10 09:22:54', '2022-06-10 09:22:54', 1, 1);
INSERT INTO `dish` VALUES (49, '米饭', 12, 2.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/76752350-2121-44d2-b477-10791c23a8ec.png', '精选五常大米', 1, '2022-06-10 09:30:17', '2022-06-10 09:30:17', 1, 1);
INSERT INTO `dish` VALUES (50, '馒头', 12, 1.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/475cc599-8661-4899-8f9e-121dd8ef7d02.png', '优质面粉', 1, '2022-06-10 09:34:28', '2022-06-10 09:34:28', 1, 1);
INSERT INTO `dish` VALUES (51, '老坛酸菜鱼', 20, 56.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/4a9cefba-6a74-467e-9fde-6e687ea725d7.png', '原料：汤，草鱼，酸菜', 1, '2022-06-10 09:40:51', '2022-06-10 09:40:51', 1, 1);
INSERT INTO `dish` VALUES (52, '经典酸菜鮰鱼', 20, 66.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/5260ff39-986c-4a97-8850-2ec8c7583efc.png', '原料：酸菜，江团，鮰鱼', 1, '2022-06-10 09:46:02', '2022-06-10 09:46:02', 1, 1);
INSERT INTO `dish` VALUES (53, '蜀味水煮草鱼', 20, 38.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/a6953d5a-4c18-4b30-9319-4926ee77261f.png', '原料：草鱼，汤', 1, '2022-06-10 09:48:37', '2022-06-10 09:48:37', 1, 1);
INSERT INTO `dish` VALUES (54, '清炒小油菜', 19, 18.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/3613d38e-5614-41c2-90ed-ff175bf50716.png', '原料：小油菜', 1, '2022-06-10 09:51:46', '2022-06-10 09:51:46', 1, 1);
INSERT INTO `dish` VALUES (55, '蒜蓉娃娃菜', 19, 18.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/4879ed66-3860-4b28-ba14-306ac025fdec.png', '原料：蒜，娃娃菜', 1, '2022-06-10 09:53:37', '2022-06-10 09:53:37', 1, 1);
INSERT INTO `dish` VALUES (56, '清炒西兰花', 19, 18.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/e9ec4ba4-4b22-4fc8-9be0-4946e6aeb937.png', '原料：西兰花', 1, '2022-06-10 09:55:44', '2022-06-10 09:55:44', 1, 1);
INSERT INTO `dish` VALUES (57, '炝炒圆白菜', 19, 18.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/22f59feb-0d44-430e-a6cd-6a49f27453ca.png', '原料：圆白菜', 1, '2022-06-10 09:58:35', '2022-06-10 09:58:35', 1, 1);
INSERT INTO `dish` VALUES (58, '清蒸鲈鱼', 18, 98.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/c18b5c67-3b71-466c-a75a-e63c6449f21c.png', '原料：鲈鱼', 1, '2022-06-10 10:12:28', '2022-06-10 10:12:28', 1, 1);
INSERT INTO `dish` VALUES (59, '东坡肘子', 18, 138.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/a80a4b8c-c93e-4f43-ac8a-856b0d5cc451.png', '原料：猪肘棒', 1, '2022-06-10 10:24:03', '2022-06-10 10:24:03', 1, 1);
INSERT INTO `dish` VALUES (60, '梅菜扣肉', 18, 58.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/6080b118-e30a-4577-aab4-45042e3f88be.png', '原料：猪肉，梅菜', 1, '2022-06-10 10:26:03', '2022-06-10 10:26:03', 1, 1);
INSERT INTO `dish` VALUES (61, '剁椒鱼头', 18, 66.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/13da832f-ef2c-484d-8370-5934a1045a06.png', '原料：鲢鱼，剁椒', 1, '2022-06-10 10:28:54', '2022-06-10 10:28:54', 1, 1);
INSERT INTO `dish` VALUES (62, '金汤酸菜牛蛙', 17, 88.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/7694a5d8-7938-4e9d-8b9e-2075983a2e38.png', '原料：鲜活牛蛙，酸菜', 1, '2022-06-10 10:33:05', '2022-06-10 10:33:05', 1, 1);
INSERT INTO `dish` VALUES (63, '香锅牛蛙', 17, 88.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/f5ac8455-4793-450c-97ba-173795c34626.png', '配料：鲜活牛蛙，莲藕，青笋', 1, '2022-06-10 10:35:40', '2022-06-10 10:35:40', 1, 1);
INSERT INTO `dish` VALUES (64, '馋嘴牛蛙', 17, 88.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/7a55b845-1f2b-41fa-9486-76d187ee9ee1.png', '配料：鲜活牛蛙，丝瓜，黄豆芽', 1, '2022-06-10 10:37:52', '2022-06-10 10:37:52', 1, 1);
INSERT INTO `dish` VALUES (65, '草鱼2斤', 16, 68.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/b544d3ba-a1ae-4d20-a860-81cb5dec9e03.png', '原料：草鱼，黄豆芽，莲藕', 1, '2022-06-10 10:41:08', '2022-06-10 10:41:08', 1, 1);
INSERT INTO `dish` VALUES (66, '江团鱼2斤', 16, 119.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/a101a1e9-8f8b-47b2-afa4-1abd47ea0a87.png', '配料：江团鱼，黄豆芽，莲藕', 1, '2022-06-10 10:42:42', '2022-06-10 10:42:42', 1, 1);
INSERT INTO `dish` VALUES (67, '鮰鱼2斤', 16, 72.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/8cfcc576-4b66-4a09-ac68-ad5b273c2590.png', '原料：鮰鱼，黄豆芽，莲藕', 1, '2022-06-10 10:43:56', '2022-06-10 10:43:56', 1, 1);
INSERT INTO `dish` VALUES (68, '鸡蛋汤', 21, 4.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/c09a0ee8-9d19-428d-81b9-746221824113.png', '配料：鸡蛋，紫菜', 1, '2022-06-10 10:54:25', '2022-06-10 10:54:25', 1, 1);
INSERT INTO `dish` VALUES (69, '平菇豆腐汤', 21, 6.00, 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/16d0a3d6-2253-4cfc-9b49-bf7bd9eb2ad2.png', '配料：豆腐，平菇', 1, '2022-06-10 10:55:02', '2022-06-10 10:55:02', 1, 1);
INSERT INTO `dish` VALUES (70, '委屈委屈', 16, 1123.00, 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', '123', 1, '2025-08-13 22:40:22', '2025-08-13 22:42:42', 1, 1);
INSERT INTO `dish` VALUES (71, '七个v啊', 16, 12.00, 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/2f00dc6f-bdad-4cf0-9946-f062cc726875.png', '', 1, '2025-08-14 01:26:17', '2025-08-14 01:26:21', 1, 1);
INSERT INTO `dish` VALUES (72, '萨芬全国', 23, 12.00, 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/d47afa3a-c42c-41a6-ae35-95a9255e27fc.png', '', 1, '2025-08-14 01:27:53', '2025-08-14 01:29:12', 1, 1);

-- ----------------------------
-- Table structure for dish_flavor
-- ----------------------------
DROP TABLE IF EXISTS `dish_flavor`;
CREATE TABLE `dish_flavor`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dish_id` bigint(20) NOT NULL COMMENT '菜品',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '口味名称',
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '口味数据list',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 108 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '菜品口味关系表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of dish_flavor
-- ----------------------------
INSERT INTO `dish_flavor` VALUES (40, 10, '甜味', '[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]');
INSERT INTO `dish_flavor` VALUES (41, 7, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]');
INSERT INTO `dish_flavor` VALUES (42, 7, '温度', '[\"热饮\",\"常温\",\"去冰\",\"少冰\",\"多冰\"]');
INSERT INTO `dish_flavor` VALUES (45, 6, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]');
INSERT INTO `dish_flavor` VALUES (46, 6, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (47, 5, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (48, 5, '甜味', '[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]');
INSERT INTO `dish_flavor` VALUES (49, 2, '甜味', '[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]');
INSERT INTO `dish_flavor` VALUES (50, 4, '甜味', '[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]');
INSERT INTO `dish_flavor` VALUES (51, 3, '甜味', '[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]');
INSERT INTO `dish_flavor` VALUES (52, 3, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]');
INSERT INTO `dish_flavor` VALUES (86, 52, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]');
INSERT INTO `dish_flavor` VALUES (87, 52, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (88, 51, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]');
INSERT INTO `dish_flavor` VALUES (89, 51, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (92, 53, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]');
INSERT INTO `dish_flavor` VALUES (93, 53, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (94, 54, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\"]');
INSERT INTO `dish_flavor` VALUES (95, 56, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]');
INSERT INTO `dish_flavor` VALUES (96, 57, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]');
INSERT INTO `dish_flavor` VALUES (97, 60, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]');
INSERT INTO `dish_flavor` VALUES (101, 66, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (102, 67, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (103, 65, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (104, 70, '甜味', '[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]');
INSERT INTO `dish_flavor` VALUES (105, 71, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (106, 72, '温度', '[\"热饮\",\"常温\",\"去冰\",\"少冰\",\"多冰\"]');
INSERT INTO `dish_flavor` VALUES (107, 72, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '姓名',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密码',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '手机号',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '性别',
  `id_number` varchar(18) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '身份证号',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态 0:禁用，1:启用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '员工信息' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (1, '管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '13812312312', '1', '110101199001010047', 1, '2022-02-15 15:51:20', '2022-02-17 09:16:20', 10, 1);
INSERT INTO `employee` VALUES (2, '士大夫', 'sas', 'e10adc3949ba59abbe56e057f20f883e', '18026714983', '1', '445222200204300016', 1, '2025-08-13 22:43:12', '2025-08-13 22:43:12', 1, 1);

-- ----------------------------
-- Table structure for order_detail
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '名字',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '图片',
  `order_id` bigint(20) NOT NULL COMMENT '订单id',
  `dish_id` bigint(20) NULL DEFAULT NULL COMMENT '菜品id',
  `setmeal_id` bigint(20) NULL DEFAULT NULL COMMENT '套餐id',
  `dish_flavor` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '口味',
  `number` int(11) NOT NULL DEFAULT 1 COMMENT '数量',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '订单明细表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of order_detail
-- ----------------------------
INSERT INTO `order_detail` VALUES (1, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 1, 70, NULL, '半糖', 1, 1123.00);
INSERT INTO `order_detail` VALUES (2, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 1, 70, NULL, '无糖', 1, 1123.00);
INSERT INTO `order_detail` VALUES (3, '鮰鱼2斤', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/8cfcc576-4b66-4a09-ac68-ad5b273c2590.png', 2, 67, NULL, '不辣', 1, 72.00);
INSERT INTO `order_detail` VALUES (4, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 3, 70, NULL, '无糖', 1, 1123.00);
INSERT INTO `order_detail` VALUES (5, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 4, 70, NULL, '无糖', 1, 1123.00);
INSERT INTO `order_detail` VALUES (6, '江团鱼2斤', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/a101a1e9-8f8b-47b2-afa4-1abd47ea0a87.png', 5, 66, NULL, '中辣', 1, 119.00);
INSERT INTO `order_detail` VALUES (7, '江团鱼2斤', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/a101a1e9-8f8b-47b2-afa4-1abd47ea0a87.png', 6, 66, NULL, '中辣', 1, 119.00);
INSERT INTO `order_detail` VALUES (8, '草鱼2斤', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/b544d3ba-a1ae-4d20-a860-81cb5dec9e03.png', 7, 65, NULL, '不辣', 1, 68.00);
INSERT INTO `order_detail` VALUES (9, '馋嘴牛蛙', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/7a55b845-1f2b-41fa-9486-76d187ee9ee1.png', 8, 64, NULL, NULL, 1, 88.00);
INSERT INTO `order_detail` VALUES (10, '江团鱼2斤', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/a101a1e9-8f8b-47b2-afa4-1abd47ea0a87.png', 9, 66, NULL, '不辣', 1, 119.00);
INSERT INTO `order_detail` VALUES (11, '北冰洋', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/4451d4be-89a2-4939-9c69-3a87151cb979.png', 10, 47, NULL, NULL, 1, 4.00);
INSERT INTO `order_detail` VALUES (12, '雪花啤酒', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/bf8cbfc1-04d2-40e8-9826-061ee41ab87c.png', 10, 48, NULL, NULL, 1, 4.00);
INSERT INTO `order_detail` VALUES (13, '平菇豆腐汤', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/16d0a3d6-2253-4cfc-9b49-bf7bd9eb2ad2.png', 10, 69, NULL, NULL, 1, 6.00);
INSERT INTO `order_detail` VALUES (14, '蜀味水煮草鱼', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/a6953d5a-4c18-4b30-9319-4926ee77261f.png', 10, 53, NULL, '不要葱,不辣', 1, 38.00);
INSERT INTO `order_detail` VALUES (15, '萨芬全国', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/d47afa3a-c42c-41a6-ae35-95a9255e27fc.png', 11, 72, NULL, '热饮,不辣', 1, 12.00);
INSERT INTO `order_detail` VALUES (16, '粉色的四个', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/85d2cdb9-6e80-472f-8216-5334945fa224.png', 11, NULL, 2, NULL, 1, 1802.00);
INSERT INTO `order_detail` VALUES (17, '馋嘴牛蛙', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/7a55b845-1f2b-41fa-9486-76d187ee9ee1.png', 12, 64, NULL, NULL, 1, 88.00);
INSERT INTO `order_detail` VALUES (18, '粉色的四个', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/85d2cdb9-6e80-472f-8216-5334945fa224.png', 13, NULL, 2, NULL, 1, 1802.00);
INSERT INTO `order_detail` VALUES (19, '萨芬全国', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/d47afa3a-c42c-41a6-ae35-95a9255e27fc.png', 17, 72, NULL, '', 1, 9.60);
INSERT INTO `order_detail` VALUES (20, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 18, 70, NULL, '', 1, 800.00);
INSERT INTO `order_detail` VALUES (21, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 19, 46, NULL, '', 1, 4.80);
INSERT INTO `order_detail` VALUES (22, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 20, 70, NULL, '', 2, 800.00);
INSERT INTO `order_detail` VALUES (23, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 21, 46, NULL, '', 1, 4.80);
INSERT INTO `order_detail` VALUES (24, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 22, 46, NULL, '', 1, 4.70);
INSERT INTO `order_detail` VALUES (25, '北冰洋', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/4451d4be-89a2-4939-9c69-3a87151cb979.png', 23, 47, NULL, '', 5, 3.20);
INSERT INTO `order_detail` VALUES (26, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 24, 70, NULL, '', 2, 800.00);
INSERT INTO `order_detail` VALUES (27, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 25, 46, NULL, '', 2, 4.80);
INSERT INTO `order_detail` VALUES (28, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 26, 46, NULL, '', 13, 4.70);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `number` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '订单号',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款',
  `user_id` bigint(20) NOT NULL COMMENT '下单用户',
  `address_book_id` bigint(20) NOT NULL COMMENT '地址id',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `checkout_time` datetime NULL DEFAULT NULL COMMENT '结账时间',
  `pay_method` int(11) NOT NULL DEFAULT 1 COMMENT '支付方式 1微信,2支付宝',
  `pay_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '支付状态 0未支付 1已支付 2退款',
  `amount` decimal(10, 2) NOT NULL COMMENT '实收金额',
  `remark` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '备注',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '手机号',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '地址',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '用户名称',
  `consignee` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '收货人',
  `cancel_reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '订单取消原因',
  `rejection_reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '订单拒绝原因',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '订单取消时间',
  `estimated_delivery_time` datetime NULL DEFAULT NULL COMMENT '预计送达时间',
  `delivery_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '配送状态  1立即送出  0选择具体时间',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '送达时间',
  `pack_amount` int(11) NULL DEFAULT NULL COMMENT '打包费',
  `tableware_number` int(11) NULL DEFAULT NULL COMMENT '餐具数量',
  `tableware_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '餐具数量状态  1按餐量提供  0选择具体数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '订单表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, '1755102031104', 5, 4, 1, '2025-08-14 00:20:31', '2025-08-14 00:20:33', 1, 1, 2253.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-14 00:50:00', 0, '2025-08-14 00:39:59', 2, 0, 0);
INSERT INTO `orders` VALUES (2, '1755103092569', 6, 4, 1, '2025-08-14 00:38:12', '2025-08-14 00:38:14', 1, 1, 78.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, '菜品已销售完，暂时无法接单', '2025-08-14 00:39:12', '2025-08-14 01:08:00', 0, NULL, 1, 0, 0);
INSERT INTO `orders` VALUES (3, '1755104529201', 6, 4, 1, '2025-08-14 01:02:09', '2025-08-14 01:02:11', 1, 1, 1130.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, '菜品已销售完，暂时无法接单', '2025-08-14 01:02:34', '2025-08-14 01:32:00', 0, NULL, 1, 0, 0);
INSERT INTO `orders` VALUES (4, '1755104585495', 5, 4, 1, '2025-08-14 01:03:05', '2025-08-14 01:03:10', 1, 1, 1130.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-14 01:32:00', 0, '2025-08-14 01:10:05', 1, 2, 0);
INSERT INTO `orders` VALUES (5, '1755104836485', 6, 4, 1, '2025-08-14 01:07:16', '2025-08-14 01:07:22', 1, 2, 126.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', '用户取消', NULL, '2025-08-14 01:07:26', '2025-08-14 01:37:00', 0, NULL, 1, 2, 0);
INSERT INTO `orders` VALUES (6, '1755104858015', 6, 4, 1, '2025-08-14 01:07:38', '2025-08-14 01:07:39', 1, 2, 126.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', '用户取消', NULL, '2025-08-14 01:07:44', '2025-08-14 01:37:00', 0, NULL, 1, 0, 0);
INSERT INTO `orders` VALUES (7, '1755104976808', 6, 4, 1, '2025-08-14 01:09:36', '2025-08-14 01:09:38', 1, 2, 75.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', '用户取消', NULL, '2025-08-14 01:09:42', '2025-08-14 01:39:00', 0, NULL, 1, 0, 0);
INSERT INTO `orders` VALUES (8, '1755105211015', 6, 4, 1, '2025-08-14 01:13:31', NULL, 1, 0, 95.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', '订单超时，自动取消', NULL, '2025-08-14 01:29:00', '2025-08-14 01:43:00', 0, NULL, 1, 0, 0);
INSERT INTO `orders` VALUES (9, '1755105852376', 6, 4, 1, '2025-08-14 01:24:12', '2025-08-14 01:24:14', 1, 1, 126.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, '餐厅已打烊，暂时无法接单', '2025-08-14 01:24:45', '2025-08-14 01:54:00', 0, NULL, 1, 0, 0);
INSERT INTO `orders` VALUES (10, '1755105932769', 5, 4, 1, '2025-08-14 01:25:32', '2025-08-14 01:25:34', 1, 1, 62.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-14 01:55:00', 0, '2025-08-14 01:26:53', 4, 0, 0);
INSERT INTO `orders` VALUES (11, '1755106183141', 6, 4, 1, '2025-08-14 01:29:43', '2025-08-14 01:29:44', 1, 1, 1822.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', '菜品已销售完，暂时无法接单', NULL, '2025-08-14 02:24:42', '2025-08-14 01:59:00', 0, '2025-08-14 01:30:40', 2, 0, 0);
INSERT INTO `orders` VALUES (12, '1755109456528', 5, 4, 1, '2025-08-14 02:24:16', '2025-08-14 02:24:18', 1, 1, 95.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-14 02:54:00', 0, '2025-08-14 02:24:34', 1, 0, 0);
INSERT INTO `orders` VALUES (13, '1755282303077', 5, 4, 1, '2025-08-16 02:25:03', '2025-08-16 02:25:05', 1, 1, 1809.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-16 02:54:00', 0, '2025-08-16 13:24:41', 1, 0, 0);
INSERT INTO `orders` VALUES (14, 'SK1755492898691', 5, 4, 1, '2025-08-18 12:54:58', '2025-08-18 12:54:58', 1, 1, 3.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-18 13:54:58', 1, NULL, 0, 1, 1);
INSERT INTO `orders` VALUES (15, 'SK1755494187654', 5, 4, 1, '2025-08-18 13:16:27', '2025-08-18 13:16:27', 1, 1, 3.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-18 14:16:27', 1, NULL, 0, 1, 1);
INSERT INTO `orders` VALUES (16, 'SK1755495651058', 5, 4, 1, '2025-08-18 13:40:51', '2025-08-18 13:40:51', 1, 1, 9.60, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-18 14:40:51', 1, '2025-08-18 14:08:18', 0, 1, 1);
INSERT INTO `orders` VALUES (17, 'SK1755496159340', 5, 4, 1, '2025-08-18 13:49:19', '2025-08-18 13:49:19', 1, 1, 9.60, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-18 14:49:19', 1, '2025-08-18 14:08:17', 0, 1, 1);
INSERT INTO `orders` VALUES (18, 'SK1755496506826', 5, 4, 1, '2025-08-18 13:55:06', '2025-08-18 13:55:06', 1, 1, 800.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-18 14:55:06', 1, '2025-08-18 14:08:16', 0, 1, 1);
INSERT INTO `orders` VALUES (19, 'SK1755497096460', 5, 4, 1, '2025-08-18 14:04:56', '2025-08-18 14:04:56', 1, 1, 4.80, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-18 15:04:56', 1, '2025-08-18 14:08:15', 0, 1, 1);
INSERT INTO `orders` VALUES (20, 'SK1755499344839', 5, 4, 1, '2025-08-18 14:42:24', '2025-08-18 14:42:24', 1, 1, 1600.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-18 15:42:24', 1, NULL, 0, 1, 1);
INSERT INTO `orders` VALUES (21, 'SK1755539506731', 5, 4, 1, '2025-08-19 01:51:46', '2025-08-19 01:51:46', 1, 1, 4.80, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-19 02:51:46', 1, NULL, 0, 1, 1);
INSERT INTO `orders` VALUES (22, 'SK1755540298539', 5, 4, 1, '2025-08-19 02:04:58', '2025-08-19 02:04:58', 1, 1, 4.70, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-19 03:04:58', 1, NULL, 0, 1, 1);
INSERT INTO `orders` VALUES (23, 'SK1755540625803', 5, 4, 1, '2025-08-19 02:10:25', '2025-08-19 02:10:25', 1, 1, 16.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-19 03:10:25', 1, NULL, 0, 1, 1);
INSERT INTO `orders` VALUES (24, 'SK1755541136325', 5, 4, 1, '2025-08-19 02:18:56', '2025-08-19 02:18:56', 1, 1, 1600.00, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-19 03:18:56', 1, NULL, 0, 1, 1);
INSERT INTO `orders` VALUES (25, 'SK1755541303753', 5, 4, 1, '2025-08-19 02:21:43', '2025-08-19 02:21:44', 1, 1, 9.60, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-19 03:21:43', 1, '2025-08-19 02:23:42', 0, 1, 1);
INSERT INTO `orders` VALUES (26, 'SK1755541922190', 5, 4, 1, '2025-08-19 02:32:02', '2025-08-19 02:32:02', 1, 1, 61.10, '', '18026714983', '八栋7b503房间', NULL, '徐徐', NULL, NULL, NULL, '2025-08-19 03:32:02', 1, '2025-08-19 02:32:52', 0, 1, 1);

-- ----------------------------
-- Table structure for seckill_activity
-- ----------------------------
DROP TABLE IF EXISTS `seckill_activity`;
CREATE TABLE `seckill_activity`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '����',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '��ɱ�����',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '�ͼƬ',
  `start_time` datetime NOT NULL COMMENT '��ɱ��ʼʱ��',
  `end_time` datetime NOT NULL COMMENT '��ɱ����ʱ��',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '�״̬ 0:δ��ʼ 1:������ 2:�ѽ��� 3:��ȡ��',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '�����',
  `redis_preload_time` datetime NULL DEFAULT NULL COMMENT 'RedisԤ����ʱ��',
  `redis_sync_status` int(11) NOT NULL DEFAULT 0 COMMENT 'Redisͬ��״̬ 0:δͬ�� 1:��ͬ�� 2:ͬ��ʧ��',
  `hot_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'normal' COMMENT '�ȶȵȼ� low:�� normal:��ͨ high:��',
  `participant_count` int(11) NOT NULL DEFAULT 0 COMMENT '参与用户数',
  `total_sales` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '总销售额',
  `current_qps` int(11) NOT NULL DEFAULT 0 COMMENT '当前QPS',
  `peak_qps` int(11) NOT NULL DEFAULT 0 COMMENT '峰值QPS',
  `avg_response_time` int(11) NOT NULL DEFAULT 0 COMMENT '平均响应时间(毫秒)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '����ʱ��',
  `update_time` datetime NULL DEFAULT NULL COMMENT '����ʱ��',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '������',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '�޸���',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_start_time`(`start_time`) USING BTREE,
  INDEX `idx_end_time`(`end_time`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_redis_sync_status`(`redis_sync_status`) USING BTREE,
  INDEX `idx_hot_level`(`hot_level`) USING BTREE,
  INDEX `idx_participant_count`(`participant_count`) USING BTREE,
  INDEX `idx_current_qps`(`current_qps`) USING BTREE,
  INDEX `idx_peak_qps`(`peak_qps`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '��ɱ���' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of seckill_activity
-- ----------------------------
INSERT INTO `seckill_activity` VALUES (2, '撒法发', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/ff5222bf-252c-4278-bd0f-df7dcba88dbc.png', '2025-08-17 00:00:00', '2025-08-21 00:00:00', 1, '啊伺服驱动器', NULL, 0, 'normal', 0, 0.00, 0, 0, 0, '2025-08-17 15:35:53', '2025-08-17 15:50:52', 1, 1);
INSERT INTO `seckill_activity` VALUES (3, '完全缺乏', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/8acc31f2-168f-4974-a9e6-01a80150b0cb.png', '2025-08-17 00:00:00', '2025-08-20 00:00:00', 1, '', NULL, 0, 'normal', 0, 0.00, 0, 0, 0, '2025-08-17 15:44:10', '2025-08-17 15:44:27', 1, 1);
INSERT INTO `seckill_activity` VALUES (4, '大师傅v阿哥', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/c90de013-5e6b-4463-8295-2aefacfd20cc.png', '2025-08-17 00:00:00', '2025-08-19 00:00:00', 1, '按时发送给全国曲棍球', NULL, 0, 'normal', 0, 0.00, 0, 0, 0, '2025-08-17 15:51:35', '2025-08-17 15:51:39', 1, 1);

-- ----------------------------
-- Table structure for seckill_goods
-- ----------------------------
DROP TABLE IF EXISTS `seckill_goods`;
CREATE TABLE `seckill_goods`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '����',
  `activity_id` bigint(20) NOT NULL COMMENT '��ɱ�ID',
  `goods_type` int(11) NOT NULL DEFAULT 1 COMMENT '��Ʒ���� 1:��Ʒ 2:�ײ�',
  `goods_id` bigint(20) NOT NULL COMMENT '��ƷID����ƷID���ײ�ID��',
  `goods_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '��Ʒ����',
  `goods_image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '��ƷͼƬ',
  `original_price` decimal(10, 2) NOT NULL COMMENT 'ԭ��',
  `seckill_price` decimal(10, 2) NOT NULL COMMENT '��ɱ��',
  `total_stock` int(11) NOT NULL DEFAULT 0 COMMENT '�ܿ��',
  `available_stock` int(11) NOT NULL DEFAULT 0 COMMENT '���ÿ��',
  `sold_count` int(11) NOT NULL DEFAULT 0 COMMENT '��������',
  `limit_count` int(11) NOT NULL DEFAULT 1 COMMENT '�޹�����',
  `version` int(11) NOT NULL DEFAULT 0 COMMENT '�汾�ţ������ֹ�����',
  `redis_stock_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Redis����',
  `stock_warning_threshold` int(11) NOT NULL DEFAULT 10 COMMENT '���Ԥ����ֵ',
  `hot_ranking` int(11) NOT NULL DEFAULT 0 COMMENT '��������',
  `last_purchase_time` datetime NULL DEFAULT NULL COMMENT '最后购买时间',
  `purchase_velocity` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '购买速度(件/分钟)',
  `stock_sync_time` datetime NULL DEFAULT NULL COMMENT '库存同步时间',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '״̬ 0:�¼� 1:�ϼ�',
  `create_time` datetime NULL DEFAULT NULL COMMENT '����ʱ��',
  `update_time` datetime NULL DEFAULT NULL COMMENT '����ʱ��',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '������',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '�޸���',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_activity_goods`(`activity_id`, `goods_type`, `goods_id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id`) USING BTREE,
  INDEX `idx_goods_type_id`(`goods_type`, `goods_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_stock_warning`(`stock_warning_threshold`) USING BTREE,
  INDEX `idx_hot_ranking`(`hot_ranking`) USING BTREE,
  INDEX `idx_last_purchase_time`(`last_purchase_time`) USING BTREE,
  INDEX `idx_purchase_velocity`(`purchase_velocity`) USING BTREE,
  INDEX `idx_stock_sync_time`(`stock_sync_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '��ɱ��Ʒ��' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of seckill_goods
-- ----------------------------
INSERT INTO `seckill_goods` VALUES (3, 2, 1, 46, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 6.00, 4.70, 100, 86, 14, 84, 2, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:35:53', '2025-08-19 02:32:02', 1, 1);
INSERT INTO `seckill_goods` VALUES (4, 2, 1, 70, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 1123.00, 800.00, 100, 98, 2, 84, 1, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:35:53', '2025-08-19 02:18:56', 1, 1);
INSERT INTO `seckill_goods` VALUES (5, 3, 1, 46, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 6.00, 4.80, 100, 97, 3, 20, 2, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:44:10', '2025-08-19 02:21:43', 1, 1);
INSERT INTO `seckill_goods` VALUES (6, 3, 1, 47, '北冰洋', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/4451d4be-89a2-4939-9c69-3a87151cb979.png', 4.00, 3.20, 100, 95, 5, 20, 1, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:44:10', '2025-08-19 02:10:25', 1, 1);
INSERT INTO `seckill_goods` VALUES (7, 3, 1, 48, '雪花啤酒', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/bf8cbfc1-04d2-40e8-9826-061ee41ab87c.png', 4.00, 3.20, 100, 100, 0, 20, 0, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:44:10', '2025-08-17 15:44:10', 1, 1);
INSERT INTO `seckill_goods` VALUES (8, 3, 1, 70, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 1123.00, 898.40, 100, 100, 0, 20, 0, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:44:10', '2025-08-17 15:44:10', 1, 1);
INSERT INTO `seckill_goods` VALUES (9, 3, 1, 71, '七个v啊', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/2f00dc6f-bdad-4cf0-9946-f062cc726875.png', 12.00, 9.60, 100, 100, 0, 20, 0, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:44:10', '2025-08-17 15:44:10', 1, 1);
INSERT INTO `seckill_goods` VALUES (10, 4, 1, 46, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 6.00, 4.80, 100, 100, 0, 20, 0, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:51:35', '2025-08-17 15:51:35', 1, 1);
INSERT INTO `seckill_goods` VALUES (11, 4, 1, 50, '馒头', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/475cc599-8661-4899-8f9e-121dd8ef7d02.png', 1.00, 0.80, 100, 100, 0, 20, 0, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:51:35', '2025-08-17 15:51:35', 1, 1);
INSERT INTO `seckill_goods` VALUES (12, 4, 1, 49, '米饭', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/76752350-2121-44d2-b477-10791c23a8ec.png', 2.00, 1.60, 100, 100, 0, 20, 0, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:51:35', '2025-08-17 15:51:35', 1, 1);
INSERT INTO `seckill_goods` VALUES (13, 4, 1, 60, '梅菜扣肉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/6080b118-e30a-4577-aab4-45042e3f88be.png', 58.00, 46.40, 100, 100, 0, 20, 0, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:51:35', '2025-08-17 15:51:35', 1, 1);
INSERT INTO `seckill_goods` VALUES (14, 4, 1, 61, '剁椒鱼头', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/13da832f-ef2c-484d-8370-5934a1045a06.png', 66.00, 52.80, 100, 100, 0, 20, 0, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:51:35', '2025-08-17 15:51:35', 1, 1);
INSERT INTO `seckill_goods` VALUES (15, 4, 1, 72, '萨芬全国', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/d47afa3a-c42c-41a6-ae35-95a9255e27fc.png', 12.00, 9.60, 100, 100, 0, 20, 0, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:51:35', '2025-08-17 15:51:35', 1, 1);
INSERT INTO `seckill_goods` VALUES (16, 4, 1, 70, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 1123.00, 898.40, 100, 100, 0, 20, 0, NULL, 10, 0, NULL, 0.00, NULL, 1, '2025-08-17 15:51:35', '2025-08-17 15:51:35', 1, 1);

-- ----------------------------
-- Table structure for seckill_lua_script
-- ----------------------------
DROP TABLE IF EXISTS `seckill_lua_script`;
CREATE TABLE `seckill_lua_script`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `script_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '脚本名称',
  `script_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '脚本内容',
  `script_sha1` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '脚本SHA1值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '脚本描述',
  `version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1.0' COMMENT '脚本版本',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
  `load_time` datetime NULL DEFAULT NULL COMMENT '加载时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_script_name`(`script_name`) USING BTREE,
  UNIQUE INDEX `idx_script_sha1`(`script_sha1`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Lua脚本管理表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of seckill_lua_script
-- ----------------------------
INSERT INTO `seckill_lua_script` VALUES (1, 'stock_deduct', '-- 库存扣减 + 防超卖 + 一人一单\nlocal stock = redis.call(\'GET\', KEYS[1])\nlocal userPurchased = redis.call(\'GET\', KEYS[2]) or \"0\"\nlocal quantity = tonumber(ARGV[1])\nlocal limitCount = tonumber(ARGV[2])\nlocal userId = ARGV[3]\nlocal expireTime = tonumber(ARGV[4])\n\nif not stock or tonumber(stock) < quantity then\n    return {code = 50006, msg = \"库存不足\"}\nend\n\nif tonumber(userPurchased) + quantity > limitCount then\n    return {code = 50008, msg = \"用户已达购买上限\"}\nend\n\nredis.call(\'DECRBY\', KEYS[1], quantity)\nredis.call(\'INCRBY\', KEYS[2], quantity)\nredis.call(\'EXPIRE\', KEYS[2], expireTime)\nredis.call(\'INCRBY\', KEYS[3], quantity)\n\nreturn {code = 1, msg = \"success\", data = {remainingStock = tonumber(stock) - quantity}}', 'abc123def456789', '库存扣减脚本，支持防超卖和一人一单限制', '1.0', 1, NULL, '2025-08-19 23:07:40', '2025-08-19 23:07:40', 1, 1);
INSERT INTO `seckill_lua_script` VALUES (2, 'activity_check', '-- 检查活动状态和时间\nlocal status = redis.call(\'GET\', KEYS[1])\nlocal timeInfo = redis.call(\'HMGET\', KEYS[2], \'startTime\', \'endTime\')\nlocal currentTime = tonumber(ARGV[1])\n\nif not status or status ~= \"1\" then\n    return {code = 50002, msg = \"秒杀活动未开始或已结束\"}\nend\n\nlocal startTime = tonumber(timeInfo[1])\nlocal endTime = tonumber(timeInfo[2])\n\nif currentTime < startTime then\n    return {code = 50002, msg = \"秒杀未开始\"}\nend\n\nif currentTime > endTime then\n    return {code = 50003, msg = \"秒杀已结束\"}\nend\n\nreturn {code = 1, msg = \"success\"}', 'def456ghi789012', '活动状态和时间检查脚本', '1.0', 1, NULL, '2025-08-19 23:07:40', '2025-08-19 23:07:40', 1, 1);
INSERT INTO `seckill_lua_script` VALUES (3, 'user_eligibility', '-- 检查用户购买资格\nlocal userPurchased = tonumber(redis.call(\'GET\', KEYS[1]) or \"0\")\nlocal stock = tonumber(redis.call(\'GET\', KEYS[2]) or \"0\")\nlocal limitCount = tonumber(ARGV[1])\nlocal quantity = tonumber(ARGV[2])\n\nreturn {\n    code = 1,\n    msg = \"success\",\n    data = {\n        canPurchase = (userPurchased + quantity <= limitCount and stock >= quantity),\n        remainingQuota = math.max(0, limitCount - userPurchased),\n        limitCount = limitCount,\n        userPurchased = userPurchased,\n        availableStock = stock\n    }\n}', 'ghi789jkl012345', '用户购买资格检查脚本', '1.0', 1, NULL, '2025-08-19 23:07:40', '2025-08-19 23:07:40', 1, 1);

-- ----------------------------
-- Table structure for seckill_lua_statistics
-- ----------------------------
DROP TABLE IF EXISTS `seckill_lua_statistics`;
CREATE TABLE `seckill_lua_statistics`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `script_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '脚本名称',
  `execution_date` date NOT NULL COMMENT '执行日期',
  `total_executions` int(11) NOT NULL DEFAULT 0 COMMENT '总执行次数',
  `success_executions` int(11) NOT NULL DEFAULT 0 COMMENT '成功执行次数',
  `failed_executions` int(11) NOT NULL DEFAULT 0 COMMENT '失败执行次数',
  `avg_execution_time` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '平均执行时间(毫秒)',
  `max_execution_time` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '最大执行时间(毫秒)',
  `min_execution_time` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '最小执行时间(毫秒)',
  `peak_qps` int(11) NOT NULL DEFAULT 0 COMMENT '峰值QPS',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_script_date`(`script_name`, `execution_date`) USING BTREE,
  INDEX `idx_execution_date`(`execution_date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Lua脚本执行统计表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for seckill_lua_version
-- ----------------------------
DROP TABLE IF EXISTS `seckill_lua_version`;
CREATE TABLE `seckill_lua_version`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `script_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '脚本名称',
  `version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '版本号',
  `script_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '脚本内容',
  `script_sha1` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '脚本SHA1值',
  `change_log` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '变更日志',
  `is_active` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为当前版本 0:否 1:是',
  `deploy_time` datetime NULL DEFAULT NULL COMMENT '部署时间',
  `rollback_version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回滚版本',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_script_version`(`script_name`, `version`) USING BTREE,
  UNIQUE INDEX `idx_script_sha1`(`script_sha1`) USING BTREE,
  INDEX `idx_script_name`(`script_name`) USING BTREE,
  INDEX `idx_is_active`(`is_active`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Lua脚本版本管理表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of seckill_lua_version
-- ----------------------------
INSERT INTO `seckill_lua_version` VALUES (1, 'stock_deduct', '1.0', '-- 库存扣减 + 防超卖 + 一人一单\nlocal stock = redis.call(\'GET\', KEYS[1])\nlocal userPurchased = redis.call(\'GET\', KEYS[2]) or \"0\"\nlocal quantity = tonumber(ARGV[1])\nlocal limitCount = tonumber(ARGV[2])\nlocal userId = ARGV[3]\nlocal expireTime = tonumber(ARGV[4])\n\nif not stock or tonumber(stock) < quantity then\n    return {code = 50006, msg = \"库存不足\"}\nend\n\nif tonumber(userPurchased) + quantity > limitCount then\n    return {code = 50008, msg = \"用户已达购买上限\"}\nend\n\nredis.call(\'DECRBY\', KEYS[1], quantity)\nredis.call(\'INCRBY\', KEYS[2], quantity)\nredis.call(\'EXPIRE\', KEYS[2], expireTime)\nredis.call(\'INCRBY\', KEYS[3], quantity)\n\nreturn {code = 1, msg = \"success\", data = {remainingStock = tonumber(stock) - quantity}}', 'abc123def456789', '初始版本，实现库存扣减、防超卖和一人一单功能', 1, '2025-08-19 23:07:40', NULL, '2025-08-19 23:07:40', 1);
INSERT INTO `seckill_lua_version` VALUES (2, 'activity_check', '1.0', '-- 检查活动状态和时间\nlocal status = redis.call(\'GET\', KEYS[1])\nlocal timeInfo = redis.call(\'HMGET\', KEYS[2], \'startTime\', \'endTime\')\nlocal currentTime = tonumber(ARGV[1])\n\nif not status or status ~= \"1\" then\n    return {code = 50002, msg = \"秒杀活动未开始或已结束\"}\nend\n\nlocal startTime = tonumber(timeInfo[1])\nlocal endTime = tonumber(timeInfo[2])\n\nif currentTime < startTime then\n    return {code = 50002, msg = \"秒杀未开始\"}\nend\n\nif currentTime > endTime then\n    return {code = 50003, msg = \"秒杀已结束\"}\nend\n\nreturn {code = 1, msg = \"success\"}', 'def456ghi789012', '初始版本，实现活动状态和时间检查', 1, '2025-08-19 23:07:40', NULL, '2025-08-19 23:07:40', 1);
INSERT INTO `seckill_lua_version` VALUES (3, 'user_eligibility', '1.0', '-- 检查用户购买资格\nlocal userPurchased = tonumber(redis.call(\'GET\', KEYS[1]) or \"0\")\nlocal stock = tonumber(redis.call(\'GET\', KEYS[2]) or \"0\")\nlocal limitCount = tonumber(ARGV[1])\nlocal quantity = tonumber(ARGV[2])\n\nreturn {\n    code = 1,\n    msg = \"success\",\n    data = {\n        canPurchase = (userPurchased + quantity <= limitCount and stock >= quantity),\n        remainingQuota = math.max(0, limitCount - userPurchased),\n        limitCount = limitCount,\n        userPurchased = userPurchased,\n        availableStock = stock\n    }\n}', 'ghi789jkl012345', '初始版本，实现用户购买资格检查', 1, '2025-08-19 23:07:40', NULL, '2025-08-19 23:07:40', 1);

-- ----------------------------
-- Table structure for seckill_monitor_data
-- ----------------------------
DROP TABLE IF EXISTS `seckill_monitor_data`;
CREATE TABLE `seckill_monitor_data`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `monitor_time` datetime NOT NULL COMMENT '监控时间',
  `total_orders` int(11) NOT NULL DEFAULT 0 COMMENT '总订单数',
  `success_orders` int(11) NOT NULL DEFAULT 0 COMMENT '成功订单数',
  `cancelled_orders` int(11) NOT NULL DEFAULT 0 COMMENT '取消订单数',
  `total_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
  `success_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '成功率',
  `current_qps` int(11) NOT NULL DEFAULT 0 COMMENT '当前QPS',
  `avg_response_time` int(11) NOT NULL DEFAULT 0 COMMENT '平均响应时间(毫秒)',
  `total_stock` int(11) NOT NULL DEFAULT 0 COMMENT '总库存',
  `sold_count` int(11) NOT NULL DEFAULT 0 COMMENT '已售数量',
  `remaining_stock` int(11) NOT NULL DEFAULT 0 COMMENT '剩余库存',
  `participant_count` int(11) NOT NULL DEFAULT 0 COMMENT '参与用户数',
  `repeat_purchase_attempts` int(11) NOT NULL DEFAULT 0 COMMENT '重复购买尝试次数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_monitor`(`activity_id`, `monitor_time`) USING BTREE,
  INDEX `idx_monitor_time`(`monitor_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀实时监控数据表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for seckill_order
-- ----------------------------
DROP TABLE IF EXISTS `seckill_order`;
CREATE TABLE `seckill_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的订单ID',
  `number` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '订单号',
  `activity_id` bigint(20) NOT NULL COMMENT '秒杀活动ID',
  `seckill_goods_id` bigint(20) NOT NULL COMMENT '秒杀商品ID',
  `goods_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '商品名称',
  `goods_image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '商品图片',
  `goods_type` int(11) NOT NULL DEFAULT 1 COMMENT '商品类型 1菜品 2套餐',
  `original_price` decimal(10, 2) NOT NULL COMMENT '商品原价',
  `seckill_price` decimal(10, 2) NOT NULL COMMENT '秒杀价格',
  `quantity` int(11) NOT NULL DEFAULT 1 COMMENT '购买数量',
  `amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消',
  `pay_status` int(11) NOT NULL DEFAULT 0 COMMENT '支付状态 0未支付 1已支付 2退款',
  `pay_method` int(11) NULL DEFAULT NULL COMMENT '支付方式 1微信 2支付宝',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `checkout_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `pay_expire_time` datetime NOT NULL COMMENT '支付超时时间',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '备注',
  `address_book_id` bigint(20) NOT NULL COMMENT '收货地址ID',
  `consignee` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '收货人',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '手机号',
  `address` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '收货地址',
  `lua_script_hash` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Lua脚本SHA1哈希',
  `script_execution_time` int(11) NOT NULL DEFAULT 0 COMMENT '脚本执行时间(毫秒)',
  `stock_locked` tinyint(1) NOT NULL DEFAULT 0 COMMENT '库存是否锁定 0:否 1:是',
  `pre_check_token` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预检查令牌',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `update_user` bigint(20) NOT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_order_number`(`number`) USING BTREE COMMENT '订单号唯一索引',
  INDEX `idx_user_id`(`user_id`) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_activity_id`(`activity_id`) USING BTREE COMMENT '活动ID索引',
  INDEX `idx_seckill_goods_id`(`seckill_goods_id`) USING BTREE COMMENT '秒杀商品ID索引',
  INDEX `idx_pay_expire_time`(`pay_expire_time`) USING BTREE COMMENT '支付超时时间索引',
  INDEX `idx_order_time`(`order_time`) USING BTREE COMMENT '下单时间索引',
  INDEX `idx_lua_script_hash`(`lua_script_hash`) USING BTREE,
  INDEX `idx_script_execution_time`(`script_execution_time`) USING BTREE,
  INDEX `idx_stock_locked`(`stock_locked`) USING BTREE,
  INDEX `idx_pre_check_token`(`pre_check_token`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '秒杀订单' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of seckill_order
-- ----------------------------
INSERT INTO `seckill_order` VALUES (1, 14, 'SK1755492898691', 1, 11, '���ϼ�', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 1, 6.00, 3.00, 1, 3.00, 4, 2, 1, 1, '2025-08-18 12:54:58', '2025-08-18 12:54:58', '2025-08-18 13:09:58', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-18 12:54:58', '2025-08-18 12:54:58', 4, 4);
INSERT INTO `seckill_order` VALUES (2, 15, 'SK1755494187654', 1, 6, '���ϼ�', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 1, 6.00, 3.00, 1, 3.00, 4, 2, 1, 1, '2025-08-18 13:16:27', '2025-08-18 13:16:27', '2025-08-18 13:31:27', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-18 13:16:27', '2025-08-18 13:16:27', 4, 4);
INSERT INTO `seckill_order` VALUES (3, 16, 'SK1755495651058', 3, 9, '七个v啊', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/2f00dc6f-bdad-4cf0-9946-f062cc726875.png', 1, 12.00, 9.60, 1, 9.60, 4, 2, 1, 1, '2025-08-18 13:40:51', '2025-08-18 13:40:51', '2025-08-18 13:55:51', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-18 13:40:51', '2025-08-18 13:40:51', 4, 4);
INSERT INTO `seckill_order` VALUES (4, 17, 'SK1755496159340', 4, 15, '萨芬全国', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/d47afa3a-c42c-41a6-ae35-95a9255e27fc.png', 1, 12.00, 9.60, 1, 9.60, 4, 2, 1, 1, '2025-08-18 13:49:19', '2025-08-18 13:49:19', '2025-08-18 14:04:19', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-18 13:49:19', '2025-08-18 13:49:19', 4, 4);
INSERT INTO `seckill_order` VALUES (5, 18, 'SK1755496506826', 2, 4, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 1, 1123.00, 800.00, 1, 800.00, 4, 2, 1, 1, '2025-08-18 13:55:06', '2025-08-18 13:55:06', '2025-08-18 14:10:06', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-18 13:55:06', '2025-08-18 13:55:06', 4, 4);
INSERT INTO `seckill_order` VALUES (6, 19, 'SK1755497096460', 4, 10, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 1, 6.00, 4.80, 1, 4.80, 4, 2, 1, 1, '2025-08-18 14:04:56', '2025-08-18 14:04:56', '2025-08-18 14:19:56', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-18 14:04:56', '2025-08-18 14:04:56', 4, 4);
INSERT INTO `seckill_order` VALUES (7, 20, 'SK1755499344839', 2, 4, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 1, 1123.00, 800.00, 2, 1600.00, 4, 5, 1, 1, '2025-08-18 14:42:24', '2025-08-18 14:42:24', '2025-08-18 14:57:24', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-18 14:42:24', '2025-08-18 14:42:24', 4, 4);
INSERT INTO `seckill_order` VALUES (8, 21, 'SK1755539506731', 3, 5, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 1, 6.00, 4.80, 1, 4.80, 4, 5, 1, 1, '2025-08-19 01:51:46', '2025-08-19 01:51:46', '2025-08-19 02:06:46', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-19 01:51:46', '2025-08-19 01:51:46', 4, 4);
INSERT INTO `seckill_order` VALUES (9, 22, 'SK1755540298539', 2, 3, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 1, 6.00, 4.70, 1, 4.70, 4, 5, 1, 1, '2025-08-19 02:04:58', '2025-08-19 02:04:58', '2025-08-19 02:19:58', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-19 02:04:58', '2025-08-19 02:04:58', 4, 4);
INSERT INTO `seckill_order` VALUES (10, 23, 'SK1755540625803', 3, 6, '北冰洋', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/4451d4be-89a2-4939-9c69-3a87151cb979.png', 1, 4.00, 3.20, 5, 16.00, 4, 5, 1, 1, '2025-08-19 02:10:25', '2025-08-19 02:10:25', '2025-08-19 02:25:25', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-19 02:10:25', '2025-08-19 02:10:25', 4, 4);
INSERT INTO `seckill_order` VALUES (11, 24, 'SK1755541136325', 2, 4, '委屈委屈', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/037c0e09-a00b-4aa8-a44d-39d4e28a30be.png', 1, 1123.00, 800.00, 2, 1600.00, 4, 5, 1, 1, '2025-08-19 02:18:56', '2025-08-19 02:18:56', '2025-08-19 02:33:56', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-19 02:18:56', '2025-08-19 02:18:56', 4, 4);
INSERT INTO `seckill_order` VALUES (12, 25, 'SK1755541303753', 3, 5, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 1, 6.00, 4.80, 2, 9.60, 4, 2, 1, 1, '2025-08-19 02:21:43', '2025-08-19 02:21:43', '2025-08-19 02:36:43', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-19 02:21:43', '2025-08-19 02:21:43', 4, 4);
INSERT INTO `seckill_order` VALUES (13, 26, 'SK1755541922190', 2, 3, '王老吉', 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', 1, 6.00, 4.70, 13, 61.10, 4, 5, 1, 1, '2025-08-19 02:32:02', '2025-08-19 02:32:02', '2025-08-19 02:47:02', '', 1, '徐徐', '18026714983', '八栋7b503房间', NULL, 0, 0, NULL, '2025-08-19 02:32:02', '2025-08-19 02:32:52', 4, 1);

-- ----------------------------
-- Table structure for seckill_performance_monitor
-- ----------------------------
DROP TABLE IF EXISTS `seckill_performance_monitor`;
CREATE TABLE `seckill_performance_monitor`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `monitor_time` datetime NOT NULL COMMENT '监控时间',
  `redis_memory_usage` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Redis内存使用量',
  `redis_connection_count` int(11) NOT NULL DEFAULT 0 COMMENT 'Redis连接数',
  `redis_script_cache_hits` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT 'Redis脚本缓存命中率',
  `redis_avg_latency` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT 'Redis平均延迟(毫秒)',
  `lua_total_executions` int(11) NOT NULL DEFAULT 0 COMMENT 'Lua脚本总执行次数',
  `lua_avg_execution_time` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT 'Lua脚本平均执行时间(毫秒)',
  `lua_error_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT 'Lua脚本错误率',
  `business_stock_accuracy` decimal(5, 4) NOT NULL DEFAULT 1.0000 COMMENT '库存准确率',
  `business_oversell_count` int(11) NOT NULL DEFAULT 0 COMMENT '超卖次数',
  `business_user_limit_violations` int(11) NOT NULL DEFAULT 0 COMMENT '用户限购违规次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_monitor_time`(`monitor_time`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统性能监控表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for seckill_pre_check_token
-- ----------------------------
DROP TABLE IF EXISTS `seckill_pre_check_token`;
CREATE TABLE `seckill_pre_check_token`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `token` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '令牌',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `seckill_goods_id` bigint(20) NOT NULL COMMENT '秒杀商品ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `check_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '检查结果JSON',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态 1:有效 2:已使用 3:已过期',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_token`(`token`) USING BTREE,
  INDEX `idx_user_goods`(`user_id`, `seckill_goods_id`) USING BTREE,
  INDEX `idx_expire_time`(`expire_time`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀预检查令牌表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for seckill_redis_keys
-- ----------------------------
DROP TABLE IF EXISTS `seckill_redis_keys`;
CREATE TABLE `seckill_redis_keys`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `key_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Redis键名',
  `key_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '键类型(string/hash/set/list/zset)',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '键描述',
  `ttl` int(11) NOT NULL DEFAULT -1 COMMENT '过期时间(秒)，-1表示永不过期',
  `data_size` bigint(20) NOT NULL DEFAULT 0 COMMENT '数据大小(字节)',
  `last_access_time` datetime NULL DEFAULT NULL COMMENT '最后访问时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_activity_key`(`activity_id`, `key_name`) USING BTREE,
  INDEX `idx_key_type`(`key_type`) USING BTREE,
  INDEX `idx_ttl`(`ttl`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Redis缓存键管理表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for seckill_redis_sync_log
-- ----------------------------
DROP TABLE IF EXISTS `seckill_redis_sync_log`;
CREATE TABLE `seckill_redis_sync_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `sync_type` int(11) NOT NULL COMMENT '同步类型 1:初始化 2:更新 3:清理',
  `redis_keys` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '涉及的Redis键',
  `sync_status` int(11) NOT NULL DEFAULT 0 COMMENT '同步状态 0:进行中 1:成功 2:失败',
  `error_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误信息',
  `sync_data_count` int(11) NOT NULL DEFAULT 0 COMMENT '同步数据条数',
  `execution_time` int(11) NOT NULL DEFAULT 0 COMMENT '执行时间(毫秒)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id`) USING BTREE,
  INDEX `idx_sync_type`(`sync_type`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Redis缓存同步记录表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for seckill_stock_log
-- ----------------------------
DROP TABLE IF EXISTS `seckill_stock_log`;
CREATE TABLE `seckill_stock_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '����',
  `seckill_goods_id` bigint(20) NOT NULL COMMENT '��ɱ��ƷID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '�û�ID',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '����ID',
  `operation_type` int(11) NOT NULL COMMENT '�������� 1:�ۼ���� 2:�ͷſ�� 3:��ʼ�����',
  `quantity` int(11) NOT NULL COMMENT '��������',
  `before_stock` int(11) NOT NULL COMMENT '����ǰ���',
  `after_stock` int(11) NOT NULL COMMENT '��������',
  `version` int(11) NOT NULL COMMENT '����ʱ�İ汾��',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '��ע',
  `lua_script_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ִ�е�Lua�ű�����',
  `execution_time` int(11) NOT NULL DEFAULT 0 COMMENT 'ִ��ʱ��(����)',
  `redis_keys` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�漰��Redis��',
  `create_time` datetime NULL DEFAULT NULL COMMENT '����ʱ��',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_seckill_goods_id`(`seckill_goods_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_lua_script`(`lua_script_name`) USING BTREE,
  INDEX `idx_execution_time`(`execution_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '��ɱ��������־��' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for seckill_user_record
-- ----------------------------
DROP TABLE IF EXISTS `seckill_user_record`;
CREATE TABLE `seckill_user_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '����',
  `activity_id` bigint(20) NOT NULL COMMENT '��ɱ�ID',
  `seckill_goods_id` bigint(20) NOT NULL COMMENT '��ɱ��ƷID',
  `user_id` bigint(20) NOT NULL COMMENT '�û�ID',
  `quantity` int(11) NOT NULL DEFAULT 1 COMMENT '�ѹ�������',
  `redis_cache_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Redis�����',
  `cache_expire_time` datetime NULL DEFAULT NULL COMMENT '�������ʱ��',
  `create_time` datetime NULL DEFAULT NULL COMMENT '����ʱ��',
  `update_time` datetime NULL DEFAULT NULL COMMENT '����ʱ��',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_record`(`activity_id`, `seckill_goods_id`, `user_id`) USING BTREE,
  INDEX `idx_activity_goods_user`(`activity_id`, `seckill_goods_id`, `user_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_cache_expire`(`cache_expire_time`) USING BTREE,
  INDEX `idx_redis_cache_key`(`redis_cache_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '��ɱ�û������¼��' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of seckill_user_record
-- ----------------------------
INSERT INTO `seckill_user_record` VALUES (1, 3, 5, 4, 3, NULL, NULL, '2025-08-19 01:51:46', '2025-08-19 02:21:43');
INSERT INTO `seckill_user_record` VALUES (2, 2, 3, 4, 14, NULL, NULL, '2025-08-19 02:04:58', '2025-08-19 02:32:02');
INSERT INTO `seckill_user_record` VALUES (3, 3, 6, 4, 5, NULL, NULL, '2025-08-19 02:10:25', '2025-08-19 02:10:25');
INSERT INTO `seckill_user_record` VALUES (4, 2, 4, 4, 2, NULL, NULL, '2025-08-19 02:18:56', '2025-08-19 02:18:56');

-- ----------------------------
-- Table structure for setmeal
-- ----------------------------
DROP TABLE IF EXISTS `setmeal`;
CREATE TABLE `setmeal`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint(20) NOT NULL COMMENT '菜品分类id',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '套餐名称',
  `price` decimal(10, 2) NOT NULL COMMENT '套餐价格',
  `status` int(11) NULL DEFAULT 1 COMMENT '售卖状态 0:停售 1:起售',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '描述信息',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '图片',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_setmeal_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '套餐' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of setmeal
-- ----------------------------
INSERT INTO `setmeal` VALUES (1, 13, '敦煌艺术丝路圣', 546.00, 1, '武器', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/89759211-fd14-465c-a563-09b0b4abf7cf.png', '2025-08-13 22:42:29', '2025-08-14 01:28:29', 1, 1);
INSERT INTO `setmeal` VALUES (2, 24, '粉色的四个', 1802.00, 1, '', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/85d2cdb9-6e80-472f-8216-5334945fa224.png', '2025-08-14 01:28:23', '2025-08-14 01:28:27', 1, 1);

-- ----------------------------
-- Table structure for setmeal_dish
-- ----------------------------
DROP TABLE IF EXISTS `setmeal_dish`;
CREATE TABLE `setmeal_dish`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `setmeal_id` bigint(20) NULL DEFAULT NULL COMMENT '套餐id',
  `dish_id` bigint(20) NULL DEFAULT NULL COMMENT '菜品id',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '菜品名称 （冗余字段）',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '菜品单价（冗余字段）',
  `copies` int(11) NULL DEFAULT NULL COMMENT '菜品份数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '套餐菜品关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of setmeal_dish
-- ----------------------------
INSERT INTO `setmeal_dish` VALUES (1, 1, 61, '剁椒鱼头', 66.00, 1);
INSERT INTO `setmeal_dish` VALUES (2, 1, 67, '鮰鱼2斤', 72.00, 1);
INSERT INTO `setmeal_dish` VALUES (3, 2, 65, '草鱼2斤', 68.00, 1);
INSERT INTO `setmeal_dish` VALUES (4, 2, 66, '江团鱼2斤', 119.00, 1);
INSERT INTO `setmeal_dish` VALUES (5, 2, 67, '鮰鱼2斤', 72.00, 1);
INSERT INTO `setmeal_dish` VALUES (6, 2, 70, '委屈委屈', 1123.00, 1);
INSERT INTO `setmeal_dish` VALUES (7, 2, 71, '七个v啊', 12.00, 1);

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '商品名称',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '图片',
  `user_id` bigint(20) NOT NULL COMMENT '主键',
  `dish_id` bigint(20) NULL DEFAULT NULL COMMENT '菜品id',
  `setmeal_id` bigint(20) NULL DEFAULT NULL COMMENT '套餐id',
  `dish_flavor` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '口味',
  `number` int(11) NOT NULL DEFAULT 1 COMMENT '数量',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '购物车' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of shopping_cart
-- ----------------------------
INSERT INTO `shopping_cart` VALUES (2, '粉色的四个', 'https://kuikuiwww.oss-cn-guangzhou.aliyuncs.com/85d2cdb9-6e80-472f-8216-5334945fa224.png', 4, NULL, 2, NULL, 1, 1802.00, '2025-08-18 14:47:29');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openid` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '微信用户唯一标识',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '姓名',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '手机号',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '性别',
  `id_number` varchar(18) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '身份证号',
  `avatar` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '用户信息' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (4, 'oqflt7dfiQAOGe1KkHtI_I4zRjMM', NULL, NULL, NULL, NULL, NULL, '2025-08-13 15:27:39');

-- ----------------------------
-- View structure for v_active_activity_realtime
-- ----------------------------
DROP VIEW IF EXISTS `v_active_activity_realtime`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_active_activity_realtime` AS SELECT 
    a.id,
    a.name,
    a.start_time,
    a.end_time,
    a.current_qps,
    a.peak_qps,
    a.participant_count,
    a.total_sales,
    COUNT(g.id) as goods_count,
    SUM(g.total_stock) as total_stock,
    SUM(g.available_stock) as available_stock,
    SUM(g.sold_count) as total_sold,
    COUNT(CASE WHEN g.available_stock <= g.stock_warning_threshold THEN 1 END) as low_stock_goods,
    CASE 
        WHEN a.end_time < NOW() THEN '已结束'
        WHEN DATE_ADD(a.end_time, INTERVAL -5 MINUTE) < NOW() THEN '即将结束'
        WHEN a.start_time <= NOW() THEN '进行中'
        ELSE '未开始'
    END as real_time_status
FROM seckill_activity a
LEFT JOIN seckill_goods g ON a.id = g.activity_id
WHERE a.status = 1 
  AND NOW() BETWEEN DATE_SUB(a.start_time, INTERVAL 1 HOUR) AND DATE_ADD(a.end_time, INTERVAL 1 HOUR)
GROUP BY a.id, a.name, a.start_time, a.end_time, a.current_qps, 
         a.peak_qps, a.participant_count, a.total_sales; ;

-- ----------------------------
-- View structure for v_activity_summary
-- ----------------------------
DROP VIEW IF EXISTS `v_activity_summary`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_activity_summary` AS SELECT 
    a.id as activity_id,
    a.name as activity_name,
    a.status,
    a.start_time,
    a.end_time,
    a.hot_level,
    a.participant_count,
    a.total_sales,
    a.current_qps,
    a.peak_qps,
    COUNT(g.id) as goods_count,
    COALESCE(SUM(g.total_stock), 0) as total_stock,
    COALESCE(SUM(g.available_stock), 0) as available_stock,
    COALESCE(SUM(g.sold_count), 0) as total_sold,
    CASE 
        WHEN COALESCE(SUM(g.total_stock), 0) = 0 THEN 0
        ELSE ROUND((COALESCE(SUM(g.sold_count), 0) / COALESCE(SUM(g.total_stock), 0)) * 100, 2)
    END as overall_sales_rate
FROM seckill_activity a
LEFT JOIN seckill_goods g ON a.id = g.activity_id
GROUP BY a.id, a.name, a.status, a.start_time, a.end_time, a.hot_level, 
         a.participant_count, a.total_sales, a.current_qps, a.peak_qps; ;

-- ----------------------------
-- View structure for v_goods_sales_info
-- ----------------------------
DROP VIEW IF EXISTS `v_goods_sales_info`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_goods_sales_info` AS SELECT 
    g.id,
    g.activity_id,
    g.goods_name,
    g.goods_image,
    g.original_price,
    g.seckill_price,
    g.total_stock,
    g.sold_count,
    g.hot_ranking,
    a.name as activity_name,
    CASE 
        WHEN g.total_stock = 0 THEN 0
        ELSE ROUND((g.sold_count / g.total_stock) * 100, 2)
    END as sales_rate,
    g.sold_count * g.seckill_price as sales_amount,
    g.original_price - g.seckill_price as discount_amount
FROM seckill_goods g
LEFT JOIN seckill_activity a ON g.activity_id = a.id
WHERE g.status = 1; ;

-- ----------------------------
-- View structure for v_hot_goods_analysis
-- ----------------------------
DROP VIEW IF EXISTS `v_hot_goods_analysis`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_hot_goods_analysis` AS SELECT 
    g.id,
    g.activity_id,
    g.goods_name,
    g.seckill_price,
    g.total_stock,
    g.sold_count,
    g.purchase_velocity,
    g.last_purchase_time,
    a.name as activity_name,
    COUNT(DISTINCT o.user_id) as unique_buyers,
    CASE 
        WHEN COUNT(o.id) = 0 THEN 0
        ELSE ROUND(AVG(o.quantity), 2)
    END as avg_quantity_per_order,
    CASE 
        WHEN g.total_stock = 0 THEN 0
        ELSE ROUND((g.sold_count / g.total_stock) * 100, 2)
    END as sellout_rate,
    CASE 
        WHEN g.sold_count = 0 THEN 0
        ELSE ROUND((COUNT(DISTINCT o.user_id) / g.sold_count) * 100, 2)
    END as buyer_diversity_rate
FROM seckill_goods g
LEFT JOIN seckill_activity a ON g.activity_id = a.id
LEFT JOIN seckill_order o ON g.id = o.seckill_goods_id
GROUP BY g.id, g.activity_id, g.goods_name, g.seckill_price, g.total_stock, 
         g.sold_count, g.purchase_velocity, g.last_purchase_time, a.name; ;

-- ----------------------------
-- View structure for v_lua_performance_stats
-- ----------------------------
DROP VIEW IF EXISTS `v_lua_performance_stats`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_lua_performance_stats` AS SELECT 
    ls.script_name,
    ls.version,
    ls.status,
    ls.load_time,
    COUNT(o.id) as usage_count,
    CASE 
        WHEN COUNT(o.id) = 0 THEN 0
        ELSE ROUND(AVG(o.script_execution_time), 2)
    END as avg_execution_time,
    CASE 
        WHEN COUNT(o.id) = 0 THEN 0
        ELSE MIN(o.script_execution_time)
    END as min_execution_time,
    CASE 
        WHEN COUNT(o.id) = 0 THEN 0
        ELSE MAX(o.script_execution_time)
    END as max_execution_time,
    CASE 
        WHEN ls.status = 1 THEN '启用'
        ELSE '禁用'
    END as status_name
FROM seckill_lua_script ls
LEFT JOIN seckill_order o ON ls.script_sha1 = o.lua_script_hash
GROUP BY ls.script_name, ls.version, ls.status, ls.load_time; ;

-- ----------------------------
-- View structure for v_order_status_stats
-- ----------------------------
DROP VIEW IF EXISTS `v_order_status_stats`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_order_status_stats` AS SELECT 
    o.activity_id,
    a.name as activity_name,
    COUNT(*) as total_orders,
    SUM(CASE WHEN o.status = 1 THEN 1 ELSE 0 END) as pending_payment,
    SUM(CASE WHEN o.status = 2 THEN 1 ELSE 0 END) as pending_accept,
    SUM(CASE WHEN o.status = 3 THEN 1 ELSE 0 END) as accepted,
    SUM(CASE WHEN o.status = 4 THEN 1 ELSE 0 END) as delivering,
    SUM(CASE WHEN o.status = 5 THEN 1 ELSE 0 END) as completed,
    SUM(CASE WHEN o.status = 6 THEN 1 ELSE 0 END) as cancelled,
    SUM(CASE WHEN o.pay_status = 1 THEN 1 ELSE 0 END) as paid_orders,
    SUM(CASE WHEN o.pay_status = 0 THEN 1 ELSE 0 END) as unpaid_orders,
    COALESCE(SUM(CASE WHEN o.status = 5 THEN o.amount ELSE 0 END), 0) as completed_amount,
    CASE 
        WHEN COUNT(*) = 0 THEN 0
        ELSE ROUND((SUM(CASE WHEN o.status = 5 THEN 1 ELSE 0 END) / COUNT(*)) * 100, 2)
    END as completion_rate,
    CASE 
        WHEN COUNT(*) = 0 THEN 0
        ELSE ROUND((SUM(CASE WHEN o.pay_status = 1 THEN 1 ELSE 0 END) / COUNT(*)) * 100, 2)
    END as payment_rate
FROM seckill_order o
LEFT JOIN seckill_activity a ON o.activity_id = a.id
GROUP BY o.activity_id, a.name; ;

-- ----------------------------
-- View structure for v_seckill_activity_info
-- ----------------------------
DROP VIEW IF EXISTS `v_seckill_activity_info`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_seckill_activity_info` AS SELECT 
    a.id,
    a.name,
    a.image,
    a.start_time,
    a.end_time,
    a.status,
    a.description,
    a.hot_level,
    a.participant_count,
    a.total_sales,
    a.current_qps,
    a.peak_qps,
    a.avg_response_time,
    a.redis_sync_status,
    a.create_time,
    a.update_time,
    CASE 
        WHEN a.status = 0 THEN '未开始'
        WHEN a.status = 1 THEN '进行中'
        WHEN a.status = 2 THEN '已结束'
        WHEN a.status = 3 THEN '已取消'
        ELSE '未知状态'
    END as status_name,
    CASE 
        WHEN a.hot_level = 'low' THEN '低热度'
        WHEN a.hot_level = 'normal' THEN '普通'
        WHEN a.hot_level = 'high' THEN '高热度'
        ELSE '未知'
    END as hot_level_name,
    CASE 
        WHEN NOW() < a.start_time THEN '未开始'
        WHEN NOW() BETWEEN a.start_time AND a.end_time THEN '进行中'
        WHEN NOW() > a.end_time THEN '已结束'
        ELSE '未知'
    END as time_status
FROM seckill_activity a; ;

-- ----------------------------
-- View structure for v_seckill_activity_stats
-- ----------------------------
DROP VIEW IF EXISTS `v_seckill_activity_stats`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_seckill_activity_stats` AS SELECT 
    a.id,
    a.name,
    a.status,
    a.start_time,
    a.end_time,
    a.participant_count,
    a.total_sales,
    a.current_qps,
    a.peak_qps,
    a.avg_response_time,
    COUNT(g.id) as goods_count,
    SUM(g.total_stock) as total_stock,
    SUM(g.sold_count) as total_sold,
    SUM(g.available_stock) as remaining_stock,
    ROUND(SUM(g.sold_count) / NULLIF(SUM(g.total_stock), 0) * 100, 2) as sales_rate,
    COUNT(DISTINCT o.user_id) as actual_participants,
    COUNT(o.id) as total_orders,
    SUM(CASE WHEN o.status = 5 THEN 1 ELSE 0 END) as completed_orders,
    ROUND(SUM(CASE WHEN o.status = 5 THEN 1 ELSE 0 END) / NULLIF(COUNT(o.id), 0) * 100, 2) as success_rate
FROM seckill_activity a
LEFT JOIN seckill_goods g ON a.id = g.activity_id
LEFT JOIN seckill_order o ON a.id = o.activity_id
GROUP BY a.id, a.name, a.status, a.start_time, a.end_time, a.participant_count, a.total_sales, a.current_qps, a.peak_qps, a.avg_response_time; ;

-- ----------------------------
-- View structure for v_seckill_goods_detail
-- ----------------------------
DROP VIEW IF EXISTS `v_seckill_goods_detail`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_seckill_goods_detail` AS SELECT 
    g.id,
    g.activity_id,
    g.goods_type,
    g.goods_id,
    g.goods_name,
    g.goods_image,
    g.original_price,
    g.seckill_price,
    g.total_stock,
    g.available_stock,
    g.sold_count,
    g.limit_count,
    g.version,
    g.hot_ranking,
    g.last_purchase_time,
    g.purchase_velocity,
    g.stock_sync_time,
    g.status,
    a.name as activity_name,
    a.start_time as activity_start_time,
    a.end_time as activity_end_time,
    CASE 
        WHEN g.goods_type = 1 THEN '菜品'
        WHEN g.goods_type = 2 THEN '套餐'
        ELSE '未知'
    END as goods_type_name,
    CASE 
        WHEN g.status = 0 THEN '下架'
        WHEN g.status = 1 THEN '上架'
        ELSE '未知'
    END as status_name,
    ROUND((g.seckill_price / g.original_price) * 100, 1) as discount_rate,
    CASE 
        WHEN g.total_stock = 0 THEN 0
        ELSE ROUND((g.sold_count / g.total_stock) * 100, 2)
    END as sales_rate,
    CASE 
        WHEN g.total_stock = 0 THEN 0
        ELSE ROUND((g.available_stock / g.total_stock) * 100, 2)
    END as stock_rate,
    CASE 
        WHEN g.available_stock <= g.stock_warning_threshold THEN 1
        ELSE 0
    END as is_low_stock,
    g.original_price - g.seckill_price as discount_amount
FROM seckill_goods g
LEFT JOIN seckill_activity a ON g.activity_id = a.id; ;

-- ----------------------------
-- View structure for v_seckill_order_detail
-- ----------------------------
DROP VIEW IF EXISTS `v_seckill_order_detail`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_seckill_order_detail` AS SELECT 
    o.id,
    o.order_id,
    o.number,
    o.activity_id,
    o.seckill_goods_id,
    o.goods_name,
    o.goods_image,
    o.goods_type,
    o.original_price,
    o.seckill_price,
    o.quantity,
    o.amount,
    o.user_id,
    o.status,
    o.pay_status,
    o.pay_method,
    o.order_time,
    o.checkout_time,
    o.pay_expire_time,
    o.consignee,
    o.phone,
    o.address,
    o.lua_script_hash,
    o.script_execution_time,
    o.stock_locked,
    o.pre_check_token,
    a.name as activity_name,
    g.goods_name as goods_detail_name,
    CASE 
        WHEN o.status = 1 THEN '待付款'
        WHEN o.status = 2 THEN '待接单'
        WHEN o.status = 3 THEN '已接单'
        WHEN o.status = 4 THEN '派送中'
        WHEN o.status = 5 THEN '已完成'
        WHEN o.status = 6 THEN '已取消'
        ELSE '未知状态'
    END as status_name,
    CASE 
        WHEN o.pay_status = 0 THEN '未支付'
        WHEN o.pay_status = 1 THEN '已支付'
        WHEN o.pay_status = 2 THEN '退款'
        ELSE '未知'
    END as pay_status_name,
    CASE 
        WHEN o.pay_method = 1 THEN '微信'
        WHEN o.pay_method = 2 THEN '支付宝'
        ELSE '未知'
    END as pay_method_name,
    CASE 
        WHEN o.pay_expire_time < NOW() AND o.pay_status = 0 THEN 1
        ELSE 0
    END as is_expired,
    o.original_price - o.seckill_price as saved_amount
FROM seckill_order o
LEFT JOIN seckill_activity a ON o.activity_id = a.id
LEFT JOIN seckill_goods g ON o.seckill_goods_id = g.id; ;

-- ----------------------------
-- View structure for v_stock_warning
-- ----------------------------
DROP VIEW IF EXISTS `v_stock_warning`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_stock_warning` AS SELECT 
    g.id,
    g.activity_id,
    g.goods_name,
    g.total_stock,
    g.available_stock,
    g.sold_count,
    g.stock_warning_threshold,
    a.name as activity_name,
    a.end_time,
    CASE 
        WHEN g.available_stock = 0 THEN '售罄'
        WHEN g.available_stock <= g.stock_warning_threshold THEN '库存不足'
        WHEN g.available_stock <= (g.total_stock * 0.1) THEN '库存紧张'
        ELSE '库存充足'
    END as warning_level,
    CASE 
        WHEN g.total_stock = 0 THEN 0
        ELSE ROUND((g.available_stock / g.total_stock) * 100, 2)
    END as stock_percentage
FROM seckill_goods g
LEFT JOIN seckill_activity a ON g.activity_id = a.id
WHERE g.status = 1 
  AND a.status = 1
  AND (g.available_stock <= g.stock_warning_threshold OR g.available_stock <= (g.total_stock * 0.1)); ;

-- ----------------------------
-- View structure for v_system_performance_summary
-- ----------------------------
DROP VIEW IF EXISTS `v_system_performance_summary`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_system_performance_summary` AS SELECT 
    pm.monitor_time,
    pm.redis_memory_usage,
    pm.redis_connection_count,
    pm.redis_script_cache_hits,
    pm.redis_avg_latency,
    pm.lua_total_executions,
    pm.lua_avg_execution_time,
    pm.lua_error_rate,
    pm.business_stock_accuracy,
    pm.business_oversell_count,
    pm.business_user_limit_violations,
    CASE 
        WHEN pm.redis_script_cache_hits >= 0.95 THEN '优秀'
        WHEN pm.redis_script_cache_hits >= 0.90 THEN '良好'
        WHEN pm.redis_script_cache_hits >= 0.80 THEN '一般'
        ELSE '较差'
    END as cache_performance_level,
    CASE 
        WHEN pm.lua_error_rate <= 0.001 THEN '优秀'
        WHEN pm.lua_error_rate <= 0.005 THEN '良好'
        WHEN pm.lua_error_rate <= 0.01 THEN '一般'
        ELSE '较差'
    END as script_performance_level,
    CASE 
        WHEN pm.business_oversell_count = 0 THEN '完美'
        WHEN pm.business_oversell_count <= 5 THEN '良好'
        ELSE '需要关注'
    END as oversell_status
FROM seckill_performance_monitor pm; ;

-- ----------------------------
-- View structure for v_user_behavior_stats
-- ----------------------------
DROP VIEW IF EXISTS `v_user_behavior_stats`;
CREATE ALGORITHM = UNDEFINED DEFINER = `root`@`localhost` SQL SECURITY DEFINER VIEW `v_user_behavior_stats` AS SELECT 
    r.user_id,
    r.activity_id,
    a.name as activity_name,
    COUNT(DISTINCT r.seckill_goods_id) as purchased_goods_count,
    SUM(r.quantity) as total_quantity,
    COUNT(o.id) as order_count,
    COALESCE(SUM(CASE WHEN o.status = 5 THEN o.amount ELSE 0 END), 0) as total_spent,
    MAX(r.create_time) as last_purchase_time
FROM seckill_user_record r
LEFT JOIN seckill_activity a ON r.activity_id = a.id
LEFT JOIN seckill_order o ON r.user_id = o.user_id AND r.activity_id = o.activity_id
GROUP BY r.user_id, r.activity_id, a.name; ;

-- ----------------------------
-- Procedure structure for CleanExpiredSeckillCache
-- ----------------------------
DROP PROCEDURE IF EXISTS `CleanExpiredSeckillCache`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `CleanExpiredSeckillCache`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_activity_id BIGINT;
    DECLARE cur CURSOR FOR 
        SELECT id FROM seckill_activity 
        WHERE end_time < DATE_SUB(NOW(), INTERVAL 1 DAY) 
        AND redis_sync_status = 1;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    START TRANSACTION;
    
    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_activity_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- ����Redisͬ��״̬Ϊδͬ��
        UPDATE seckill_activity 
        SET redis_sync_status = 0, update_time = NOW()
        WHERE id = v_activity_id;
        
        -- ��¼������־
        INSERT INTO seckill_redis_sync_log (
            activity_id, sync_type, sync_status, 
            sync_data_count, execution_time, create_time, update_time
        ) VALUES (
            v_activity_id, 3, 1, 
            0, 0, NOW(), NOW()
        );
        
        -- ����Redis�������¼
        DELETE FROM seckill_redis_keys WHERE activity_id = v_activity_id;
        
    END LOOP;
    CLOSE cur;
    
    -- ������ڵļ�����ݣ�����30�죩
    DELETE FROM seckill_monitor_data 
    WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    -- ������ڵ�ͳ�����ݣ�����90�죩
    DELETE FROM seckill_lua_statistics 
    WHERE execution_date < DATE_SUB(NOW(), INTERVAL 90 DAY);
    
    -- ������ڵ�Ԥ������ƣ�����7�죩
    DELETE FROM seckill_pre_check_token 
    WHERE expire_time < DATE_SUB(NOW(), INTERVAL 7 DAY);
    
    -- ������ڵ����ܼ�����ݣ�����7�죩
    DELETE FROM seckill_performance_monitor 
    WHERE create_time < DATE_SUB(NOW(), INTERVAL 7 DAY);
    
    COMMIT;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table seckill_order
-- ----------------------------
DROP TRIGGER IF EXISTS `tr_seckill_order_insert`;
delimiter ;;
CREATE TRIGGER `tr_seckill_order_insert` AFTER INSERT ON `seckill_order` FOR EACH ROW BEGIN
    -- ���»�����û���
    UPDATE seckill_activity 
    SET participant_count = (
        SELECT COUNT(DISTINCT user_id) 
        FROM seckill_order 
        WHERE activity_id = NEW.activity_id
    ),
    total_sales = (
        SELECT COALESCE(SUM(amount), 0) 
        FROM seckill_order 
        WHERE activity_id = NEW.activity_id AND status = 5
    ),
    update_time = NOW()
    WHERE id = NEW.activity_id;
    
    -- ������Ʒ�����ʱ��
    UPDATE seckill_goods 
    SET last_purchase_time = NOW(),
        update_time = NOW()
    WHERE id = NEW.seckill_goods_id;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table seckill_order
-- ----------------------------
DROP TRIGGER IF EXISTS `tr_seckill_order_update`;
delimiter ;;
CREATE TRIGGER `tr_seckill_order_update` AFTER UPDATE ON `seckill_order` FOR EACH ROW BEGIN
    -- ������״̬��Ϊ�����ʱ�����»�����۶�
    IF NEW.status = 5 AND OLD.status != 5 THEN
        UPDATE seckill_activity 
        SET total_sales = (
            SELECT COALESCE(SUM(amount), 0) 
            FROM seckill_order 
            WHERE activity_id = NEW.activity_id AND status = 5
        ),
        update_time = NOW()
        WHERE id = NEW.activity_id;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
