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

    private static final long serialVersionUID = 1L;

    private Long id;

    // �ID
    private Long activityId;

    // �����
    private String activityName;

    // ��Ʒ���� 1:��Ʒ 2:�ײ�
    private Integer goodsType;

    // ��ƷID
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

    // ��Ʒ״̬ 0:�¼� 1:�ϼ�
    private Integer status;

    // �汾�ţ��ֹ�����
    private Integer version;

    // �û��ѹ�������
    private Integer userPurchased;

    // �Ƿ���Թ���
    private Boolean canPurchase;

    // ��Ʒ����
    private String description;

    // ���ʼʱ��
    private LocalDateTime startTime;

    // �����ʱ��
    private LocalDateTime endTime;

    // ʣ��ʱ�䣨�룩
    private Long remainingTime;

    // ��������
    private String categoryName;
}