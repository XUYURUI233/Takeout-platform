package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * ��ɱ�����ʸ���VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillEligibilityVO implements Serializable {

    // �Ƿ�ɹ���
    private Boolean canPurchase;

    // ʣ�๺����
    private Integer remainingQuota;

    // �޹�����
    private Integer limitCount;

    // �û��ѹ�������
    private Integer userPurchased;

    // ���ÿ��
    private Integer availableStock;
}



