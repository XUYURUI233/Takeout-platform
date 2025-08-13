package com.sky.dto;

import lombok.Data;

/**
 * 秒杀库存更新DTO
 */
@Data
public class SeckillStockUpdateDTO {

    //秒杀商品项ID
    private Long itemId;

    //库存变化量
    private Integer stockChange;

    //操作类型：INCREASE(增加)、DECREASE(减少)、SET(设置)
    private String operationType;

    //订单ID
    private String orderId;

    //备注
    private String remark;
}

