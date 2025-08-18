package com.sky.service.impl;

import com.sky.entity.SeckillGoods;
import com.sky.entity.SeckillOrder;
import com.sky.entity.SeckillStockLog;
import com.sky.mapper.SeckillGoodsMapper;
import com.sky.mapper.SeckillOrderMapper;
import com.sky.mapper.SeckillStockLogMapper;
import com.sky.service.SeckillStockRollbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀库存回滚服务实现类
 */
@Service
@Slf4j
public class SeckillStockRollbackServiceImpl implements SeckillStockRollbackService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillStockLogMapper seckillStockLogMapper;

    @Override
    @Transactional
    public void rollbackByOrderId(Long orderId) {
        log.info("开始回滚订单库存，订单ID: {}", orderId);
        
        try {
            // 查询订单信息
            SeckillOrder seckillOrder = seckillOrderMapper.getByOrderId(orderId);
            if (seckillOrder == null) {
                log.warn("订单不存在，无法回滚库存，订单ID: {}", orderId);
                return;
            }

            // 回滚库存
            rollbackStock(seckillOrder.getSeckillGoodsId(), seckillOrder.getQuantity(), 
                         orderId, "订单取消回滚");
            
            log.info("订单库存回滚成功，订单ID: {}", orderId);
        } catch (Exception e) {
            log.error("订单库存回滚失败，订单ID: {}", orderId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void rollbackByGoodsId(Long seckillGoodsId, Integer quantity, String reason) {
        log.info("开始回滚商品库存，商品ID: {}, 数量: {}, 原因: {}", seckillGoodsId, quantity, reason);
        
        try {
            rollbackStock(seckillGoodsId, quantity, null, reason);
            log.info("商品库存回滚成功，商品ID: {}", seckillGoodsId);
        } catch (Exception e) {
            log.error("商品库存回滚失败，商品ID: {}", seckillGoodsId, e);
            throw e;
        }
    }

    @Override
    public List<SeckillStockLog> getStockLogs(Long seckillGoodsId) {
        return seckillStockLogMapper.getBySeckillGoodsId(seckillGoodsId);
    }

    @Override
    @Transactional
    public void batchRollback(List<Long> orderIds) {
        log.info("开始批量回滚库存，订单数量: {}", orderIds.size());
        
        for (Long orderId : orderIds) {
            try {
                rollbackByOrderId(orderId);
            } catch (Exception e) {
                log.error("批量回滚中单个订单失败，订单ID: {}", orderId, e);
                // 继续处理下一个订单，不中断整个批量操作
            }
        }
        
        log.info("批量回滚库存完成，订单数量: {}", orderIds.size());
    }

    /**
     * 执行库存回滚
     */
    private void rollbackStock(Long seckillGoodsId, Integer quantity, Long orderId, String reason) {
        // 查询当前库存信息
        SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
        if (seckillGoods == null) {
            throw new RuntimeException("秒杀商品不存在: " + seckillGoodsId);
        }

        Integer beforeStock = seckillGoods.getAvailableStock();
        Integer afterStock = beforeStock + quantity;

        // 更新库存
        seckillGoods.setAvailableStock(afterStock);
        seckillGoods.setSoldCount(seckillGoods.getSoldCount() - quantity);
        seckillGoods.setUpdateTime(LocalDateTime.now());
        
        seckillGoodsMapper.update(seckillGoods);
        log.info("库存回滚成功，商品ID: {}, 数量: {}, 回滚前: {}, 回滚后: {}", 
                seckillGoodsId, quantity, beforeStock, afterStock);

        // 记录库存日志
        SeckillStockLog stockLog = SeckillStockLog.builder()
                .seckillGoodsId(seckillGoodsId)
                .operationType(2) // 释放库存
                .quantity(quantity)
                .beforeStock(beforeStock)
                .afterStock(afterStock)
                .orderId(orderId)
                .description(reason)
                .createTime(LocalDateTime.now())
                .build();
        
        seckillStockLogMapper.insert(stockLog);
    }

    /**
     * 回滚订单库存
     * @param orderId 订单ID
     * @param userId 用户ID（可为null）
     * @param reason 回滚原因
     * @return 是否回滚成功
     */
    @Override
    @Transactional
    public boolean rollbackOrderStock(Long orderId, Long userId, String reason) {
        log.info("回滚订单库存，订单ID：{}，用户ID：{}，原因：{}", orderId, userId, reason);
        
        try {
            // 查询秒杀订单信息
            SeckillOrder seckillOrder = seckillOrderMapper.getByOrderId(orderId);
            if (seckillOrder == null) {
                log.warn("秒杀订单不存在，无法回滚库存，订单ID：{}", orderId);
                return false;
            }
            
            // 回滚库存
            rollbackStock(seckillOrder.getSeckillGoodsId(), seckillOrder.getQuantity(), 
                         orderId, "订单库存回滚：" + reason);
            
            log.info("订单库存回滚成功，订单ID：{}", orderId);
            return true;
            
        } catch (Exception e) {
            log.error("订单库存回滚失败，订单ID：{}", orderId, e);
            return false;
        }
    }

    /**
     * 批量回滚库存
     * @param seckillGoodsId 秒杀商品ID
     * @param maxRollbackCount 最大回滚数量
     * @param reason 回滚原因
     * @return 实际回滚数量
     */
    @Override
    @Transactional
    public int batchRollbackStock(Long seckillGoodsId, Integer maxRollbackCount, String reason) {
        log.info("批量回滚库存，商品ID：{}，最大回滚数量：{}，原因：{}", seckillGoodsId, maxRollbackCount, reason);
        
        try {
            // 查询可回滚的订单（待支付状态的订单）
            List<SeckillOrder> rollbackOrders = seckillOrderMapper.getRollbackableOrders(
                    seckillGoodsId, maxRollbackCount);
            
            int actualRollbackCount = 0;
            for (SeckillOrder order : rollbackOrders) {
                try {
                    rollbackStock(order.getSeckillGoodsId(), order.getQuantity(), 
                                 order.getOrderId(), "批量回滚：" + reason);
                    actualRollbackCount += order.getQuantity();
                } catch (Exception e) {
                    log.error("批量回滚中单个订单失败，订单ID：{}", order.getOrderId(), e);
                }
            }
            
            log.info("批量回滚库存完成，商品ID：{}，实际回滚数量：{}", seckillGoodsId, actualRollbackCount);
            return actualRollbackCount;
            
        } catch (Exception e) {
            log.error("批量回滚库存失败，商品ID：{}", seckillGoodsId, e);
            return 0;
        }
    }

    /**
     * 根据时间范围回滚库存
     * @param seckillGoodsId 秒杀商品ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param reason 回滚原因
     * @return 回滚订单数
     */
    @Override
    @Transactional
    public int rollbackStockByTimeRange(Long seckillGoodsId, String startTime, String endTime, String reason) {
        log.info("根据时间范围回滚库存，商品ID：{}，时间范围：{} - {}，原因：{}", 
                seckillGoodsId, startTime, endTime, reason);
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);
            
            // 查询时间范围内的订单
            List<SeckillOrder> ordersInRange = seckillOrderMapper.getOrdersByTimeRange(
                    seckillGoodsId, start, end);
            
            int rollbackCount = 0;
            for (SeckillOrder order : ordersInRange) {
                try {
                    rollbackStock(order.getSeckillGoodsId(), order.getQuantity(), 
                                 order.getOrderId(), "时间范围回滚：" + reason);
                    rollbackCount++;
                } catch (Exception e) {
                    log.error("时间范围回滚中单个订单失败，订单ID：{}", order.getOrderId(), e);
                }
            }
            
            log.info("时间范围回滚完成，商品ID：{}，回滚订单数：{}", seckillGoodsId, rollbackCount);
            return rollbackCount;
            
        } catch (Exception e) {
            log.error("时间范围回滚失败，商品ID：{}", seckillGoodsId, e);
            return 0;
        }
    }

    /**
     * 检查库存一致性
     * @param seckillGoodsId 秒杀商品ID
     * @return 一致性检查结果
     */
    @Override
    public Object checkStockConsistency(Long seckillGoodsId) {
        log.info("检查库存一致性，商品ID：{}", seckillGoodsId);
        
        try {
            // 获取商品库存信息
            SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
            if (seckillGoods == null) {
                throw new RuntimeException("秒杀商品不存在");
            }
            
            // 统计已完成订单的总数量
            Integer completedOrderQuantity = seckillOrderMapper.getCompletedOrderQuantity(seckillGoodsId);
            if (completedOrderQuantity == null) {
                completedOrderQuantity = 0;
            }
            
            // 计算预期的可用库存
            Integer expectedAvailableStock = seckillGoods.getTotalStock() - completedOrderQuantity;
            
            // 检查一致性
            boolean isConsistent = seckillGoods.getAvailableStock().equals(expectedAvailableStock);
            
            Map<String, Object> result = new HashMap<>();
            result.put("seckillGoodsId", seckillGoodsId);
            result.put("totalStock", seckillGoods.getTotalStock());
            result.put("currentAvailableStock", seckillGoods.getAvailableStock());
            result.put("currentSoldCount", seckillGoods.getSoldCount());
            result.put("completedOrderQuantity", completedOrderQuantity);
            result.put("expectedAvailableStock", expectedAvailableStock);
            result.put("isConsistent", isConsistent);
            result.put("stockDifference", seckillGoods.getAvailableStock() - expectedAvailableStock);
            
            return result;
            
        } catch (Exception e) {
            log.error("检查库存一致性失败，商品ID：{}", seckillGoodsId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "检查失败：" + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 修复库存不一致问题
     * @param seckillGoodsId 秒杀商品ID
     * @param targetStock 目标库存值
     * @param reason 修复原因
     * @return 是否修复成功
     */
    @Override
    @Transactional
    public boolean fixStockInconsistency(Long seckillGoodsId, Integer targetStock, String reason) {
        log.info("修复库存不一致，商品ID：{}，目标库存：{}，原因：{}", seckillGoodsId, targetStock, reason);
        
        try {
            // 获取当前商品信息
            SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
            if (seckillGoods == null) {
                log.error("秒杀商品不存在，商品ID：{}", seckillGoodsId);
                return false;
            }
            
            Integer beforeStock = seckillGoods.getAvailableStock();
            
            // 检查目标库存是否合理
            if (targetStock < 0 || targetStock > seckillGoods.getTotalStock()) {
                log.error("目标库存值不合理，商品ID：{}，目标库存：{}，总库存：{}", 
                        seckillGoodsId, targetStock, seckillGoods.getTotalStock());
                return false;
            }
            
            // 更新库存
            SeckillGoods updateGoods = SeckillGoods.builder()
                    .id(seckillGoodsId)
                    .availableStock(targetStock)
                    .soldCount(seckillGoods.getTotalStock() - targetStock)
                    .updateTime(LocalDateTime.now())
                    .build();
            
            seckillGoodsMapper.update(updateGoods);
            
            // 记录修复日志
            SeckillStockLog stockLog = SeckillStockLog.builder()
                    .seckillGoodsId(seckillGoodsId)
                    .operationType(3) // 修复操作
                    .quantity(targetStock - beforeStock)
                    .beforeStock(beforeStock)
                    .afterStock(targetStock)
                    .description("库存一致性修复：" + reason)
                    .createTime(LocalDateTime.now())
                    .build();
            
            seckillStockLogMapper.insert(stockLog);
            
            log.info("库存不一致修复成功，商品ID：{}，修复前：{}，修复后：{}", 
                    seckillGoodsId, beforeStock, targetStock);
            return true;
            
        } catch (Exception e) {
            log.error("库存不一致修复失败，商品ID：{}", seckillGoodsId, e);
            return false;
        }
    }
}
