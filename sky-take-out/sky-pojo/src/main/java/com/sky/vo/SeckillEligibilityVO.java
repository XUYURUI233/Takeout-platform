package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 秒杀购买资格检查VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillEligibilityVO implements Serializable {

    // 是否可购买
    private Boolean canPurchase;

    // 剩余购买额度
    private Integer remainingQuota;

    // 限购数量
    private Integer limitCount;

    // 用户已购买数量
    private Integer userPurchased;

    // 可用库存
    private Integer availableStock;
}



