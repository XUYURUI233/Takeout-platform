<!--秒杀订单详情-->
<template>
  <view class="seckill_order_detail">
    <uni-nav-bar @clickLeft="goBack" left-icon="back" leftIcon="arrowleft" title="秒杀订单详情" statusBar="true"
      fixed="true" color="#ffffff" backgroundColor="#ff4d4f"></uni-nav-bar>
    
    <view class="content">
      <!-- 订单状态 -->
      <view class="status_section">
        <view class="status_icon">
          <image :src="getStatusIcon(orderDetail.status)" mode="aspectFit"></image>
        </view>
        <view class="status_text">
          <text class="status_title">{{ statusWord(orderDetail.status) }}</text>
          <text class="status_desc" v-if="orderDetail.status === 1">请在{{ getOvertime(orderDetail.orderTime) }}分钟内完成支付</text>
          <text class="status_desc" v-else-if="orderDetail.status === 2">商家正在处理您的订单</text>
          <text class="status_desc" v-else-if="orderDetail.status === 3">商家已接单，正在准备中</text>
          <text class="status_desc" v-else-if="orderDetail.status === 4">订单正在配送中，请耐心等待</text>
          <text class="status_desc" v-else-if="orderDetail.status === 5">订单已完成，感谢您的购买</text>
          <text class="status_desc" v-else-if="orderDetail.status === 6">订单已取消</text>
        </view>
      </view>

      <!-- 秒杀商品信息 -->
      <view class="goods_section">
        <view class="section_title">商品信息</view>
        <view class="goods_item">
          <view class="goods_image">
            <image :src="orderDetail.seckillInfo.goodsImage" mode="aspectFill"></image>
          </view>
          <view class="goods_detail">
            <view class="activity_tag">{{ orderDetail.seckillInfo.activityName }}</view>
            <view class="goods_name">{{ orderDetail.seckillInfo.goodsName }}</view>
            <view class="price_info">
              <text class="seckill_price">￥{{ orderDetail.seckillInfo.seckillPrice }}</text>
              <text class="original_price">￥{{ orderDetail.seckillInfo.originalPrice }}</text>
            </view>
          </view>
          <view class="quantity">
            <text>x{{ orderDetail.seckillInfo.quantity }}</text>
          </view>
        </view>
      </view>

      <!-- 配送信息 -->
      <view class="delivery_section" v-if="orderDetail.address">
        <view class="section_title">配送信息</view>
        <view class="delivery_info">
          <view class="address_info">
            <view class="consignee">
              <text class="name">{{ orderDetail.consignee }}</text>
              <text class="phone">{{ orderDetail.phone }}</text>
            </view>
            <view class="address">{{ orderDetail.address }}</view>
          </view>
          <view class="delivery_time" v-if="orderDetail.estimatedDeliveryTime">
            <text class="label">预计送达：</text>
            <text class="time">{{ orderDetail.estimatedDeliveryTime }}</text>
          </view>
        </view>
      </view>

      <!-- 订单信息 -->
      <view class="order_section">
        <view class="section_title">订单信息</view>
        <view class="order_info">
          <view class="info_item">
            <text class="label">订单号：</text>
            <text class="value">{{ orderDetail.number }}</text>
          </view>
          <view class="info_item">
            <text class="label">下单时间：</text>
            <text class="value">{{ orderDetail.orderTime }}</text>
          </view>
          <view class="info_item" v-if="orderDetail.checkoutTime">
            <text class="label">支付时间：</text>
            <text class="value">{{ orderDetail.checkoutTime }}</text>
          </view>
          <view class="info_item">
            <text class="label">支付方式：</text>
            <text class="value">{{ getPayMethodText(orderDetail.payMethod) }}</text>
          </view>
          <view class="info_item" v-if="orderDetail.remark">
            <text class="label">备注：</text>
            <text class="value">{{ orderDetail.remark }}</text>
          </view>
        </view>
      </view>

      <!-- 费用明细 -->
      <view class="cost_section">
        <view class="section_title">费用明细</view>
        <view class="cost_info">
          <view class="cost_item">
            <text class="label">商品总价</text>
            <text class="value">￥{{ orderDetail.amount ? orderDetail.amount.toFixed(2) : '0.00' }}</text>
          </view>
          <view class="cost_item total">
            <text class="label">实付金额</text>
            <text class="value">￥{{ orderDetail.amount ? orderDetail.amount.toFixed(2) : '0.00' }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom_actions" v-if="orderDetail.status === 1">
      <button class="action_btn cancel_btn" @click="cancelOrder">取消订单</button>
      <button class="action_btn pay_btn" @click="payOrder">立即支付</button>
    </view>
  </view>
</template>

<script>
import {
  getSeckillOrderDetail,
  cancelSeckillOrder,
  paySeckillOrder
} from "../api/api.js";
import { statusWord, getOvertime } from "@/utils/index.js";

export default {
  data() {
    return {
      orderId: "",
      orderDetail: {
        seckillInfo: {}
      },
    };
  },
  onLoad(options) {
    if (options.orderId) {
      this.orderId = options.orderId;
      this.getOrderDetail();
    }
  },
  methods: {
    statusWord(status) {
      return statusWord(status);
    },
    getOvertime(time) {
      return getOvertime(time);
    },
    // 获取订单详情
    getOrderDetail() {
      uni.showLoading({ title: "加载中", mask: true });
      getSeckillOrderDetail(this.orderId).then((res) => {
        uni.hideLoading();
        if (res.code === 1) {
          this.orderDetail = res.data;
        } else {
          uni.showToast({
            title: res.msg || "获取订单详情失败",
            icon: "none"
          });
        }
      }).catch((error) => {
        uni.hideLoading();
        console.error("获取秒杀订单详情失败:", error);
        uni.showToast({
          title: "获取订单详情失败",
          icon: "none"
        });
      });
    },
    // 获取状态图标
    getStatusIcon(status) {
      const iconMap = {
        1: "/static/order_unpaid.png",    // 待付款
        2: "/static/order_paid.png",      // 待接单
        3: "/static/order_accepted.png",  // 已接单
        4: "/static/order_delivery.png",  // 派送中
        5: "/static/order_completed.png", // 已完成
        6: "/static/order_cancelled.png"  // 已取消
      };
      return iconMap[status] || "/static/order_default.png";
    },
    // 获取支付方式文本
    getPayMethodText(payMethod) {
      const methodMap = {
        1: "微信支付",
        2: "支付宝支付"
      };
      return methodMap[payMethod] || "未知";
    },
    // 取消订单
    cancelOrder() {
      uni.showModal({
        title: "提示",
        content: "确定要取消这个订单吗？",
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({ title: "取消中", mask: true });
            cancelSeckillOrder(this.orderId).then((res) => {
              uni.hideLoading();
              if (res.code === 1) {
                uni.showToast({
                  title: "订单已取消",
                  icon: "success"
                });
                setTimeout(() => {
                  this.getOrderDetail(); // 刷新订单详情
                }, 1500);
              } else {
                uni.showToast({
                  title: res.msg || "取消失败",
                  icon: "none"
                });
              }
            }).catch((error) => {
              uni.hideLoading();
              console.error("取消秒杀订单失败:", error);
              uni.showToast({
                title: "取消失败，请重试",
                icon: "none"
              });
            });
          }
        }
      });
    },
    // 支付订单
    payOrder() {
      uni.showLoading({ title: "支付中", mask: true });
      const params = {
        orderNumber: this.orderDetail.number,
        payMethod: 1 // 默认微信支付
      };
      paySeckillOrder(params).then((res) => {
        uni.hideLoading();
        if (res.code === 1) {
          uni.showToast({
            title: "支付成功",
            icon: "success"
          });
          setTimeout(() => {
            this.getOrderDetail(); // 刷新订单详情
          }, 1500);
        } else {
          uni.showToast({
            title: res.msg || "支付失败",
            icon: "none"
          });
        }
      }).catch((error) => {
        uni.hideLoading();
        console.error("秒杀订单支付失败:", error);
        uni.showToast({
          title: "支付失败，请重试",
          icon: "none"
        });
      });
    },
    // 返回上一页
    goBack() {
      uni.navigateBack();
    },
  },
};
</script>

