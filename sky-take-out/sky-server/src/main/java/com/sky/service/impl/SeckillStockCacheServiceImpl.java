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
 * ��ɱ��滺�����ʵ����
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
    
    private static final int CACHE_EXPIRE_MINUTES = 30; // �������ʱ��30����
    private static final int LOW_STOCK_CACHE_MINUTES = 5; // �Ϳ�滺��5����

    /**
     * Ԥ�ȿ�滺��
     * @param activityId �ID
     */
    @Override
    public void warmUpStockCache(Long activityId) {
        try {
            log.info("��ʼԤ�ȿ�滺�棬�ID��{}", activityId);
            
            // 1. ��ѯ��µ�������ɱ��Ʒ
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.getByActivityId(activityId);
            
            // 2. �������Ϣ���뻺��
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                String cacheKey = STOCK_CACHE_PREFIX + seckillGoods.getId();
                
                // �����������ֻ�洢�ؼ���Ϣ�Խ�ʡ�ڴ�
                StockCacheInfo cacheInfo = new StockCacheInfo();
                cacheInfo.setId(seckillGoods.getId());
                cacheInfo.setAvailableStock(seckillGoods.getAvailableStock());
                cacheInfo.setSoldCount(seckillGoods.getSoldCount());
                cacheInfo.setTotalStock(seckillGoods.getTotalStock());
                cacheInfo.setVersion(seckillGoods.getVersion().longValue());
                cacheInfo.setLimitCount(seckillGoods.getLimitCount());
                
                redisTemplate.opsForValue().set(cacheKey, cacheInfo, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            }
            
            // 3. ��¼��Ļ���Ԥ����Ϣ
            String activityCacheKey = ACTIVITY_STOCK_PREFIX + activityId;
            redisTemplate.opsForValue().set(activityCacheKey, System.currentTimeMillis(), CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            log.info("��滺��Ԥ����ɣ��ID��{}����Ʒ������{}", activityId, seckillGoodsList.size());
            
        } catch (Exception e) {
            log.error("Ԥ�ȿ�滺��ʧ�ܣ��ID��{}", activityId, e);
        }
    }

    /**
     * ��ȡ�����еĿ����Ϣ
     * @param seckillGoodsId ��ɱ��ƷID
     * @return �����Ϣ
     */
    @Override
    public SeckillGoods getCachedStock(Long seckillGoodsId) {
        try {
            String cacheKey = STOCK_CACHE_PREFIX + seckillGoodsId;
            StockCacheInfo cacheInfo = (StockCacheInfo) redisTemplate.opsForValue().get(cacheKey);
            
            if (cacheInfo != null) {
                // ��������Ϣת��ΪSeckillGoods����
                SeckillGoods seckillGoods = new SeckillGoods();
                seckillGoods.setId(cacheInfo.getId());
                seckillGoods.setAvailableStock(cacheInfo.getAvailableStock());
                seckillGoods.setSoldCount(cacheInfo.getSoldCount());
                seckillGoods.setTotalStock(cacheInfo.getTotalStock());
                seckillGoods.setVersion(cacheInfo.getVersion().intValue());
                seckillGoods.setLimitCount(cacheInfo.getLimitCount());
                
                return seckillGoods;
            }
            
            // ����δ���У������ݿ��ѯ�����»���
            SeckillGoods seckillGoods = seckillGoodsMapper.getStockSnapshot(seckillGoodsId);
            if (seckillGoods != null) {
                updateCachedStock(seckillGoodsId, seckillGoods.getAvailableStock(), 
                                seckillGoods.getSoldCount(), seckillGoods.getVersion().longValue());
            }
            
            return seckillGoods;
            
        } catch (Exception e) {
            log.error("��ȡ������ʧ�ܣ���ƷID��{}", seckillGoodsId, e);
            // ����ʧ��ʱֱ�Ӳ�ѯ���ݿ�
            return seckillGoodsMapper.getStockSnapshot(seckillGoodsId);
        }
    }

    /**
     * ���»����еĿ����Ϣ
     * @param seckillGoodsId ��ɱ��ƷID
     * @param availableStock ���ÿ��
     * @param soldCount ��������
     * @param version �汾��
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
                // ������治���ڣ������µĻ�����Ŀ
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
            log.error("���»�����ʧ�ܣ���ƷID��{}", seckillGoodsId, e);
        }
    }

    /**
     * ɾ����滺��
     * @param seckillGoodsId ��ɱ��ƷID
     */
    @Override
    public void evictStockCache(Long seckillGoodsId) {
        try {
            String cacheKey = STOCK_CACHE_PREFIX + seckillGoodsId;
            redisTemplate.delete(cacheKey);
            log.debug("ɾ����滺�棬��ƷID��{}", seckillGoodsId);
        } catch (Exception e) {
            log.error("ɾ����滺��ʧ�ܣ���ƷID��{}", seckillGoodsId, e);
        }
    }

    /**
     * ����ɾ����滺��
     * @param activityId �ID
     */
    @Override
    public void evictActivityStockCache(Long activityId) {
        try {
            // ��ѯ��µ�������Ʒ
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.getByActivityId(activityId);
            
            // ɾ��ÿ����Ʒ�Ŀ�滺��
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                evictStockCache(seckillGoods.getId());
            }
            
            // ɾ���������
            String activityCacheKey = ACTIVITY_STOCK_PREFIX + activityId;
            redisTemplate.delete(activityCacheKey);
            
            log.info("����ɾ�����滺����ɣ��ID��{}", activityId);
            
        } catch (Exception e) {
            log.error("����ɾ�����滺��ʧ�ܣ��ID��{}", activityId, e);
        }
    }

    /**
     * ������Ƿ���㣨�Ȳ黺�棩
     * @param seckillGoodsId ��ɱ��ƷID
     * @param quantity ��Ҫ������
     * @return �Ƿ����
     */
    @Override
    public boolean checkStockAvailable(Long seckillGoodsId, Integer quantity) {
        try {
            SeckillGoods cachedGoods = getCachedStock(seckillGoodsId);
            return cachedGoods != null && cachedGoods.getAvailableStock() >= quantity;
        } catch (Exception e) {
            log.error("����������ʧ�ܣ���ƷID��{}��������{}", seckillGoodsId, quantity, e);
            // �쳣ʱ��ѯ���ݿ�
            SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
            return seckillGoods != null && seckillGoods.getAvailableStock() >= quantity;
        }
    }

    /**
     * ��ȡ�Ϳ����Ʒ�б������棩
     * @param threshold �����ֵ
     * @param activityId �ID
     * @return �Ϳ����Ʒ�б�
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
            
            // ����δ���У���ѯ���ݿ�
            List<SeckillGoods> lowStockGoods = seckillGoodsMapper.getLowStockGoods(threshold, activityId);
            
            // ���뻺�棨�϶̵Ĺ���ʱ�䣬��Ϊ���仯Ƶ����
            redisTemplate.opsForValue().set(cacheKey, lowStockGoods, LOW_STOCK_CACHE_MINUTES, TimeUnit.MINUTES);
            
            return lowStockGoods;
            
        } catch (Exception e) {
            log.error("��ȡ�Ϳ����Ʒ�б�ʧ�ܣ���ֵ��{}���ID��{}", threshold, activityId, e);
            return seckillGoodsMapper.getLowStockGoods(threshold, activityId);
        }
    }

    /**
     * ˢ�����п�滺��
     */
    @Override
    public void refreshAllStockCache() {
        try {
            log.info("��ʼˢ�����п�滺��");
            
            // ɾ�����п����صĻ���
            redisTemplate.delete(redisTemplate.keys(STOCK_CACHE_PREFIX + "*"));
            redisTemplate.delete(redisTemplate.keys(ACTIVITY_STOCK_PREFIX + "*"));
            redisTemplate.delete(redisTemplate.keys(LOW_STOCK_CACHE_KEY + "*"));
            
            log.info("���п�滺��ˢ�����");
            
        } catch (Exception e) {
            log.error("ˢ�����п�滺��ʧ��", e);
        }
    }

    /**
     * ��滺����Ϣ�ڲ���
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
