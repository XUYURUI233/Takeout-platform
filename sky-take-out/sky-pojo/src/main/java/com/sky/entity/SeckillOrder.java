package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ��ɱ����
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //������
    private String orderNumber;

    //��ɱ�ID
    private Long seckillId;

    //��ɱ��Ʒ��ID
    private Long itemId;

    //�û�ID
    private Long userId;

    //��ƷID
    private Long dishId;

    //��Ʒ����
    private String dishName;

    //��������
    private Integer quantity;

    //��ɱ�۸�
    private BigDecimal seckillPrice;

    //�ܽ��
    private BigDecimal totalAmount;

    //��ַID
    private Long addressId;

    //��ע
    private String remark;

    //����״̬ 1-��֧�� 2-��֧�� 3-��ȡ��
    private Integer status;

    //֧����ʽ 1-΢�� 2-֧����
    private Integer payMethod;

    //֧��ʱ��
    private LocalDateTime payTime;

    //֧����ֹʱ��
    private LocalDateTime payDeadline;

    //����ʱ��
    private LocalDateTime createTime;

    //����ʱ��
    private LocalDateTime updateTime;
}

