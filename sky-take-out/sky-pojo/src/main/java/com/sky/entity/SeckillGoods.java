package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 秒杀活动ID
    private Long activityId;

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

    // 可用库存
    private Integer availableStock;

    // 已售数量
    private Integer soldCount;

    // 限购数量
    private Integer limitCount;

    // 版本号（乐观锁防止超卖）
    private Integer version;

    // 状态 0:下架 1:上架
    private Integer status;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 创建人
    private Long createUser;

    // 修改人
    private Long updateUser;
}