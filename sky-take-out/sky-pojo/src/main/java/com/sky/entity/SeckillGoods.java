package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ��ɱ��Ʒʵ����
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ��Ʒ״̬����
     */
    public static final Integer OFFLINE = 0; // �¼�
    public static final Integer ONLINE = 1;  // �ϼ�

    private Long id;

    // �ID
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

    // ��Ʒ״̬ 0:�¼� 1:�ϼ�
    private Integer status;

    // �汾�ţ��ֹ�����
    private Integer version;

    // ����ʱ��
    private LocalDateTime createTime;

    // ����ʱ��
    private LocalDateTime updateTime;

    // ������
    private Long createUser;

    // ������
    private Long updateUser;
}