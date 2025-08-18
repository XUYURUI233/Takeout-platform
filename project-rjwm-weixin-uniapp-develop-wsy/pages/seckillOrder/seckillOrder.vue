<!--秒杀订单-->
<template>
  <view class="seckill_order">
    <uni-nav-bar @clickLeft="goBack" left-icon="back" leftIcon="arrowleft" title="秒杀订单" statusBar="true" fixed="true"
      color="#ffffff" backgroundColor="#ff4d4f"></uni-nav-bar>
    <!-- 根据scrollinto和:id="'tab'+index"切换下方轮播 -->
    <scroll-view scroll-x class="scroll-row" :scroll-into-view="scrollinto" :scroll-with-animation="true" enable-flex>
      <view v-for="(item, index) in tabBars" :key="index" :id="'tab' + index" class="scroll-row-item"
        @click="changeTab(index)">
        <view :class="tabIndex == index ? 'scroll-row-item-act' : ''"><text class="line"></text>{{ item }}</view>
      </view>
    </scroll-view>
    <!--  滑块内容 对应的是顶部选项卡的切换 :current="tabIndex"  设置的是y方向上可以滚动-->
    <swiper :current="tabIndex" @change="onChangeSwiperTab" :style="{ height: scrollH + 'px' }">
      <swiper-item v-for="(item, index) in tabBars" :key="index">
        <!-- 垂直滚动区域  scroll和swiper的高度都要给且是一样的高度-->
        <scroll-view scroll-y="true" :style="{ height: scrollH + 'px' }" @scrolltolower="lower">
          <!-- 可垂直滚动区域 显示真正内容-->
          <view class="main recent_orders" v-if="seckillOrdersList && seckillOrdersList.length > 0">
            <!-- 秒杀订单列表 -->
            <view class="box order_lists" v-for="(item, index) in seckillOrdersList" :key="index" :class="{
              'item-last': Number(index) + 1 === seckillOrdersList.length,
            }">
              <!-- 时间和支付状态 -->
              <view class="date_type">
                <!-- 时间 -->
                <text class="time">{{ item.orderTime }}</text>
                <!-- 支付状态 -->
                <text class="type status" :class="{ status: item.status == 2 }">{{
                  statusWord(item.status)
                }}</text>
              </view>
              <!-- 秒杀商品内容 -->
              <view class="orderBox" @click="goDetail(item.id)">
                <view class="seckill_goods_info">
                  <view class="goods_image">
                    <image :src="item.seckillInfo.goodsImage" mode="aspectFill"></image>
                  </view>
                  <view class="goods_detail">
                    <view class="activity_name">{{ item.seckillInfo.activityName }}</view>
                    <view class="goods_name">{{ item.seckillInfo.goodsName }}</view>
                    <view class="price_info">
                      <text class="seckill_price">￥{{ item.seckillInfo.seckillPrice }}</text>
                      <text class="original_price">￥{{ item.seckillInfo.originalPrice }}</text>
                      <text class="quantity">x{{ item.seckillInfo.quantity }}</text>
                    </view>
                  </view>
                </view>
                <!-- 订单金额 -->
                <view class="order_amount">
                  <text>￥{{ item.amount.toFixed(2) }}</text>
                </view>
              </view>
              <view class="againBtn">
                <button class="new_btn" type="default" @click="oneMoreOrder(item.id)">
                  再来一单
                </button>
                <button class="new_btn btn" type="default" @click="goDetail(item.id)"
                  v-if="item.status === 1 && getOvertime(item.orderTime) > 0">
                  去支付
                </button>
                <button class="new_btn btn" type="default" @click="handleReminder('center', item.id)"
                  v-if="item.status === 2">
                  催单
                </button>
              </view>
            </view>
          </view>
          <!-- 空状态 -->
          <empty v-if="isEmpty && (!seckillOrdersList || seckillOrdersList.length === 0)"></empty>
        </scroll-view>
      </swiper-item>
    </swiper>
    <uni-popup ref="commonPopup" class="comPopupBox">
      <view class="popup-content">
        <view class="text">{{ textTip }}</view>
        <view class="btn" v-if="showConfirm">
          <view @click="closePopup">确认</view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import {
  getSeckillOrderList,
  getSeckillOrderDetail,
  repeatSeckillOrder,
  cancelSeckillOrder,
  delShoppingCart,
} from "../api/api.js";
import { mapMutations } from "vuex";
import Empty from "@/components/empty/empty";
import { statusWord, getOvertime } from "@/utils/index.js";
export default {
  components: {
    Empty,
  },
  data() {
    return {
      seckillOrdersList: [],
      pageInfo: {
        page: 1,
        pageSize: 10,
        total: 0,
      },
      status: "",
      loadingType: 0,
      showTitle: false,
      scrollinto: "tab0",
      scrollH: 0,
      tabIndex: 0,
      tabBars: ["全部订单", "待付款", "已完成"],
      textTip: "",
      showConfirm: false,
      isEmpty: false,
    };
  },
  onLoad() {
    this.getList();
  },
  onUnload() {
    this.showTitle = false;
  },
  onReady() {
    uni.getSystemInfo({
      success: (res) => {
        this.scrollH = res.windowHeight - uni.upx2px(100);
      },
    });
  },
  onPullDownRefresh() {
    this.pageInfo.page = 1;
    this.loadingType = 0;
    this.seckillOrdersList = [];
    this.finished = false;
    this.getList();
    uni.stopPullDownRefresh();
    this.showTitle = true;
  },
  onReachBottom() {
    if (this.seckillOrdersList.length < Number(this.pageInfo.total)) {
      this.pageInfo.page++;
      this.loadingStatus = "loading";
      this.getList(this.status);
      this.showTitle = true;
    }
  },
  methods: {
    ...mapMutations(["setAddressBackUrl"]),
    statusWord(status) {
      return statusWord(status);
    },
    getOvertime(time) {
      return getOvertime(time);
    },
    // 获取秒杀订单列表
    getList() {
      const params = {
        pageSize: 10,
        page: this.pageInfo.page,
        status: this.status,
      };
      uni.showLoading({ title: "加载中", mask: true });
      getSeckillOrderList(params).then((res) => {
        if (res.code === 1) {
          setTimeout(function () {
            uni.hideLoading();
          }, 100);
          this.seckillOrdersList = this.seckillOrdersList.concat(
            res.data.records
          );
          this.pageInfo.total = res.data.total;
          this.isEmpty = true;
        }
      }).catch((error) => {
        console.error("获取秒杀订单列表失败:", error);
        uni.hideLoading();
        this.isEmpty = true;
      });
    },
    // 再来一单
    async oneMoreOrder(id) {
      let pages = getCurrentPages();
      let routeIndex = pages.findIndex(
        (item) => item.route === "pages/index/index"
      );
      try {
        // 先清空购物车
        await delShoppingCart();
        const res = await repeatSeckillOrder(id);
        if (res.code === 1) {
          uni.showToast({
            title: "已加入购物车",
            icon: "success"
          });
          uni.navigateBack({
            delta: routeIndex > -1 ? pages.length - routeIndex : 1,
          });
        }
      } catch (error) {
        console.error("再来一单失败:", error);
        uni.showToast({
          title: "操作失败，请重试",
          icon: "none"
        });
      }
    },
    // tab选项卡切换轮播
    changeTab(index) {
      // 点击的还是当前数据的时候直接return
      if (this.tabIndex == index) {
        return;
      }
      this.tabIndex = index;
      if (index === 1) {
        // 待付款
        this.status = 1;
      } else if (index === 2) {
        // 已完成
        this.status = 2;
      } else {
        // 全部
        this.status = "";
      }
      this.pageInfo.page = 1;
      this.seckillOrdersList = [];
      this.getList();
      // 滑动
      this.scrollinto = "tab" + index;
    },
    onChangeSwiperTab(e) {
      this.changeTab(e.detail.current);
    },
    dataAdd() {
      const pages = Math.ceil(this.pageInfo.total / 10); //计算总页数
      if (this.pageInfo.page === pages) {
        this.loadingText = "没有更多了";
        this.loading = true;
      } else {
        this.pageInfo.page++;
        this.getList();
      }
    },

    lower() {
      this.loadingText = "数据加载中...";
      this.loading = true;
      this.dataAdd();
    },
    // 去详情页面
    goDetail(id) {
      this.setAddressBackUrl("/pages/seckillOrder/seckillOrder");
      uni.navigateTo({ 
        url: "/pages/seckillOrderDetail/seckillOrderDetail?orderId=" + id 
      });
    },
    // 催单
    handleReminder(type, id) {
      // 秒杀订单暂不支持催单功能
      this.showConfirm = true;
      this.textTip = "秒杀订单暂不支持催单功能！";
      this.$refs.commonPopup.open(type);
    },
    // 关闭弹层
    closePopup(type) {
      this.$refs.commonPopup.close(type);
    },
    // 返回我的
    goBack() {
      uni.redirectTo({
        url: "/pages/my/my",
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.seckill_order {
  height: 100%;

  .recent_orders {
    padding-top: 8rpx;
  }
}

.scroll-row {
  height: 88rpx;
  line-height: 88rpx;
  background-color: #fff;
  padding: 0 30rpx;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.06);
  width: 100vw;
  box-sizing: border-box;
  flex-wrap: nowrap;
  overflow: auto;
  display: flex;
}

.scroll-row-item {
  margin-right: 88rpx;
  color: #666;
  display: inline-block;
  font-size: 28rpx;
  flex-shrink: 0;
}

.scroll-row-item-act {
  color: #ff4d4f;
  position: relative;
  font-weight: 600;

  .line {
    width: 32rpx;
    height: 8rpx;
    display: block;
    background: #ff4d4f;
    border-radius: 8rpx;
    transform: translate(-50%, -50%);
    position: absolute;
    bottom: -4rpx;
    left: 50%;
  }
}

// 秒杀订单特有样式
.box.order_lists {
  margin: 16rpx 20rpx;
  padding: 24rpx;
  background: #fff;
  border-radius: 12rpx;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.1);

  .date_type {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    padding-bottom: 16rpx;
    border-bottom: 1px solid #f5f5f5;

    .time {
      font-size: 24rpx;
      color: #999;
    }

    .type {
      font-size: 24rpx;
      color: #ff4d4f;
      font-weight: 500;
    }
  }

  .orderBox {
    margin-bottom: 20rpx;

    .seckill_goods_info {
      display: flex;
      align-items: center;
      margin-bottom: 16rpx;

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

        .activity_name {
          font-size: 24rpx;
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
          display: flex;
          align-items: center;

          .seckill_price {
            font-size: 32rpx;
            color: #ff4d4f;
            font-weight: 600;
            margin-right: 16rpx;
          }

          .original_price {
            font-size: 24rpx;
            color: #999;
            text-decoration: line-through;
            margin-right: 16rpx;
          }

          .quantity {
            font-size: 24rpx;
            color: #666;
          }
        }
      }
    }

    .order_amount {
      text-align: right;
      
      text {
        font-size: 32rpx;
        color: #ff4d4f;
        font-weight: 600;
      }
    }
  }

  .againBtn {
    display: flex;
    justify-content: flex-end;
    gap: 16rpx;

    .new_btn {
      width: 160rpx;
      height: 60rpx;
      line-height: 60rpx;
      font-size: 24rpx;
      border-radius: 30rpx;
      border: 1px solid #ddd;
      background: #fff;
      color: #666;

      &.btn {
        background: #ff4d4f;
        color: #fff;
        border-color: #ff4d4f;
      }
    }
  }
}
</style>






