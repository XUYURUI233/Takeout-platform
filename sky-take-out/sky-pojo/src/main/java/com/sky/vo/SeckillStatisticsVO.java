package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ��ɱͳ��VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

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