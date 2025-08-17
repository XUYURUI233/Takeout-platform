package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ��ɱ��ƷVO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillGoodsVO implements Serializable {

    private Long id;
    
    // ǰ����Ʒѡ��Ի�����Ҫ���ֶ�
    private String name;
    private String image;
    private java.math.BigDecimal price;
    private Integer type;

    // ��ɱ�ID
    private Long activityId;

    // �����
    private String activityName;

    // ��Ʒ���� 1:��Ʒ 2:�ײ�
    private Integer goodsType;

    // ��ƷID����ƷID���ײ�ID��
    private Long goodsId;

    // ��Ʒ����
    private String goodsName;

    // ��ƷͼƬ
    private String goodsImage;

    // ��������
    private String categoryName;

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

    // �û��ѹ�������
    private Integer userPurchased;

    // �Ƿ�ɹ���
    private Boolean canPurchase;

    // ״̬ 0:�¼� 1:�ϼ�
    private Integer status;

    // ��ɱ��ʼʱ��
    private LocalDateTime startTime;

    // ��ɱ����ʱ��
    private LocalDateTime endTime;

    // ʣ��ʱ�䣨�룩
    private Long remainingTime;

    // ��Ʒ����
    private String description;
}