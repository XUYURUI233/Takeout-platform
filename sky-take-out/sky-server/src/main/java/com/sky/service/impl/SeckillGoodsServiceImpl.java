package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.SeckillConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SeckillGoodsDTO;
import com.sky.entity.SeckillGoods;
import com.sky.entity.SeckillStockLog;
import com.sky.entity.SeckillUserRecord;
import com.sky.exception.BaseException;
import com.sky.mapper.SeckillGoodsMapper;
import com.sky.mapper.SeckillOrderMapper;
import com.sky.mapper.SeckillStockLogMapper;
import com.sky.mapper.SeckillUserRecordMapper;
import com.sky.service.SeckillGoodsService;
import com.sky.vo.SeckillGoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 秒杀商品Service实现类
 */
@Service
@Slf4j
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private SeckillUserRecordMapper seckillUserRecordMapper;
    @Autowired
    private SeckillStockLogMapper seckillStockLogMapper;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    /**
     * 查询可用商品列表
     * @param type
     * @param name
     * @return
     */
    @Override
    public List<SeckillGoodsVO> getAvailableGoods(Integer type, String name) {
        List<com.sky.vo.AvailableGoodsVO> availableGoods = seckillGoodsMapper.getAvailableGoods(type, name);
        List<SeckillGoodsVO> voList = new ArrayList<>();
        
        for (com.sky.vo.AvailableGoodsVO availableGood : availableGoods) {
            SeckillGoodsVO vo = new SeckillGoodsVO();
            // 设置商品基本信息，字段名与前端匹配
            vo.setId(availableGood.getId());  // 前端需要的是 id
            vo.setName(availableGood.getName());  // 前端需要的是 name
            vo.setImage(availableGood.getImage());  // 前端需要的是 image
            vo.setPrice(availableGood.getPrice());  // 前端需要的是 price
            vo.setType(availableGood.getType());  // 前端需要的是 type
            vo.setCategoryName(availableGood.getCategoryName());
            
            // 同时设置原有字段以保持兼容性
            vo.setGoodsId(availableGood.getId());
            vo.setGoodsName(availableGood.getName());
            vo.setGoodsImage(availableGood.getImage());
            vo.setOriginalPrice(availableGood.getPrice());
            vo.setGoodsType(availableGood.getType());
            voList.add(vo);
        }
        
        return voList;
    }

    /**
     * 修改秒杀商品信息
     * @param seckillGoodsDTO
     */
    @Override
    public void update(SeckillGoodsDTO seckillGoodsDTO) {
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtils.copyProperties(seckillGoodsDTO, seckillGoods);
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
     * 根据活动id查询秒杀商品列表
     * @param activityId
     * @return
     */
    @Override
    public List<SeckillGoodsVO> getByActivityId(Long activityId) {
        List<SeckillGoods> goodsList = seckillGoodsMapper.getByActivityId(activityId);
        List<SeckillGoodsVO> voList = new ArrayList<>();
        
        Long userId = BaseContext.getCurrentId();
        for (SeckillGoods goods : goodsList) {
            SeckillGoodsVO vo = new SeckillGoodsVO();
            BeanUtils.copyProperties(goods, vo);
            
            // 查询用户购买记录
            if (userId != null) {
                SeckillUserRecord record = seckillUserRecordMapper.getByActivityGoodsUser(
                    activityId, goods.getId(), userId);
                if (record != null) {
                    vo.setUserPurchased(record.getQuantity());
                    vo.setCanPurchase(record.getQuantity() < goods.getLimitCount());
                } else {
                    vo.setUserPurchased(0);
                    vo.setCanPurchase(true);
                }
            }
            
            voList.add(vo);
        }
        
        return voList;
    }

    /**
     * 查询秒杀商品详情
     * @param id
     * @return
     */
    @Override
    public SeckillGoodsVO getById(Long id) {
        SeckillGoods goods = seckillGoodsMapper.getById(id);
        if (goods == null) {
            throw new BaseException(MessageConstant.SECKILL_GOODS_NOT_EXISTS);
        }
        
        SeckillGoodsVO vo = new SeckillGoodsVO();
        BeanUtils.copyProperties(goods, vo);
        
        // 查询用户购买记录
        Long userId = BaseContext.getCurrentId();
        if (userId != null) {
            SeckillUserRecord record = seckillUserRecordMapper.getByActivityGoodsUser(
                goods.getActivityId(), goods.getId(), userId);
            if (record != null) {
                vo.setUserPurchased(record.getQuantity());
                vo.setCanPurchase(record.getQuantity() < goods.getLimitCount());
            } else {
                vo.setUserPurchased(0);
                vo.setCanPurchase(true);
            }
        }
        
        return vo;
    }

    /**
     * 检查商品库存
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Override
    public boolean checkStock(Long seckillGoodsId, Integer quantity) {
        SeckillGoods goods = seckillGoodsMapper.getById(seckillGoodsId);
        if (goods == null) {
            return false;
        }
        return goods.getAvailableStock() >= quantity;
    }

    /**
     * 扣减库存
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Override
    @Transactional
    public boolean decreaseStock(Long seckillGoodsId, Integer quantity) {
        // 查询当前商品信息
        SeckillGoods goods = seckillGoodsMapper.getById(seckillGoodsId);
        if (goods == null) {
            throw new BaseException(MessageConstant.SECKILL_GOODS_NOT_EXISTS);
        }

        // 记录扣减前库存
        int beforeStock = goods.getAvailableStock();
        
        // 使用乐观锁扣减库存
        int rows = seckillGoodsMapper.deductStock(seckillGoodsId, quantity, goods.getVersion());
        
        if (rows > 0) {
            // 记录库存操作日志
            SeckillStockLog stockLog = SeckillStockLog.builder()
                .seckillGoodsId(seckillGoodsId)
                .userId(BaseContext.getCurrentId())
                .operationType(SeckillConstant.STOCK_OP_DEDUCT)
                .quantity(quantity)
                .beforeStock(beforeStock)
                .afterStock(beforeStock - quantity)
                .version(goods.getVersion())
                .remark("用户下单扣减库存")
                .createTime(LocalDateTime.now())
                .build();
            seckillStockLogMapper.insert(stockLog);
            
            return true;
        }
        
        return false;
    }

    /**
     * 释放库存
     * @param seckillGoodsId
     * @param quantity
     */
    @Override
    @Transactional
    public void releaseStock(Long seckillGoodsId, Integer quantity) {
        // 查询当前商品信息
        SeckillGoods goods = seckillGoodsMapper.getById(seckillGoodsId);
        if (goods == null) {
            return;
        }

        // 记录释放前库存
        int beforeStock = goods.getAvailableStock();
        
        // 释放库存
        int rows = seckillGoodsMapper.releaseStock(seckillGoodsId, quantity);
        
        if (rows > 0) {
            // 记录库存操作日志
            SeckillStockLog stockLog = SeckillStockLog.builder()
                .seckillGoodsId(seckillGoodsId)
                .userId(BaseContext.getCurrentId())
                .operationType(SeckillConstant.STOCK_OP_RELEASE)
                .quantity(quantity)
                .beforeStock(beforeStock)
                .afterStock(beforeStock + quantity)
                .version(goods.getVersion())
                .remark("订单取消释放库存")
                .createTime(LocalDateTime.now())
                .build();
            seckillStockLogMapper.insert(stockLog);
        }
    }

    /**
     * 扣减库存（别名方法）
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Override
    public boolean deductStock(Long seckillGoodsId, Integer quantity) {
        return decreaseStock(seckillGoodsId, quantity);
    }

    /**
     * 检查用户购买资格
     * @param seckillGoodsId
     * @param quantity
     * @return
     */
    @Override
    public boolean checkEligibility(Long seckillGoodsId, Integer quantity) {
        // 检查库存
        if (!checkStock(seckillGoodsId, quantity)) {
            return false;
        }
        
        // 检查用户限购
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return false;
        }
        
        // 获取用户已购买数量
        Integer userBoughtCount = seckillOrderMapper.getUserBoughtCount(seckillGoodsId, userId);
        if (userBoughtCount == null) {
            userBoughtCount = 0;
        }
        
        // 获取商品限购数量
        SeckillGoods goods = seckillGoodsMapper.getById(seckillGoodsId);
        if (goods == null) {
            return false;
        }
        
        // 检查是否超过限购数量
        return userBoughtCount + quantity <= goods.getLimitCount();
    }
}


