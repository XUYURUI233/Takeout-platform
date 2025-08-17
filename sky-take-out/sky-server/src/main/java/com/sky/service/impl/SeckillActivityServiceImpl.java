package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.SeckillConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SeckillActivityDTO;
import com.sky.dto.SeckillActivityPageQueryDTO;
import com.sky.dto.SeckillGoodsDTO;
import com.sky.entity.SeckillActivity;
import com.sky.entity.SeckillGoods;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SeckillActivityMapper;
import com.sky.mapper.SeckillGoodsMapper;
import com.sky.result.PageResult;
import com.sky.service.SeckillActivityService;
import com.sky.vo.SeckillActivityVO;
import com.sky.vo.SeckillGoodsVO;
import com.sky.vo.SeckillStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 秒杀活动Service实现类
 */
@Service
@Slf4j
public class SeckillActivityServiceImpl implements SeckillActivityService {

    @Autowired
    private SeckillActivityMapper seckillActivityMapper;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     * 分页查询秒杀活动
     * @param seckillActivityPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SeckillActivityPageQueryDTO seckillActivityPageQueryDTO) {
        PageHelper.startPage(seckillActivityPageQueryDTO.getPage(), seckillActivityPageQueryDTO.getPageSize());
        Page<SeckillActivity> page = seckillActivityMapper.pageQuery(seckillActivityPageQueryDTO);

        // 转换为VO
        List<SeckillActivityVO> list = new ArrayList<>();
        for (SeckillActivity activity : page.getResult()) {
            SeckillActivityVO vo = new SeckillActivityVO();
            BeanUtils.copyProperties(activity, vo);
            
            // 查询商品数量
            List<SeckillGoods> goodsList = seckillGoodsMapper.getByActivityId(activity.getId());
            vo.setGoodsCount(goodsList.size());
            
            list.add(vo);
        }

        return new PageResult(page.getTotal(), list);
    }

    /**
     * 新增秒杀活动
     * @param seckillActivityDTO
     */
    @Override
    @Transactional
    public void save(SeckillActivityDTO seckillActivityDTO) {
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtils.copyProperties(seckillActivityDTO, seckillActivity);
        seckillActivity.setStatus(SeckillConstant.ACTIVITY_STATUS_NOT_STARTED);

        // 插入活动
        seckillActivityMapper.insert(seckillActivity);
        Long activityId = seckillActivity.getId();

        // 插入商品
        if (seckillActivityDTO.getGoodsList() != null && !seckillActivityDTO.getGoodsList().isEmpty()) {
            List<SeckillGoods> seckillGoodsList = new ArrayList<>();
            for (SeckillGoodsDTO goodsDTO : seckillActivityDTO.getGoodsList()) {
                SeckillGoods seckillGoods = new SeckillGoods();
                BeanUtils.copyProperties(goodsDTO, seckillGoods);
                seckillGoods.setActivityId(activityId);
                seckillGoods.setAvailableStock(goodsDTO.getTotalStock());
                seckillGoods.setSoldCount(0);
                seckillGoods.setVersion(0);
                seckillGoods.setStatus(SeckillConstant.GOODS_STATUS_ON);
                seckillGoods.setCreateTime(LocalDateTime.now());
                seckillGoods.setUpdateTime(LocalDateTime.now());
                seckillGoods.setCreateUser(BaseContext.getCurrentId());
                seckillGoods.setUpdateUser(BaseContext.getCurrentId());
                seckillGoodsList.add(seckillGoods);
            }
            seckillGoodsMapper.insertBatch(seckillGoodsList);
        }
    }

    /**
     * 修改秒杀活动
     * @param seckillActivityDTO
     */
    @Override
    @Transactional
    public void update(SeckillActivityDTO seckillActivityDTO) {
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtils.copyProperties(seckillActivityDTO, seckillActivity);
        
        // 只有未开始的活动才能修改
        SeckillActivity existActivity = seckillActivityMapper.getById(seckillActivity.getId());
        if (existActivity.getStatus() != SeckillConstant.ACTIVITY_STATUS_NOT_STARTED) {
            throw new DeletionNotAllowedException("只有未开始的活动才能修改");
        }

        seckillActivityMapper.update(seckillActivity);
    }

    /**
     * 删除秒杀活动
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        // 只有未开始的活动才能删除
        SeckillActivity activity = seckillActivityMapper.getById(id);
        if (activity.getStatus() != SeckillConstant.ACTIVITY_STATUS_NOT_STARTED) {
            throw new DeletionNotAllowedException("只有未开始的活动才能删除");
        }

        // 删除活动和相关商品
        seckillActivityMapper.deleteById(id);
        seckillGoodsMapper.deleteByActivityId(id);
    }

    /**
     * 根据id查询秒杀活动详情
     * @param id
     * @return
     */
    @Override
    public SeckillActivityVO getById(Long id) {
        SeckillActivity activity = seckillActivityMapper.getById(id);
        SeckillActivityVO vo = new SeckillActivityVO();
        BeanUtils.copyProperties(activity, vo);

        // 查询商品列表
        List<SeckillGoods> goodsList = seckillGoodsMapper.getByActivityId(id);
        List<SeckillGoodsVO> goodsVOList = new ArrayList<>();
        for (SeckillGoods goods : goodsList) {
            SeckillGoodsVO goodsVO = new SeckillGoodsVO();
            BeanUtils.copyProperties(goods, goodsVO);
            goodsVOList.add(goodsVO);
        }
        vo.setGoodsList(goodsVOList);

        return vo;
    }

    /**
     * 启用或停用秒杀活动
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        SeckillActivity activity = new SeckillActivity();
        activity.setId(id);
        activity.setStatus(status);
        activity.setUpdateUser(BaseContext.getCurrentId());
        seckillActivityMapper.startOrStop(status, id, BaseContext.getCurrentId());
    }

    /**
     * 查询进行中的秒杀活动
     * @return
     */
    @Override
    public List<SeckillActivityVO> getActiveActivities() {
        List<SeckillActivity> activities = seckillActivityMapper.getActiveActivities(LocalDateTime.now());
        List<SeckillActivityVO> voList = new ArrayList<>();
        
        for (SeckillActivity activity : activities) {
            SeckillActivityVO vo = new SeckillActivityVO();
            BeanUtils.copyProperties(activity, vo);
            
            // 计算剩余时间
            LocalDateTime now = LocalDateTime.now();
            if (activity.getEndTime().isAfter(now)) {
                vo.setRemainingTime(ChronoUnit.SECONDS.between(now, activity.getEndTime()));
            } else {
                vo.setRemainingTime(0L);
            }
            
            voList.add(vo);
        }
        
        return voList;
    }

    /**
     * 查询秒杀活动统计数据
     * @param activityId
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public SeckillStatisticsVO getStatistics(Long activityId, LocalDate beginDate, LocalDate endDate) {
        // TODO: 实现统计逻辑
        return new SeckillStatisticsVO();
    }
}
