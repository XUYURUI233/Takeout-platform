package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ��ɱ�����־ʵ����
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillStockLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * �������ͳ���
     */
    public static final Integer DEDUCT_STOCK = 1;  // �ۼ����
    public static final Integer RELEASE_STOCK = 2; // �ͷſ��

    private Long id;

    // ��ɱ��ƷID
    private Long seckillGoodsId;

    // �������� 1:�ۼ� 2:�ͷ�
    private Integer operationType;

    // ��������
    private Integer quantity;

    // ����ǰ���
    private Integer beforeStock;

    // ��������
    private Integer afterStock;

    // ��������ID
    private Long orderId;

    // ��������
    private String description;

    // ����ʱ��
    private LocalDateTime createTime;
}