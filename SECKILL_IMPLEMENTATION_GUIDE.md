# 秒杀功能实现指南

## 功能概述

本指南介绍了如何在苍穹外卖系统中实现秒杀功能，包括商家端管理和用户端购买。

## 商家端功能

### 1. 秒杀管理页面

访问地址：`http://localhost:8080/sky/seckill-management.html`

#### 功能特性：
- **侧边栏导航**：包含工作台、秒杀活动、新增秒杀、秒杀订单、数据统计等菜单
- **新增秒杀**：创建新的秒杀活动，设置活动时间、商品信息
- **活动管理**：查看、编辑、删除秒杀活动
- **订单管理**：查看秒杀订单数据
- **数据统计**：查看活动效果分析

#### 使用方法：
1. 点击左侧菜单"新增秒杀"
2. 填写活动基本信息（名称、时间、描述）
3. 选择参与秒杀的商品
4. 设置秒杀价格、库存、限购数量
5. 保存活动

### 2. 后端API接口

#### 秒杀活动管理接口：
- `POST /api/admin/seckill/create` - 创建秒杀活动
- `GET /api/admin/seckill/list` - 获取秒杀活动列表
- `PUT /api/admin/seckill/update/{seckillId}` - 更新秒杀活动
- `DELETE /api/admin/seckill/delete/{seckillId}` - 删除秒杀活动

#### 秒杀订单管理接口：
- `GET /api/admin/seckill/orders/list` - 获取秒杀订单列表
- `POST /api/admin/seckill/stock/update` - 更新秒杀库存
- `POST /api/admin/seckill/stock/rollback` - 回滚秒杀库存

## 用户端功能

### 1. 秒杀横幅

在商家信息下方显示滚动式秒杀横幅，包含：
- 活动名称和描述
- 剩余时间倒计时
- 立即抢购按钮

#### 实现位置：
- 文件：`mp-weixin/mp-weixin/pages/index/index-seckill.wxml`
- 样式：`mp-weixin/mp-weixin/pages/index/index-seckill.wxss`
- 逻辑：`mp-weixin/mp-weixin/pages/index/index-seckill.js`

### 2. 秒杀页面

点击横幅后跳转到秒杀页面，包含：
- 秒杀商品列表
- 商品详情和价格
- 库存状态
- 抢购按钮

#### 实现位置：
- 文件：`mp-weixin/mp-weixin/pages/seckill/seckill.wxml`
- 样式：`mp-weixin/mp-weixin/pages/seckill/seckill.wxss`
- 逻辑：`mp-weixin/mp-weixin/pages/seckill/seckill.js`

### 3. 后端API接口

#### 用户端秒杀接口：
- `GET /api/user/seckill/banner/list` - 获取秒杀横幅列表
- `GET /api/user/seckill/goods/list` - 获取秒杀商品列表
- `POST /api/user/seckill/purchase` - 参与秒杀购买
- `GET /api/user/seckill/orders/list` - 获取用户秒杀订单

## 部署说明

### 1. 商家端部署

1. 将 `seckill-management.html` 文件放到 nginx 的静态文件目录
2. 确保后端API服务正常运行
3. 访问 `http://localhost:8080/sky/seckill-management.html`

### 2. 用户端部署

1. 将新的页面文件添加到小程序项目中
2. 在 `app.json` 中注册新页面
3. 编译并上传到微信开发者工具

### 3. 数据库配置

确保数据库中有以下表：
- `seckill_activity` - 秒杀活动表
- `seckill_goods` - 秒杀商品表
- `seckill_order` - 秒杀订单表

## 技术特性

### 1. 高并发处理
- 使用Redis缓存秒杀库存
- 实现库存预扣减机制
- 防止超卖和重复购买

### 2. 用户体验
- 实时倒计时显示
- 库存状态实时更新
- 抢购结果即时反馈

### 3. 安全性
- 用户身份验证
- 防刷机制
- 订单数据加密

## 注意事项

1. **性能优化**：秒杀活动期间需要特别注意系统性能
2. **库存管理**：确保库存数据的准确性
3. **用户体验**：提供清晰的抢购流程和状态反馈
4. **数据监控**：实时监控活动效果和系统状态

## 扩展功能

1. **秒杀预热**：活动开始前的预热机制
2. **排队机制**：高并发时的排队系统
3. **防刷策略**：更复杂的防刷算法
4. **数据分析**：更详细的数据统计和分析

## 联系方式

如有问题，请联系开发团队。

