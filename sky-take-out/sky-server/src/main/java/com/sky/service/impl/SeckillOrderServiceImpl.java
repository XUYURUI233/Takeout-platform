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
     * 提交秒杀订单
     * @param seckillOrderSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public SeckillOrderSubmitVO submitOrder(SeckillOrderSubmitDTO seckillOrderSubmitDTO) {
        log.info("提交秒杀订单: {}", seckillOrderSubmitDTO);

        // 获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // 模拟用户ID，实际应该从JWT中获取
        }

        // 1. 验证地址簿
        AddressBook addressBook = addressBookMapper.getById(seckillOrderSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 2. 获取秒杀商品信息并进行各种检查
        SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillOrderSubmitDTO.getSeckillGoodsId());
        if (seckillGoods == null) {
            throw new OrderBusinessException("秒杀商品不存在");
        }
        
        // 检查商品状态
        if (seckillGoods.getStatus() != SeckillGoods.ONLINE) {
            throw new OrderBusinessException("秒杀商品已下架");
        }
        
        // 检查库存是否充足
        if (seckillGoods.getAvailableStock() < seckillOrderSubmitDTO.getQuantity()) {
            throw new OrderBusinessException("库存不足");
        }
        
        // 检查用户限购
        SeckillUserRecord userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillOrderSubmitDTO.getSeckillGoodsId());
        int alreadyPurchased = userRecord != null ? userRecord.getQuantity() : 0;
        if (alreadyPurchased + seckillOrderSubmitDTO.getQuantity() > seckillGoods.getLimitCount()) {
            throw new OrderBusinessException("用户已达购买上限");
        }
        
        // 3. 扣减库存（使用新的库存服务，带日志记录）
        String orderNumber = "SK" + System.currentTimeMillis();
        boolean deductSuccess = seckillStockService.deductStock(
            seckillGoods.getId(), 
            seckillOrderSubmitDTO.getQuantity(), 
            seckillGoods.getVersion() != null ? seckillGoods.getVersion().longValue() : 1L,
            null, // 订单ID稍后设置
            orderNumber,
            userId
        );
        
        if (!deductSuccess) {
            // 库存扣减失败，可能是并发导致的版本号不匹配或库存不足
            throw new OrderBusinessException("系统繁忙，请稍后重试");
        }
        
        // 4. 更新或创建用户购买记录（使用MySQL的ON DUPLICATE KEY UPDATE避免并发问题）
        try {
            if (userRecord == null) {
                // 创建新的购买记录
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
                // 更新已有购买记录
                userRecord.setQuantity(alreadyPurchased + seckillOrderSubmitDTO.getQuantity());
                userRecord.setUpdateTime(LocalDateTime.now());
                seckillUserRecordMapper.update(userRecord);
            }
        } catch (DataIntegrityViolationException e) {
            // 处理并发情况下的重复键异常
            log.warn("用户 {} 在秒杀商品 {} 时出现并发插入，尝试更新现有记录", userId, seckillOrderSubmitDTO.getSeckillGoodsId());
            // 重新查询用户记录并更新
            userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillOrderSubmitDTO.getSeckillGoodsId());
            if (userRecord != null) {
                userRecord.setQuantity(userRecord.getQuantity() + seckillOrderSubmitDTO.getQuantity());
                userRecord.setUpdateTime(LocalDateTime.now());
                seckillUserRecordMapper.update(userRecord);
            } else {
                throw new OrderBusinessException("系统繁忙，请稍后重试");
            }
        }
        
        // 5. 创建普通订单记录
        Orders orders = new Orders();
        orders.setNumber(orderNumber);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setAddressBookId(seckillOrderSubmitDTO.getAddressBookId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPayMethod(1); // 设置默认支付方式为微信支付
        
        // 计算订单金额
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
        // 不设置deliveryFee，因为OrderMapper.xml中没有这个字段

        // 插入订单表
        orderMapper.insert(orders);

        // 6. 创建秒杀订单记录
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(orders.getId());
        seckillOrder.setNumber(orders.getNumber());
        seckillOrder.setActivityId(seckillGoods.getActivityId());
        seckillOrder.setSeckillGoodsId(seckillOrderSubmitDTO.getSeckillGoodsId());
        
        // 获取真实的商品信息
        String goodsName;
        String goodsImage;
        BigDecimal originalPrice;
        
        if (seckillGoods.getGoodsType() == 1) {
            // 菜品类型
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
            // 套餐类型
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
        seckillOrder.setPayExpireTime(LocalDateTime.now().plusMinutes(15)); // 15分钟支付超时
        seckillOrder.setRemark(seckillOrderSubmitDTO.getRemark());
        seckillOrder.setAddressBookId(seckillOrderSubmitDTO.getAddressBookId());
        seckillOrder.setConsignee(addressBook.getConsignee());
        seckillOrder.setPhone(addressBook.getPhone());
        seckillOrder.setAddress(addressBook.getDetail());
        seckillOrder.setCreateTime(LocalDateTime.now());
        seckillOrder.setUpdateTime(LocalDateTime.now());
        seckillOrder.setCreateUser(userId);
        seckillOrder.setUpdateUser(userId);

        // 插入秒杀订单表
        seckillOrderMapper.insert(seckillOrder);

        // 7. 创建订单明细记录（为了兼容普通订单详情接口）
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orders.getId());
        orderDetail.setName(goodsName);
        orderDetail.setImage(goodsImage);
        orderDetail.setDishId(seckillGoods.getGoodsType() == 1 ? seckillGoods.getGoodsId() : null);
        orderDetail.setSetmealId(seckillGoods.getGoodsType() == 2 ? seckillGoods.getGoodsId() : null);
        orderDetail.setDishFlavor(""); // 秒杀商品没有口味选择
        orderDetail.setNumber(seckillOrderSubmitDTO.getQuantity());
        orderDetail.setAmount(seckillGoods.getSeckillPrice());

        // 插入订单明细表
        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderDetailList.add(orderDetail);
        orderDetailMapper.insertBatch(orderDetailList);

        // 8. 返回结果
        SeckillOrderSubmitVO submitVO = SeckillOrderSubmitVO.builder()
                .orderId(seckillOrder.getId()) // 使用秒杀订单ID而不是普通订单ID
                .orderNumber(orders.getNumber())
                .payExpireTime(seckillOrder.getPayExpireTime())
                .totalAmount(seckillOrder.getAmount())
                .payTimeLimit(15 * 60) // 15分钟
                .build();

        log.info("秒杀订单提交成功: {}", submitVO);
        return submitVO;
    }

    /**
     * 使用Lua脚本提交秒杀订单
     */
    @Override
    @Transactional
    public SeckillOrderSubmitVO submitOrderWithLua(SeckillOrderSubmitDTO seckillOrderSubmitDTO) {
        log.info("[Lua] 提交秒杀订单: {}", seckillOrderSubmitDTO);

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
            throw new OrderBusinessException("秒杀商品不存在");
        }
        if (seckillGoods.getStatus() != SeckillGoods.ONLINE) {
            throw new OrderBusinessException("秒杀商品已下架");
        }

        // 使用Lua进行库存原子扣减 + 限购校验
        Map<String, Object> luaResult = seckillLuaService.executeStockDeduct(
                seckillGoods.getId(),
                userId,
                seckillOrderSubmitDTO.getQuantity(),
                seckillGoods.getLimitCount(),
                24 * 60 * 60 // 用户限购记录默认1天过期，可按需调整
        );
        Integer code = (Integer) (luaResult.get("code") instanceof Integer ? luaResult.get("code") : Integer.valueOf(String.valueOf(luaResult.get("code"))));
        if (code == null || code != 1) {
            String msg = String.valueOf(luaResult.get("msg"));
            throw new OrderBusinessException(msg != null ? msg : "系统繁忙，请稍后重试");
        }

        // 继续与 submitOrder 基本一致的落库流程（不再二次扣库）
        String orderNumber = "SK" + System.currentTimeMillis();

        // 用户购买记录 +1（与Lua保持一致，这里仅做最终一致性保障；若存在并发冲突按更新处理）
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
            log.warn("[Lua] 用户记录并发，改为更新");
            userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillGoods.getId());
            if (userRecord != null) {
                userRecord.setQuantity(userRecord.getQuantity() + seckillOrderSubmitDTO.getQuantity());
                userRecord.setUpdateTime(LocalDateTime.now());
                seckillUserRecordMapper.update(userRecord);
            }
        }

        // 创建普通订单
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

        // 生成秒杀订单
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

        // 订单明细
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
        log.info("[Lua] 秒杀订单提交成功: {}", submitVO);
        return submitVO;
    }

    /**
     * 秒杀订单支付
     * @param ordersPaymentDTO
     * @return
     * @throws Exception
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("开始处理秒杀订单支付，订单号：{}，支付方式：{}", ordersPaymentDTO.getOrderNumber(), ordersPaymentDTO.getPayMethod());

        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // 模拟用户ID
        }
        User user = userMapper.getById(userId);

        log.info("当前用户ID：{}，用户信息：{}", userId, user);

        // 调用微信支付接口，生成预支付交易单（这里先模拟）
        // JSONObject jsonObject = weChatPayUtil.pay(
        //         ordersPaymentDTO.getOrderNumber(), //商户订单号
        //         new BigDecimal(3.00), //支付金额，单位 元
        //         "苍穹外卖秒杀订单", //商品描述
        //         user.getOpenid() //微信用户的openid
        // );

        // 手动调用支付成功方法，模拟支付完成
        paySuccess(ordersPaymentDTO.getOrderNumber());

        log.info("秒杀订单支付处理完成，订单号：{}", ordersPaymentDTO.getOrderNumber());

        // 返回支付信息
        OrderPaymentVO vo = new OrderPaymentVO();
        vo.setNonceStr("mock_nonce_" + System.currentTimeMillis());
        vo.setPaySign("mock_sign_" + System.currentTimeMillis());
        vo.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        vo.setSignType("RSA");
        vo.setPackageStr("prepay_id=mock_prepay_" + System.currentTimeMillis());

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    @Override
    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // 模拟用户ID
        }

        // 根据订单号查询当前用户的秒杀订单
        SeckillOrder seckillOrderDB = seckillOrderMapper.getByNumberAndUserId(outTradeNo, userId);

        // 检查订单是否存在
        if (seckillOrderDB == null) {
            log.error("秒杀订单不存在，订单号：{}，用户ID：{}", outTradeNo, userId);
            throw new OrderBusinessException("订单不存在");
        }

        // 更新秒杀订单状态 - 秒杀订单支付成功后设为待接单状态
        SeckillOrder seckillOrder = SeckillOrder.builder()
                .id(seckillOrderDB.getId())
                .status(Orders.TO_BE_CONFIRMED) // 支付成功后设为待接单状态
                .payStatus(Orders.PAID)
                .payMethod(1) // 微信支付
                .checkoutTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .updateUser(userId)
                .build();

        seckillOrderMapper.update(seckillOrder);

        // 同时更新普通订单表的状态
        if (seckillOrderDB.getOrderId() != null) {
            Orders orders = Orders.builder()
                    .id(seckillOrderDB.getOrderId())
                    .status(Orders.TO_BE_CONFIRMED) // 支付成功后设为待接单状态
                    .payStatus(Orders.PAID)
                    .payMethod(1)
                    .checkoutTime(LocalDateTime.now())
                    .build();

            orderMapper.update(orders);
        }

        log.info("秒杀订单支付成功，订单号：{}，订单ID：{}", outTradeNo, seckillOrderDB.getId());
    }

    /**
     * 分页查询用户秒杀订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult pageQueryByUser(int page, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(page, pageSize);

        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // 模拟用户ID
        }

        Page<SeckillOrder> pageResult = seckillOrderMapper.pageQueryByUser(userId, status);

        // 转换数据格式，添加seckillInfo嵌套对象
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

            // 构建seckillInfo对象
            Map<String, Object> seckillInfo = new HashMap<>();
            
            // 获取真实的活动名称
            String activityName = "秒杀活动"; // 默认名称
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
     * 根据ID查询秒杀订单详情
     * @param id
     * @return
     */
    @Override
    public Object getOrderDetail(Long id) {
        SeckillOrder seckillOrder = seckillOrderMapper.getById(id);
        if (seckillOrder == null) {
            throw new OrderBusinessException("订单不存在");
        }

        // 构建返回数据
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

        // 秒杀订单特有信息
        Map<String, Object> seckillInfo = new HashMap<>();
        
        // 获取真实的活动名称
        String activityName = "秒杀活动"; // 默认名称
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
     * 取消秒杀订单
     * @param id
     */
    @Override
    @Transactional
    public void cancelOrder(Long id) {
        SeckillOrder seckillOrder = seckillOrderMapper.getById(id);
        if (seckillOrder == null) {
            throw new OrderBusinessException("订单不存在");
        }

        // 只有待支付状态的订单才能取消
        if (!Orders.PENDING_PAYMENT.equals(seckillOrder.getStatus())) {
            throw new OrderBusinessException("订单状态不允许取消");
        }

        // 1. 恢复库存（使用新的库存服务）
        seckillStockService.releaseStock(
            seckillOrder.getSeckillGoodsId(), 
            seckillOrder.getQuantity(),
            seckillOrder.getOrderId(),
            seckillOrder.getNumber(),
            seckillOrder.getUserId(),
            "用户主动取消订单"
        );
        
        // 2. 更新用户购买记录
        updateUserRecordOnCancel(seckillOrder.getUserId(), seckillOrder.getSeckillGoodsId(), seckillOrder.getQuantity());

        // 3. 更新订单状态为已取消
        SeckillOrder updateOrder = SeckillOrder.builder()
                .id(id)
                .status(Orders.CANCELLED)
                .updateTime(LocalDateTime.now())
                .build();

        seckillOrderMapper.update(updateOrder);

        // 4. 同时更新普通订单表的状态
        if (seckillOrder.getOrderId() != null) {
            Orders orders = Orders.builder()
                    .id(seckillOrder.getOrderId())
                    .status(Orders.CANCELLED)
                    .build();

            orderMapper.update(orders);
        }

        log.info("秒杀订单取消成功，订单ID：{}，已恢复库存：{}", id, seckillOrder.getQuantity());
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @Override
    public SeckillOrderSubmitVO repeatOrder(Long id) {
        // 查询原订单
        SeckillOrder originalOrder = seckillOrderMapper.getById(id);
        if (originalOrder == null) {
            throw new OrderBusinessException("原订单不存在");
        }

        // 构建新的订单提交数据
        SeckillOrderSubmitDTO submitDTO = new SeckillOrderSubmitDTO();
        submitDTO.setSeckillGoodsId(originalOrder.getSeckillGoodsId());
        submitDTO.setQuantity(originalOrder.getQuantity());
        submitDTO.setAddressBookId(originalOrder.getAddressBookId());
        submitDTO.setRemark(originalOrder.getRemark());

        // 提交新订单
        return submitOrder(submitDTO);
    }

    /**
     * 处理超时未支付的秒杀订单
     */
    @Override
    public void handleExpiredOrders() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            List<SeckillOrder> expiredOrders = seckillOrderMapper.getExpiredOrders(currentTime);
            
            for (SeckillOrder expiredOrder : expiredOrders) {
                log.info("处理超时订单: {}", expiredOrder.getNumber());
                
                // 1. 恢复库存（使用新的库存服务）
                seckillStockService.releaseStock(
                    expiredOrder.getSeckillGoodsId(), 
                    expiredOrder.getQuantity(),
                    expiredOrder.getOrderId(),
                    expiredOrder.getNumber(),
                    expiredOrder.getUserId(),
                    "订单支付超时自动取消"
                );
                
                // 2. 更新用户购买记录
                updateUserRecordOnCancel(expiredOrder.getUserId(), expiredOrder.getSeckillGoodsId(), expiredOrder.getQuantity());
                
                // 3. 取消秒杀订单
                SeckillOrder cancelOrder = SeckillOrder.builder()
                        .id(expiredOrder.getId())
                        .status(Orders.CANCELLED)
                        .updateTime(LocalDateTime.now())
                        .build();
                seckillOrderMapper.update(cancelOrder);
                
                // 4. 同时取消普通订单
                if (expiredOrder.getOrderId() != null) {
                    Orders orders = Orders.builder()
                            .id(expiredOrder.getOrderId())
                            .status(Orders.CANCELLED)
                            .build();
                    orderMapper.update(orders);
                }
                
                log.info("超时订单处理完成: {}，已恢复库存：{}", expiredOrder.getNumber(), expiredOrder.getQuantity());
            }
            
        } catch (Exception e) {
            log.error("处理超时订单失败", e);
        }
    }



    /**
     * 取消订单时更新用户购买记录
     * @param userId 用户ID
     * @param seckillGoodsId 秒杀商品ID
     * @param quantity 取消数量
     */
    private void updateUserRecordOnCancel(Long userId, Long seckillGoodsId, Integer quantity) {
        try {
                    SeckillUserRecord userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillGoodsId);
        if (userRecord != null) {
            int newCount = userRecord.getQuantity() - quantity;
            if (newCount <= 0) {
                // 如果购买数量减为0或负数，可以选择删除记录或设为0
                newCount = 0;
            }
            
            userRecord.setQuantity(newCount);
            userRecord.setUpdateTime(LocalDateTime.now());
                seckillUserRecordMapper.update(userRecord);
                
                log.info("更新用户购买记录成功，用户ID：{}，商品ID：{}，新购买数量：{}", userId, seckillGoodsId, newCount);
            }
        } catch (Exception e) {
            log.error("更新用户购买记录失败，用户ID：{}，商品ID：{}", userId, seckillGoodsId, e);
            // 这里不抛异常，避免影响订单取消流程
        }
    }

    /**
     * 检查用户购买资格
     * @param seckillGoodsId 秒杀商品ID
     * @param quantity 购买数量
     * @return 购买资格信息
     */
    @Override
    public Object checkEligibility(Long seckillGoodsId, Integer quantity) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            userId = 4L; // 模拟用户ID
        }

        // 获取秒杀商品信息
        SeckillGoods seckillGoods = seckillGoodsMapper.getById(seckillGoodsId);
        if (seckillGoods == null) {
            throw new OrderBusinessException("秒杀商品不存在");
        }

        // 检查用户已购买数量
        SeckillUserRecord userRecord = seckillUserRecordMapper.getByUserIdAndSeckillGoodsId(userId, seckillGoodsId);
        int userPurchased = userRecord != null ? userRecord.getQuantity() : 0;
        
        // 计算剩余可购买数量
        int remainingQuota = seckillGoods.getLimitCount() - userPurchased;
        boolean canPurchase = remainingQuota >= quantity && seckillGoods.getAvailableStock() >= quantity;

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("canPurchase", canPurchase);
        result.put("remainingQuota", Math.max(0, remainingQuota));
        result.put("limitCount", seckillGoods.getLimitCount());
        result.put("userPurchased", userPurchased);
        result.put("availableStock", seckillGoods.getAvailableStock());

        return result;
    }
}