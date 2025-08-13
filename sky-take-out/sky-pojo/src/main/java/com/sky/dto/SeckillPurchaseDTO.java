package com.sky.dto;

import lombok.Data;

/**
 * 秒杀购买DTO
 */
@Data
public class SeckillPurchaseDTO {

    //秒杀商品项ID
    private Long itemId;

    //购买数量
    private Integer quantity;

    //地址ID
    private Long addressId;

    //备注
    private String remark;
}

