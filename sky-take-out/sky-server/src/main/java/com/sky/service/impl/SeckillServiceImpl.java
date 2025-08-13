package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.SeckillDTO;
import com.sky.dto.SeckillItemDTO;
import com.sky.dto.SeckillPurchaseDTO;
import com.sky.dto.SeckillStockUpdateDTO;
import com.sky.entity.Seckill;
import com.sky.entity.SeckillItem;
import com.sky.entity.SeckillOrder;
import com.sky.mapper.SeckillItemMapper;
import com.sky.mapper.SeckillMapper;
import com.sky.mapper.SeckillOrderMapper;
import com.sky.result.PageResult;
import com.sky.service.SeckillService;
import com.sky.vo.SeckillGoodsVO;
import com.sky.vo.SeckillOrderVO;
import com.sky.vo.SeckillVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 秒杀服务实现类
 */
@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SeckillItemMapper seckillItemMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 创建秒杀活动
     */
    @Override
    @Transactional
    public void createSeckill(SeckillDTO seckillDTO) {
        // 创建秒杀活动
        Seckill seckill = Seckill.builder()
                .seckillName(seckillDTO.getSeckillName())
                .seckillDescription(seckillDTO.getSeckillDescription())
                .startTime(seckillDTO.getStartTime())
                .endTime(seckillDTO.getEndTime())
                .bannerImage(seckillDTO.getBannerImage())
                .status(seckillDTO.getStatus())
                .createTime(LocalDateTime.now())
                .createUser(BaseContext.getCurrentId())
                .build();

        seckillMapper.insert(seckill);

        // 创建秒杀商品项
        for (SeckillItemDTO itemDTO : seckillDTO.getSeckillItems()) {
            SeckillItem seckillItem = SeckillItem.builder()
                    .seckillId(seckill.getId())
                    .dishId(itemDTO.getDishId())
                    .dishName(itemDTO.getDishName())
                    .originalPrice(itemDTO.getOriginalPrice())
                    .seckillPrice(itemDTO.getSeckillPrice())
                    .seckillStock(itemDTO.getSeckillStock())
                    .currentStock(itemDTO.getSeckillStock())
                    .limitPerUser(itemDTO.getLimitPerUser())
                    .status(1)
                    .createTime(LocalDateTime.now())
                    .build();

            seckillItemMapper.insert(seckillItem);
        }
    }

    /**
     * 分页查询秒杀活动
     */
    @Override
    public PageResult getSeckillList(Integer page, Integer pageSize, Integer status) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询秒杀活动列表
        Seckill seckill = new Seckill();
        seckill.setStatus(status);
        List<Seckill> seckillList = seckillMapper.list(seckill);

        // 计算总数
        long total = seckillList.size();

        // 分页处理
        List<Seckill> pageList = seckillList.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());

        return new PageResult(total, pageList);
    }

    /**
     * 更新秒杀活动
     */
    @Override
    @Transactional
    public void updateSeckill(Long seckillId, SeckillDTO seckillDTO) {
        Seckill seckill = Seckill.builder()
                .id(seckillId)
                .seckillName(seckillDTO.getSeckillName())
                .seckillDescription(seckillDTO.getSeckillDescription())
                .startTime(seckillDTO.getStartTime())
                .endTime(seckillDTO.getEndTime())
                .bannerImage(seckillDTO.getBannerImage())
                .status(seckillDTO.getStatus())
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();

        seckillMapper.update(seckill);
    }

    /**
     * 删除秒杀活动
     */
    @Override
    @Transactional
    public void deleteSeckill(Long seckillId) {
        // 删除秒杀商品项
        seckillItemMapper.deleteBySeckillId(seckillId);
        // 删除秒杀活动
        seckillMapper.deleteById(seckillId);
    }

    /**
     * 获取秒杀横幅列表
     */
    @Override
    public List<SeckillVO> getBannerList() {
        LocalDateTime now = LocalDateTime.now();
        List<Seckill> seckillList = seckillMapper.getActiveSeckills(now);

        return seckillList.stream().map(seckill -> {
            long remainingTime = java.time.Duration.between(now, seckill.getEndTime()).getSeconds();
            return SeckillVO.builder()
                    .seckillId(seckill.getId())
                    .seckillName(seckill.getSeckillName())
                    .bannerImage(seckill.getBannerImage())
                    .startTime(seckill.getStartTime())
                    .endTime(seckill.getEndTime())
                    .status(seckill.getStatus())
                    .remainingTime(remainingTime)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 获取秒杀商品列表
     */
    @Override
    public PageResult getGoodsList(Long seckillId, Integer page, Integer pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询秒杀商品列表
        List<SeckillItem> itemList = seckillItemMapper.getBySeckillId(seckillId);

        // 计算总数
        long total = itemList.size();

        // 分页处理
        List<SeckillItem> pageList = itemList.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());

        // 转换为VO
        Long userId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();
        List<SeckillGoodsVO> goodsVOList = pageList.stream().map(item -> {
            // 计算用户已购买数量
            Integer userBoughtCount = seckillOrderMapper.getUserBoughtCount(item.getId(), userId);
            
            // 计算剩余时间
            Seckill seckill = seckillMapper.getById(item.getSeckillId());
            long remainingTime = java.time.Duration.between(now, seckill.getEndTime()).getSeconds();

            return SeckillGoodsVO.builder()
                    .seckillId(item.getSeckillId())
                    .itemId(item.getId())
                    .dishId(item.getDishId())
                    .dishName(item.getDishName())
                    .originalPrice(item.getOriginalPrice())
                    .seckillPrice(item.getSeckillPrice())
                    .seckillStock(item.getSeckillStock())
                    .currentStock(item.getCurrentStock())
                    .limitPerUser(item.getLimitPerUser())
                    .userBoughtCount(userBoughtCount)
                    .remainingTime(remainingTime)
                    .build();
        }).collect(Collectors.toList());

        return new PageResult(total, goodsVOList);
    }

    /**
     * 参与秒杀购买
     */
    @Override
    @Transactional
    public SeckillOrderVO purchase(SeckillPurchaseDTO purchaseDTO) {
        Long userId = BaseContext.getCurrentId();
        Long itemId = purchaseDTO.getItemId();
        Integer quantity = purchaseDTO.getQuantity();

        // 查询秒杀商品项
        SeckillItem seckillItem = seckillItemMapper.getById(itemId);
        if (seckillItem == null) {
            throw new RuntimeException("秒杀商品不存在");
        }

        // 检查活动状态
        Seckill seckill = seckillMapper.getById(seckillItem.getSeckillId());
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(seckill.getStartTime()) || now.isAfter(seckill.getEndTime())) {
            throw new RuntimeException("秒杀活动未开始或已结束");
        }

        // 检查库存
        if (seckillItem.getCurrentStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        // 检查用户限购
        Integer userBoughtCount = seckillOrderMapper.getUserBoughtCount(itemId, userId);
        if (userBoughtCount + quantity > seckillItem.getLimitPerUser()) {
            throw new RuntimeException("超出限购数量");
        }

        // 扣减库存
        int updateResult = seckillItemMapper.updateStock(itemId, -quantity);
        if (updateResult == 0) {
            throw new RuntimeException("库存扣减失败");
        }

        // 创建订单
        String orderNumber = generateOrderNumber();
        BigDecimal totalAmount = seckillItem.getSeckillPrice().multiply(new BigDecimal(quantity));
        LocalDateTime payDeadline = now.plusMinutes(30); // 30分钟支付期限

        SeckillOrder seckillOrder = SeckillOrder.builder()
                .orderNumber(orderNumber)
                .seckillId(seckillItem.getSeckillId())
                .itemId(itemId)
                .userId(userId)
                .dishId(seckillItem.getDishId())
                .dishName(seckillItem.getDishName())
                .quantity(quantity)
                .seckillPrice(seckillItem.getSeckillPrice())
                .totalAmount(totalAmount)
                .addressId(purchaseDTO.getAddressId())
                .remark(purchaseDTO.getRemark())
                .status(1) // 待支付
                .payDeadline(payDeadline)
                .createTime(now)
                .build();

        seckillOrderMapper.insert(seckillOrder);

        return SeckillOrderVO.builder()
                .orderId(orderNumber)
                .seckillId(seckillItem.getSeckillId())
                .dishName(seckillItem.getDishName())
                .quantity(quantity)
                .seckillPrice(seckillItem.getSeckillPrice())
                .totalAmount(totalAmount)
                .payDeadline(payDeadline)
                .build();
    }

    /**
     * 获取用户秒杀订单列表
     */
    @Override
    public PageResult getUserOrders(Integer page, Integer pageSize, Integer status) {
        Long userId = BaseContext.getCurrentId();
        
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询用户订单列表
        List<SeckillOrder> orderList = seckillOrderMapper.getByUserId(userId);

        // 过滤状态
        if (status != null) {
            orderList = orderList.stream()
                    .filter(order -> order.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        // 计算总数
        long total = orderList.size();

        // 分页处理
        List<SeckillOrder> pageList = orderList.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());

        // 转换为VO
        List<SeckillOrderVO> orderVOList = pageList.stream().map(order -> {
            Seckill seckill = seckillMapper.getById(order.getSeckillId());
            return SeckillOrderVO.builder()
                    .orderId(order.getOrderNumber())
                    .seckillId(order.getSeckillId())
                    .seckillName(seckill.getSeckillName())
                    .dishName(order.getDishName())
                    .quantity(order.getQuantity())
                    .seckillPrice(order.getSeckillPrice())
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus())
                    .createTime(order.getCreateTime())
                    .payDeadline(order.getPayDeadline())
                    .build();
        }).collect(Collectors.toList());

        return new PageResult(total, orderVOList);
    }

    /**
     * 更新秒杀库存
     */
    @Override
    public void updateStock(SeckillStockUpdateDTO stockUpdateDTO) {
        Long itemId = stockUpdateDTO.getItemId();
        Integer stockChange = stockUpdateDTO.getStockChange();
        String operationType = stockUpdateDTO.getOperationType();

        int updateResult;
        switch (operationType) {
            case "INCREASE":
                updateResult = seckillItemMapper.updateStock(itemId, stockChange);
                break;
            case "DECREASE":
                updateResult = seckillItemMapper.updateStock(itemId, -stockChange);
                break;
            case "SET":
                // 这里需要先查询当前库存，然后计算差值
                SeckillItem item = seckillItemMapper.getById(itemId);
                int diff = stockChange - item.getCurrentStock();
                updateResult = seckillItemMapper.updateStock(itemId, diff);
                break;
            default:
                throw new RuntimeException("不支持的操作类型");
        }

        if (updateResult == 0) {
            throw new RuntimeException("库存更新失败");
        }
    }

    /**
     * 回滚秒杀库存
     */
    @Override
    public void rollbackStock(String orderId, Long itemId, Integer rollbackQuantity) {
        int updateResult = seckillItemMapper.updateStock(itemId, rollbackQuantity);
        if (updateResult == 0) {
            throw new RuntimeException("库存回滚失败");
        }
    }

    /**
     * 获取秒杀统计数据
     */
    @Override
    public Object getStatistics(Long seckillId) {
        // 这里可以实现具体的统计逻辑
        // 暂时返回简单的统计信息
        return new Object();
    }

    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return "SK" + timestamp + String.format("%04d", (int)(Math.random() * 10000));
    }
}

