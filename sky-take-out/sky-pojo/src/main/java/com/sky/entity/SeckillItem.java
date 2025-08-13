package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ��ɱ��Ʒ��
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //��ɱ�ID
    private Long seckillId;

    //��ƷID
    private Long dishId;

    //��Ʒ����
    private String dishName;

    //ԭ��
    private BigDecimal originalPrice;

    //��ɱ��
    private BigDecimal seckillPrice;

    //��ɱ���
    private Integer seckillStock;

    //��ǰ���
    private Integer currentStock;

    //ÿ���޹�����
    private Integer limitPerUser;

    //״̬ 0-���� 1-����
    private Integer status;

    //����ʱ��
    private LocalDateTime createTime;

    //����ʱ��
    private LocalDateTime updateTime;
}

