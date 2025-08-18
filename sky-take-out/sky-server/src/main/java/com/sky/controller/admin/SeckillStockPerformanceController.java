package com.sky.controller.admin;

import com.sky.entity.SeckillGoods;
import com.sky.result.Result;
import com.sky.service.SeckillStockCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 秒杀库存性能优化控制器
 */
@RestController
@RequestMapping("/admin/seckill/stock/performance")
@Api(tags = "秒杀库存性能优化相关接口")
@Slf4j
public class SeckillStockPerformanceController {

    @Autowired
    private SeckillStockCacheService seckillStockCacheService;

    /**
     * 预热库存缓存
     * @param activityId 活动ID
     * @return 操作结果
     */
    @PostMapping("/warm-up/{activityId}")
    @ApiOperation("预热库存缓存")
    public Result<String> warmUpStockCache(
            @PathVariable @ApiParam("活动ID") Long activityId) {
        
        log.info("预热库存缓存，活动ID：{}", activityId);
        
        try {
            seckillStockCacheService.warmUpStockCache(activityId);
            return Result.success("库存缓存预热成功");
        } catch (Exception e) {
            log.error("库存缓存预热失败，活动ID：{}", activityId, e);
            return Result.error("库存缓存预热失败：" + e.getMessage());
        }
    }

    /**
     * 刷新所有库存缓存
     * @return 操作结果
     */
    @PostMapping("/refresh-cache")
    @ApiOperation("刷新所有库存缓存")
    public Result<String> refreshAllStockCache() {
        
        log.info("刷新所有库存缓存");
        
        try {
            seckillStockCacheService.refreshAllStockCache();
            return Result.success("所有库存缓存刷新成功");
        } catch (Exception e) {
            log.error("刷新所有库存缓存失败", e);
            return Result.error("刷新所有库存缓存失败：" + e.getMessage());
        }
    }

    /**
     * 清除活动库存缓存
     * @param activityId 活动ID
     * @return 操作结果
     */
    @DeleteMapping("/cache/{activityId}")
    @ApiOperation("清除活动库存缓存")
    public Result<String> evictActivityStockCache(
            @PathVariable @ApiParam("活动ID") Long activityId) {
        
        log.info("清除活动库存缓存，活动ID：{}", activityId);
        
        try {
            seckillStockCacheService.evictActivityStockCache(activityId);
            return Result.success("活动库存缓存清除成功");
        } catch (Exception e) {
            log.error("清除活动库存缓存失败，活动ID：{}", activityId, e);
            return Result.error("清除活动库存缓存失败：" + e.getMessage());
        }
    }

    /**
     * 获取缓存中的库存信息
     * @param seckillGoodsId 秒杀商品ID
     * @return 库存信息
     */
    @GetMapping("/cached-stock/{seckillGoodsId}")
    @ApiOperation("获取缓存中的库存信息")
    public Result<SeckillGoods> getCachedStock(
            @PathVariable @ApiParam("秒杀商品ID") Long seckillGoodsId) {
        
        log.info("获取缓存库存信息，商品ID：{}", seckillGoodsId);
        
        try {
            SeckillGoods cachedStock = seckillStockCacheService.getCachedStock(seckillGoodsId);
            if (cachedStock != null) {
                return Result.success(cachedStock);
            } else {
                return Result.error("商品不存在或缓存未命中");
            }
        } catch (Exception e) {
            log.error("获取缓存库存信息失败，商品ID：{}", seckillGoodsId, e);
            return Result.error("获取缓存库存信息失败：" + e.getMessage());
        }
    }

    /**
     * 检查库存是否充足
     * @param seckillGoodsId 秒杀商品ID
     * @param quantity 需要的数量
     * @return 检查结果
     */
    @GetMapping("/check-stock")
    @ApiOperation("检查库存是否充足")
    public Result<Object> checkStockAvailable(
            @RequestParam @ApiParam("秒杀商品ID") Long seckillGoodsId,
            @RequestParam @ApiParam("需要的数量") Integer quantity) {
        
        log.info("检查库存可用性，商品ID：{}，数量：{}", seckillGoodsId, quantity);
        
        try {
            boolean available = seckillStockCacheService.checkStockAvailable(seckillGoodsId, quantity);
            return Result.success(available ? "库存充足" : "库存不足");
        } catch (Exception e) {
            log.error("检查库存可用性失败，商品ID：{}，数量：{}", seckillGoodsId, quantity, e);
            return Result.error("检查库存可用性失败：" + e.getMessage());
        }
    }

    /**
     * 获取低库存商品列表
     * @param threshold 库存阈值
     * @param activityId 活动ID（可选）
     * @return 低库存商品列表
     */
    @GetMapping("/low-stock")
    @ApiOperation("获取低库存商品列表")
    public Result<List<SeckillGoods>> getLowStockGoods(
            @RequestParam(defaultValue = "10") @ApiParam("库存阈值") Integer threshold,
            @RequestParam(required = false) @ApiParam("活动ID") Long activityId) {
        
        log.info("获取低库存商品列表，阈值：{}，活动ID：{}", threshold, activityId);
        
        try {
            List<SeckillGoods> lowStockGoods = seckillStockCacheService.getLowStockGoods(threshold, activityId);
            return Result.success(lowStockGoods);
        } catch (Exception e) {
            log.error("获取低库存商品列表失败，阈值：{}，活动ID：{}", threshold, activityId, e);
            return Result.error("获取低库存商品列表失败：" + e.getMessage());
        }
    }
}

