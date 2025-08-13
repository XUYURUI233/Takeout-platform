package com.sky.controller.user;

import com.sky.dto.SeckillPurchaseDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SeckillService;
import com.sky.vo.SeckillGoodsVO;
import com.sky.vo.SeckillOrderVO;
import com.sky.vo.SeckillVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序端秒杀接口
 */
@RestController("userSeckillController")
@RequestMapping("/user/seckill")
@Api(tags = "小程序端秒杀接口")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 获取秒杀横幅列表
     */
    @GetMapping("/banner/list")
    @ApiOperation("获取秒杀横幅列表")
    public Result<List<SeckillVO>> getBannerList() {
        log.info("获取秒杀横幅列表");
        List<SeckillVO> bannerList = seckillService.getBannerList();
        return Result.success(bannerList);
    }

    /**
     * 获取秒杀商品列表
     */
    @GetMapping("/goods/list")
    @ApiOperation("获取秒杀商品列表")
    public Result<PageResult> getGoodsList(
            @RequestParam(required = false) Long seckillId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取秒杀商品列表：seckillId={}, page={}, pageSize={}", seckillId, page, pageSize);
        PageResult pageResult = seckillService.getGoodsList(seckillId, page, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 参与秒杀购买
     */
    @PostMapping("/purchase")
    @ApiOperation("参与秒杀购买")
    public Result<SeckillOrderVO> purchase(@RequestBody SeckillPurchaseDTO purchaseDTO) {
        log.info("参与秒杀购买：{}", purchaseDTO);
        SeckillOrderVO orderVO = seckillService.purchase(purchaseDTO);
        return Result.success(orderVO);
    }

    /**
     * 获取用户秒杀订单列表
     */
    @GetMapping("/orders/list")
    @ApiOperation("获取用户秒杀订单列表")
    public Result<PageResult> getUserOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        log.info("获取用户秒杀订单列表：page={}, pageSize={}, status={}", page, pageSize, status);
        PageResult pageResult = seckillService.getUserOrders(page, pageSize, status);
        return Result.success(pageResult);
    }
}

