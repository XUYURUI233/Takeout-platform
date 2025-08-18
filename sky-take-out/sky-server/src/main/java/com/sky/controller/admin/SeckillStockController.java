package com.sky.controller.admin;

import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SeckillStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 秒杀库存管理控制器
 */
@RestController
@RequestMapping("/admin/seckill/stock")
@Api(tags = "秒杀库存管理相关接口")
@Slf4j
public class SeckillStockController {

    @Autowired
    private SeckillStockService seckillStockService;

    /**
     * 分页查询库存日志
     * @param page 页码
     * @param pageSize 页大小
     * @param seckillGoodsId 秒杀商品ID
     * @param operationType 操作类型（1-扣减，2-释放，3-初始化）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    @GetMapping("/log/page")
    @ApiOperation("分页查询库存日志")
    public Result<PageResult> pageQueryStockLog(
            @RequestParam(defaultValue = "1") @ApiParam("页码") int page,
            @RequestParam(defaultValue = "10") @ApiParam("页大小") int pageSize,
            @RequestParam(required = false) @ApiParam("秒杀商品ID") Long seckillGoodsId,
            @RequestParam(required = false) @ApiParam("操作类型") Integer operationType,
            @RequestParam(required = false) @ApiParam("开始时间") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @ApiParam("结束时间")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        log.info("分页查询库存日志，页码：{}，页大小：{}，商品ID：{}，操作类型：{}，时间范围：{} - {}", 
                page, pageSize, seckillGoodsId, operationType, startTime, endTime);
        
        PageResult pageResult = seckillStockService.pageQueryStockLog(
                page, pageSize, seckillGoodsId, operationType, startTime, endTime);
        
        return Result.success(pageResult);
    }

    /**
     * 获取库存统计信息
     * @param seckillGoodsId 秒杀商品ID
     * @return 统计信息
     */
    @GetMapping("/statistics/{seckillGoodsId}")
    @ApiOperation("获取库存统计信息")
    public Result<Object> getStockStatistics(
            @PathVariable @ApiParam("秒杀商品ID") Long seckillGoodsId) {
        
        log.info("获取库存统计信息，商品ID：{}", seckillGoodsId);
        
        Object statistics = seckillStockService.getStockStatistics(seckillGoodsId);
        
        return Result.success(statistics);
    }

    /**
     * 手动释放库存（紧急情况使用）
     * @param seckillGoodsId 秒杀商品ID
     * @param quantity 释放数量
     * @param reason 释放原因
     * @return 操作结果
     */
    @PostMapping("/release")
    @ApiOperation("手动释放库存")
    public Result<String> releaseStock(
            @RequestParam @ApiParam("秒杀商品ID") Long seckillGoodsId,
            @RequestParam @ApiParam("释放数量") Integer quantity,
            @RequestParam @ApiParam("释放原因") String reason) {
        
        log.info("手动释放库存，商品ID：{}，数量：{}，原因：{}", seckillGoodsId, quantity, reason);
        
        boolean success = seckillStockService.releaseStock(
                seckillGoodsId, quantity, null, null, null, "管理员手动释放：" + reason);
        
        if (success) {
            return Result.success("库存释放成功");
        } else {
            return Result.error("库存释放失败");
        }
    }
}



