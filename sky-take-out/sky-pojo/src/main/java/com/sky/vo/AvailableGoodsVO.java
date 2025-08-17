package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ������ƷVO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableGoodsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ��ƷID����ƷID���ײ�ID��
    private Long id;
    
    // ��Ʒ����
    private String name;
    
    // ��ƷͼƬ
    private String image;
    
    // ��Ʒ�۸�
    private BigDecimal price;
    
    // ��Ʒ���� 1:��Ʒ 2:�ײ�
    private Integer type;
    
    // ��������
    private String categoryName;
}


