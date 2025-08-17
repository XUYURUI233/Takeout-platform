package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ��ɱ����VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderVO implements Serializable {

    // ����ID
    private Long id;

    // ������
    private String number;

    // �û�ID
    private Long userId;

    // �µ�ʱ��
    private LocalDateTime orderTime;

    // ����ʱ��
    private LocalDateTime checkoutTime;

    // ֧����ʽ
    private Integer payMethod;

    // ֧��״̬
    private Integer payStatus;

    // �������
    private BigDecimal amount;

    // ��ע
    private String remark;

    // �ֻ���
    private String phone;

    // ��ַ
    private String address;

    // �ջ���
    private String consignee;

    // Ԥ���ʹ�ʱ��
    private LocalDateTime estimatedDeliveryTime;

    // ��ɱ��Ϣ
    private SeckillInfo seckillInfo;

    // ��ɱ��Ϣ�ڲ���
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeckillInfo implements Serializable {
        
        // �ID
        private Long activityId;
        
        // �����
        private String activityName;
        
        // ��ɱ��ƷID
        private Long seckillGoodsId;
        
        // ��Ʒ����
        private String goodsName;
        
        // ��ƷͼƬ
        private String goodsImage;
        
        // ԭ��
        private BigDecimal originalPrice;
        
        // ��ɱ��
        private BigDecimal seckillPrice;
        
        // ��������
        private Integer quantity;
    }
}