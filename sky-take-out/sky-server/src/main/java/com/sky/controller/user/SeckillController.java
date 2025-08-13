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
 * С�������ɱ�ӿ�
 */
@RestController("userSeckillController")
@RequestMapping("/user/seckill")
@Api(tags = "С�������ɱ�ӿ�")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * ��ȡ��ɱ����б�
     */
    @GetMapping("/banner/list")
    @ApiOperation("��ȡ��ɱ����б�")
    public Result<List<SeckillVO>> getBannerList() {
        log.info("��ȡ��ɱ����б�");
        List<SeckillVO> bannerList = seckillService.getBannerList();
        return Result.success(bannerList);
    }

    /**
     * ��ȡ��ɱ��Ʒ�б�
     */
    @GetMapping("/goods/list")
    @ApiOperation("��ȡ��ɱ��Ʒ�б�")
    public Result<PageResult> getGoodsList(
            @RequestParam(required = false) Long seckillId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("��ȡ��ɱ��Ʒ�б�seckillId={}, page={}, pageSize={}", seckillId, page, pageSize);
        PageResult pageResult = seckillService.getGoodsList(seckillId, page, pageSize);
        return Result.success(pageResult);
    }

    /**
     * ������ɱ����
     */
    @PostMapping("/purchase")
    @ApiOperation("������ɱ����")
    public Result<SeckillOrderVO> purchase(@RequestBody SeckillPurchaseDTO purchaseDTO) {
        log.info("������ɱ����{}", purchaseDTO);
        SeckillOrderVO orderVO = seckillService.purchase(purchaseDTO);
        return Result.success(orderVO);
    }

    /**
     * ��ȡ�û���ɱ�����б�
     */
    @GetMapping("/orders/list")
    @ApiOperation("��ȡ�û���ɱ�����б�")
    public Result<PageResult> getUserOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        log.info("��ȡ�û���ɱ�����б�page={}, pageSize={}, status={}", page, pageSize, status);
        PageResult pageResult = seckillService.getUserOrders(page, pageSize, status);
        return Result.success(pageResult);
    }
}

