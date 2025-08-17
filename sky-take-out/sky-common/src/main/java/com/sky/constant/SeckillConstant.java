package com.sky.constant;

/**
 * 秒杀业务常量
 */
public class SeckillConstant {

    // 秒杀活动状态
    public static final Integer ACTIVITY_STATUS_NOT_STARTED = 0; // 未开始
    public static final Integer ACTIVITY_STATUS_IN_PROGRESS = 1; // 进行中
    public static final Integer ACTIVITY_STATUS_ENDED = 2;       // 已结束
    public static final Integer ACTIVITY_STATUS_CANCELLED = 3;   // 已取消

    // 秒杀商品状态
    public static final Integer GOODS_STATUS_OFF_SHELF = 0; // 下架
    public static final Integer GOODS_STATUS_ON_SHELF = 1;  // 上架

    // 商品类型
    public static final Integer GOODS_TYPE_DISH = 1;    // 菜品
    public static final Integer GOODS_TYPE_SETMEAL = 2; // 套餐

    // 订单支付状态
    public static final Integer PAY_STATUS_UNPAID = 0;    // 未支付
    public static final Integer PAY_STATUS_PAID = 1;      // 已支付
    public static final Integer PAY_STATUS_TIMEOUT = 2; // 支付超时取消

    // 库存操作类型
    public static final Integer STOCK_OP_DEDUCT = 1; // 扣减库存
    public static final Integer STOCK_OP_RELEASE = 2;  // 释放库存
    public static final Integer STOCK_OP_INIT = 3;     // 初始化库存

    // 支付超时时间（秒）
    public static final Integer PAY_TIME_LIMIT = 900; // 15分钟 = 900秒
    
    // 商品状态别名（为了兼容现有代码）
    public static final Integer GOODS_STATUS_ON = GOODS_STATUS_ON_SHELF;
}