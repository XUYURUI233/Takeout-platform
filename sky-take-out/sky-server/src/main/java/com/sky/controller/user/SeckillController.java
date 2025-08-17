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
 * �û�����ɱ��ؽӿ�
 */
@RestController("userSeckillController")
@RequestMapping("/user/seckill")
@Api(tags = "�û�����ɱ��ؽӿ�")
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
     * ��ѯ�����е���ɱ�
     * @return
     */
    @GetMapping("/activity/active")
    @ApiOperation("��ѯ�����е���ɱ�")
    public Result<List<SeckillActivityVO>> getActiveActivities() {
        log.info("��ѯ�����е���ɱ�");
        List<SeckillActivityVO> list = seckillActivityService.getActiveActivities();
        return Result.success(list);
    }

    /**
     * ��ѯ��ɱ���Ʒ�б�
     * @param activityId
     * @return
     */
    @GetMapping("/activity/{activityId}/goods")
    @ApiOperation("��ѯ��ɱ���Ʒ�б�")
    public Result<List<SeckillGoodsVO>> getActivityGoods(@PathVariable Long activityId) {
        log.info("��ѯ��ɱ���Ʒ�б�{}", activityId);
        List<SeckillGoodsVO> list = seckillGoodsService.getByActivityId(activityId);
        return Result.success(list);
    }

    /**
     * ��ѯ��ɱ��Ʒ����
     * @param id
     * @return
     */
    @GetMapping("/goods/{id}")
    @ApiOperation("��ѯ��ɱ��Ʒ����")
    public Result<SeckillGoodsVO> getGoodsById(@PathVariable Long id) {
        log.info("��ѯ��ɱ��Ʒ���飺{}", id);
        SeckillGoodsVO seckillGoodsVO = seckillGoodsService.getById(id);
        return Result.success(seckillGoodsVO);
    }

    /**
     * ��������������ɱ������
     * @param seckillOrderDTO
     * @return
     */
    @PostMapping("/order/submit")
    @ApiOperation("��������������ɱ������")
    public Result<SeckillOrderSubmitVO> submitOrder(@RequestBody SeckillOrderDTO seckillOrderDTO) {
        log.info("�û��ύ��ɱ������{}", seckillOrderDTO);
        SeckillOrderSubmitVO seckillOrderSubmitVO = seckillOrderService.submitOrder(seckillOrderDTO);
        return Result.success(seckillOrderSubmitVO);
    }

    /**
     * ��ɱ����֧��
     * @param orderNumber
     * @param payMethod
     * @return
     */
    @PutMapping("/order/payment")
    @ApiOperation("��ɱ����֧��")
    public Result<String> payment(@RequestParam String orderNumber, @RequestParam Integer payMethod) {
        log.info("��ɱ����֧����{}��{}", orderNumber, payMethod);
        seckillOrderService.payment(orderNumber, payMethod);
        return Result.success();
    }

    /**
     * ȡ����ɱ����
     * @param id
     * @return
     */
    @PutMapping("/order/cancel/{id}")
    @ApiOperation("ȡ����ɱ����")
    public Result<String> cancelOrder(@PathVariable Long id) {
        log.info("�û�ȡ����ɱ������{}", id);
        seckillOrderService.cancelOrder(id);
        return Result.success();
    }

    /**
     * ��ѯ�û���ɱ�����б�
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/order/list")
    @ApiOperation("��ѯ�û���ɱ�����б�")
    public Result<PageResult> getOrderList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) Integer status) {
        log.info("��ѯ�û���ɱ�����б�{}��{}��{}", page, pageSize, status);
        PageResult pageResult = seckillOrderService.userPageQuery(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * ��ѯ��ɱ��������
     * @param id
     * @return
     */
    @GetMapping("/order/details/{id}")
    @ApiOperation("��ѯ��ɱ��������")
    public Result<SeckillOrderVO> getOrderDetails(@PathVariable Long id) {
        log.info("��ѯ�û���ɱ�������飺{}", id);
        SeckillOrderVO seckillOrderVO = seckillOrderService.getUserOrderDetails(id);
        return Result.success(seckillOrderVO);
    }

    /**
     * ����һ��
     * @param id
     * @return
     */
    @PostMapping("/order/again/{id}")
    @ApiOperation("����һ��")
    public Result<SeckillOrderSubmitVO> againOrder(@PathVariable Long id) {
        log.info("����һ����{}", id);
        SeckillOrderSubmitVO seckillOrderSubmitVO = seckillOrderService.againOrder(id);
        return Result.success(seckillOrderSubmitVO);
    }

    /**
     * ����û������ʸ�
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @GetMapping("/check/eligibility")
    @ApiOperation("����û������ʸ�")
    public Result<SeckillEligibilityVO> checkEligibility(@RequestParam Long seckillGoodsId,
                                                        @RequestParam Integer quantity) {
        log.info("����û������ʸ�{}��{}", seckillGoodsId, quantity);
        boolean canPurchase = seckillGoodsService.checkEligibility(seckillGoodsId, quantity);
        
        // �������ص�VO����
        SeckillEligibilityVO eligibilityVO = new SeckillEligibilityVO();
        eligibilityVO.setCanPurchase(canPurchase);
        
        if (canPurchase) {
            // ��ȡ��Ʒ��Ϣ
            SeckillGoodsVO goods = seckillGoodsService.getById(seckillGoodsId);
            if (goods != null) {
                eligibilityVO.setLimitCount(goods.getLimitCount());
                eligibilityVO.setAvailableStock(goods.getAvailableStock());
                
                // ��ȡ�û��ѹ�������
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