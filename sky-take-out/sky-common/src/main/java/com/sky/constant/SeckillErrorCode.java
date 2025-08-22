package com.sky.constant;

/**
 * 秒杀业务错误码常量类
 */
public class SeckillErrorCode {

    // 成功
    public static final Integer SUCCESS = 1;

    // 秒杀业务错误码
    public static final Integer ACTIVITY_NOT_FOUND = 50001;      // 秒杀活动不存在
    public static final Integer ACTIVITY_NOT_STARTED = 50002;    // 秒杀未开始
    public static final Integer ACTIVITY_ENDED = 50003;          // 秒杀已结束
    public static final Integer GOODS_NOT_FOUND = 50004;         // 秒杀商品不存在
    public static final Integer GOODS_OFFLINE = 50005;           // 秒杀商品已下架
    public static final Integer STOCK_INSUFFICIENT = 50006;      // 库存不足
    public static final Integer PURCHASE_LIMIT_EXCEEDED = 50007; // 超出购买限制
    public static final Integer USER_LIMIT_EXCEEDED = 50008;     // 用户已达购买上限
    public static final Integer PAYMENT_TIMEOUT = 50009;         // 订单支付超时
    public static final Integer SYSTEM_BUSY = 50010;             // 系统繁忙，请稍后重试
}


