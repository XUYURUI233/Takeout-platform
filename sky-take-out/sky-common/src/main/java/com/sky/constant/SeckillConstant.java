package com.sky.constant;

/**
 * ��ɱҵ����
 */
public class SeckillConstant {

    // ��ɱ�״̬
    public static final Integer ACTIVITY_STATUS_NOT_STARTED = 0; // δ��ʼ
    public static final Integer ACTIVITY_STATUS_IN_PROGRESS = 1; // ������
    public static final Integer ACTIVITY_STATUS_ENDED = 2;       // �ѽ���
    public static final Integer ACTIVITY_STATUS_CANCELLED = 3;   // ��ȡ��

    // ��ɱ��Ʒ״̬
    public static final Integer GOODS_STATUS_OFF_SHELF = 0; // �¼�
    public static final Integer GOODS_STATUS_ON_SHELF = 1;  // �ϼ�

    // ��Ʒ����
    public static final Integer GOODS_TYPE_DISH = 1;    // ��Ʒ
    public static final Integer GOODS_TYPE_SETMEAL = 2; // �ײ�

    // ����֧��״̬
    public static final Integer PAY_STATUS_UNPAID = 0;    // δ֧��
    public static final Integer PAY_STATUS_PAID = 1;      // ��֧��
    public static final Integer PAY_STATUS_TIMEOUT = 2; // ֧����ʱȡ��

    // ����������
    public static final Integer STOCK_OP_DEDUCT = 1; // �ۼ����
    public static final Integer STOCK_OP_RELEASE = 2;  // �ͷſ��
    public static final Integer STOCK_OP_INIT = 3;     // ��ʼ�����

    // ֧����ʱʱ�䣨�룩
    public static final Integer PAY_TIME_LIMIT = 900; // 15���� = 900��
    
    // ��Ʒ״̬������Ϊ�˼������д��룩
    public static final Integer GOODS_STATUS_ON = GOODS_STATUS_ON_SHELF;
}