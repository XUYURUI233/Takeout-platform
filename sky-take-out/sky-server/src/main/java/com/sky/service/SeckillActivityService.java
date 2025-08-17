package com.sky.service;

import com.sky.dto.SeckillActivityDTO;
import com.sky.dto.SeckillActivityPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SeckillActivityVO;
import com.sky.vo.SeckillStatisticsVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 秒杀活动Service接口
 */
public interface SeckillActivityService {

    /**
     * 分页查询秒杀活动
     * @param seckillActivityPageQueryDTO
     * @return
     */
    PageResult pageQuery(SeckillActivityPageQueryDTO seckillActivityPageQueryDTO);

    /**
     * 新增秒杀活动
     * @param seckillActivityDTO
     */
    void save(SeckillActivityDTO seckillActivityDTO);

    /**
     * 修改秒杀活动
     * @param seckillActivityDTO
     */
    void update(SeckillActivityDTO seckillActivityDTO);

    /**
     * 删除秒杀活动
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据id查询秒杀活动详情
     * @param id
     * @return
     */
    SeckillActivityVO getById(Long id);

    /**
     * 启用或停用秒杀活动
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 查询进行中的秒杀活动
     * @return
     */
    List<SeckillActivityVO> getActiveActivities();

    /**
     * 查询秒杀活动统计数据
     * @param activityId
     * @param beginDate
     * @param endDate
     * @return
     */
    SeckillStatisticsVO getStatistics(Long activityId, LocalDate beginDate, LocalDate endDate);
}



