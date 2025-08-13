package com.sky.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * ��ɱ��Ʒ��DTO
 */
@Data
public class SeckillItemDTO {

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

    //ÿ���޹�����
    private Integer limitPerUser;
}

