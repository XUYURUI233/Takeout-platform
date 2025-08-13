package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ????VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderVO {

    private String orderId;

    private Long seckillId;

    private String seckillName;

    private String dishName;

    private Integer quantity;

    private BigDecimal seckillPrice;

    private BigDecimal totalAmount;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime payDeadline;
}

