package com.sky.service.impl;

import com.sky.entity.SeckillGoods;
import com.sky.mapper.SeckillGoodsMapper;
import com.sky.service.SeckillStockCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀库存缓存服务实现类
 */
@Service
@Slf4j
public class SeckillStockCacheServiceImpl implements SeckillStockCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    private static final String STOCK_CACHE_PREFIX = "seckill:stock:";
    private static final String ACTIVITY_STOCK_PREFIX = "seckill:activity:stock:";
    private static final String LOW_STOCK_CACHE_KEY = "seckill:low_stock:";
    
    private static final int CACHE_EXPIRE_MINUTES = 30; // 缓存过期时间30分钟
    private static final int LOW_STOCK_CACHE_MINUTES = 5; // 低库存缓存5分钟

    /**
     * 预热库存缓存
     * @param activityId 活动ID
     */
    @Override
    public void warmUpStockCache(Long activityId) {
        try {
            log.info("开始预热库存缓存，活动ID：{}", activityId);
            
            // 1. 查询活动下的所有秒杀商品
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.getByActivityId(activityId);
            
            // 2. 将库存信息存入缓存
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                String cacheKey = STOCK_CACHE_PREFIX + seckillGoods.getId();
                
                // 创建缓存对象，只存储关键信息以节省内存
                StockCacheInfo cacheInfo = new StockCacheInfo();
                cacheInfo.setId(seckillGoods.getId());
                cacheInfo.setAvailableStock(seckillGoods.getAvailableStock());
                cacheInfo.setSoldCount(seckillGoods.getSoldCount());
                cacheInfo.setTotalStock(seckillGoods.getTotalStock());
                cacheInfo.setVersion(seckillGoods.getVersion().longValue());
                cacheInfo.setLimitCount(seckillGoods.getLimitCount());
                
                redisTemplate.opsForValue().set(cacheKey, cacheInfo, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            }
            
            // 3. 记录活动的缓存预热信息
            String activityCacheKey = ACTIVITY_STOCK_PREFIX + activityId;
            redisTemplate.opsForValue().set(activityCacheKey, System.currentTimeMillis(), CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            log.info("库存缓存预热完成，活动ID：{}，商品数量：{}", activityId, seckillGoodsList.size());
            
        } catch (Exception e) {
            log.error("预热库存缓存失败，活动ID：{}", activityId, e);
        }
    }

    /**
     * 获取缓存中的库存信息
     * @param seckillGoodsId 秒杀商品ID
     * @return 库存信息
     */
    @Override
    public SeckillGoods getCachedStock(Long seckillGoodsId) {
        try {
            String cacheKey = STOCK_CACHE_PREFIX + seckillGoodsId;
            StockCacheInfo cacheInfo = (StockCacheInfo) redisTemplate.opsForValue().get(cacheKey);
            
            if (cacheInfo != null) {
                // 将缓存信息转换为SeckillGoods对象
                SeckillGoods seckillGoods = new SeckillGoods();
                seckillGoods.setId(cacheInfo.getId());
                seckillGoods.setAvailableStock(cacheInfo.getAvailableStock());
                seckillGoods.setSoldCount(cacheInfo.getSoldCount());
                seckillGoods.setTotalStock(cacheInfo.getTotalStock());
                seckillGoods.setVersion(cacheInfo.getVersion().intValue());
                seckillGoods.setLimitCount(cacheInfo.getLimitCount());
                
                return seckillGoods;
            }
            
            // 缓存未命中，从数据库查询并更新缓存
            SeckillGoods seckillGoods = seckillGoodsMapper.getStockSnapshot(seckillGoodsId);
            if (seckillGoods != null) {
                updateCachedStock(seckillGoodsId, seckillGoods.getAvailableStock(), 
                                seckillGoods.getSoldCount(), seckillGoods.getVersion().longValue());
            }
            
            return seckillGoods;
            
        } catch (Exception e) {
            log.error("获取缓存库存失败，商品ID：{}", seckillGoodsId, e);
            // 缓存失败时直接查询数据库
            return seckillGoodsMapper.getStockSnapshot(seckillGoodsId);
        }
    }

    /**
     * 更新缓存中的库存信息
     * @param seckillGoodsId 秒杀商品ID
     * @param availableStock 可用库存
     * @param soldCount 已售数量
     * @param version 版本号
     */
    @Override
    public void updateCachedStock(Long seckillGoodsId, Integer availableStock, Integer soldCount, Long version) {
        try {
            String cacheKey = STOCK_CACHE_PREFIX + seckillGoodsId;
            StockCacheInfo existingInfo = (StockCacheInfo) redisTemplate.opsForValue().get(cacheKey);
            
            if (existingInfo != null) {
                existingInfo.setAvailableStock(availableStock);
                existingInfo.setSoldCount(soldCount);
                existingInfo.setVersion(version);
                
                redisTemplate.opsForValue().set(cacheKey, existingInfo, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            } else {
                // 如果缓存不存在，创建新的缓存条目
                SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
                if (seckillGoods != null) {
                    StockCacheInfo cacheInfo = new StockCacheInfo();
                    cacheInfo.setId(seckillGoodsId);
                    cacheInfo.setAvailableStock(availableStock);
                    cacheInfo.setSoldCount(soldCount);
                    cacheInfo.setTotalStock(seckillGoods.getTotalStock());
                    cacheInfo.setVersion(version);
                    cacheInfo.setLimitCount(seckillGoods.getLimitCount());
                    
                    redisTemplate.opsForValue().set(cacheKey, cacheInfo, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
                }
            }
            
        } catch (Exception e) {
            log.error("更新缓存库存失败，商品ID：{}", seckillGoodsId, e);
        }
    }

    /**
     * 删除库存缓存
     * @param seckillGoodsId 秒杀商品ID
     */
    @Override
    public void evictStockCache(Long seckillGoodsId) {
        try {
            String cacheKey = STOCK_CACHE_PREFIX + seckillGoodsId;
            redisTemplate.delete(cacheKey);
            log.debug("删除库存缓存，商品ID：{}", seckillGoodsId);
        } catch (Exception e) {
            log.error("删除库存缓存失败，商品ID：{}", seckillGoodsId, e);
        }
    }

    /**
     * 批量删除库存缓存
     * @param activityId 活动ID
     */
    @Override
    public void evictActivityStockCache(Long activityId) {
        try {
            // 查询活动下的所有商品
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.getByActivityId(activityId);
            
            // 删除每个商品的库存缓存
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                evictStockCache(seckillGoods.getId());
            }
            
            // 删除活动缓存标记
            String activityCacheKey = ACTIVITY_STOCK_PREFIX + activityId;
            redisTemplate.delete(activityCacheKey);
            
            log.info("批量删除活动库存缓存完成，活动ID：{}", activityId);
            
        } catch (Exception e) {
            log.error("批量删除活动库存缓存失败，活动ID：{}", activityId, e);
        }
    }

    /**
     * 检查库存是否充足（先查缓存）
     * @param seckillGoodsId 秒杀商品ID
     * @param quantity 需要的数量
     * @return 是否充足
     */
    @Override
    public boolean checkStockAvailable(Long seckillGoodsId, Integer quantity) {
        try {
            SeckillGoods cachedGoods = getCachedStock(seckillGoodsId);
            return cachedGoods != null && cachedGoods.getAvailableStock() >= quantity;
        } catch (Exception e) {
            log.error("检查库存可用性失败，商品ID：{}，数量：{}", seckillGoodsId, quantity, e);
            // 异常时查询数据库
            SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
            return seckillGoods != null && seckillGoods.getAvailableStock() >= quantity;
        }
    }

    /**
     * 获取低库存商品列表（带缓存）
     * @param threshold 库存阈值
     * @param activityId 活动ID
     * @return 低库存商品列表
     */
    @Override
    public List<SeckillGoods> getLowStockGoods(Integer threshold, Long activityId) {
        try {
            String cacheKey = LOW_STOCK_CACHE_KEY + threshold + ":" + (activityId != null ? activityId : "all");
            
            @SuppressWarnings("unchecked")
            List<SeckillGoods> cachedResult = (List<SeckillGoods>) redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedResult != null) {
                return cachedResult;
            }
            
            // 缓存未命中，查询数据库
            List<SeckillGoods> lowStockGoods = seckillGoodsMapper.getLowStockGoods(threshold, activityId);
            
            // 存入缓存（较短的过期时间，因为库存变化频繁）
            redisTemplate.opsForValue().set(cacheKey, lowStockGoods, LOW_STOCK_CACHE_MINUTES, TimeUnit.MINUTES);
            
            return lowStockGoods;
            
        } catch (Exception e) {
            log.error("获取低库存商品列表失败，阈值：{}，活动ID：{}", threshold, activityId, e);
            return seckillGoodsMapper.getLowStockGoods(threshold, activityId);
        }
    }

    /**
     * 刷新所有库存缓存
     */
    @Override
    public void refreshAllStockCache() {
        try {
            log.info("开始刷新所有库存缓存");
            
            // 删除所有库存相关的缓存
            redisTemplate.delete(redisTemplate.keys(STOCK_CACHE_PREFIX + "*"));
            redisTemplate.delete(redisTemplate.keys(ACTIVITY_STOCK_PREFIX + "*"));
            redisTemplate.delete(redisTemplate.keys(LOW_STOCK_CACHE_KEY + "*"));
            
            log.info("所有库存缓存刷新完成");
            
        } catch (Exception e) {
            log.error("刷新所有库存缓存失败", e);
        }
    }

    /**
     * 库存缓存信息内部类
     */
    private static class StockCacheInfo {
        private Long id;
        private Integer availableStock;
        private Integer soldCount;
        private Integer totalStock;
        private Long version;
        private Integer limitCount;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Integer getAvailableStock() { return availableStock; }
        public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
        
        public Integer getSoldCount() { return soldCount; }
        public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }
        
        public Integer getTotalStock() { return totalStock; }
        public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
        
        public Long getVersion() { return version; }
        public void setVersion(Long version) { this.version = version; }
        
        public Integer getLimitCount() { return limitCount; }
        public void setLimitCount(Integer limitCount) { this.limitCount = limitCount; }
    }
}
