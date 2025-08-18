package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ��ɱ�����ύ���VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderSubmitVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ����ID
    private Long orderId;

    // ������
    private String orderNumber;

    // ֧����ʱʱ��
    private LocalDateTime payExpireTime;

    // �����ܽ��
    private BigDecimal totalAmount;

    // ֧��ʱ�ޣ��룩
    private Integer payTimeLimit;
}