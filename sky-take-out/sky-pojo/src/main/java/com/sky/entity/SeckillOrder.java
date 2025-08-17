package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ��ɱ������
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // ����ID������orders��
    private Long orderId;

    // ��ɱ�ID
    private Long activityId;

    // ��ɱ��ƷID
    private Long seckillGoodsId;

    // �û�ID
    private Long userId;

    // ��������
    private Integer quantity;

    // ��ɱ�۸�
    private BigDecimal seckillPrice;

    // �����ܽ��
    private BigDecimal totalAmount;

    // ֧��״̬ 0:δ֧�� 1:��֧�� 2:֧����ʱȡ��
    private Integer payStatus;

    // ֧����ʱʱ��
    private LocalDateTime payExpireTime;

    // ����ʱ��
    private LocalDateTime createTime;

    // ����ʱ��
    private LocalDateTime updateTime;
}