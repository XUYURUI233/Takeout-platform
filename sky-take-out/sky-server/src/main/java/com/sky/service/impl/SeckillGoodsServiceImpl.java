package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.SeckillGoodsDTO;
import com.sky.entity.Dish;
import com.sky.entity.SeckillActivity;
import com.sky.entity.SeckillGoods;
import com.sky.entity.SeckillUserRecord;
import com.sky.entity.Setmeal;
import com.sky.exception.BaseException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SeckillActivityMapper;
import com.sky.mapper.SeckillGoodsMapper;
import com.sky.mapper.SeckillUserRecordMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SeckillGoodsService;
import com.sky.vo.SeckillGoodsVO;
import com.sky.vo.AvailableGoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀商品Service实现类
 */
@Service
@Slf4j
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SeckillActivityMapper seckillActivityMapper;
    @Autowired
    private SeckillUserRecordMapper seckillUserRecordMapper;

    /**
     * 根据活动ID查询秒杀商品列表
     * @param activityId
     * @return
     */
    @Override
    public List<SeckillGoodsVO> getByActivityId(Long activityId) {
        List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.getByActivityId(activityId);
        List<SeckillGoodsVO> voList = new ArrayList<>();
        
        for (SeckillGoods seckillGoods : seckillGoodsList) {
            SeckillGoodsVO vo = new SeckillGoodsVO();
            BeanUtils.copyProperties(seckillGoods, vo);
            
            // 模拟用户购买数据
            vo.setUserPurchased(0);
            vo.setCanPurchase(seckillGoods.getAvailableStock() > 0);
            
            voList.add(vo);
        }
        
        return voList;
    }

    /**
     * 根据ID查询秒杀商品详情
     * @param id
     * @return
     */
    @Override
    public SeckillGoodsVO getById(Long id) {
        SeckillGoods seckillGoods = seckillGoodsMapper.getById(id);
        if (seckillGoods == null) {
            return null;
        }
        
        SeckillGoodsVO vo = new SeckillGoodsVO();
        BeanUtils.copyProperties(seckillGoods, vo);
        
        // 获取商品基本信息（菜品或套餐）
        if (seckillGoods.getGoodsType() == 1) {
            // 菜品
            Dish dish = dishMapper.getById(seckillGoods.getGoodsId());
            if (dish != null) {
                vo.setGoodsName(dish.getName());
                vo.setGoodsImage(dish.getImage());
                vo.setDescription(dish.getDescription());
                vo.setOriginalPrice(dish.getPrice());
            }
        } else {
            // 套餐
            Setmeal setmeal = setmealMapper.getById(seckillGoods.getGoodsId());
            if (setmeal != null) {
                vo.setGoodsName(setmeal.getName());
                vo.setGoodsImage(setmeal.getImage());
                vo.setDescription(setmeal.getDescription());
                vo.setOriginalPrice(setmeal.getPrice());
            }
        }
        
        // 获取活动信息
        SeckillActivity activity = seckillActivityMapper.getById(seckillGoods.getActivityId());
        if (activity != null) {
            vo.setActivityName(activity.getName());
            vo.setStartTime(activity.getStartTime());
            vo.setEndTime(activity.getEndTime());
            // 计算剩余时间，如果活动已结束则设为0
            LocalDateTime now = LocalDateTime.now();
            if (activity.getEndTime().isAfter(now)) {
                vo.setRemainingTime(Duration.between(now, activity.getEndTime()).getSeconds());
            } else {
                vo.setRemainingTime(0L);
            }
        } else {
            // 如果找不到活动，使用默认值
            vo.setActivityName("限时秒杀活动");
            vo.setStartTime(LocalDateTime.now().minusHours(1));
            vo.setEndTime(LocalDateTime.now().plusHours(2));
            vo.setRemainingTime(Duration.between(LocalDateTime.now(), vo.getEndTime()).getSeconds());
        }
        
        // 检查用户购买记录
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // 模拟用户ID
        }
        
        SeckillUserRecord userRecord = seckillUserRecordMapper.getByUserIdAndGoodsId(userId, id);
        Integer userPurchased = userRecord != null ? userRecord.getQuantity() : 0;
        vo.setUserPurchased(userPurchased);
        vo.setCanPurchase(seckillGoods.getAvailableStock() > 0 && vo.getUserPurchased() < seckillGoods.getLimitCount());
        
        return vo;
    }

    /**
     * 修改秒杀商品信息
     * @param seckillGoodsDTO
     */
    @Override
    public void update(SeckillGoodsDTO seckillGoodsDTO) {
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtils.copyProperties(seckillGoodsDTO, seckillGoods);
        
        seckillGoods.setUpdateTime(LocalDateTime.now());
        seckillGoods.setUpdateUser(BaseContext.getCurrentId());
        
        seckillGoodsMapper.update(seckillGoods);
    }

    /**
     * 删除秒杀商品
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        seckillGoodsMapper.deleteById(id);
    }

    /**
     * 查询可用商品列表
     * @param type
     * @param name
     * @return
     */
    @Override
    public List<AvailableGoodsVO> getAvailableGoods(Integer type, String name) {
        List<AvailableGoodsVO> voList = new ArrayList<>();
        
        if (type == null || type == 1) {
            // 查询菜品
            List<Dish> dishes = dishMapper.list(null);
            for (Dish dish : dishes) {
                if (dish.getStatus() == 1) { // 只返回启用的菜品
                    // 如果指定了名称，进行过滤
                    if (name != null && !name.trim().isEmpty() && 
                        !dish.getName().contains(name.trim())) {
                        continue;
                    }
                    AvailableGoodsVO vo = new AvailableGoodsVO();
                    vo.setId(dish.getId());
                    vo.setName(dish.getName());
                    vo.setImage(dish.getImage());
                    vo.setPrice(dish.getPrice());
                    vo.setType(1);
                    voList.add(vo);
                }
            }
        }
        
        if (type == null || type == 2) {
            // 查询套餐
            List<Setmeal> setmeals = setmealMapper.list(null);
            for (Setmeal setmeal : setmeals) {
                if (setmeal.getStatus() == 1) { // 只返回启用的套餐
                    // 如果指定了名称，进行过滤
                    if (name != null && !name.trim().isEmpty() && 
                        !setmeal.getName().contains(name.trim())) {
                        continue;
                    }
                    AvailableGoodsVO vo = new AvailableGoodsVO();
                    vo.setId(setmeal.getId());
                    vo.setName(setmeal.getName());
                    vo.setImage(setmeal.getImage());
                    vo.setPrice(setmeal.getPrice());
                    vo.setType(2);
                    voList.add(vo);
                }
            }
        }
        
        return voList;
    }

    /**
     * 检查用户购买资格
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Override
    public Object checkEligibility(Long seckillGoodsId, Integer quantity) {
        SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
        if (seckillGoods == null) {
            throw new BaseException("秒杀商品不存在");
        }
        
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // 模拟用户ID
        }
        
        // 检查库存
        boolean canPurchase = seckillGoods.getAvailableStock() >= quantity;
        
        // 检查用户购买记录
        SeckillUserRecord userRecord = seckillUserRecordMapper.getByUserIdAndGoodsId(userId, seckillGoodsId);
        Integer userPurchased = userRecord != null ? userRecord.getQuantity() : 0;
        
        // 检查限购
        Integer remainingQuota = seckillGoods.getLimitCount() - userPurchased;
        if (remainingQuota < quantity) {
            canPurchase = false;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("canPurchase", canPurchase);
        result.put("remainingQuota", remainingQuota);
        result.put("limitCount", seckillGoods.getLimitCount());
        result.put("userPurchased", userPurchased);
        result.put("availableStock", seckillGoods.getAvailableStock());
        
        return result;
    }
}