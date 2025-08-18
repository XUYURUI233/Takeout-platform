package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.SeckillStockRollbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀库存回滚管理控制器
 */
@RestController
@RequestMapping("/admin/seckill/stock/rollback")
@Api(tags = "秒杀库存回滚管理相关接口")
@Slf4j
public class SeckillStockRollbackController {

    @Autowired
    private SeckillStockRollbackService seckillStockRollbackService;

    /**
     * 回滚订单库存
     * @param orderId 订单ID
     * @param reason 回滚原因
     * @return 操作结果
     */
    @PostMapping("/order")
    @ApiOperation("回滚订单库存")
    public Result<String> rollbackOrderStock(
            @RequestParam @ApiParam("订单ID") Long orderId,
            @RequestParam @ApiParam("回滚原因") String reason) {
        
        log.info("回滚订单库存，订单ID：{}，原因：{}", orderId, reason);
        
        boolean success = seckillStockRollbackService.rollbackOrderStock(orderId, null, reason);
        
        if (success) {
            return Result.success("订单库存回滚成功");
        } else {
            return Result.error("订单库存回滚失败");
        }
    }

    /**
     * 批量回滚库存
     * @param seckillGoodsId 秒杀商品ID
     * @param maxRollbackCount 最大回滚数量
     * @param reason 回滚原因
     * @return 操作结果
     */
    @PostMapping("/batch")
    @ApiOperation("批量回滚库存")
    public Result<Object> batchRollbackStock(
            @RequestParam @ApiParam("秒杀商品ID") Long seckillGoodsId,
            @RequestParam @ApiParam("最大回滚数量") Integer maxRollbackCount,
            @RequestParam @ApiParam("回滚原因") String reason) {
        
        log.info("批量回滚库存，商品ID：{}，最大回滚数量：{}，原因：{}", seckillGoodsId, maxRollbackCount, reason);
        
        int actualRollbackCount = seckillStockRollbackService.batchRollbackStock(seckillGoodsId, maxRollbackCount, reason);
        
        if (actualRollbackCount > 0) {
            return Result.success("批量回滚成功，实际回滚数量：" + actualRollbackCount);
        } else {
            return Result.error("批量回滚失败，没有可回滚的库存");
        }
    }

    /**
     * 根据时间范围回滚库存
     * @param seckillGoodsId 秒杀商品ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param reason 回滚原因
     * @return 操作结果
     */
    @PostMapping("/time-range")
    @ApiOperation("根据时间范围回滚库存")
    public Result<Object> rollbackStockByTimeRange(
            @RequestParam @ApiParam("秒杀商品ID") Long seckillGoodsId,
            @RequestParam @ApiParam("开始时间(yyyy-MM-dd HH:mm:ss)") String startTime,
            @RequestParam @ApiParam("结束时间(yyyy-MM-dd HH:mm:ss)") String endTime,
            @RequestParam @ApiParam("回滚原因") String reason) {
        
        log.info("根据时间范围回滚库存，商品ID：{}，时间范围：{} - {}，原因：{}", 
                seckillGoodsId, startTime, endTime, reason);
        
        int rollbackCount = seckillStockRollbackService.rollbackStockByTimeRange(
                seckillGoodsId, startTime, endTime, reason);
        
        if (rollbackCount > 0) {
            return Result.success("时间范围回滚成功，回滚订单数：" + rollbackCount);
        } else {
            return Result.error("时间范围回滚失败，没有找到可回滚的记录");
        }
    }

    /**
     * 检查库存一致性
     * @param seckillGoodsId 秒杀商品ID
     * @return 一致性检查结果
     */
    @GetMapping("/consistency-check/{seckillGoodsId}")
    @ApiOperation("检查库存一致性")
    public Result<Object> checkStockConsistency(
            @PathVariable @ApiParam("秒杀商品ID") Long seckillGoodsId) {
        
        log.info("检查库存一致性，商品ID：{}", seckillGoodsId);
        
        Object result = seckillStockRollbackService.checkStockConsistency(seckillGoodsId);
        
        return Result.success(result);
    }

    /**
     * 修复库存不一致问题
     * @param seckillGoodsId 秒杀商品ID
     * @param targetStock 目标库存值
     * @param reason 修复原因
     * @return 操作结果
     */
    @PostMapping("/fix-inconsistency")
    @ApiOperation("修复库存不一致问题")
    public Result<String> fixStockInconsistency(
            @RequestParam @ApiParam("秒杀商品ID") Long seckillGoodsId,
            @RequestParam @ApiParam("目标库存值") Integer targetStock,
            @RequestParam @ApiParam("修复原因") String reason) {
        
        log.info("修复库存不一致，商品ID：{}，目标库存：{}，原因：{}", seckillGoodsId, targetStock, reason);
        
        boolean success = seckillStockRollbackService.fixStockInconsistency(seckillGoodsId, targetStock, reason);
        
        if (success) {
            return Result.success("库存不一致修复成功");
        } else {
            return Result.error("库存不一致修复失败");
        }
    }
}



