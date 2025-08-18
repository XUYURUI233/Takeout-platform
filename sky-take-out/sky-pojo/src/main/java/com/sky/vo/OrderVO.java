package com.sky.vo;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Orders implements Serializable {

    //订单菜品信息
    private String orderDishes;

    //订单详情
    private List<OrderDetail> orderDetailList;

    //是否为秒杀订单
    private Boolean isSeckillOrder;

    //秒杀订单ID
    private Long seckillOrderId;

    //支付超时时间（秒杀订单特有）
    private LocalDateTime payExpireTime;

}
