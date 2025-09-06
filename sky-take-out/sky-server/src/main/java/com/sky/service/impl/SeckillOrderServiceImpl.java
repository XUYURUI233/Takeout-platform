package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.SeckillOrderSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.Dish;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.SeckillActivity;
import com.sky.entity.SeckillGoods;
import com.sky.entity.SeckillOrder;
import com.sky.entity.SeckillUserRecord;
import com.sky.entity.Setmeal;
import com.sky.entity.User;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SeckillActivityMapper;
import com.sky.mapper.SeckillGoodsMapper;
import com.sky.mapper.SeckillOrderMapper;
import com.sky.mapper.SeckillUserRecordMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.SeckillOrderService;
import com.sky.service.SeckillLuaService;
import com.sky.service.SeckillStockService;

import com.sky.vo.OrderPaymentVO;
import com.sky.vo.SeckillOrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SeckillUserRecordMapper seckillUserRecordMapper;

    @Autowired
    private SeckillStockService seckillStockService;

    @Autowired
    private SeckillLuaService seckillLuaService;

    /**
     * �ύ��ɱ����
     * @param seckillOrderSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public SeckillOrderSubmitVO submitOrder(SeckillOrderSubmitDTO seckillOrderSubmitDTO) {
        log.info("�ύ��ɱ����: {}", seckillOrderSubmitDTO);

        // ��ȡ��ǰ�û�ID
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // ģ���û�ID��ʵ��Ӧ�ô�JWT�л�ȡ
        }

        // 1. ��֤��ַ��
        AddressBook addressBook = addressBookMapper.getById(seckillOrderSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 2. ��ȡ��ɱ��Ʒ��Ϣ�����и��ּ��
        SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillOrderSubmitDTO.getSeckillGoodsId());
        if (seckillGoods == null) {
            throw new OrderBusinessException("��ɱ��Ʒ������");
        }
        
        // �����Ʒ״̬
        if (seckillGoods.getStatus() != SeckillGoods.ONLINE) {
            throw new OrderBusinessException("��ɱ��Ʒ���¼�");
        }
        
        // ������Ƿ����
        if (seckillGoods.getAvailableStock() < seckillOrderSubmitDTO.getQuantity()) {
            throw new OrderBusinessException("��治��");
        }
        
        // ����û��޹�
        SeckillUserRecord userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillOrderSubmitDTO.getSeckillGoodsId());
        int alreadyPurchased = userRecord != null ? userRecord.getQuantity() : 0;
        if (alreadyPurchased + seckillOrderSubmitDTO.getQuantity() > seckillGoods.getLimitCount()) {
            throw new OrderBusinessException("�û��Ѵﹺ������");
        }
        
        // 3. �ۼ���棨ʹ���µĿ����񣬴���־��¼��
        String orderNumber = "SK" + System.currentTimeMillis();
        boolean deductSuccess = seckillStockService.deductStock(
            seckillGoods.getId(), 
            seckillOrderSubmitDTO.getQuantity(), 
            seckillGoods.getVersion() != null ? seckillGoods.getVersion().longValue() : 1L,
            null, // ����ID�Ժ�����
            orderNumber,
            userId
        );
        
        if (!deductSuccess) {
            // ���ۼ�ʧ�ܣ������ǲ������µİ汾�Ų�ƥ����治��
            throw new OrderBusinessException("ϵͳ��æ�����Ժ�����");
        }
        
        // 4. ���»򴴽��û������¼��ʹ��MySQL��ON DUPLICATE KEY UPDATE���Ⲣ�����⣩
        try {
            if (userRecord == null) {
                // �����µĹ����¼
                userRecord = SeckillUserRecord.builder()
                    .activityId(seckillGoods.getActivityId())
                    .seckillGoodsId(seckillOrderSubmitDTO.getSeckillGoodsId())
                    .userId(userId)
                    .quantity(seckillOrderSubmitDTO.getQuantity())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
                seckillUserRecordMapper.insertOrUpdate(userRecord);
            } else {
                // �������й����¼
                userRecord.setQuantity(alreadyPurchased + seckillOrderSubmitDTO.getQuantity());
                userRecord.setUpdateTime(LocalDateTime.now());
                seckillUserRecordMapper.update(userRecord);
            }
        } catch (DataIntegrityViolationException e) {
            // ����������µ��ظ����쳣
            log.warn("�û� {} ����ɱ��Ʒ {} ʱ���ֲ������룬���Ը������м�¼", userId, seckillOrderSubmitDTO.getSeckillGoodsId());
            // ���²�ѯ�û���¼������
            userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillOrderSubmitDTO.getSeckillGoodsId());
            if (userRecord != null) {
                userRecord.setQuantity(userRecord.getQuantity() + seckillOrderSubmitDTO.getQuantity());
                userRecord.setUpdateTime(LocalDateTime.now());
                seckillUserRecordMapper.update(userRecord);
            } else {
                throw new OrderBusinessException("ϵͳ��æ�����Ժ�����");
            }
        }
        
        // 5. ������ͨ������¼
        Orders orders = new Orders();
        orders.setNumber(orderNumber);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setAddressBookId(seckillOrderSubmitDTO.getAddressBookId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPayMethod(1); // ����Ĭ��֧����ʽΪ΢��֧��
        
        // ���㶩�����
        BigDecimal totalAmount = seckillGoods.getSeckillPrice().multiply(new BigDecimal(seckillOrderSubmitDTO.getQuantity()));
        orders.setAmount(totalAmount);
        orders.setRemark(seckillOrderSubmitDTO.getRemark());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));
        orders.setDeliveryStatus(1);
        orders.setPackAmount(0);
        orders.setTablewareNumber(1);
        orders.setTablewareStatus(1);
        // ������deliveryFee����ΪOrderMapper.xml��û������ֶ�

        // ���붩����
        orderMapper.insert(orders);

        // 6. ������ɱ������¼
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(orders.getId());
        seckillOrder.setNumber(orders.getNumber());
        seckillOrder.setActivityId(seckillGoods.getActivityId());
        seckillOrder.setSeckillGoodsId(seckillOrderSubmitDTO.getSeckillGoodsId());
        
        // ��ȡ��ʵ����Ʒ��Ϣ
        String goodsName;
        String goodsImage;
        BigDecimal originalPrice;
        
        if (seckillGoods.getGoodsType() == 1) {
            // ��Ʒ����
            Dish dish = dishMapper.getById(seckillGoods.getGoodsId());
            if (dish != null) {
                goodsName = dish.getName();
                goodsImage = dish.getImage();
                originalPrice = dish.getPrice();
            } else {
                goodsName = seckillGoods.getGoodsName();
                goodsImage = seckillGoods.getGoodsImage();
                originalPrice = seckillGoods.getOriginalPrice();
            }
        } else {
            // �ײ�����
            Setmeal setmeal = setmealMapper.getById(seckillGoods.getGoodsId());
            if (setmeal != null) {
                goodsName = setmeal.getName();
                goodsImage = setmeal.getImage();
                originalPrice = setmeal.getPrice();
            } else {
                goodsName = seckillGoods.getGoodsName();
                goodsImage = seckillGoods.getGoodsImage();
                originalPrice = seckillGoods.getOriginalPrice();
            }
        }
        
        seckillOrder.setGoodsName(goodsName);
        seckillOrder.setGoodsImage(goodsImage);
        seckillOrder.setGoodsType(seckillGoods.getGoodsType());
        seckillOrder.setOriginalPrice(originalPrice);
        seckillOrder.setSeckillPrice(seckillGoods.getSeckillPrice());
        seckillOrder.setQuantity(seckillOrderSubmitDTO.getQuantity());
        seckillOrder.setAmount(totalAmount);
        seckillOrder.setUserId(userId);
        seckillOrder.setStatus(Orders.PENDING_PAYMENT);
        seckillOrder.setPayStatus(Orders.UN_PAID);
        seckillOrder.setOrderTime(LocalDateTime.now());
        seckillOrder.setPayExpireTime(LocalDateTime.now().plusMinutes(15)); // 15����֧����ʱ
        seckillOrder.setRemark(seckillOrderSubmitDTO.getRemark());
        seckillOrder.setAddressBookId(seckillOrderSubmitDTO.getAddressBookId());
        seckillOrder.setConsignee(addressBook.getConsignee());
        seckillOrder.setPhone(addressBook.getPhone());
        seckillOrder.setAddress(addressBook.getDetail());
        seckillOrder.setCreateTime(LocalDateTime.now());
        seckillOrder.setUpdateTime(LocalDateTime.now());
        seckillOrder.setCreateUser(userId);
        seckillOrder.setUpdateUser(userId);

        // ������ɱ������
        seckillOrderMapper.insert(seckillOrder);

        // 7. ����������ϸ��¼��Ϊ�˼�����ͨ��������ӿڣ�
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orders.getId());
        orderDetail.setName(goodsName);
        orderDetail.setImage(goodsImage);
        orderDetail.setDishId(seckillGoods.getGoodsType() == 1 ? seckillGoods.getGoodsId() : null);
        orderDetail.setSetmealId(seckillGoods.getGoodsType() == 2 ? seckillGoods.getGoodsId() : null);
        orderDetail.setDishFlavor(""); // ��ɱ��Ʒû�п�ζѡ��
        orderDetail.setNumber(seckillOrderSubmitDTO.getQuantity());
        orderDetail.setAmount(seckillGoods.getSeckillPrice());

        // ���붩����ϸ��
        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderDetailList.add(orderDetail);
        orderDetailMapper.insertBatch(orderDetailList);

        // 8. ���ؽ��
        SeckillOrderSubmitVO submitVO = SeckillOrderSubmitVO.builder()
                .orderId(seckillOrder.getId()) // ʹ����ɱ����ID��������ͨ����ID
                .orderNumber(orders.getNumber())
                .payExpireTime(seckillOrder.getPayExpireTime())
                .totalAmount(seckillOrder.getAmount())
                .payTimeLimit(15 * 60) // 15����
                .build();

        log.info("��ɱ�����ύ�ɹ�: {}", submitVO);
        return submitVO;
    }

    /**
     * ʹ��Lua�ű��ύ��ɱ����
     */
    @Override
    @Transactional
    public SeckillOrderSubmitVO submitOrderWithLua(SeckillOrderSubmitDTO seckillOrderSubmitDTO) {
        log.info("[Lua] �ύ��ɱ����: {}", seckillOrderSubmitDTO);

        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L;
        }

        AddressBook addressBook = addressBookMapper.getById(seckillOrderSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillOrderSubmitDTO.getSeckillGoodsId());
        if (seckillGoods == null) {
            throw new OrderBusinessException("��ɱ��Ʒ������");
        }
        if (seckillGoods.getStatus() != SeckillGoods.ONLINE) {
            throw new OrderBusinessException("��ɱ��Ʒ���¼�");
        }

        // ʹ��Lua���п��ԭ�ӿۼ� + �޹�У��
        Map<String, Object> luaResult = seckillLuaService.executeStockDeduct(
                seckillGoods.getId(),
                userId,
                seckillOrderSubmitDTO.getQuantity(),
                seckillGoods.getLimitCount(),
                24 * 60 * 60 // �û��޹���¼Ĭ��1����ڣ��ɰ������
        );
        Integer code = (Integer) (luaResult.get("code") instanceof Integer ? luaResult.get("code") : Integer.valueOf(String.valueOf(luaResult.get("code"))));
        if (code == null || code != 1) {
            String msg = String.valueOf(luaResult.get("msg"));
            throw new OrderBusinessException(msg != null ? msg : "ϵͳ��æ�����Ժ�����");
        }

        // ������ submitOrder ����һ�µ�������̣����ٶ��οۿ⣩
        String orderNumber = "SK" + System.currentTimeMillis();

        // �û������¼ +1����Lua����һ�£������������һ���Ա��ϣ������ڲ�����ͻ�����´���
        SeckillUserRecord userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillGoods.getId());
        int alreadyPurchased = userRecord != null ? userRecord.getQuantity() : 0;
        try {
            if (userRecord == null) {
                userRecord = SeckillUserRecord.builder()
                        .activityId(seckillGoods.getActivityId())
                        .seckillGoodsId(seckillGoods.getId())
                        .userId(userId)
                        .quantity(seckillOrderSubmitDTO.getQuantity())
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
                seckillUserRecordMapper.insertOrUpdate(userRecord);
            } else {
                userRecord.setQuantity(alreadyPurchased + seckillOrderSubmitDTO.getQuantity());
                userRecord.setUpdateTime(LocalDateTime.now());
                seckillUserRecordMapper.update(userRecord);
            }
        } catch (DataIntegrityViolationException e) {
            log.warn("[Lua] �û���¼��������Ϊ����");
            userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillGoods.getId());
            if (userRecord != null) {
                userRecord.setQuantity(userRecord.getQuantity() + seckillOrderSubmitDTO.getQuantity());
                userRecord.setUpdateTime(LocalDateTime.now());
                seckillUserRecordMapper.update(userRecord);
            }
        }

        // ������ͨ����
        Orders orders = new Orders();
        orders.setNumber(orderNumber);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setAddressBookId(seckillOrderSubmitDTO.getAddressBookId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPayMethod(1);

        java.math.BigDecimal totalAmount = seckillGoods.getSeckillPrice().multiply(new java.math.BigDecimal(seckillOrderSubmitDTO.getQuantity()));
        orders.setAmount(totalAmount);
        orders.setRemark(seckillOrderSubmitDTO.getRemark());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));
        orders.setDeliveryStatus(1);
        orders.setPackAmount(0);
        orders.setTablewareNumber(1);
        orders.setTablewareStatus(1);
        orderMapper.insert(orders);

        // ������ɱ����
        String goodsName;
        String goodsImage;
        java.math.BigDecimal originalPrice;
        if (seckillGoods.getGoodsType() == 1) {
            Dish dish = dishMapper.getById(seckillGoods.getGoodsId());
            if (dish != null) {
                goodsName = dish.getName();
                goodsImage = dish.getImage();
                originalPrice = dish.getPrice();
            } else {
                goodsName = seckillGoods.getGoodsName();
                goodsImage = seckillGoods.getGoodsImage();
                originalPrice = seckillGoods.getOriginalPrice();
            }
        } else {
            Setmeal setmeal = setmealMapper.getById(seckillGoods.getGoodsId());
            if (setmeal != null) {
                goodsName = setmeal.getName();
                goodsImage = setmeal.getImage();
                originalPrice = setmeal.getPrice();
            } else {
                goodsName = seckillGoods.getGoodsName();
                goodsImage = seckillGoods.getGoodsImage();
                originalPrice = seckillGoods.getOriginalPrice();
            }
        }

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(orders.getId());
        seckillOrder.setNumber(orders.getNumber());
        seckillOrder.setActivityId(seckillGoods.getActivityId());
        seckillOrder.setSeckillGoodsId(seckillGoods.getId());
        seckillOrder.setGoodsName(goodsName);
        seckillOrder.setGoodsImage(goodsImage);
        seckillOrder.setGoodsType(seckillGoods.getGoodsType());
        seckillOrder.setOriginalPrice(originalPrice);
        seckillOrder.setSeckillPrice(seckillGoods.getSeckillPrice());
        seckillOrder.setQuantity(seckillOrderSubmitDTO.getQuantity());
        seckillOrder.setAmount(totalAmount);
        seckillOrder.setUserId(userId);
        seckillOrder.setStatus(Orders.PENDING_PAYMENT);
        seckillOrder.setPayStatus(Orders.UN_PAID);
        seckillOrder.setOrderTime(LocalDateTime.now());
        seckillOrder.setPayExpireTime(LocalDateTime.now().plusMinutes(15));
        seckillOrder.setRemark(seckillOrderSubmitDTO.getRemark());
        seckillOrder.setAddressBookId(seckillOrderSubmitDTO.getAddressBookId());
        seckillOrder.setConsignee(addressBook.getConsignee());
        seckillOrder.setPhone(addressBook.getPhone());
        seckillOrder.setAddress(addressBook.getDetail());
        seckillOrder.setCreateTime(LocalDateTime.now());
        seckillOrder.setUpdateTime(LocalDateTime.now());
        seckillOrder.setCreateUser(userId);
        seckillOrder.setUpdateUser(userId);
        seckillOrderMapper.insert(seckillOrder);

        // ������ϸ
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orders.getId());
        orderDetail.setName(goodsName);
        orderDetail.setImage(goodsImage);
        orderDetail.setDishId(seckillGoods.getGoodsType() == 1 ? seckillGoods.getGoodsId() : null);
        orderDetail.setSetmealId(seckillGoods.getGoodsType() == 2 ? seckillGoods.getGoodsId() : null);
        orderDetail.setDishFlavor("");
        orderDetail.setNumber(seckillOrderSubmitDTO.getQuantity());
        orderDetail.setAmount(seckillGoods.getSeckillPrice());
        java.util.List<OrderDetail> list = new java.util.ArrayList<>();
        list.add(orderDetail);
        orderDetailMapper.insertBatch(list);

        SeckillOrderSubmitVO submitVO = SeckillOrderSubmitVO.builder()
                .orderId(seckillOrder.getId())
                .orderNumber(orders.getNumber())
                .payExpireTime(seckillOrder.getPayExpireTime())
                .totalAmount(seckillOrder.getAmount())
                .payTimeLimit(15 * 60)
                .build();
        log.info("[Lua] ��ɱ�����ύ�ɹ�: {}", submitVO);
        return submitVO;
    }

    /**
     * ��ɱ����֧��
     * @param ordersPaymentDTO
     * @return
     * @throws Exception
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("��ʼ������ɱ����֧���������ţ�{}��֧����ʽ��{}", ordersPaymentDTO.getOrderNumber(), ordersPaymentDTO.getPayMethod());

        // ��ǰ��¼�û�id
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // ģ���û�ID
        }
        User user = userMapper.getById(userId);

        log.info("��ǰ�û�ID��{}���û���Ϣ��{}", userId, user);

        // ����΢��֧���ӿڣ�����Ԥ֧�����׵���������ģ�⣩
        // JSONObject jsonObject = weChatPayUtil.pay(
        //         ordersPaymentDTO.getOrderNumber(), //�̻�������
        //         new BigDecimal(3.00), //֧������λ Ԫ
        //         "���������ɱ����", //��Ʒ����
        //         user.getOpenid() //΢���û���openid
        // );

        // �ֶ�����֧���ɹ�������ģ��֧�����
        paySuccess(ordersPaymentDTO.getOrderNumber());

        log.info("��ɱ����֧��������ɣ������ţ�{}", ordersPaymentDTO.getOrderNumber());

        // ����֧����Ϣ
        OrderPaymentVO vo = new OrderPaymentVO();
        vo.setNonceStr("mock_nonce_" + System.currentTimeMillis());
        vo.setPaySign("mock_sign_" + System.currentTimeMillis());
        vo.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        vo.setSignType("RSA");
        vo.setPackageStr("prepay_id=mock_prepay_" + System.currentTimeMillis());

        return vo;
    }

    /**
     * ֧���ɹ����޸Ķ���״̬
     * @param outTradeNo
     */
    @Override
    public void paySuccess(String outTradeNo) {
        // ��ǰ��¼�û�id
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // ģ���û�ID
        }

        // ���ݶ����Ų�ѯ��ǰ�û�����ɱ����
        SeckillOrder seckillOrderDB = seckillOrderMapper.getByNumberAndUserId(outTradeNo, userId);

        // ��鶩���Ƿ����
        if (seckillOrderDB == null) {
            log.error("��ɱ���������ڣ������ţ�{}���û�ID��{}", outTradeNo, userId);
            throw new OrderBusinessException("����������");
        }

        // ������ɱ����״̬ - ��ɱ����֧���ɹ�����Ϊ���ӵ�״̬
        SeckillOrder seckillOrder = SeckillOrder.builder()
                .id(seckillOrderDB.getId())
                .status(Orders.TO_BE_CONFIRMED) // ֧���ɹ�����Ϊ���ӵ�״̬
                .payStatus(Orders.PAID)
                .payMethod(1) // ΢��֧��
                .checkoutTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .updateUser(userId)
                .build();

        seckillOrderMapper.update(seckillOrder);

        // ͬʱ������ͨ�������״̬
        if (seckillOrderDB.getOrderId() != null) {
            Orders orders = Orders.builder()
                    .id(seckillOrderDB.getOrderId())
                    .status(Orders.TO_BE_CONFIRMED) // ֧���ɹ�����Ϊ���ӵ�״̬
                    .payStatus(Orders.PAID)
                    .payMethod(1)
                    .checkoutTime(LocalDateTime.now())
                    .build();

            orderMapper.update(orders);
        }

        log.info("��ɱ����֧���ɹ��������ţ�{}������ID��{}", outTradeNo, seckillOrderDB.getId());
    }

    /**
     * ��ҳ��ѯ�û���ɱ����
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult pageQueryByUser(int page, int pageSize, Integer status) {
        // ���÷�ҳ
        PageHelper.startPage(page, pageSize);

        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // ģ���û�ID
        }

        Page<SeckillOrder> pageResult = seckillOrderMapper.pageQueryByUser(userId, status);

        // ת�����ݸ�ʽ�����seckillInfoǶ�׶���
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (SeckillOrder seckillOrder : pageResult.getResult()) {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("id", seckillOrder.getId());
            orderData.put("number", seckillOrder.getNumber());
            orderData.put("status", seckillOrder.getStatus());
            orderData.put("orderTime", seckillOrder.getOrderTime());
            orderData.put("checkoutTime", seckillOrder.getCheckoutTime());
            orderData.put("payMethod", seckillOrder.getPayMethod());
            orderData.put("payStatus", seckillOrder.getPayStatus());
            orderData.put("amount", seckillOrder.getAmount());

            // ����seckillInfo����
            Map<String, Object> seckillInfo = new HashMap<>();
            
            // ��ȡ��ʵ�Ļ����
            String activityName = "��ɱ�"; // Ĭ������
            if (seckillOrder.getActivityId() != null) {
                SeckillActivity activity = seckillActivityMapper.getById(seckillOrder.getActivityId());
                if (activity != null) {
                    activityName = activity.getName();
                }
            }
            
            seckillInfo.put("activityName", activityName);
            seckillInfo.put("goodsName", seckillOrder.getGoodsName());
            seckillInfo.put("goodsImage", seckillOrder.getGoodsImage());
            seckillInfo.put("originalPrice", seckillOrder.getOriginalPrice());
            seckillInfo.put("seckillPrice", seckillOrder.getSeckillPrice());
            seckillInfo.put("quantity", seckillOrder.getQuantity());

            orderData.put("seckillInfo", seckillInfo);
            resultList.add(orderData);
        }

        return new PageResult(pageResult.getTotal(), resultList);
    }

    /**
     * ����ID��ѯ��ɱ��������
     * @param id
     * @return
     */
    @Override
    public Object getOrderDetail(Long id) {
        SeckillOrder seckillOrder = seckillOrderMapper.getById(id);
        if (seckillOrder == null) {
            throw new OrderBusinessException("����������");
        }

        // ������������
        Map<String, Object> orderDetail = new HashMap<>();
        orderDetail.put("id", seckillOrder.getId());
        orderDetail.put("number", seckillOrder.getNumber());
        orderDetail.put("status", seckillOrder.getStatus());
        orderDetail.put("orderTime", seckillOrder.getOrderTime());
        orderDetail.put("checkoutTime", seckillOrder.getCheckoutTime());
        orderDetail.put("payMethod", seckillOrder.getPayMethod());
        orderDetail.put("payStatus", seckillOrder.getPayStatus());
        orderDetail.put("amount", seckillOrder.getAmount());
        orderDetail.put("remark", seckillOrder.getRemark());
        orderDetail.put("phone", seckillOrder.getPhone());
        orderDetail.put("address", seckillOrder.getAddress());
        orderDetail.put("consignee", seckillOrder.getConsignee());
        orderDetail.put("estimatedDeliveryTime", LocalDateTime.now().plusHours(1));

        // ��ɱ����������Ϣ
        Map<String, Object> seckillInfo = new HashMap<>();
        
        // ��ȡ��ʵ�Ļ����
        String activityName = "��ɱ�"; // Ĭ������
        if (seckillOrder.getActivityId() != null) {
            SeckillActivity activity = seckillActivityMapper.getById(seckillOrder.getActivityId());
            if (activity != null) {
                activityName = activity.getName();
            }
        }
        
        seckillInfo.put("activityName", activityName);
        seckillInfo.put("goodsName", seckillOrder.getGoodsName());
        seckillInfo.put("goodsImage", seckillOrder.getGoodsImage());
        seckillInfo.put("originalPrice", seckillOrder.getOriginalPrice());
        seckillInfo.put("seckillPrice", seckillOrder.getSeckillPrice());
        seckillInfo.put("quantity", seckillOrder.getQuantity());

        orderDetail.put("seckillInfo", seckillInfo);

        return orderDetail;
    }

    /**
     * ȡ����ɱ����
     * @param id
     */
    @Override
    @Transactional
    public void cancelOrder(Long id) {
        SeckillOrder seckillOrder = seckillOrderMapper.getById(id);
        if (seckillOrder == null) {
            throw new OrderBusinessException("����������");
        }

        // ֻ�д�֧��״̬�Ķ�������ȡ��
        if (!Orders.PENDING_PAYMENT.equals(seckillOrder.getStatus())) {
            throw new OrderBusinessException("����״̬������ȡ��");
        }

        // 1. �ָ���棨ʹ���µĿ�����
        seckillStockService.releaseStock(
            seckillOrder.getSeckillGoodsId(), 
            seckillOrder.getQuantity(),
            seckillOrder.getOrderId(),
            seckillOrder.getNumber(),
            seckillOrder.getUserId(),
            "�û�����ȡ������"
        );
        
        // 2. �����û������¼
        updateUserRecordOnCancel(seckillOrder.getUserId(), seckillOrder.getSeckillGoodsId(), seckillOrder.getQuantity());

        // 3. ���¶���״̬Ϊ��ȡ��
        SeckillOrder updateOrder = SeckillOrder.builder()
                .id(id)
                .status(Orders.CANCELLED)
                .updateTime(LocalDateTime.now())
                .build();

        seckillOrderMapper.update(updateOrder);

        // 4. ͬʱ������ͨ�������״̬
        if (seckillOrder.getOrderId() != null) {
            Orders orders = Orders.builder()
                    .id(seckillOrder.getOrderId())
                    .status(Orders.CANCELLED)
                    .build();

            orderMapper.update(orders);
        }

        log.info("��ɱ����ȡ���ɹ�������ID��{}���ѻָ���棺{}", id, seckillOrder.getQuantity());
    }

    /**
     * ����һ��
     * @param id
     * @return
     */
    @Override
    public SeckillOrderSubmitVO repeatOrder(Long id) {
        // ��ѯԭ����
        SeckillOrder originalOrder = seckillOrderMapper.getById(id);
        if (originalOrder == null) {
            throw new OrderBusinessException("ԭ����������");
        }

        // �����µĶ����ύ����
        SeckillOrderSubmitDTO submitDTO = new SeckillOrderSubmitDTO();
        submitDTO.setSeckillGoodsId(originalOrder.getSeckillGoodsId());
        submitDTO.setQuantity(originalOrder.getQuantity());
        submitDTO.setAddressBookId(originalOrder.getAddressBookId());
        submitDTO.setRemark(originalOrder.getRemark());

        // �ύ�¶���
        return submitOrder(submitDTO);
    }

    /**
     * ����ʱδ֧������ɱ����
     */
    @Override
    public void handleExpiredOrders() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            List<SeckillOrder> expiredOrders = seckillOrderMapper.getExpiredOrders(currentTime);
            
            for (SeckillOrder expiredOrder : expiredOrders) {
                log.info("����ʱ����: {}", expiredOrder.getNumber());
                
                // 1. �ָ���棨ʹ���µĿ�����
                seckillStockService.releaseStock(
                    expiredOrder.getSeckillGoodsId(), 
                    expiredOrder.getQuantity(),
                    expiredOrder.getOrderId(),
                    expiredOrder.getNumber(),
                    expiredOrder.getUserId(),
                    "����֧����ʱ�Զ�ȡ��"
                );
                
                // 2. �����û������¼
                updateUserRecordOnCancel(expiredOrder.getUserId(), expiredOrder.getSeckillGoodsId(), expiredOrder.getQuantity());
                
                // 3. ȡ����ɱ����
                SeckillOrder cancelOrder = SeckillOrder.builder()
                        .id(expiredOrder.getId())
                        .status(Orders.CANCELLED)
                        .updateTime(LocalDateTime.now())
                        .build();
                seckillOrderMapper.update(cancelOrder);
                
                // 4. ͬʱȡ����ͨ����
                if (expiredOrder.getOrderId() != null) {
                    Orders orders = Orders.builder()
                            .id(expiredOrder.getOrderId())
                            .status(Orders.CANCELLED)
                            .build();
                    orderMapper.update(orders);
                }
                
                log.info("��ʱ�����������: {}���ѻָ���棺{}", expiredOrder.getNumber(), expiredOrder.getQuantity());
            }
            
        } catch (Exception e) {
            log.error("����ʱ����ʧ��", e);
        }
    }



    /**
     * ȡ������ʱ�����û������¼
     * @param userId �û�ID
     * @param seckillGoodsId ��ɱ��ƷID
     * @param quantity ȡ������
     */
    private void updateUserRecordOnCancel(Long userId, Long seckillGoodsId, Integer quantity) {
        try {
                    SeckillUserRecord userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillGoodsId);
        if (userRecord != null) {
            int newCount = userRecord.getQuantity() - quantity;
            if (newCount <= 0) {
                // �������������Ϊ0����������ѡ��ɾ����¼����Ϊ0
                newCount = 0;
            }
            
            userRecord.setQuantity(newCount);
            userRecord.setUpdateTime(LocalDateTime.now());
                seckillUserRecordMapper.update(userRecord);
                
                log.info("�����û������¼�ɹ����û�ID��{}����ƷID��{}���¹���������{}", userId, seckillGoodsId, newCount);
            }
        } catch (Exception e) {
            log.error("�����û������¼ʧ�ܣ��û�ID��{}����ƷID��{}", userId, seckillGoodsId, e);
            // ���ﲻ���쳣������Ӱ�충��ȡ������
        }
    }

    /**
     * ����û������ʸ�
     * @param seckillGoodsId ��ɱ��ƷID
     * @param quantity ��������
     * @return �����ʸ���Ϣ
     */
    @Override
    public Object checkEligibility(Long seckillGoodsId, Integer quantity) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // ģ���û�ID
        }

        // ��ȡ��ɱ��Ʒ��Ϣ
        SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
        if (seckillGoods == null) {
            throw new OrderBusinessException("��ɱ��Ʒ������");
        }

        // ����û��ѹ�������
        SeckillUserRecord userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillGoodsId);
        int userPurchased = userRecord != null ? userRecord.getQuantity() : 0;
        
        // ����ʣ��ɹ�������
        int remainingQuota = seckillGoods.getLimitCount() - userPurchased;
        boolean canPurchase = remainingQuota >= quantity && seckillGoods.getAvailableStock() >= quantity;

        // �������ؽ��
        Map<String, Object> result = new HashMap<>();
        result.put("canPurchase", canPurchase);
        result.put("remainingQuota", Math.max(0, remainingQuota));
        result.put("limitCount", seckillGoods.getLimitCount());
        result.put("userPurchased", userPurchased);
        result.put("availableStock", seckillGoods.getAvailableStock());

        return result;
    }
}