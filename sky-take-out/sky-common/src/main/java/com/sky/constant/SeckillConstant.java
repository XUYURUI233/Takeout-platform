package com.sky.constant;

/**
 * ��ɱ��س���
 */
public class SeckillConstant {

    // ��ɱ������ǰ׺
    public static final String SECKILL_ORDER_PREFIX = "SK";
    public static final String SECKILL_LOCK_PREFIX = "seckill:lock:";
    public static final String SECKILL_CACHE_PREFIX = "seckill:cache:";
    public static final String SECKILL_USER_LIMIT_PREFIX = "seckill:user:limit:";

    // ��Ʒ״̬
    public static final Integer GOODS_STATUS_OFFLINE = 0;
    public static final Integer GOODS_STATUS_ONLINE = 1;

    // ����������
    public static final Integer STOCK_OPERATION_DEDUCT = 1;
    public static final Integer STOCK_OPERATION_RELEASE = 2;
    public static final Integer STOCK_OPERATION_INIT = 3;

    // ����֧����ʱʱ�䣨���ӣ�
    public static final Integer PAY_TIMEOUT_MINUTES = 15;
    public static final String SECKILL_ORDER_TIMEOUT_KEY = "seckill:order:timeout:";
    public static final String SECKILL_ACTIVITY_CACHE_KEY = "seckill:activity:";
    public static final String SECKILL_GOODS_STOCK_KEY = "seckill:stock:";

    // ��滺�����ʱ�䣨�룩
    public static final Long STOCK_CACHE_EXPIRE_SECONDS = 300L;

    // ��Ʒ״̬
    public static final Integer GOODS_DISABLED = 0;
    public static final Integer GOODS_ENABLED = 1;

}

