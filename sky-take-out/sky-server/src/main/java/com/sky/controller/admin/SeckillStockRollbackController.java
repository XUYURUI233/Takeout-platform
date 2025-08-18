package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.SeckillStockRollbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ��ɱ���ع����������
 */
@RestController
@RequestMapping("/admin/seckill/stock/rollback")
@Api(tags = "��ɱ���ع�������ؽӿ�")
@Slf4j
public class SeckillStockRollbackController {

    @Autowired
    private SeckillStockRollbackService seckillStockRollbackService;

    /**
     * �ع��������
     * @param orderId ����ID
     * @param reason �ع�ԭ��
     * @return �������
     */
    @PostMapping("/order")
    @ApiOperation("�ع��������")
    public Result<String> rollbackOrderStock(
            @RequestParam @ApiParam("����ID") Long orderId,
            @RequestParam @ApiParam("�ع�ԭ��") String reason) {
        
        log.info("�ع�������棬����ID��{}��ԭ��{}", orderId, reason);
        
        boolean success = seckillStockRollbackService.rollbackOrderStock(orderId, null, reason);
        
        if (success) {
            return Result.success("�������ع��ɹ�");
        } else {
            return Result.error("�������ع�ʧ��");
        }
    }

    /**
     * �����ع����
     * @param seckillGoodsId ��ɱ��ƷID
     * @param maxRollbackCount ���ع�����
     * @param reason �ع�ԭ��
     * @return �������
     */
    @PostMapping("/batch")
    @ApiOperation("�����ع����")
    public Result<Object> batchRollbackStock(
            @RequestParam @ApiParam("��ɱ��ƷID") Long seckillGoodsId,
            @RequestParam @ApiParam("���ع�����") Integer maxRollbackCount,
            @RequestParam @ApiParam("�ع�ԭ��") String reason) {
        
        log.info("�����ع���棬��ƷID��{}�����ع�������{}��ԭ��{}", seckillGoodsId, maxRollbackCount, reason);
        
        int actualRollbackCount = seckillStockRollbackService.batchRollbackStock(seckillGoodsId, maxRollbackCount, reason);
        
        if (actualRollbackCount > 0) {
            return Result.success("�����ع��ɹ���ʵ�ʻع�������" + actualRollbackCount);
        } else {
            return Result.error("�����ع�ʧ�ܣ�û�пɻع��Ŀ��");
        }
    }

    /**
     * ����ʱ�䷶Χ�ع����
     * @param seckillGoodsId ��ɱ��ƷID
     * @param startTime ��ʼʱ��
     * @param endTime ����ʱ��
     * @param reason �ع�ԭ��
     * @return �������
     */
    @PostMapping("/time-range")
    @ApiOperation("����ʱ�䷶Χ�ع����")
    public Result<Object> rollbackStockByTimeRange(
            @RequestParam @ApiParam("��ɱ��ƷID") Long seckillGoodsId,
            @RequestParam @ApiParam("��ʼʱ��(yyyy-MM-dd HH:mm:ss)") String startTime,
            @RequestParam @ApiParam("����ʱ��(yyyy-MM-dd HH:mm:ss)") String endTime,
            @RequestParam @ApiParam("�ع�ԭ��") String reason) {
        
        log.info("����ʱ�䷶Χ�ع���棬��ƷID��{}��ʱ�䷶Χ��{} - {}��ԭ��{}", 
                seckillGoodsId, startTime, endTime, reason);
        
        int rollbackCount = seckillStockRollbackService.rollbackStockByTimeRange(
                seckillGoodsId, startTime, endTime, reason);
        
        if (rollbackCount > 0) {
            return Result.success("ʱ�䷶Χ�ع��ɹ����ع���������" + rollbackCount);
        } else {
            return Result.error("ʱ�䷶Χ�ع�ʧ�ܣ�û���ҵ��ɻع��ļ�¼");
        }
    }

    /**
     * �����һ����
     * @param seckillGoodsId ��ɱ��ƷID
     * @return һ���Լ����
     */
    @GetMapping("/consistency-check/{seckillGoodsId}")
    @ApiOperation("�����һ����")
    public Result<Object> checkStockConsistency(
            @PathVariable @ApiParam("��ɱ��ƷID") Long seckillGoodsId) {
        
        log.info("�����һ���ԣ���ƷID��{}", seckillGoodsId);
        
        Object result = seckillStockRollbackService.checkStockConsistency(seckillGoodsId);
        
        return Result.success(result);
    }

    /**
     * �޸���治һ������
     * @param seckillGoodsId ��ɱ��ƷID
     * @param targetStock Ŀ����ֵ
     * @param reason �޸�ԭ��
     * @return �������
     */
    @PostMapping("/fix-inconsistency")
    @ApiOperation("�޸���治һ������")
    public Result<String> fixStockInconsistency(
            @RequestParam @ApiParam("��ɱ��ƷID") Long seckillGoodsId,
            @RequestParam @ApiParam("Ŀ����ֵ") Integer targetStock,
            @RequestParam @ApiParam("�޸�ԭ��") String reason) {
        
        log.info("�޸���治һ�£���ƷID��{}��Ŀ���棺{}��ԭ��{}", seckillGoodsId, targetStock, reason);
        
        boolean success = seckillStockRollbackService.fixStockInconsistency(seckillGoodsId, targetStock, reason);
        
        if (success) {
            return Result.success("��治һ���޸��ɹ�");
        } else {
            return Result.error("��治һ���޸�ʧ��");
        }
    }
}



