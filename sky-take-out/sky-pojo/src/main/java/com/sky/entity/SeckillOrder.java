package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀订单实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单状态常量 - 与Orders类保持一致
     */
    public static final Integer PENDING_PAYMENT = 1;    // 待支付
    public static final Integer TO_BE_CONFIRMED = 2;    // 待接单
    public static final Integer CONFIRMED = 3;          // 已接单
    public static final Integer DELIVERY_IN_PROGRESS = 4; // 派送中
    public static final Integer COMPLETED = 5;          // 已完成
    public static final Integer CANCELLED = 6;          // 已取消

    /**
     * 支付状态常量 - 与Orders类保持一致
     */
    public static final Integer UN_PAID = 0;   // 未支付
    public static final Integer PAID = 1;      // 已支付
    public static final Integer REFUND = 2;    // 退款

    private Long id;

    // 关联的普通订单ID
    private Long orderId;

    // 订单号
    private String number;

    // 活动ID
    private Long activityId;

    // 秒杀商品ID
    private Long seckillGoodsId;

    // 商品名称
    private String goodsName;

    // 商品图片
    private String goodsImage;

    // 商品类型 1:菜品 2:套餐
    private Integer goodsType;

    // 原价
    private BigDecimal originalPrice;

    // 秒杀价
    private BigDecimal seckillPrice;

    // 购买数量
    private Integer quantity;

    // 订单金额
    private BigDecimal amount;

    // 用户ID
    private Long userId;

    // 订单状态 1:待支付 2:待接单 3:已接单 4:派送中 5:已完成 6:已取消
    private Integer status;

    // 支付状态 0:未支付 1:已支付 2:退款
    private Integer payStatus;

    // 支付方式 1:微信 2:支付宝
    private Integer payMethod;

    // 下单时间
    private LocalDateTime orderTime;

    // 结账时间
    private LocalDateTime checkoutTime;

    // 支付超时时间
    private LocalDateTime payExpireTime;

    // 备注
    private String remark;

    // 地址簿ID
    private Long addressBookId;

    // 收货人
    private String consignee;

    // 手机号
    private String phone;

    // 地址
    private String address;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 创建人
    private Long createUser;

    // 更新人
    private Long updateUser;
}