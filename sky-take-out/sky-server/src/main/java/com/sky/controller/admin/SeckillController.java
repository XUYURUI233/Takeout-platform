package com.sky.controller.admin;

import com.sky.dto.SeckillActivityDTO;
import com.sky.dto.SeckillActivityPageQueryDTO;
import com.sky.dto.SeckillGoodsDTO;
import com.sky.dto.SeckillOrderPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SeckillActivityService;
import com.sky.service.SeckillGoodsService;
import com.sky.service.SeckillOrderService;
import com.sky.vo.SeckillActivityVO;
import com.sky.vo.SeckillGoodsVO;
import com.sky.vo.SeckillOrderVO;
import com.sky.vo.SeckillStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 秒杀管理相关接口
 */
@RestController
@RequestMapping("/admin/seckill")
@Api(tags = "秒杀管理相关接口")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillActivityService seckillActivityService;
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 分页查询秒杀活动列表
     * @param seckillActivityPageQueryDTO
     * @return
     */
    @GetMapping("/activity/page")
    @ApiOperation("分页查询秒杀活动列表")
    public Result<PageResult> pageQuery(SeckillActivityPageQueryDTO seckillActivityPageQueryDTO) {
        log.info("分页查询秒杀活动：{}", seckillActivityPageQueryDTO);
        PageResult pageResult = seckillActivityService.pageQuery(seckillActivityPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增秒杀活动
     * @param seckillActivityDTO
     * @return
     */
    @PostMapping("/activity")
    @ApiOperation("新增秒杀活动")
    public Result<String> save(@RequestBody SeckillActivityDTO seckillActivityDTO) {
        log.info("新增秒杀活动：{}", seckillActivityDTO);
        seckillActivityService.save(seckillActivityDTO);
        return Result.success();
    }

    /**
     * 修改秒杀活动
     * @param seckillActivityDTO
     * @return
     */
    @PutMapping("/activity")
    @ApiOperation("修改秒杀活动")
    public Result<String> update(@RequestBody SeckillActivityDTO seckillActivityDTO) {
        log.info("修改秒杀活动：{}", seckillActivityDTO);
        seckillActivityService.update(seckillActivityDTO);
        return Result.success();
    }

    /**
     * 删除秒杀活动
     * @param id
     * @return
     */
    @DeleteMapping("/activity")
    @ApiOperation("删除秒杀活动")
    public Result<String> delete(@RequestParam Long id) {
        log.info("删除秒杀活动：{}", id);
        seckillActivityService.deleteById(id);
        return Result.success();
    }

    /**
     * 查询秒杀活动详情
     * @param id
     * @return
     */
    @GetMapping("/activity/{id}")
    @ApiOperation("查询秒杀活动详情")
    public Result<SeckillActivityVO> getById(@PathVariable Long id) {
        log.info("查询秒杀活动详情：{}", id);
        SeckillActivityVO seckillActivityVO = seckillActivityService.getById(id);
        return Result.success(seckillActivityVO);
    }

    /**
     * 启用/停用秒杀活动
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/activity/status/{status}")
    @ApiOperation("启用/停用秒杀活动")
    public Result<String> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("启用/停用秒杀活动：{}，{}", status, id);
        seckillActivityService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 查询可用商品列表
     * @param type
     * @param name
     * @return
     */
    @GetMapping("/goods/available")
    @ApiOperation("查询可用商品列表")
    public Result<List<SeckillGoodsVO>> getAvailableGoods(@RequestParam Integer type, 
                                                          @RequestParam(required = false) String name) {
        log.info("查询可用商品列表：{}，{}", type, name);
        List<SeckillGoodsVO> list = seckillGoodsService.getAvailableGoods(type, name);
        return Result.success(list);
    }

    /**
     * 修改秒杀商品信息
     * @param seckillGoodsDTO
     * @return
     */
    @PutMapping("/goods")
    @ApiOperation("修改秒杀商品信息")
    public Result<String> updateGoods(@RequestBody SeckillGoodsDTO seckillGoodsDTO) {
        log.info("修改秒杀商品信息：{}", seckillGoodsDTO);
        seckillGoodsService.update(seckillGoodsDTO);
        return Result.success();
    }

    /**
     * 删除秒杀商品
     * @param id
     * @return
     */
    @DeleteMapping("/goods")
    @ApiOperation("删除秒杀商品")
    public Result<String> deleteGoods(@RequestParam Long id) {
        log.info("删除秒杀商品：{}", id);
        seckillGoodsService.deleteById(id);
        return Result.success();
    }

    /**
     * 查询秒杀订单列表
     * @param seckillOrderPageQueryDTO
     * @return
     */
    @GetMapping("/order/page")
    @ApiOperation("查询秒杀订单列表")
    public Result<PageResult> orderPageQuery(SeckillOrderPageQueryDTO seckillOrderPageQueryDTO) {
        log.info("分页查询秒杀订单：{}", seckillOrderPageQueryDTO);
        PageResult pageResult = seckillOrderService.adminPageQuery(seckillOrderPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询秒杀订单详情
     * @param id
     * @return
     */
    @GetMapping("/order/details/{id}")
    @ApiOperation("查询秒杀订单详情")
    public Result<SeckillOrderVO> getOrderDetails(@PathVariable Long id) {
        log.info("查询秒杀订单详情：{}", id);
        SeckillOrderVO seckillOrderVO = seckillOrderService.getOrderDetails(id);
        return Result.success(seckillOrderVO);
    }

    /**
     * 查询秒杀活动统计数据
     * @param activityId
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("查询秒杀活动统计数据")
    public Result<SeckillStatisticsVO> getStatistics(@RequestParam Long activityId,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("查询秒杀活动统计数据：{}，{}，{}", activityId, beginDate, endDate);
        SeckillStatisticsVO statisticsVO = seckillActivityService.getStatistics(activityId, beginDate, endDate);
        return Result.success(statisticsVO);
    }
}