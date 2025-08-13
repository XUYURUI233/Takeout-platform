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
 * ��ɱ����ʵ����
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
     * ������ɱ�
     */
    @Override
    @Transactional
    public void createSeckill(SeckillDTO seckillDTO) {
        // ������ɱ�
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

        // ������ɱ��Ʒ��
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
     * ��ҳ��ѯ��ɱ�
     */
    @Override
    public PageResult getSeckillList(Integer page, Integer pageSize, Integer status) {
        // ����ƫ����
        int offset = (page - 1) * pageSize;

        // ��ѯ��ɱ��б�
        Seckill seckill = new Seckill();
        seckill.setStatus(status);
        List<Seckill> seckillList = seckillMapper.list(seckill);

        // ��������
        long total = seckillList.size();

        // ��ҳ����
        List<Seckill> pageList = seckillList.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());

        return new PageResult(total, pageList);
    }

    /**
     * ������ɱ�
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
     * ɾ����ɱ�
     */
    @Override
    @Transactional
    public void deleteSeckill(Long seckillId) {
        // ɾ����ɱ��Ʒ��
        seckillItemMapper.deleteBySeckillId(seckillId);
        // ɾ����ɱ�
        seckillMapper.deleteById(seckillId);
    }

    /**
     * ��ȡ��ɱ����б�
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
     * ��ȡ��ɱ��Ʒ�б�
     */
    @Override
    public PageResult getGoodsList(Long seckillId, Integer page, Integer pageSize) {
        // ����ƫ����
        int offset = (page - 1) * pageSize;

        // ��ѯ��ɱ��Ʒ�б�
        List<SeckillItem> itemList = seckillItemMapper.getBySeckillId(seckillId);

        // ��������
        long total = itemList.size();

        // ��ҳ����
        List<SeckillItem> pageList = itemList.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());

        // ת��ΪVO
        Long userId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();
        List<SeckillGoodsVO> goodsVOList = pageList.stream().map(item -> {
            // �����û��ѹ�������
            Integer userBoughtCount = seckillOrderMapper.getUserBoughtCount(item.getId(), userId);
            
            // ����ʣ��ʱ��
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
     * ������ɱ����
     */
    @Override
    @Transactional
    public SeckillOrderVO purchase(SeckillPurchaseDTO purchaseDTO) {
        Long userId = BaseContext.getCurrentId();
        Long itemId = purchaseDTO.getItemId();
        Integer quantity = purchaseDTO.getQuantity();

        // ��ѯ��ɱ��Ʒ��
        SeckillItem seckillItem = seckillItemMapper.getById(itemId);
        if (seckillItem == null) {
            throw new RuntimeException("��ɱ��Ʒ������");
        }

        // ���״̬
        Seckill seckill = seckillMapper.getById(seckillItem.getSeckillId());
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(seckill.getStartTime()) || now.isAfter(seckill.getEndTime())) {
            throw new RuntimeException("��ɱ�δ��ʼ���ѽ���");
        }

        // �����
        if (seckillItem.getCurrentStock() < quantity) {
            throw new RuntimeException("��治��");
        }

        // ����û��޹�
        Integer userBoughtCount = seckillOrderMapper.getUserBoughtCount(itemId, userId);
        if (userBoughtCount + quantity > seckillItem.getLimitPerUser()) {
            throw new RuntimeException("�����޹�����");
        }

        // �ۼ����
        int updateResult = seckillItemMapper.updateStock(itemId, -quantity);
        if (updateResult == 0) {
            throw new RuntimeException("���ۼ�ʧ��");
        }

        // ��������
        String orderNumber = generateOrderNumber();
        BigDecimal totalAmount = seckillItem.getSeckillPrice().multiply(new BigDecimal(quantity));
        LocalDateTime payDeadline = now.plusMinutes(30); // 30����֧������

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
                .status(1) // ��֧��
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
     * ��ȡ�û���ɱ�����б�
     */
    @Override
    public PageResult getUserOrders(Integer page, Integer pageSize, Integer status) {
        Long userId = BaseContext.getCurrentId();
        
        // ����ƫ����
        int offset = (page - 1) * pageSize;

        // ��ѯ�û������б�
        List<SeckillOrder> orderList = seckillOrderMapper.getByUserId(userId);

        // ����״̬
        if (status != null) {
            orderList = orderList.stream()
                    .filter(order -> order.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        // ��������
        long total = orderList.size();

        // ��ҳ����
        List<SeckillOrder> pageList = orderList.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());

        // ת��ΪVO
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
     * ������ɱ���
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
                // ������Ҫ�Ȳ�ѯ��ǰ��棬Ȼ������ֵ
                SeckillItem item = seckillItemMapper.getById(itemId);
                int diff = stockChange - item.getCurrentStock();
                updateResult = seckillItemMapper.updateStock(itemId, diff);
                break;
            default:
                throw new RuntimeException("��֧�ֵĲ�������");
        }

        if (updateResult == 0) {
            throw new RuntimeException("������ʧ��");
        }
    }

    /**
     * �ع���ɱ���
     */
    @Override
    public void rollbackStock(String orderId, Long itemId, Integer rollbackQuantity) {
        int updateResult = seckillItemMapper.updateStock(itemId, rollbackQuantity);
        if (updateResult == 0) {
            throw new RuntimeException("���ع�ʧ��");
        }
    }

    /**
     * ��ȡ��ɱͳ������
     */
    @Override
    public Object getStatistics(Long seckillId) {
        // �������ʵ�־����ͳ���߼�
        // ��ʱ���ؼ򵥵�ͳ����Ϣ
        return new Object();
    }

    /**
     * ���ɶ�����
     */
    private String generateOrderNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return "SK" + timestamp + String.format("%04d", (int)(Math.random() * 10000));
    }
}

