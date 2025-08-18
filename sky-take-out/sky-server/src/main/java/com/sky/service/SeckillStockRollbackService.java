package com.sky.service;

import com.sky.entity.SeckillStockLog;

import java.util.List;

/**
 * ��ɱ���ع�Service�ӿ�
 */
public interface SeckillStockRollbackService {

    /**
     * ���ݶ���ID�ع����
     * @param orderId ����ID
     */
    void rollbackByOrderId(Long orderId);

    /**
     * ������ƷID�ع�ָ�������Ŀ��
     * @param seckillGoodsId ��ɱ��ƷID
     * @param quantity �ع�����
     * @param reason �ع�ԭ��
     */
    void rollbackByGoodsId(Long seckillGoodsId, Integer quantity, String reason);

    /**
     * ��ȡ��������־
     * @param seckillGoodsId ��ɱ��ƷID
     * @return �����־�б�
     */
    List<SeckillStockLog> getStockLogs(Long seckillGoodsId);

    /**
     * �����ع����
     * @param orderIds ����ID�б�
     */
    void batchRollback(List<Long> orderIds);

    /**
     * �ع��������
     * @param orderId ����ID
     * @param userId �û�ID����Ϊnull��
     * @param reason �ع�ԭ��
     * @return �Ƿ�ع��ɹ�
     */
    boolean rollbackOrderStock(Long orderId, Long userId, String reason);

    /**
     * �����ع����
     * @param seckillGoodsId ��ɱ��ƷID
     * @param maxRollbackCount ���ع�����
     * @param reason �ع�ԭ��
     * @return ʵ�ʻع�����
     */
    int batchRollbackStock(Long seckillGoodsId, Integer maxRollbackCount, String reason);

    /**
     * ����ʱ�䷶Χ�ع����
     * @param seckillGoodsId ��ɱ��ƷID
     * @param startTime ��ʼʱ��
     * @param endTime ����ʱ��
     * @param reason �ع�ԭ��
     * @return �ع�������
     */
    int rollbackStockByTimeRange(Long seckillGoodsId, String startTime, String endTime, String reason);

    /**
     * �����һ����
     * @param seckillGoodsId ��ɱ��ƷID
     * @return һ���Լ����
     */
    Object checkStockConsistency(Long seckillGoodsId);

    /**
     * �޸���治һ������
     * @param seckillGoodsId ��ɱ��ƷID
     * @param targetStock Ŀ����ֵ
     * @param reason �޸�ԭ��
     * @return �Ƿ��޸��ɹ�
     */
    boolean fixStockInconsistency(Long seckillGoodsId, Integer targetStock, String reason);
}