package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 可用商品VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableGoodsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 商品ID（菜品ID或套餐ID）
    private Long id;
    
    // 商品名称
    private String name;
    
    // 商品图片
    private String image;
    
    // 商品价格
    private BigDecimal price;
    
    // 商品类型 1:菜品 2:套餐
    private Integer type;
    
    // 分类名称
    private String categoryName;
}


