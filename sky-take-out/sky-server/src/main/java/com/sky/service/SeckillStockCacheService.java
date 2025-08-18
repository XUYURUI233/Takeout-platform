package com.sky.service;

import com.sky.entity.SeckillGoods;

import java.util.List;

/**
 * ��ɱ��滺�����ӿ�
 */
public interface SeckillStockCacheService {

    /**
     * Ԥ�ȿ�滺��
     * @param activityId �ID
     */
    void warmUpStockCache(Long activityId);

    /**
     * ��ȡ�����еĿ����Ϣ
     * @param seckillGoodsId ��ɱ��ƷID
     * @return �����Ϣ
     */
    SeckillGoods getCachedStock(Long seckillGoodsId);

    /**
     * ���»����еĿ����Ϣ
     * @param seckillGoodsId ��ɱ��ƷID
     * @param availableStock ���ÿ��
     * @param soldCount ��������
     * @param version �汾��
     */
    void updateCachedStock(Long seckillGoodsId, Integer availableStock, Integer soldCount, Long version);

    /**
     * ɾ����滺��
     * @param seckillGoodsId ��ɱ��ƷID
     */
    void evictStockCache(Long seckillGoodsId);

    /**
     * ����ɾ����滺��
     * @param activityId �ID
     */
    void evictActivityStockCache(Long activityId);

    /**
     * ������Ƿ���㣨�Ȳ黺�棩
     * @param seckillGoodsId ��ɱ��ƷID
     * @param quantity ��Ҫ������
     * @return �Ƿ����
     */
    boolean checkStockAvailable(Long seckillGoodsId, Integer quantity);

    /**
     * ��ȡ�Ϳ����Ʒ�б������棩
     * @param threshold �����ֵ
     * @param activityId �ID
     * @return �Ϳ����Ʒ�б�
     */
    List<SeckillGoods> getLowStockGoods(Integer threshold, Long activityId);

    /**
     * ˢ�����п�滺��
     */
    void refreshAllStockCache();
}



