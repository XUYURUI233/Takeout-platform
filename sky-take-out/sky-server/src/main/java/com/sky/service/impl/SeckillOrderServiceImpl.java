package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.SeckillConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SeckillOrderDTO;
import com.sky.dto.SeckillOrderPageQueryDTO;
import com.sky.entity.*;
import com.sky.exception.BaseException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.SeckillGoodsService;
import com.sky.service.SeckillOrderService;
import com.sky.vo.SeckillOrderSubmitVO;
import com.sky.vo.SeckillOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ��ɱ����Serviceʵ����
 */
@Service
@Slf4j
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private SeckillUserRecordMapper seckillUserRecordMapper;
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * �ύ��ɱ����
     * @param seckillOrderDTO
     * @return
     */
    @Override
    @Transactional
    public SeckillOrderSubmitVO submitOrder(SeckillOrderDTO seckillOrderDTO) {
        Long userId = BaseContext.getCurrentId();
        
        // ��ѯ��ɱ��Ʒ
        SeckillGoods goods = seckillGoodsMapper.getById(seckillOrderDTO.getSeckillGoodsId());
        if (goods == null) {
            throw new BaseException(MessageConstant.SECKILL_GOODS_NOT_EXISTS);
        }
        
        // ��鹺���ʸ�
        SeckillUserRecord userRecord = seckillUserRecordMapper.getByActivityGoodsUser(
            goods.getActivityId(), goods.getId(), userId);
        int userPurchased = userRecord != null ? userRecord.getQuantity() : 0;
        
        if (userPurchased + seckillOrderDTO.getQuantity() > goods.getLimitCount()) {
            throw new BaseException(MessageConstant.SECKILL_EXCEED_LIMIT);
        }
        
        // �ۼ����
        boolean deductSuccess = seckillGoodsService.deductStock(
            seckillOrderDTO.getSeckillGoodsId(), seckillOrderDTO.getQuantity());
        if (!deductSuccess) {
            throw new BaseException(MessageConstant.SECKILL_STOCK_NOT_ENOUGH);
        }
        
        try {
            // ��ѯ��ַ��Ϣ
            AddressBook addressBook = addressBookMapper.getById(seckillOrderDTO.getAddressBookId());
            if (addressBook == null) {
                throw new BaseException(MessageConstant.ADDRESS_BOOK_IS_NULL);
            }
            
            // ������ͨ����
            Orders order = new Orders();
            order.setNumber(String.valueOf(System.currentTimeMillis()));
            order.setStatus(Orders.PENDING_PAYMENT);
            order.setUserId(userId);
            order.setAddressBookId(seckillOrderDTO.getAddressBookId());
            order.setOrderTime(LocalDateTime.now());
            order.setPayMethod(1); // Ĭ��΢��֧��
            order.setPayStatus(Orders.UN_PAID);
            order.setAmount(goods.getSeckillPrice().multiply(new BigDecimal(seckillOrderDTO.getQuantity())));
            order.setRemark(seckillOrderDTO.getRemark());
            order.setPhone(addressBook.getPhone());
            order.setAddress(addressBook.getDetail());
            order.setConsignee(addressBook.getConsignee());
            order.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));
            order.setDeliveryStatus(1);
            order.setPackAmount(0);
            order.setTablewareNumber(0);
            order.setTablewareStatus(1);
            
            orderMapper.insert(order);
            
            // ������ɱ����
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setOrderId(order.getId());
            seckillOrder.setActivityId(goods.getActivityId());
            seckillOrder.setSeckillGoodsId(goods.getId());
            seckillOrder.setUserId(userId);
            seckillOrder.setQuantity(seckillOrderDTO.getQuantity());
            seckillOrder.setSeckillPrice(goods.getSeckillPrice());
            seckillOrder.setTotalAmount(order.getAmount());
            seckillOrder.setPayStatus(SeckillConstant.PAY_STATUS_UNPAID);
            seckillOrder.setPayExpireTime(LocalDateTime.now().plusSeconds(SeckillConstant.PAY_TIME_LIMIT));
            seckillOrder.setCreateTime(LocalDateTime.now());
            seckillOrder.setUpdateTime(LocalDateTime.now());
            
            seckillOrderMapper.insert(seckillOrder);
            
            // �����û������¼
            if (userRecord == null) {
                userRecord = new SeckillUserRecord();
                userRecord.setActivityId(goods.getActivityId());
                userRecord.setSeckillGoodsId(goods.getId());
                userRecord.setUserId(userId);
                userRecord.setQuantity(seckillOrderDTO.getQuantity());
                userRecord.setCreateTime(LocalDateTime.now());
                userRecord.setUpdateTime(LocalDateTime.now());
                seckillUserRecordMapper.insert(userRecord);
            } else {
                seckillUserRecordMapper.updateQuantity(goods.getActivityId(), goods.getId(), 
                    userId, seckillOrderDTO.getQuantity());
            }
            
            return SeckillOrderSubmitVO.builder()
                .orderId(order.getId())
                .orderNumber(order.getNumber())
                .payExpireTime(seckillOrder.getPayExpireTime())
                .totalAmount(order.getAmount())
                .payTimeLimit(SeckillConstant.PAY_TIME_LIMIT)
                .build();
                
        } catch (Exception e) {
            // �����������ʧ�ܣ���Ҫ�ͷſ��
            seckillGoodsService.releaseStock(seckillOrderDTO.getSeckillGoodsId(), seckillOrderDTO.getQuantity());
            throw e;
        }
    }

    /**
     * ��ɱ����֧��
     * @param orderNumber
     * @param payMethod
     */
    @Override
    @Transactional
    public void payment(String orderNumber, Integer payMethod) {
        // ��ѯ����
        Orders order = orderMapper.getByNumber(orderNumber);
        if (order == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        
        // ���¶���֧��״̬
        order.setPayStatus(Orders.PAID);
        order.setCheckoutTime(LocalDateTime.now());
        order.setStatus(Orders.TO_BE_CONFIRMED);
        orderMapper.update(order);
        
        // ������ɱ����֧��״̬
        seckillOrderMapper.updatePayStatus(order.getId(), SeckillConstant.PAY_STATUS_PAID);
    }

    /**
     * ȡ����ɱ����
     * @param id
     */
    @Override
    @Transactional
    public void cancelOrder(Long id) {
        // ��ѯ����
        Orders order = orderMapper.getById(id);
        if (order == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        
        // ��ѯ��ɱ����
        SeckillOrder seckillOrder = seckillOrderMapper.getByOrderId(id);
        if (seckillOrder == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        
        // ֻ��δ֧���Ķ�������ȡ��
        if (order.getPayStatus() != Orders.UN_PAID) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        
        // ���¶���״̬
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason("�û�ȡ��");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
        
        // ������ɱ����״̬
        seckillOrderMapper.updatePayStatus(id, SeckillConstant.PAY_STATUS_TIMEOUT);
        
        // �ͷſ��
        seckillGoodsService.releaseStock(seckillOrder.getSeckillGoodsId(), seckillOrder.getQuantity());
    }

    /**
     * ��ҳ��ѯ��ɱ����������ˣ�
     * @param seckillOrderPageQueryDTO
     * @return
     */
    @Override
    public PageResult adminPageQuery(SeckillOrderPageQueryDTO seckillOrderPageQueryDTO) {
        PageHelper.startPage(seckillOrderPageQueryDTO.getPage(), seckillOrderPageQueryDTO.getPageSize());
        Page<SeckillOrder> page = seckillOrderMapper.adminPageQuery(seckillOrderPageQueryDTO);
        
        List<SeckillOrderVO> list = new ArrayList<>();
        for (SeckillOrder seckillOrder : page.getResult()) {
            SeckillOrderVO vo = new SeckillOrderVO();
            BeanUtils.copyProperties(seckillOrder, vo);
            // TODO: ������ɱ��Ϣ
            list.add(vo);
        }
        
        return new PageResult(page.getTotal(), list);
    }

    /**
     * ��ѯ��ɱ�������飨����ˣ�
     * @param id
     * @return
     */
    @Override
    public SeckillOrderVO getOrderDetails(Long id) {
        SeckillOrder seckillOrder = seckillOrderMapper.getById(id);
        if (seckillOrder == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        
        SeckillOrderVO vo = new SeckillOrderVO();
        BeanUtils.copyProperties(seckillOrder, vo);
        // TODO: ������ɱ��Ϣ�Ͷ�������
        
        return vo;
    }

    /**
     * ��ҳ��ѯ�û���ɱ����
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult userPageQuery(Integer page, Integer pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);
        Long userId = BaseContext.getCurrentId();
        Page<SeckillOrder> orderPage = seckillOrderMapper.userPageQuery(userId, status);
        
        List<SeckillOrderVO> list = new ArrayList<>();
        for (SeckillOrder seckillOrder : orderPage.getResult()) {
            SeckillOrderVO vo = new SeckillOrderVO();
            BeanUtils.copyProperties(seckillOrder, vo);
            // TODO: ������ɱ��Ϣ
            list.add(vo);
        }
        
        return new PageResult(orderPage.getTotal(), list);
    }

    /**
     * ��ѯ�û���ɱ��������
     * @param id
     * @return
     */
    @Override
    public SeckillOrderVO getUserOrderDetails(Long id) {
        SeckillOrder seckillOrder = seckillOrderMapper.getById(id);
        if (seckillOrder == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        
        // ����Ƿ��ǵ�ǰ�û��Ķ���
        if (!seckillOrder.getUserId().equals(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        
        SeckillOrderVO vo = new SeckillOrderVO();
        BeanUtils.copyProperties(seckillOrder, vo);
        // TODO: ������ɱ��Ϣ�Ͷ�������
        
        return vo;
    }

    /**
     * ����һ��
     * @param id
     * @return
     */
    @Override
    public SeckillOrderSubmitVO againOrder(Long id) {
        // TODO: ʵ������һ���߼�
        return null;
    }

    /**
     * ����ʱδ֧������
     */
    @Override
    @Transactional
    public void handleExpiredOrders() {
        List<SeckillOrder> expiredOrders = seckillOrderMapper.getExpiredOrders(LocalDateTime.now());
        
        for (SeckillOrder seckillOrder : expiredOrders) {
            try {
                // ���¶���״̬
                Orders order = orderMapper.getById(seckillOrder.getOrderId());
                if (order != null && order.getPayStatus() == Orders.UN_PAID) {
                    order.setStatus(Orders.CANCELLED);
                    order.setCancelReason("������ʱ���Զ�ȡ��");
                    order.setCancelTime(LocalDateTime.now());
                    orderMapper.update(order);
                }
                
                // ������ɱ����״̬
                seckillOrderMapper.updatePayStatus(seckillOrder.getOrderId(), SeckillConstant.PAY_STATUS_TIMEOUT);
                
                // �ͷſ��
                seckillGoodsService.releaseStock(seckillOrder.getSeckillGoodsId(), seckillOrder.getQuantity());
                
                log.info("����ʱ����: {}", seckillOrder.getOrderId());
            } catch (Exception e) {
                log.error("����ʱ�����쳣: {}", seckillOrder.getOrderId(), e);
            }
        }
    }
}