<style lang="scss" scoped>
.seckill_order_detail {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;

  .content {
    padding: 20rpx;
  }

  // 订单状态区域
  .status_section {
    background: linear-gradient(135deg, #ff6b6b, #ff4d4f);
    border-radius: 12rpx;
    padding: 40rpx;
    margin-bottom: 20rpx;
    display: flex;
    align-items: center;
    color: white;

    .status_icon {
      width: 80rpx;
      height: 80rpx;
      margin-right: 24rpx;

      image {
        width: 100%;
        height: 100%;
      }
    }

    .status_text {
      flex: 1;

      .status_title {
        font-size: 32rpx;
        font-weight: 600;
        display: block;
        margin-bottom: 8rpx;
      }

      .status_desc {
        font-size: 24rpx;
        opacity: 0.9;
      }
    }
  }

  // 通用区域样式
  .goods_section,
  .delivery_section,
  .order_section,
  .cost_section {
    background: #fff;
    border-radius: 12rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;

    .section_title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
      margin-bottom: 24rpx;
      padding-bottom: 16rpx;
      border-bottom: 1px solid #f5f5f5;
    }
  }

  // 商品信息区域
  .goods_section {
    .goods_item {
      display: flex;
      align-items: center;

      .goods_image {
        width: 120rpx;
        height: 120rpx;
        border-radius: 8rpx;
        overflow: hidden;
        margin-right: 20rpx;

        image {
          width: 100%;
          height: 100%;
        }
      }

      .goods_detail {
        flex: 1;

        .activity_tag {
          font-size: 20rpx;
          color: #ff4d4f;
          background: #fff2f0;
          padding: 4rpx 8rpx;
          border-radius: 4rpx;
          display: inline-block;
          margin-bottom: 8rpx;
        }

        .goods_name {
          font-size: 28rpx;
          color: #333;
          font-weight: 500;
          margin-bottom: 12rpx;
        }

        .price_info {
          .seckill_price {
            font-size: 28rpx;
            color: #ff4d4f;
            font-weight: 600;
            margin-right: 16rpx;
          }

          .original_price {
            font-size: 24rpx;
            color: #999;
            text-decoration: line-through;
          }
        }
      }

      .quantity {
        font-size: 24rpx;
        color: #666;
      }
    }
  }

  // 配送信息区域
  .delivery_section {
    .delivery_info {
      .address_info {
        margin-bottom: 16rpx;

        .consignee {
          margin-bottom: 8rpx;

          .name {
            font-size: 28rpx;
            color: #333;
            font-weight: 500;
            margin-right: 20rpx;
          }

          .phone {
            font-size: 24rpx;
            color: #666;
          }
        }

        .address {
          font-size: 26rpx;
          color: #666;
          line-height: 1.4;
        }
      }

      .delivery_time {
        font-size: 24rpx;
        color: #666;

        .label {
          margin-right: 8rpx;
        }

        .time {
          color: #ff4d4f;
        }
      }
    }
  }

  // 订单信息区域
  .order_section {
    .order_info {
      .info_item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12rpx 0;
        font-size: 26rpx;

        .label {
          color: #666;
        }

        .value {
          color: #333;
          text-align: right;
          flex: 1;
          margin-left: 20rpx;
        }
      }
    }
  }

  // 费用明细区域
  .cost_section {
    .cost_info {
      .cost_item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12rpx 0;
        font-size: 26rpx;

        .label {
          color: #666;
        }

        .value {
          color: #333;
        }

        &.total {
          border-top: 1px solid #f5f5f5;
          margin-top: 12rpx;
          padding-top: 24rpx;
          font-size: 32rpx;
          font-weight: 600;

          .value {
            color: #ff4d4f;
          }
        }
      }
    }
  }

  // 底部操作栏
  .bottom_actions {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: #fff;
    padding: 20rpx;
    border-top: 1px solid #f5f5f5;
    display: flex;
    gap: 20rpx;

    .action_btn {
      flex: 1;
      height: 80rpx;
      line-height: 80rpx;
      border-radius: 40rpx;
      font-size: 28rpx;
      border: none;

      &.cancel_btn {
        background: #f5f5f5;
        color: #666;
      }

      &.pay_btn {
        background: #ff4d4f;
        color: #fff;
      }
    }
  }
}
</style>






