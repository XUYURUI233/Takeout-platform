package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 秒杀商品DTO
 */
@Data
public class SeckillGoodsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 商品类型 1:菜品 2:套餐
    private Integer goodsType;

    // 商品ID（菜品ID或套餐ID）
    private Long goodsId;

    // 商品名称
    private String goodsName;

    // 商品图片
    private String goodsImage;

    // 原价
    private BigDecimal originalPrice;

    // 秒杀价
    private BigDecimal seckillPrice;

    // 总库存
    private Integer totalStock;

    // 限购数量
    private Integer limitCount;
}