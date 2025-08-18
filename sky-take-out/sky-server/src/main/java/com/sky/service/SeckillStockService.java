package com.sky.service;

import com.sky.result.PageResult;

import java.time.LocalDateTime;

/**
 * ��ɱ���Service�ӿ�
 */
public interface SeckillStockService {

    /**
     * �ۼ���棨����־��¼��
     * @param seckillGoodsId ��ɱ��ƷID
     * @param quantity �ۼ�����
     * @param version �汾�ţ��ֹ�����
     * @param orderId ����ID
     * @param orderNumber ������
     * @param userId �û�ID
     * @return �Ƿ�ۼ��ɹ�
     */
    boolean deductStock(Long seckillGoodsId, Integer quantity, Long version, 
                       Long orderId, String orderNumber, Long userId);

    /**
     * �ͷſ�棨����־��¼��
     * @param seckillGoodsId ��ɱ��ƷID
     * @param quantity �ͷ�����
     * @param orderId ����ID
     * @param orderNumber ������
     * @param userId �û�ID
     * @param reason �ͷ�ԭ��
     * @return �Ƿ��ͷųɹ�
     */
    boolean releaseStock(Long seckillGoodsId, Integer quantity, Long orderId, 
                        String orderNumber, Long userId, String reason);

    /**
     * ��ҳ��ѯ�����־
     * @param page ҳ��
     * @param pageSize ҳ��С
     * @param seckillGoodsId ��ɱ��ƷID
     * @param operationType ��������
     * @param startTime ��ʼʱ��
     * @param endTime ����ʱ��
     * @return ��ҳ���
     */
    PageResult pageQueryStockLog(int page, int pageSize, Long seckillGoodsId, 
                                Integer operationType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * ��ȡ���ͳ����Ϣ
     * @param seckillGoodsId ��ɱ��ƷID
     * @return ͳ����Ϣ
     */
    Object getStockStatistics(Long seckillGoodsId);
}