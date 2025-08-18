package com.sky.controller.admin;

import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SeckillStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * ��ɱ�����������
 */
@RestController
@RequestMapping("/admin/seckill/stock")
@Api(tags = "��ɱ��������ؽӿ�")
@Slf4j
public class SeckillStockController {

    @Autowired
    private SeckillStockService seckillStockService;

    /**
     * ��ҳ��ѯ�����־
     * @param page ҳ��
     * @param pageSize ҳ��С
     * @param seckillGoodsId ��ɱ��ƷID
     * @param operationType �������ͣ�1-�ۼ���2-�ͷţ�3-��ʼ����
     * @param startTime ��ʼʱ��
     * @param endTime ����ʱ��
     * @return ��ҳ���
     */
    @GetMapping("/log/page")
    @ApiOperation("��ҳ��ѯ�����־")
    public Result<PageResult> pageQueryStockLog(
            @RequestParam(defaultValue = "1") @ApiParam("ҳ��") int page,
            @RequestParam(defaultValue = "10") @ApiParam("ҳ��С") int pageSize,
            @RequestParam(required = false) @ApiParam("��ɱ��ƷID") Long seckillGoodsId,
            @RequestParam(required = false) @ApiParam("��������") Integer operationType,
            @RequestParam(required = false) @ApiParam("��ʼʱ��") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @ApiParam("����ʱ��")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        log.info("��ҳ��ѯ�����־��ҳ�룺{}��ҳ��С��{}����ƷID��{}���������ͣ�{}��ʱ�䷶Χ��{} - {}", 
                page, pageSize, seckillGoodsId, operationType, startTime, endTime);
        
        PageResult pageResult = seckillStockService.pageQueryStockLog(
                page, pageSize, seckillGoodsId, operationType, startTime, endTime);
        
        return Result.success(pageResult);
    }

    /**
     * ��ȡ���ͳ����Ϣ
     * @param seckillGoodsId ��ɱ��ƷID
     * @return ͳ����Ϣ
     */
    @GetMapping("/statistics/{seckillGoodsId}")
    @ApiOperation("��ȡ���ͳ����Ϣ")
    public Result<Object> getStockStatistics(
            @PathVariable @ApiParam("��ɱ��ƷID") Long seckillGoodsId) {
        
        log.info("��ȡ���ͳ����Ϣ����ƷID��{}", seckillGoodsId);
        
        Object statistics = seckillStockService.getStockStatistics(seckillGoodsId);
        
        return Result.success(statistics);
    }

    /**
     * �ֶ��ͷſ�棨�������ʹ�ã�
     * @param seckillGoodsId ��ɱ��ƷID
     * @param quantity �ͷ�����
     * @param reason �ͷ�ԭ��
     * @return �������
     */
    @PostMapping("/release")
    @ApiOperation("�ֶ��ͷſ��")
    public Result<String> releaseStock(
            @RequestParam @ApiParam("��ɱ��ƷID") Long seckillGoodsId,
            @RequestParam @ApiParam("�ͷ�����") Integer quantity,
            @RequestParam @ApiParam("�ͷ�ԭ��") String reason) {
        
        log.info("�ֶ��ͷſ�棬��ƷID��{}��������{}��ԭ��{}", seckillGoodsId, quantity, reason);
        
        boolean success = seckillStockService.releaseStock(
                seckillGoodsId, quantity, null, null, null, "����Ա�ֶ��ͷţ�" + reason);
        
        if (success) {
            return Result.success("����ͷųɹ�");
        } else {
            return Result.error("����ͷ�ʧ��");
        }
    }
}



