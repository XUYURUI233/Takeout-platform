package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ��ɱ�ͳ��VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillStatisticsVO implements Serializable {

    // �ܶ�����
    private Integer totalOrders;

    // �ɹ�������
    private Integer successOrders;

    // �ܽ��
    private BigDecimal totalAmount;

    // �ܿ��
    private Integer totalStock;

    // ��������
    private Integer soldCount;

    // �ɹ���
    private Double successRate;
}



