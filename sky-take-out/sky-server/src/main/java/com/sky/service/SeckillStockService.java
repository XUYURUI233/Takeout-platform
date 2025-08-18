package com.sky.service;

import com.sky.result.PageResult;

import java.time.LocalDateTime;

/**
 * 秒杀库存Service接口
 */
public interface SeckillStockService {

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
    boolean deductStock(Long seckillGoodsId, Integer quantity, Long version, 
                       Long orderId, String orderNumber, Long userId);

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
    boolean releaseStock(Long seckillGoodsId, Integer quantity, Long orderId, 
                        String orderNumber, Long userId, String reason);

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
    PageResult pageQueryStockLog(int page, int pageSize, Long seckillGoodsId, 
                                Integer operationType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取库存统计信息
     * @param seckillGoodsId 秒杀商品ID
     * @return 统计信息
     */
    Object getStockStatistics(Long seckillGoodsId);
}