package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ��ɱ��Ʒ��
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // ��ɱ�ID
    private Long activityId;

    // ��Ʒ���� 1:��Ʒ 2:�ײ�
    private Integer goodsType;

    // ��ƷID����ƷID���ײ�ID��
    private Long goodsId;

    // ��Ʒ����
    private String goodsName;

    // ��ƷͼƬ
    private String goodsImage;

    // ԭ��
    private BigDecimal originalPrice;

    // ��ɱ��
    private BigDecimal seckillPrice;

    // �ܿ��
    private Integer totalStock;

    // ���ÿ��
    private Integer availableStock;

    // ��������
    private Integer soldCount;

    // �޹�����
    private Integer limitCount;

    // �汾�ţ��ֹ�����ֹ������
    private Integer version;

    // ״̬ 0:�¼� 1:�ϼ�
    private Integer status;

    // ����ʱ��
    private LocalDateTime createTime;

    // ����ʱ��
    private LocalDateTime updateTime;

    // ������
    private Long createUser;

    // �޸���
    private Long updateUser;
}