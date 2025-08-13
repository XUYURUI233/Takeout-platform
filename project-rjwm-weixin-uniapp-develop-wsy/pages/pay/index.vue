<!--购买页-->
<template>
  <view class="customer-box">
    <view class="wrap">
      <view class="contion">
        <view class="orderPay">
          <view>
            <view v-if="timeout">订单已超时</view>
            <view v-else
              >支付剩余时间<text>{{ rocallTime }}</text></view
            >
          </view>
          <view class="money"
            >￥<text>{{ orderDataInfo.orderAmount }}</text></view
          >
          <view>{{ shopInfo().shopName }}-{{ orderDataInfo.orderNumber }}</view>
        </view>
      </view>
      <view class="box payBox">
        <view class="contion">
          <view class="example-body">
            <radio-group class="uni-list" @change="styleChange">
              <view class="uni-list-item">
                <view
                  class="uni-list-item__container"
                  v-for="(item, index) in payMethodList"
                  :key="item"
                >
                  <view class="uni-list-item__content">
                    <icon class="wechatIcon"></icon
                    ><text class="uni-list-item__content-title">{{
                      item
                    }}</text>
                  </view>
                  <view class="uni-list-item__extra">
                    <radio
                      :value="item"
                      color="#FFC200"
                      :checked="index == activeRadio"
                      class="radioIcon"
                    />
                  </view>
                </view>
              </view>
            </radio-group>
          </view>
        </view>
      </view>
      <view class="bottomBox btnBox">
        <button class="add_btn" type="primary" plain="true" @click="handleSave">
          确认支付
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { mapState } from "vuex";
import { paymentOrder, cancelOrder } from "@/pages/api/api.js";
export default {
  data() {
    return {
      timeout: false,
      rocallTime: "",
      orderId: null,
      orderDataInfo: {},
      activeRadio: 0,
      payMethodList: ["微信支付"],
      times: null,
    };
  },
  created() {
    this.orderDataInfo = this.orderData();
  },
  mounted() {
    this.runTimeBack();
  },
  onLoad(options) {
    this.orderId = options.orderId;
  },
  methods: {
    ...mapState(["orderData", "shopInfo"]),
    // 支付详情
    handleSave() {
      if (this.timeout) {
        cancelOrder(this.orderId).then((res) => {});
        uni.redirectTo({
          url: "/pages/details/index?orderId=" + this.orderId,
        });
      } else {
        // 如果支付成功进入成功页
        clearTimeout(this.times);
        const params = {
          orderNumber: this.orderDataInfo.orderNumber,
          payMethod: this.activeRadio === 0 ? 1 : 2,
        };
        console.log("支付参数：", params);
        console.log("订单信息：", this.orderDataInfo);
        paymentOrder(params).then(async (res) => {
          console.log("支付接口返回：", res);
          if (res.code === 1) {
            // 实验环境：直接显示支付成功，跳过微信支付验证
            await uni.showToast({ title: "支付成功", icon: "success" });
            setTimeout(() => {
              // 支付成功，跳转到成功页面
              console.log("跳转到支付成功页面，订单ID：", this.orderId);
              uni.redirectTo({
                url: "/pages/success/index?orderId=" + this.orderId,
              });
            }, 1500);
          } else {
            console.error("支付失败：", res.msg);
            uni.showToast({
              title: res.msg || "支付失败",
              duration: 1000,
              icon: "none",
            });
          }
        }).catch(error => {
          console.error("支付请求失败:", error);
          // 实验环境：即使请求失败也显示支付成功
          uni.showToast({ title: "支付成功", icon: "success" });
          setTimeout(() => {
            uni.redirectTo({
              url: "/pages/success/index?orderId=" + this.orderId,
            });
          }, 1500);
        });
      }
    },
    // // 订单倒计时
    runTimeBack() {
      const end = Date.parse(this.orderDataInfo.orderTime.replace(/-/g, "/"));
      const now = Date.parse(new Date());
      const m15 = 15 * 60 * 1000;
      const msec = m15 - (now - end);
      if (msec < 0) {
        this.timeout = true;
        clearTimeout(this.times);
      } else {
        let min = parseInt((msec / 1000 / 60) % 60);
        let sec = parseInt((msec / 1000) % 60);
        if (min < 10) {
          min = "0" + min;
        } else {
          min = min;
        }
        if (sec < 10) {
          sec = "0" + sec;
        } else {
          sec = sec;
        }
        this.rocallTime = min + ":" + sec;
        let that = this;
        if (min >= 0 && sec >= 0) {
          if (min === 0 && sec === 0) {
            this.timeout = true;
            clearTimeout(this.times);
            return;
          }
          this.times = setTimeout(function () {
            that.runTimeBack();
          }, 1000);
        }
      }
    },
  },
};
</script>
<style src="./../common/Navbar/navbar.scss" lang="scss" scoped></style>
<style src="./../order/style.scss" lang="scss"></style>
<style>
</style>
