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
 * ��ɱ������ؽӿ�
 */
@RestController
@RequestMapping("/admin/seckill")
@Api(tags = "��ɱ������ؽӿ�")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillActivityService seckillActivityService;
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * ��ҳ��ѯ��ɱ��б�
     * @param seckillActivityPageQueryDTO
     * @return
     */
    @GetMapping("/activity/page")
    @ApiOperation("��ҳ��ѯ��ɱ��б�")
    public Result<PageResult> pageQuery(SeckillActivityPageQueryDTO seckillActivityPageQueryDTO) {
        log.info("��ҳ��ѯ��ɱ���{}", seckillActivityPageQueryDTO);
        PageResult pageResult = seckillActivityService.pageQuery(seckillActivityPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * ������ɱ�
     * @param seckillActivityDTO
     * @return
     */
    @PostMapping("/activity")
    @ApiOperation("������ɱ�")
    public Result<String> save(@RequestBody SeckillActivityDTO seckillActivityDTO) {
        log.info("������ɱ���{}", seckillActivityDTO);
        seckillActivityService.save(seckillActivityDTO);
        return Result.success();
    }

    /**
     * �޸���ɱ�
     * @param seckillActivityDTO
     * @return
     */
    @PutMapping("/activity")
    @ApiOperation("�޸���ɱ�")
    public Result<String> update(@RequestBody SeckillActivityDTO seckillActivityDTO) {
        log.info("�޸���ɱ���{}", seckillActivityDTO);
        seckillActivityService.update(seckillActivityDTO);
        return Result.success();
    }

    /**
     * ɾ����ɱ�
     * @param id
     * @return
     */
    @DeleteMapping("/activity")
    @ApiOperation("ɾ����ɱ�")
    public Result<String> delete(@RequestParam Long id) {
        log.info("ɾ����ɱ���{}", id);
        seckillActivityService.deleteById(id);
        return Result.success();
    }

    /**
     * ��ѯ��ɱ�����
     * @param id
     * @return
     */
    @GetMapping("/activity/{id}")
    @ApiOperation("��ѯ��ɱ�����")
    public Result<SeckillActivityVO> getById(@PathVariable Long id) {
        log.info("��ѯ��ɱ����飺{}", id);
        SeckillActivityVO seckillActivityVO = seckillActivityService.getById(id);
        return Result.success(seckillActivityVO);
    }

    /**
     * ����/ͣ����ɱ�
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/activity/status/{status}")
    @ApiOperation("����/ͣ����ɱ�")
    public Result<String> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("����/ͣ����ɱ���{}��{}", status, id);
        seckillActivityService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * ��ѯ������Ʒ�б�
     * @param type
     * @param name
     * @return
     */
    @GetMapping("/goods/available")
    @ApiOperation("��ѯ������Ʒ�б�")
    public Result<List<SeckillGoodsVO>> getAvailableGoods(@RequestParam Integer type, 
                                                          @RequestParam(required = false) String name) {
        log.info("��ѯ������Ʒ�б�{}��{}", type, name);
        List<SeckillGoodsVO> list = seckillGoodsService.getAvailableGoods(type, name);
        return Result.success(list);
    }

    /**
     * �޸���ɱ��Ʒ��Ϣ
     * @param seckillGoodsDTO
     * @return
     */
    @PutMapping("/goods")
    @ApiOperation("�޸���ɱ��Ʒ��Ϣ")
    public Result<String> updateGoods(@RequestBody SeckillGoodsDTO seckillGoodsDTO) {
        log.info("�޸���ɱ��Ʒ��Ϣ��{}", seckillGoodsDTO);
        seckillGoodsService.update(seckillGoodsDTO);
        return Result.success();
    }

    /**
     * ɾ����ɱ��Ʒ
     * @param id
     * @return
     */
    @DeleteMapping("/goods")
    @ApiOperation("ɾ����ɱ��Ʒ")
    public Result<String> deleteGoods(@RequestParam Long id) {
        log.info("ɾ����ɱ��Ʒ��{}", id);
        seckillGoodsService.deleteById(id);
        return Result.success();
    }

    /**
     * ��ѯ��ɱ�����б�
     * @param seckillOrderPageQueryDTO
     * @return
     */
    @GetMapping("/order/page")
    @ApiOperation("��ѯ��ɱ�����б�")
    public Result<PageResult> orderPageQuery(SeckillOrderPageQueryDTO seckillOrderPageQueryDTO) {
        log.info("��ҳ��ѯ��ɱ������{}", seckillOrderPageQueryDTO);
        PageResult pageResult = seckillOrderService.adminPageQuery(seckillOrderPageQueryDTO);
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
        log.info("��ѯ��ɱ�������飺{}", id);
        SeckillOrderVO seckillOrderVO = seckillOrderService.getOrderDetails(id);
        return Result.success(seckillOrderVO);
    }

    /**
     * ��ѯ��ɱ�ͳ������
     * @param activityId
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("��ѯ��ɱ�ͳ������")
    public Result<SeckillStatisticsVO> getStatistics(@RequestParam Long activityId,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("��ѯ��ɱ�ͳ�����ݣ�{}��{}��{}", activityId, beginDate, endDate);
        SeckillStatisticsVO statisticsVO = seckillActivityService.getStatistics(activityId, beginDate, endDate);
        return Result.success(statisticsVO);
    }
}