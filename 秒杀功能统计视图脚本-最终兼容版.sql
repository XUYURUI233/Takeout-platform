-- 秒杀功能统计视图脚本 - MySQL 5.5最终兼容版
-- 根据现有数据库结构和Lua脚本接口文档需求创建
-- 完全兼容MySQL 5.5，移除所有不兼容的语法和函数

-- ===========================
-- 1. 基础统计视图
-- ===========================

-- 1.1 秒杀活动基础信息视图
DROP VIEW IF EXISTS `v_seckill_activity_info`;
CREATE VIEW `v_seckill_activity_info` AS
SELECT 
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
FROM seckill_activity a;

-- 1.2 秒杀商品详细信息视图
DROP VIEW IF EXISTS `v_seckill_goods_detail`;
CREATE VIEW `v_seckill_goods_detail` AS
SELECT 
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
LEFT JOIN seckill_activity a ON g.activity_id = a.id;

-- 1.3 秒杀订单详细信息视图
DROP VIEW IF EXISTS `v_seckill_order_detail`;
CREATE VIEW `v_seckill_order_detail` AS
SELECT 
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
LEFT JOIN seckill_goods g ON o.seckill_goods_id = g.id;

-- ===========================
-- 2. 统计分析视图
-- ===========================

-- 2.1 活动汇总统计视图
DROP VIEW IF EXISTS `v_activity_summary`;
CREATE VIEW `v_activity_summary` AS
SELECT 
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
         a.participant_count, a.total_sales, a.current_qps, a.peak_qps;

-- 2.2 商品销售信息视图
DROP VIEW IF EXISTS `v_goods_sales_info`;
CREATE VIEW `v_goods_sales_info` AS
SELECT 
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
WHERE g.status = 1;

-- 2.3 订单状态统计视图
DROP VIEW IF EXISTS `v_order_status_stats`;
CREATE VIEW `v_order_status_stats` AS
SELECT 
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
GROUP BY o.activity_id, a.name;

-- ===========================
-- 3. 用户行为分析视图
-- ===========================

-- 3.1 用户购买行为统计视图
DROP VIEW IF EXISTS `v_user_behavior_stats`;
CREATE VIEW `v_user_behavior_stats` AS
SELECT 
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
GROUP BY r.user_id, r.activity_id, a.name;

-- 3.2 热门商品分析视图
DROP VIEW IF EXISTS `v_hot_goods_analysis`;
CREATE VIEW `v_hot_goods_analysis` AS
SELECT 
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
         g.sold_count, g.purchase_velocity, g.last_purchase_time, a.name;

-- ===========================
-- 4. 性能监控视图
-- ===========================

-- 4.1 Lua脚本性能统计视图
DROP VIEW IF EXISTS `v_lua_performance_stats`;
CREATE VIEW `v_lua_performance_stats` AS
SELECT 
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
GROUP BY ls.script_name, ls.version, ls.status, ls.load_time;

-- 4.2 系统性能监控汇总视图
DROP VIEW IF EXISTS `v_system_performance_summary`;
CREATE VIEW `v_system_performance_summary` AS
SELECT 
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
FROM seckill_performance_monitor pm;

-- ===========================
-- 5. 实时监控视图
-- ===========================

-- 5.1 当前活跃活动实时状态视图
DROP VIEW IF EXISTS `v_active_activity_realtime`;
CREATE VIEW `v_active_activity_realtime` AS
SELECT 
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
         a.peak_qps, a.participant_count, a.total_sales;

-- 5.2 库存预警视图
DROP VIEW IF EXISTS `v_stock_warning`;
CREATE VIEW `v_stock_warning` AS
SELECT 
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
  AND (g.available_stock <= g.stock_warning_threshold OR g.available_stock <= (g.total_stock * 0.1));

-- ===========================
-- 6. 执行完成提示
-- ===========================

SELECT '秒杀功能统计视图创建完成！（MySQL 5.5最终兼容版）' as message,
       '已创建11个统计视图，涵盖活动、商品、订单、用户行为、性能监控等' as details,
       '移除了所有MySQL 5.5不兼容的语法（窗口函数、变量等）' as compatibility,
       '视图支持实时数据查询和业务分析，完全兼容MySQL 5.5.36' as features,
       NOW() as create_time;



