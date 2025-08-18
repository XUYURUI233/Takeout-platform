package com.sky.constant;

/**
 * 秒杀相关常量
 */
public class SeckillConstant {

    // 秒杀订单号前缀
    public static final String SECKILL_ORDER_PREFIX = "SK";
    public static final String SECKILL_LOCK_PREFIX = "seckill:lock:";
    public static final String SECKILL_CACHE_PREFIX = "seckill:cache:";
    public static final String SECKILL_USER_LIMIT_PREFIX = "seckill:user:limit:";

    // 商品状态
    public static final Integer GOODS_STATUS_OFFLINE = 0;
    public static final Integer GOODS_STATUS_ONLINE = 1;

    // 库存操作类型
    public static final Integer STOCK_OPERATION_DEDUCT = 1;
    public static final Integer STOCK_OPERATION_RELEASE = 2;
    public static final Integer STOCK_OPERATION_INIT = 3;

    // 订单支付超时时间（分钟）
    public static final Integer PAY_TIMEOUT_MINUTES = 15;
    public static final String SECKILL_ORDER_TIMEOUT_KEY = "seckill:order:timeout:";
    public static final String SECKILL_ACTIVITY_CACHE_KEY = "seckill:activity:";
    public static final String SECKILL_GOODS_STOCK_KEY = "seckill:stock:";

    // 库存缓存过期时间（秒）
    public static final Long STOCK_CACHE_EXPIRE_SECONDS = 300L;

    // 商品状态
    public static final Integer GOODS_DISABLED = 0;
    public static final Integer GOODS_ENABLED = 1;

}

