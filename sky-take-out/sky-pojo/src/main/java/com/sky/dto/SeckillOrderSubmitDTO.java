package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀订单提交DTO
 */
@Data
public class SeckillOrderSubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 秒杀商品ID
    private Long seckillGoodsId;

    // 购买数量
    private Integer quantity;

    // 地址簿ID
    private Long addressBookId;

    // 备注
    private String remark;
}