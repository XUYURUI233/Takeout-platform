package com.sky.dto;

import lombok.Data;

/**
 * ��ɱ����DTO
 */
@Data
public class SeckillPurchaseDTO {

    //��ɱ��Ʒ��ID
    private Long itemId;

    //��������
    private Integer quantity;

    //��ַID
    private Long addressId;

    //��ע
    private String remark;
}

