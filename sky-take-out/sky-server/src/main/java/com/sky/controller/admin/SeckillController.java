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
 * �̼Ҷ���ɱ����
 */
@RestController
@RequestMapping("/admin/seckill")
@Api(tags = "�̼Ҷ���ɱ����ӿ�")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * ������ɱ�
     */
    @PostMapping("/create")
    @ApiOperation("������ɱ�")
    public Result<String> createSeckill(@RequestBody SeckillDTO seckillDTO) {
        log.info("������ɱ���{}", seckillDTO);
        seckillService.createSeckill(seckillDTO);
        return Result.success("��ɱ������ɹ�");
    }

    /**
     * ��ȡ��ɱ��б�
     */
    @GetMapping("/list")
    @ApiOperation("��ȡ��ɱ��б�")
    public Result<PageResult> getSeckillList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        log.info("��ѯ��ɱ��б�page={}, pageSize={}, status={}", page, pageSize, status);
        PageResult pageResult = seckillService.getSeckillList(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * ������ɱ�
     */
    @PutMapping("/update/{seckillId}")
    @ApiOperation("������ɱ�")
    public Result<String> updateSeckill(@PathVariable Long seckillId, @RequestBody SeckillDTO seckillDTO) {
        log.info("������ɱ���seckillId={}, seckillDTO={}", seckillId, seckillDTO);
        seckillService.updateSeckill(seckillId, seckillDTO);
        return Result.success("��ɱ����³ɹ�");
    }

    /**
     * ɾ����ɱ�
     */
    @DeleteMapping("/delete/{seckillId}")
    @ApiOperation("ɾ����ɱ�")
    public Result<String> deleteSeckill(@PathVariable Long seckillId) {
        log.info("ɾ����ɱ���seckillId={}", seckillId);
        seckillService.deleteSeckill(seckillId);
        return Result.success("��ɱ�ɾ���ɹ�");
    }

    /**
     * ������ɱ���
     */
    @PostMapping("/stock/update")
    @ApiOperation("������ɱ���")
    public Result<String> updateStock(@RequestBody SeckillStockUpdateDTO stockUpdateDTO) {
        log.info("������ɱ��棺{}", stockUpdateDTO);
        seckillService.updateStock(stockUpdateDTO);
        return Result.success("�����³ɹ�");
    }

    /**
     * �ع���ɱ���
     */
    @PostMapping("/stock/rollback")
    @ApiOperation("�ع���ɱ���")
    public Result<String> rollbackStock(
            @RequestParam String orderId,
            @RequestParam Long itemId,
            @RequestParam Integer rollbackQuantity) {
        log.info("�ع���ɱ��棺orderId={}, itemId={}, rollbackQuantity={}", orderId, itemId, rollbackQuantity);
        seckillService.rollbackStock(orderId, itemId, rollbackQuantity);
        return Result.success("���ع��ɹ�");
    }

    /**
     * ��ȡ��ɱͳ������
     */
    @GetMapping("/statistics/{seckillId}")
    @ApiOperation("��ȡ��ɱͳ������")
    public Result<Object> getStatistics(@PathVariable Long seckillId) {
        log.info("��ȡ��ɱͳ�����ݣ�seckillId={}", seckillId);
        Object statistics = seckillService.getStatistics(seckillId);
        return Result.success(statistics);
    }
}

