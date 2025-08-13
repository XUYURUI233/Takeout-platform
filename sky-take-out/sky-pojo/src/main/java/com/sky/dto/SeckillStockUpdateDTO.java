package com.sky.dto;

import lombok.Data;

/**
 * ��ɱ������DTO
 */
@Data
public class SeckillStockUpdateDTO {

    //��ɱ��Ʒ��ID
    private Long itemId;

    //���仯��
    private Integer stockChange;

    //�������ͣ�INCREASE(����)��DECREASE(����)��SET(����)
    private String operationType;

    //����ID
    private String orderId;

    //��ע
    private String remark;
}

