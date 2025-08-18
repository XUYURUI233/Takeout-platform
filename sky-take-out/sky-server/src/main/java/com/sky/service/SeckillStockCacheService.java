package com.sky.service;

import com.sky.entity.SeckillGoods;

import java.util.List;

/**
 * 秒杀库存缓存服务接口
 */
public interface SeckillStockCacheService {

    /**
     * 预热库存缓存
     * @param activityId 活动ID
     */
    void warmUpStockCache(Long activityId);

    /**
     * 获取缓存中的库存信息
     * @param seckillGoodsId 秒杀商品ID
     * @return 库存信息
     */
    SeckillGoods getCachedStock(Long seckillGoodsId);

    /**
     * 更新缓存中的库存信息
     * @param seckillGoodsId 秒杀商品ID
     * @param availableStock 可用库存
     * @param soldCount 已售数量
     * @param version 版本号
     */
    void updateCachedStock(Long seckillGoodsId, Integer availableStock, Integer soldCount, Long version);

    /**
     * 删除库存缓存
     * @param seckillGoodsId 秒杀商品ID
     */
    void evictStockCache(Long seckillGoodsId);

    /**
     * 批量删除库存缓存
     * @param activityId 活动ID
     */
    void evictActivityStockCache(Long activityId);

    /**
     * 检查库存是否充足（先查缓存）
     * @param seckillGoodsId 秒杀商品ID
     * @param quantity 需要的数量
     * @return 是否充足
     */
    boolean checkStockAvailable(Long seckillGoodsId, Integer quantity);

    /**
     * 获取低库存商品列表（带缓存）
     * @param threshold 库存阈值
     * @param activityId 活动ID
     * @return 低库存商品列表
     */
    List<SeckillGoods> getLowStockGoods(Integer threshold, Long activityId);

    /**
     * 刷新所有库存缓存
     */
    void refreshAllStockCache();
}



