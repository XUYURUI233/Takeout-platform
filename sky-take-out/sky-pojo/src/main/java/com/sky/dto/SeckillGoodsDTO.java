package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ��ɱ��ƷDTO
 */
@Data
public class SeckillGoodsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

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

    // �޹�����
    private Integer limitCount;
}