package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ��ɱ����ʵ����
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ����״̬���� - ��Orders�ౣ��һ��
     */
    public static final Integer PENDING_PAYMENT = 1;    // ��֧��
    public static final Integer TO_BE_CONFIRMED = 2;    // ���ӵ�
    public static final Integer CONFIRMED = 3;          // �ѽӵ�
    public static final Integer DELIVERY_IN_PROGRESS = 4; // ������
    public static final Integer COMPLETED = 5;          // �����
    public static final Integer CANCELLED = 6;          // ��ȡ��

    /**
     * ֧��״̬���� - ��Orders�ౣ��һ��
     */
    public static final Integer UN_PAID = 0;   // δ֧��
    public static final Integer PAID = 1;      // ��֧��
    public static final Integer REFUND = 2;    // �˿�

    private Long id;

    // ��������ͨ����ID
    private Long orderId;

    // ������
    private String number;

    // �ID
    private Long activityId;

    // ��ɱ��ƷID
    private Long seckillGoodsId;

    // ��Ʒ����
    private String goodsName;

    // ��ƷͼƬ
    private String goodsImage;

    // ��Ʒ���� 1:��Ʒ 2:�ײ�
    private Integer goodsType;

    // ԭ��
    private BigDecimal originalPrice;

    // ��ɱ��
    private BigDecimal seckillPrice;

    // ��������
    private Integer quantity;

    // �������
    private BigDecimal amount;

    // �û�ID
    private Long userId;

    // ����״̬ 1:��֧�� 2:���ӵ� 3:�ѽӵ� 4:������ 5:����� 6:��ȡ��
    private Integer status;

    // ֧��״̬ 0:δ֧�� 1:��֧�� 2:�˿�
    private Integer payStatus;

    // ֧����ʽ 1:΢�� 2:֧����
    private Integer payMethod;

    // �µ�ʱ��
    private LocalDateTime orderTime;

    // ����ʱ��
    private LocalDateTime checkoutTime;

    // ֧����ʱʱ��
    private LocalDateTime payExpireTime;

    // ��ע
    private String remark;

    // ��ַ��ID
    private Long addressBookId;

    // �ջ���
    private String consignee;

    // �ֻ���
    private String phone;

    // ��ַ
    private String address;

    // ����ʱ��
    private LocalDateTime createTime;

    // ����ʱ��
    private LocalDateTime updateTime;

    // ������
    private Long createUser;

    // ������
    private Long updateUser;
}