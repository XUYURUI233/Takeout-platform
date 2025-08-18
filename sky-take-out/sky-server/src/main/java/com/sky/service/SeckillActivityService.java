package com.sky.service;

import com.sky.dto.SeckillActivityDTO;
import com.sky.result.PageResult;
import com.sky.vo.SeckillActivityVO;

import java.util.List;

/**
 * 秒杀活动Service接口
 */
public interface SeckillActivityService {

    /**
     * 分页查询秒杀活动列表
     * @param page
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    PageResult pageQuery(int page, int pageSize, String name, Integer status);

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
     * 根据ID查询秒杀活动详情
     * @param id
     * @return
     */
    SeckillActivityVO getById(Long id);

    /**
     * 启用/停用秒杀活动
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
     * @param activityId 活动ID
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    Object getStatistics(Long activityId, String beginDate, String endDate);
}