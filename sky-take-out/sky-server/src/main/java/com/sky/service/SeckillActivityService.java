package com.sky.service;

import com.sky.dto.SeckillActivityDTO;
import com.sky.dto.SeckillActivityPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SeckillActivityVO;
import com.sky.vo.SeckillStatisticsVO;

import java.time.LocalDate;
import java.util.List;

/**
 * ��ɱ�Service�ӿ�
 */
public interface SeckillActivityService {

    /**
     * ��ҳ��ѯ��ɱ�
     * @param seckillActivityPageQueryDTO
     * @return
     */
    PageResult pageQuery(SeckillActivityPageQueryDTO seckillActivityPageQueryDTO);

    /**
     * ������ɱ�
     * @param seckillActivityDTO
     */
    void save(SeckillActivityDTO seckillActivityDTO);

    /**
     * �޸���ɱ�
     * @param seckillActivityDTO
     */
    void update(SeckillActivityDTO seckillActivityDTO);

    /**
     * ɾ����ɱ�
     * @param id
     */
    void deleteById(Long id);

    /**
     * ����id��ѯ��ɱ�����
     * @param id
     * @return
     */
    SeckillActivityVO getById(Long id);

    /**
     * ���û�ͣ����ɱ�
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * ��ѯ�����е���ɱ�
     * @return
     */
    List<SeckillActivityVO> getActiveActivities();

    /**
     * ��ѯ��ɱ�ͳ������
     * @param activityId
     * @param beginDate
     * @param endDate
     * @return
     */
    SeckillStatisticsVO getStatistics(Long activityId, LocalDate beginDate, LocalDate endDate);
}



