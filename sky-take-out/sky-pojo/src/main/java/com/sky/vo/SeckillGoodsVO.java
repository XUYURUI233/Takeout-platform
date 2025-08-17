package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillGoodsVO implements Serializable {

    private Long id;
    
    // 前端商品选择对话框需要的字段
    private String name;
    private String image;
    private java.math.BigDecimal price;
    private Integer type;

    // 秒杀活动ID
    private Long activityId;

    // 活动名称
    private String activityName;

    // 商品类型 1:菜品 2:套餐
    private Integer goodsType;

    // 商品ID（菜品ID或套餐ID）
    private Long goodsId;

    // 商品名称
    private String goodsName;

    // 商品图片
    private String goodsImage;

    // 分类名称
    private String categoryName;

    // 原价
    private BigDecimal originalPrice;

    // 秒杀价
    private BigDecimal seckillPrice;

    // 总库存
    private Integer totalStock;

    // 可用库存
    private Integer availableStock;

    // 已售数量
    private Integer soldCount;

    // 限购数量
    private Integer limitCount;

    // 用户已购买数量
    private Integer userPurchased;

    // 是否可购买
    private Boolean canPurchase;

    // 状态 0:下架 1:上架
    private Integer status;

    // 秒杀开始时间
    private LocalDateTime startTime;

    // 秒杀结束时间
    private LocalDateTime endTime;

    // 剩余时间（秒）
    private Long remainingTime;

    // 商品描述
    private String description;
}