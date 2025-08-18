package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SeckillActivityDTO;
import com.sky.dto.SeckillGoodsDTO;
import com.sky.entity.SeckillActivity;
import com.sky.entity.SeckillGoods;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SeckillActivityMapper;
import com.sky.mapper.SeckillGoodsMapper;
import com.sky.mapper.SeckillOrderMapper;
import com.sky.result.PageResult;
import com.sky.service.SeckillActivityService;
import com.sky.vo.SeckillActivityVO;
import com.sky.vo.SeckillGoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SeckillActivityServiceImpl implements SeckillActivityService {

    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    /**
     * 分页查询秒杀活动列表
     * @param page
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    @Override
    public PageResult pageQuery(int page, int pageSize, String name, Integer status) {
        PageHelper.startPage(page, pageSize);
        Page<SeckillActivity> pageResult = seckillActivityMapper.pageQuery(name, status);

        List<SeckillActivityVO> voList = new ArrayList<>();
        for (SeckillActivity activity : pageResult.getResult()) {
            SeckillActivityVO vo = new SeckillActivityVO();
            BeanUtils.copyProperties(activity, vo);
            
            // 查询商品数量
            List<SeckillGoods> goodsList = seckillGoodsMapper.getByActivityId(activity.getId());
            vo.setGoodsCount(goodsList.size());
            
            voList.add(vo);
        }

        return new PageResult(pageResult.getTotal(), voList);
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

        seckillActivity.setStatus(SeckillActivity.NOT_STARTED);
        seckillActivity.setCreateTime(LocalDateTime.now());
        seckillActivity.setUpdateTime(LocalDateTime.now());
        seckillActivity.setCreateUser(BaseContext.getCurrentId());
        seckillActivity.setUpdateUser(BaseContext.getCurrentId());

        seckillActivityMapper.insert(seckillActivity);

        // 保存商品列表
        if (seckillActivityDTO.getGoodsList() != null && !seckillActivityDTO.getGoodsList().isEmpty()) {
            List<SeckillGoods> seckillGoodsList = new ArrayList<>();
            for (SeckillGoodsDTO goodsDTO : seckillActivityDTO.getGoodsList()) {
                SeckillGoods seckillGoods = new SeckillGoods();
                BeanUtils.copyProperties(goodsDTO, seckillGoods);
                
                seckillGoods.setActivityId(seckillActivity.getId());
                seckillGoods.setAvailableStock(goodsDTO.getTotalStock());
                seckillGoods.setSoldCount(0);
                seckillGoods.setStatus(SeckillGoods.ONLINE);
                seckillGoods.setVersion(1);
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
    public void update(SeckillActivityDTO seckillActivityDTO) {
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtils.copyProperties(seckillActivityDTO, seckillActivity);

        seckillActivity.setUpdateTime(LocalDateTime.now());
        seckillActivity.setUpdateUser(BaseContext.getCurrentId());

        seckillActivityMapper.update(seckillActivity);
    }

    /**
     * 删除秒杀活动
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        SeckillActivity activity = seckillActivityMapper.getById(id);
        if (activity == null) {
            throw new DeletionNotAllowedException(MessageConstant.UNKNOWN_ERROR);
        }

        // 只能删除未开始的活动
        if (!activity.getStatus().equals(SeckillActivity.NOT_STARTED)) {
            throw new DeletionNotAllowedException("只能删除未开始的活动");
        }

        // 删除活动关联的商品
        seckillGoodsMapper.deleteByActivityId(id);
        
        // 删除活动
        seckillActivityMapper.deleteById(id);
    }

    /**
     * 根据ID查询秒杀活动详情
     * @param id
     * @return
     */
    @Override
    public SeckillActivityVO getById(Long id) {
        SeckillActivity activity = seckillActivityMapper.getById(id);
        if (activity == null) {
            return null;
        }

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
     * 启用/停用秒杀活动
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        SeckillActivity seckillActivity = SeckillActivity.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();

        seckillActivityMapper.update(seckillActivity);
    }

    /**
     * 查询进行中的秒杀活动
     * @return
     */
    @Override
    public List<SeckillActivityVO> getActiveActivities() {
        LocalDateTime now = LocalDateTime.now();
        List<SeckillActivity> activities = seckillActivityMapper.getActiveActivities(now);

        List<SeckillActivityVO> voList = new ArrayList<>();
        for (SeckillActivity activity : activities) {
            SeckillActivityVO vo = new SeckillActivityVO();
            BeanUtils.copyProperties(activity, vo);

            // 计算剩余时间
            Duration duration = Duration.between(now, activity.getEndTime());
            vo.setRemainingTime(duration.getSeconds());

            voList.add(vo);
        }

        return voList;
    }

    /**
     * 查询秒杀活动统计数据
     * @param activityId 活动ID
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    @Override
    public Object getStatistics(Long activityId, String beginDate, String endDate) {
        log.info("查询秒杀活动统计数据，活动ID：{}，开始日期：{}，结束日期：{}", activityId, beginDate, endDate);

        // 统计总订单数
        Integer totalOrders = seckillOrderMapper.countTotalOrders(activityId, beginDate, endDate);
        totalOrders = totalOrders != null ? totalOrders : 0;

        // 统计成功订单数（已完成状态的订单）
        Integer successOrders = seckillOrderMapper.countSuccessOrders(activityId, beginDate, endDate);
        successOrders = successOrders != null ? successOrders : 0;

        // 统计总金额
        Double totalAmount = seckillOrderMapper.sumTotalAmount(activityId, beginDate, endDate);
        totalAmount = totalAmount != null ? totalAmount : 0.0;

        // 统计总库存和已售出数量
        List<SeckillGoods> goodsList = seckillGoodsMapper.getByActivityId(activityId);
        Integer totalStock = 0;
        Integer soldCount = 0;
        for (SeckillGoods goods : goodsList) {
            totalStock += goods.getTotalStock();
            soldCount += goods.getSoldCount();
        }

        // 计算成功率
        Double successRate = 0.0;
        if (totalOrders > 0) {
            successRate = successOrders.doubleValue() / totalOrders.doubleValue();
        }

        // 构建返回结果
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalOrders", totalOrders);
        statistics.put("successOrders", successOrders);
        statistics.put("totalAmount", totalAmount);
        statistics.put("totalStock", totalStock);
        statistics.put("soldCount", soldCount);
        statistics.put("successRate", successRate);

        log.info("秒杀活动统计数据查询完成：{}", statistics);
        return statistics;
    }
}