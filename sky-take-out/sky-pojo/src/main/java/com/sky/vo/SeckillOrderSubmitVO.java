package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ��ɱ�����ύVO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderSubmitVO implements Serializable {

    // ����ID
    private Long orderId;

    // ������
    private String orderNumber;

    // ֧����ʱʱ��
    private LocalDateTime payExpireTime;

    // �����ܽ��
    private BigDecimal totalAmount;

    // ֧��ʱ�����ƣ��룩
    private Integer payTimeLimit;
}



