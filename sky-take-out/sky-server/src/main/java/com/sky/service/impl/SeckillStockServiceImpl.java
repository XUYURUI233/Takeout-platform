package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.entity.SeckillGoods;
import com.sky.entity.SeckillStockLog;
import com.sky.mapper.SeckillGoodsMapper;
import com.sky.mapper.SeckillStockLogMapper;
import com.sky.result.PageResult;
import com.sky.service.SeckillStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 秒杀库存服务实现类
 */
@Service
@Slf4j
public class SeckillStockServiceImpl implements SeckillStockService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillStockLogMapper seckillStockLogMapper;

    /**
     * 扣减库存（带日志记录）
     * @param seckillGoodsId 秒杀商品ID
     * @param quantity 扣减数量
     * @param version 版本号（乐观锁）
     * @param orderId 订单ID
     * @param orderNumber 订单号
     * @param userId 用户ID
     * @return 是否扣减成功
     */
    @Override
    @Transactional
    public boolean deductStock(Long seckillGoodsId, Integer quantity, Long version, 
                              Long orderId, String orderNumber, Long userId) {
        try {
            // 1. 获取扣减前的库存信息
            SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
            if (seckillGoods == null) {
                log.error("秒杀商品不存在，商品ID：{}", seckillGoodsId);
                return false;
            }

            Integer beforeStock = seckillGoods.getAvailableStock();
            
            // 2. 检查库存是否充足
            if (beforeStock < quantity) {
                log.warn("库存不足，商品ID：{}，当前库存：{}，需要扣减：{}", 
                        seckillGoodsId, beforeStock, quantity);
                recordStockLog(seckillGoodsId, SeckillStockLog.DEDUCT_STOCK, quantity, 
                              beforeStock, beforeStock, orderId, orderNumber, userId, 
                              "库存不足，扣减失败");
                return false;
            }

            // 3. 使用乐观锁扣减库存
            int updateCount = seckillGoodsMapper.deductStock(seckillGoodsId, quantity, version.intValue());
            
            if (updateCount > 0) {
                // 扣减成功，记录日志
                Integer afterStock = beforeStock - quantity;
                recordStockLog(seckillGoodsId, SeckillStockLog.DEDUCT_STOCK, quantity, 
                              beforeStock, afterStock, orderId, orderNumber, userId, 
                              "库存扣减成功");
                
                log.info("库存扣减成功，商品ID：{}，扣减数量：{}，剩余库存：{}", 
                        seckillGoodsId, quantity, afterStock);
                return true;
            } else {
                // 扣减失败（版本号不匹配或库存不足）
                recordStockLog(seckillGoodsId, SeckillStockLog.DEDUCT_STOCK, quantity, 
                              beforeStock, beforeStock, orderId, orderNumber, userId, 
                              "乐观锁失败，可能是并发冲突");
                
                log.warn("库存扣减失败，可能是并发冲突，商品ID：{}，版本号：{}", 
                        seckillGoodsId, version);
                return false;
            }
            
        } catch (Exception e) {
            log.error("扣减库存异常，商品ID：{}，数量：{}", seckillGoodsId, quantity, e);
            // 记录异常日志
            try {
                SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
                Integer beforeStock = seckillGoods != null ? seckillGoods.getAvailableStock() : 0;
                recordStockLog(seckillGoodsId, SeckillStockLog.DEDUCT_STOCK, quantity, 
                              beforeStock, beforeStock, orderId, orderNumber, userId, 
                              "扣减库存异常：" + e.getMessage());
            } catch (Exception logException) {
                log.error("记录库存异常日志失败", logException);
            }
            return false;
        }
    }

    /**
     * 释放库存（带日志记录）
     * @param seckillGoodsId 秒杀商品ID
     * @param quantity 释放数量
     * @param orderId 订单ID
     * @param orderNumber 订单号
     * @param userId 用户ID
     * @param reason 释放原因
     * @return 是否释放成功
     */
    @Override
    @Transactional
    public boolean releaseStock(Long seckillGoodsId, Integer quantity, 
                               Long orderId, String orderNumber, Long userId, String reason) {
        try {
            // 1. 获取当前库存信息
            SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
            if (seckillGoods == null) {
                log.error("秒杀商品不存在，商品ID：{}", seckillGoodsId);
                return false;
            }

            Integer beforeStock = seckillGoods.getAvailableStock();
            Integer afterStock = beforeStock + quantity;
            
            // 2. 检查释放后的库存不能超过总库存
            if (afterStock > seckillGoods.getTotalStock()) {
                log.warn("释放库存超过总库存限制，商品ID：{}，当前库存：{}，释放数量：{}，总库存：{}", 
                        seckillGoodsId, beforeStock, quantity, seckillGoods.getTotalStock());
                afterStock = seckillGoods.getTotalStock();
                quantity = afterStock - beforeStock;
            }

            // 3. 更新库存
            SeckillGoods updateGoods = SeckillGoods.builder()
                    .id(seckillGoodsId)
                    .availableStock(afterStock)
                    .soldCount(Math.max(0, seckillGoods.getSoldCount() - quantity))
                    .updateTime(LocalDateTime.now())
                    .updateUser(userId)
                    .build();
            
            seckillGoodsMapper.update(updateGoods);
            
            // 4. 记录日志
            recordStockLog(seckillGoodsId, SeckillStockLog.RELEASE_STOCK, quantity, 
                          beforeStock, afterStock, orderId, orderNumber, userId, 
                          "库存释放成功：" + reason);
            
            log.info("库存释放成功，商品ID：{}，释放数量：{}，当前库存：{}，原因：{}", 
                    seckillGoodsId, quantity, afterStock, reason);
            return true;
            
        } catch (Exception e) {
            log.error("释放库存异常，商品ID：{}，数量：{}", seckillGoodsId, quantity, e);
            // 记录异常日志
            try {
                SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
                Integer beforeStock = seckillGoods != null ? seckillGoods.getAvailableStock() : 0;
                recordStockLog(seckillGoodsId, SeckillStockLog.RELEASE_STOCK, quantity, 
                              beforeStock, beforeStock, orderId, orderNumber, userId, 
                              "释放库存异常：" + e.getMessage());
            } catch (Exception logException) {
                log.error("记录库存异常日志失败", logException);
            }
            return false;
        }
    }

    /**
     * 分页查询库存日志
     * @param page 页码
     * @param pageSize 页大小
     * @param seckillGoodsId 秒杀商品ID
     * @param operationType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    @Override
    public PageResult pageQueryStockLog(int page, int pageSize, Long seckillGoodsId, 
                                       Integer operationType, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("分页查询库存日志，页码：{}，页大小：{}，商品ID：{}，操作类型：{}，时间范围：{} - {}", 
                page, pageSize, seckillGoodsId, operationType, startTime, endTime);
        
        // 设置分页
        PageHelper.startPage(page, pageSize);
        
        // 查询库存日志
        Page<SeckillStockLog> pageResult = seckillStockLogMapper.pageQuery(
                seckillGoodsId, operationType, startTime, endTime);
        
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    /**
     * 获取库存统计信息
     * @param seckillGoodsId 秒杀商品ID
     * @return 统计信息
     */
    @Override
    public Object getStockStatistics(Long seckillGoodsId) {
        log.info("获取库存统计信息，商品ID：{}", seckillGoodsId);
        
        // 获取商品基本信息
        SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
        if (seckillGoods == null) {
            throw new RuntimeException("秒杀商品不存在");
        }
        
        // 统计库存操作次数
        Map<String, Object> deductStats = seckillStockLogMapper.getStatsByType(seckillGoodsId, SeckillStockLog.DEDUCT_STOCK);
        Map<String, Object> releaseStats = seckillStockLogMapper.getStatsByType(seckillGoodsId, SeckillStockLog.RELEASE_STOCK);
        
        // 构建统计结果
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("seckillGoodsId", seckillGoodsId);
        statistics.put("totalStock", seckillGoods.getTotalStock());
        statistics.put("availableStock", seckillGoods.getAvailableStock());
        statistics.put("soldCount", seckillGoods.getSoldCount());
        statistics.put("stockUtilization", 
                seckillGoods.getTotalStock() > 0 ? 
                        (double) seckillGoods.getSoldCount() / seckillGoods.getTotalStock() * 100 : 0);
        
        // 扣减统计
        statistics.put("totalDeductCount", deductStats.getOrDefault("totalCount", 0));
        statistics.put("totalDeductQuantity", deductStats.getOrDefault("totalQuantity", 0));
        
        // 释放统计
        statistics.put("totalReleaseCount", releaseStats.getOrDefault("totalCount", 0));
        statistics.put("totalReleaseQuantity", releaseStats.getOrDefault("totalQuantity", 0));
        
        return statistics;
    }

    /**
     * 记录库存操作日志
     */
    private void recordStockLog(Long seckillGoodsId, Integer operationType, Integer quantity,
                              Integer beforeStock, Integer afterStock, Long orderId, 
                              String orderNumber, Long userId, String description) {
        try {
            SeckillStockLog stockLog = SeckillStockLog.builder()
                    .seckillGoodsId(seckillGoodsId)
                    .operationType(operationType)
                    .quantity(quantity)
                    .beforeStock(beforeStock)
                    .afterStock(afterStock)
                    .orderId(orderId)
                    .description(description)
                    .createTime(LocalDateTime.now())
                    .build();

            seckillStockLogMapper.insert(stockLog);
            
        } catch (Exception e) {
            log.error("记录库存日志失败，商品ID：{}，操作类型：{}", seckillGoodsId, operationType, e);
        }
    }
}

