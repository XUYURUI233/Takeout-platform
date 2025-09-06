package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.SeckillOrderSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SeckillActivityService;
import com.sky.service.SeckillGoodsService;
import com.sky.service.SeckillOrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.SeckillActivityVO;
import com.sky.vo.SeckillGoodsVO;
import com.sky.vo.SeckillOrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.csp.sentinel.annotation.SentinelResource;

import java.util.List;

/**
 * 用户端秒杀相关接口
 */
@RestController("userSeckillController")
@RequestMapping("/user/seckill")
@Api(tags = "用户端秒杀相关接口")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillActivityService seckillActivityService;
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 查询进行中的秒杀活动
     * @return
     */
    @GetMapping("/activity/active")
    @ApiOperation("查询进行中的秒杀活动")
    @SentinelResource(value = "user_seckill_activity_active", blockHandler = "handleBlock")
    public Result<List<SeckillActivityVO>> getActiveActivities() {
        log.info("查询进行中的秒杀活动");
        List<SeckillActivityVO> activities = seckillActivityService.getActiveActivities();
        return Result.success(activities);
    }

    /**
     * 查询秒杀活动商品列表
     * @param activityId
     * @return
     */
    @GetMapping("/activity/{activityId}/goods")
    @ApiOperation("查询秒杀活动商品列表")
    public Result<List<SeckillGoodsVO>> getActivityGoods(@PathVariable Long activityId) {
        log.info("查询秒杀活动商品列表：{}", activityId);
        List<SeckillGoodsVO> goods = seckillGoodsService.getByActivityId(activityId);
        return Result.success(goods);
    }

    /**
     * 查询秒杀商品详情
     * @param id
     * @return
     */
    @GetMapping("/goods/{id}")
    @ApiOperation("查询秒杀商品详情")
    @SentinelResource(value = "user_seckill_goods_detail", blockHandler = "handleBlock")
    public Result<SeckillGoodsVO> getSeckillGoodsDetail(@PathVariable Long id) {
        log.info("查询秒杀商品详情：{}", id);
        SeckillGoodsVO goods = seckillGoodsService.getById(id);
        return Result.success(goods);
    }

    /**
     * 提交秒杀订单
     * @param seckillOrderSubmitDTO
     * @return
     */
    @PostMapping("/order/submit")
    @ApiOperation("提交秒杀订单")
    @SentinelResource(value = "user_seckill_order_submit", blockHandler = "handleBlock")
    public Result<SeckillOrderSubmitVO> submitOrder(@RequestBody SeckillOrderSubmitDTO seckillOrderSubmitDTO) {
        log.info("提交秒杀订单：{}", seckillOrderSubmitDTO);
        SeckillOrderSubmitVO result = seckillOrderService.submitOrder(seckillOrderSubmitDTO);
        return Result.success(result);
    }

    /**
     * 使用Lua脚本提交秒杀订单（原子扣减库存）
     */
    @PostMapping("/order/submit-lua")
    @ApiOperation("提交秒杀订单（Lua）")
    @SentinelResource(value = "user_seckill_order_submit", blockHandler = "handleBlock")
    public Result<SeckillOrderSubmitVO> submitOrderWithLua(@RequestBody SeckillOrderSubmitDTO seckillOrderSubmitDTO) {
        log.info("提交秒杀订单（Lua）：{}", seckillOrderSubmitDTO);
        SeckillOrderSubmitVO result = seckillOrderService.submitOrderWithLua(seckillOrderSubmitDTO);
        return Result.success(result);
    }

    // Sentinel 限流/熔断统一处理
    public <T> Result<T> handleBlock(Object arg1, Object arg2, Object arg3, Throwable ex) {
        return Result.error(50010, "系统繁忙，请稍后重试");
    }

    /**
     * 秒杀订单支付
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/order/payment")
    @ApiOperation("秒杀订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("秒杀订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = seckillOrderService.payment(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 查询用户秒杀订单列表
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/order/list")
    @ApiOperation("查询用户秒杀订单列表")
    public Result<PageResult> getUserSeckillOrders(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int pageSize,
                                                   @RequestParam(required = false) Integer status) {
        log.info("查询用户秒杀订单列表：page={}, pageSize={}, status={}", page, pageSize, status);
        PageResult pageResult = seckillOrderService.pageQueryByUser(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 查询秒杀订单详情
     * @param id
     * @return
     */
    @GetMapping("/order/details/{id}")
    @ApiOperation("查询秒杀订单详情")
    public Result<Object> getOrderDetail(@PathVariable Long id) {
        log.info("查询秒杀订单详情：{}", id);
        Object orderDetail = seckillOrderService.getOrderDetail(id);
        return Result.success(orderDetail);
    }

    /**
     * 取消秒杀订单
     * @param id
     * @return
     */
    @PutMapping("/order/cancel/{id}")
    @ApiOperation("取消秒杀订单")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        log.info("取消秒杀订单：{}", id);
        seckillOrderService.cancelOrder(id);
        return Result.success();
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("/order/again/{id}")
    @ApiOperation("再来一单")
    public Result<SeckillOrderSubmitVO> repeatOrder(@PathVariable Long id) {
        log.info("再来一单：{}", id);
        SeckillOrderSubmitVO result = seckillOrderService.repeatOrder(id);
        return Result.success(result);
    }

    /**
     * 检查用户购买资格
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @GetMapping("/check/eligibility")
    @ApiOperation("检查用户购买资格")
    public Result<Object> checkEligibility(@RequestParam Long seckillGoodsId,
                                          @RequestParam Integer quantity) {
        log.info("检查用户购买资格：seckillGoodsId={}, quantity={}", seckillGoodsId, quantity);
        Object eligibility = seckillGoodsService.checkEligibility(seckillGoodsId, quantity);
        return Result.success(eligibility);
    }
}