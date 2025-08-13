package com.sky.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 秒杀商品项DTO
 */
@Data
public class SeckillItemDTO {

    //菜品ID
    private Long dishId;

    //菜品名称
    private String dishName;

    //原价
    private BigDecimal originalPrice;

    //秒杀价
    private BigDecimal seckillPrice;

    //秒杀库存
    private Integer seckillStock;

    //每人限购数量
    private Integer limitPerUser;
}

