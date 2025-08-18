package com.sky.service;

import com.sky.entity.SeckillStockLog;

import java.util.List;

/**
 * 秒杀库存回滚Service接口
 */
public interface SeckillStockRollbackService {

    /**
     * 根据订单ID回滚库存
     * @param orderId 订单ID
     */
    void rollbackByOrderId(Long orderId);

    /**
     * 根据商品ID回滚指定数量的库存
     * @param seckillGoodsId 秒杀商品ID
     * @param quantity 回滚数量
     * @param reason 回滚原因
     */
    void rollbackByGoodsId(Long seckillGoodsId, Integer quantity, String reason);

    /**
     * 获取库存操作日志
     * @param seckillGoodsId 秒杀商品ID
     * @return 库存日志列表
     */
    List<SeckillStockLog> getStockLogs(Long seckillGoodsId);

    /**
     * 批量回滚库存
     * @param orderIds 订单ID列表
     */
    void batchRollback(List<Long> orderIds);

    /**
     * 回滚订单库存
     * @param orderId 订单ID
     * @param userId 用户ID（可为null）
     * @param reason 回滚原因
     * @return 是否回滚成功
     */
    boolean rollbackOrderStock(Long orderId, Long userId, String reason);

    /**
     * 批量回滚库存
     * @param seckillGoodsId 秒杀商品ID
     * @param maxRollbackCount 最大回滚数量
     * @param reason 回滚原因
     * @return 实际回滚数量
     */
    int batchRollbackStock(Long seckillGoodsId, Integer maxRollbackCount, String reason);

    /**
     * 根据时间范围回滚库存
     * @param seckillGoodsId 秒杀商品ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param reason 回滚原因
     * @return 回滚订单数
     */
    int rollbackStockByTimeRange(Long seckillGoodsId, String startTime, String endTime, String reason);

    /**
     * 检查库存一致性
     * @param seckillGoodsId 秒杀商品ID
     * @return 一致性检查结果
     */
    Object checkStockConsistency(Long seckillGoodsId);

    /**
     * 修复库存不一致问题
     * @param seckillGoodsId 秒杀商品ID
     * @param targetStock 目标库存值
     * @param reason 修复原因
     * @return 是否修复成功
     */
    boolean fixStockInconsistency(Long seckillGoodsId, Integer targetStock, String reason);
}