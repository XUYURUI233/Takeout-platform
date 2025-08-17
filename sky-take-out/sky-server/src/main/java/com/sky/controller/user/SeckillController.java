package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.SeckillOrderDTO;
import com.sky.mapper.SeckillOrderMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SeckillActivityService;
import com.sky.service.SeckillGoodsService;
import com.sky.service.SeckillOrderService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    /**
     * 查询进行中的秒杀活动
     * @return
     */
    @GetMapping("/activity/active")
    @ApiOperation("查询进行中的秒杀活动")
    public Result<List<SeckillActivityVO>> getActiveActivities() {
        log.info("查询进行中的秒杀活动");
        List<SeckillActivityVO> list = seckillActivityService.getActiveActivities();
        return Result.success(list);
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
        List<SeckillGoodsVO> list = seckillGoodsService.getByActivityId(activityId);
        return Result.success(list);
    }

    /**
     * 查询秒杀商品详情
     * @param id
     * @return
     */
    @GetMapping("/goods/{id}")
    @ApiOperation("查询秒杀商品详情")
    public Result<SeckillGoodsVO> getGoodsById(@PathVariable Long id) {
        log.info("查询秒杀商品详情：{}", id);
        SeckillGoodsVO seckillGoodsVO = seckillGoodsService.getById(id);
        return Result.success(seckillGoodsVO);
    }

    /**
     * 立即购买（生成秒杀订单）
     * @param seckillOrderDTO
     * @return
     */
    @PostMapping("/order/submit")
    @ApiOperation("立即购买（生成秒杀订单）")
    public Result<SeckillOrderSubmitVO> submitOrder(@RequestBody SeckillOrderDTO seckillOrderDTO) {
        log.info("用户提交秒杀订单：{}", seckillOrderDTO);
        SeckillOrderSubmitVO seckillOrderSubmitVO = seckillOrderService.submitOrder(seckillOrderDTO);
        return Result.success(seckillOrderSubmitVO);
    }

    /**
     * 秒杀订单支付
     * @param orderNumber
     * @param payMethod
     * @return
     */
    @PutMapping("/order/payment")
    @ApiOperation("秒杀订单支付")
    public Result<String> payment(@RequestParam String orderNumber, @RequestParam Integer payMethod) {
        log.info("秒杀订单支付：{}，{}", orderNumber, payMethod);
        seckillOrderService.payment(orderNumber, payMethod);
        return Result.success();
    }

    /**
     * 取消秒杀订单
     * @param id
     * @return
     */
    @PutMapping("/order/cancel/{id}")
    @ApiOperation("取消秒杀订单")
    public Result<String> cancelOrder(@PathVariable Long id) {
        log.info("用户取消秒杀订单：{}", id);
        seckillOrderService.cancelOrder(id);
        return Result.success();
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
    public Result<PageResult> getOrderList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) Integer status) {
        log.info("查询用户秒杀订单列表：{}，{}，{}", page, pageSize, status);
        PageResult pageResult = seckillOrderService.userPageQuery(page, pageSize, status);
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
        log.info("查询用户秒杀订单详情：{}", id);
        SeckillOrderVO seckillOrderVO = seckillOrderService.getUserOrderDetails(id);
        return Result.success(seckillOrderVO);
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("/order/again/{id}")
    @ApiOperation("再来一单")
    public Result<SeckillOrderSubmitVO> againOrder(@PathVariable Long id) {
        log.info("再来一单：{}", id);
        SeckillOrderSubmitVO seckillOrderSubmitVO = seckillOrderService.againOrder(id);
        return Result.success(seckillOrderSubmitVO);
    }

    /**
     * 检查用户购买资格
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @GetMapping("/check/eligibility")
    @ApiOperation("检查用户购买资格")
    public Result<SeckillEligibilityVO> checkEligibility(@RequestParam Long seckillGoodsId,
                                                        @RequestParam Integer quantity) {
        log.info("检查用户购买资格：{}，{}", seckillGoodsId, quantity);
        boolean canPurchase = seckillGoodsService.checkEligibility(seckillGoodsId, quantity);
        
        // 构建返回的VO对象
        SeckillEligibilityVO eligibilityVO = new SeckillEligibilityVO();
        eligibilityVO.setCanPurchase(canPurchase);
        
        if (canPurchase) {
            // 获取商品信息
            SeckillGoodsVO goods = seckillGoodsService.getById(seckillGoodsId);
            if (goods != null) {
                eligibilityVO.setLimitCount(goods.getLimitCount());
                eligibilityVO.setAvailableStock(goods.getAvailableStock());
                
                // 获取用户已购买数量
                Long userId = BaseContext.getCurrentId();
                if (userId != null) {
                    Integer userBoughtCount = seckillOrderMapper.getUserBoughtCount(seckillGoodsId, userId);
                    eligibilityVO.setUserPurchased(userBoughtCount != null ? userBoughtCount : 0);
                    eligibilityVO.setRemainingQuota(goods.getLimitCount() - eligibilityVO.getUserPurchased());
                }
            }
        }
        
        return Result.success(eligibilityVO);
    }
}