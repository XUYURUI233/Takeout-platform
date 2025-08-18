package com.sky.service;

import com.sky.dto.SeckillActivityDTO;
import com.sky.result.PageResult;
import com.sky.vo.SeckillActivityVO;

import java.util.List;

/**
 * ��ɱ�Service�ӿ�
 */
public interface SeckillActivityService {

    /**
     * ��ҳ��ѯ��ɱ��б�
     * @param page
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    PageResult pageQuery(int page, int pageSize, String name, Integer status);

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
     * ����ID��ѯ��ɱ�����
     * @param id
     * @return
     */
    SeckillActivityVO getById(Long id);

    /**
     * ����/ͣ����ɱ�
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
     * @param activityId �ID
     * @param beginDate ��ʼ����
     * @param endDate ��������
     * @return
     */
    Object getStatistics(Long activityId, String beginDate, String endDate);
}