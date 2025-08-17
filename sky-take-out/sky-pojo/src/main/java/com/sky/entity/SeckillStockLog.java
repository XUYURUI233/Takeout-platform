package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ��ɱ��������־��
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillStockLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // ��ɱ��ƷID
    private Long seckillGoodsId;

    // �û�ID
    private Long userId;

    // ����ID
    private Long orderId;

    // �������� 1:�ۼ���� 2:�ͷſ�� 3:��ʼ�����
    private Integer operationType;

    // ��������
    private Integer quantity;

    // ����ǰ���
    private Integer beforeStock;

    // ��������
    private Integer afterStock;

    // ����ʱ�İ汾��
    private Integer version;

    // ��ע
    private String remark;

    // ����ʱ��
    private LocalDateTime createTime;
}



