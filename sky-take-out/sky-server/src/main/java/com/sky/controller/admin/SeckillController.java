package com.sky.controller.admin;

import com.sky.dto.SeckillDTO;
import com.sky.dto.SeckillStockUpdateDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家端秒杀管理
 */
@RestController
@RequestMapping("/admin/seckill")
@Api(tags = "商家端秒杀管理接口")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 创建秒杀活动
     */
    @PostMapping("/create")
    @ApiOperation("创建秒杀活动")
    public Result<String> createSeckill(@RequestBody SeckillDTO seckillDTO) {
        log.info("创建秒杀活动：{}", seckillDTO);
        seckillService.createSeckill(seckillDTO);
        return Result.success("秒杀活动创建成功");
    }

    /**
     * 获取秒杀活动列表
     */
    @GetMapping("/list")
    @ApiOperation("获取秒杀活动列表")
    public Result<PageResult> getSeckillList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        log.info("查询秒杀活动列表：page={}, pageSize={}, status={}", page, pageSize, status);
        PageResult pageResult = seckillService.getSeckillList(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 更新秒杀活动
     */
    @PutMapping("/update/{seckillId}")
    @ApiOperation("更新秒杀活动")
    public Result<String> updateSeckill(@PathVariable Long seckillId, @RequestBody SeckillDTO seckillDTO) {
        log.info("更新秒杀活动：seckillId={}, seckillDTO={}", seckillId, seckillDTO);
        seckillService.updateSeckill(seckillId, seckillDTO);
        return Result.success("秒杀活动更新成功");
    }

    /**
     * 删除秒杀活动
     */
    @DeleteMapping("/delete/{seckillId}")
    @ApiOperation("删除秒杀活动")
    public Result<String> deleteSeckill(@PathVariable Long seckillId) {
        log.info("删除秒杀活动：seckillId={}", seckillId);
        seckillService.deleteSeckill(seckillId);
        return Result.success("秒杀活动删除成功");
    }

    /**
     * 更新秒杀库存
     */
    @PostMapping("/stock/update")
    @ApiOperation("更新秒杀库存")
    public Result<String> updateStock(@RequestBody SeckillStockUpdateDTO stockUpdateDTO) {
        log.info("更新秒杀库存：{}", stockUpdateDTO);
        seckillService.updateStock(stockUpdateDTO);
        return Result.success("库存更新成功");
    }

    /**
     * 回滚秒杀库存
     */
    @PostMapping("/stock/rollback")
    @ApiOperation("回滚秒杀库存")
    public Result<String> rollbackStock(
            @RequestParam String orderId,
            @RequestParam Long itemId,
            @RequestParam Integer rollbackQuantity) {
        log.info("回滚秒杀库存：orderId={}, itemId={}, rollbackQuantity={}", orderId, itemId, rollbackQuantity);
        seckillService.rollbackStock(orderId, itemId, rollbackQuantity);
        return Result.success("库存回滚成功");
    }

    /**
     * 获取秒杀统计数据
     */
    @GetMapping("/statistics/{seckillId}")
    @ApiOperation("获取秒杀统计数据")
    public Result<Object> getStatistics(@PathVariable Long seckillId) {
        log.info("获取秒杀统计数据：seckillId={}", seckillId);
        Object statistics = seckillService.getStatistics(seckillId);
        return Result.success(statistics);
    }
}

