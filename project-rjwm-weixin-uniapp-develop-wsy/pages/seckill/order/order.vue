<template>
  <view class="seckill-order-container">
    <!-- 导航栏 -->
    <navBar title="确认订单"></navBar>
    
    <view class="content" :style="{ paddingTop: ht + 'px' }">
      <!-- 收货地址 -->
      <view class="address-section" @tap="selectAddress">
        <view class="address-content" v-if="selectedAddress">
          <view class="address-info">
            <view class="address-row">
              <text class="consignee">{{ selectedAddress.consignee }}</text>
              <text class="phone">{{ selectedAddress.phone }}</text>
            </view>
            <text class="address-detail">
              {{ selectedAddress.provinceName }}{{ selectedAddress.cityName }}{{ selectedAddress.districtName }}{{ selectedAddress.detail }}
            </text>
          </view>
          <image src="/static/arrow-right.png" class="arrow-icon" />
        </view>
        <view class="no-address" v-else>
          <image src="/static/location.png" class="location-icon" />
          <text class="address-tip">请选择收货地址</text>
          <image src="/static/arrow-right.png" class="arrow-icon" />
        </view>
      </view>

      <!-- 商品信息 -->
      <view class="goods-section">
        <view class="section-header">
          <text class="section-title">秒杀商品</text>
          <view class="seckill-tag">
            <text class="seckill-text">秒杀</text>
          </view>
        </view>
        <view class="goods-item">
          <image :src="goodsInfo.goodsImage" class="goods-image" mode="aspectFill" />
          <view class="goods-info">
            <text class="goods-name">{{ goodsInfo.goodsName }}</text>
            <view class="goods-price">
              <text class="seckill-price">￥{{ goodsInfo.seckillPrice }}</text>
              <text class="original-price">￥{{ goodsInfo.originalPrice }}</text>
            </view>
            <text class="goods-quantity">x{{ quantity }}</text>
          </view>
        </view>
      </view>

      <!-- 支付倒计时 -->
      <view class="countdown-section" v-if="payCountdown">
        <view class="countdown-header">
          <image src="/static/clock.png" class="clock-icon" />
          <text class="countdown-title">请在以下时间内完成支付</text>
        </view>
        <view class="countdown-time">
          <view class="time-block">
            <text class="time-num">{{ formatTimeUnit(payCountdown.minutes) }}</text>
            <text class="time-unit">分</text>
          </view>
          <text class="time-separator">:</text>
          <view class="time-block">
            <text class="time-num">{{ formatTimeUnit(payCountdown.seconds) }}</text>
            <text class="time-unit">秒</text>
          </view>
        </view>
        <text class="countdown-tip">超时将自动取消订单</text>
      </view>

      <!-- 订单信息 -->
      <view class="order-info-section">
        <view class="info-row">
          <text class="info-label">商品总价</text>
          <text class="info-value">￥{{ (goodsInfo.seckillPrice * quantity).toFixed(2) }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">配送费</text>
          <text class="info-value">￥{{ deliveryFee.toFixed(2) }}</text>
        </view>
        <view class="info-row total">
          <text class="info-label">实付款</text>
          <text class="info-value total-price">￥{{ totalAmount.toFixed(2) }}</text>
        </view>
      </view>

      <!-- 备注 -->
      <view class="remark-section">
        <text class="remark-label">订单备注</text>
        <textarea 
          class="remark-input" 
          placeholder="选填，可以告诉商家您的特殊要求"
          v-model="remark"
          maxlength="50"
        />
        <text class="remark-count">{{ remark.length }}/50</text>
      </view>

      <!-- 底部操作按钮 -->
      <view class="bottom-actions">
        <view class="action-buttons">
          <view class="cancel-btn" @tap="cancelOrder">取消订单</view>
          <view class="pay-btn" @tap="submitOrder">立即支付</view>
        </view>
      </view>

      <!-- 加载遮罩 -->
      <view class="loading-mask" v-if="submitting">
        <view class="loading-content">
          <image src="/static/loading.gif" class="loading-icon" />
          <text class="loading-text">{{ loadingText }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import navBar from "../../common/Navbar/navbar.vue"
import { 
  getSeckillGoodsDetail,
  submitSeckillOrder,
  paySeckillOrder,
  getAddressBookDefault
} from "../../api/api.js"
import { mapState } from "vuex"

export default {
  name: 'SeckillOrder',
  components: {
    navBar
  },
  data() {
    return {
      goodsId: null,
      quantity: 1,
      goodsInfo: {},
      selectedAddress: null,
      remark: '',
      deliveryFee: 6,
      payCountdown: null,
      countdownTimer: null,
      submitting: false,
      loadingText: '提交订单中...',
      ht: 0 // 导航栏高度
    }
  },
  computed: {
    ...mapState(['token', 'userInfo']),
    
    // 总金额
    totalAmount() {
      return (this.goodsInfo.seckillPrice || 0) * this.quantity + this.deliveryFee
    }
  },
  onLoad(options) {
    this.goodsId = options.goodsId
    this.quantity = parseInt(options.quantity) || 1
    
    // 处理从地址页面返回的地址信息
    if (options.address) {
      try {
        this.selectedAddress = JSON.parse(options.address)
        console.log('接收到选择的地址:', this.selectedAddress)
      } catch (error) {
        console.error('解析地址参数失败:', error)
        this.loadDefaultAddress()
      }
    } else {
      this.loadDefaultAddress()
    }
    
    this.loadGoodsDetail()
    this.getNavBarHeight()
  },
  onReady() {
    this.startPayCountdown()
  },
  onUnload() {
    this.clearCountdown()
  },
  methods: {
    // 获取导航栏高度
    getNavBarHeight() {
      const systemInfo = uni.getSystemInfoSync()
      // 导航栏背景图高度是304rpx，转换为px约152px，加上状态栏高度
      this.ht = systemInfo.statusBarHeight + 152 // 304rpx ≈ 152px
    },

    // 加载商品详情
    async loadGoodsDetail() {
      try {
        console.log('订单页面加载商品详情，商品ID:', this.goodsId)
        const res = await getSeckillGoodsDetail(this.goodsId)
        console.log('订单页面商品详情API响应:', res)
        
        if (res.code === 1 && res.data) {
          this.goodsInfo = res.data
          console.log('订单页面使用后端商品数据:', this.goodsInfo)
        } else {
          console.log('订单页面后端商品数据获取失败，使用模拟数据')
          this.loadMockGoodsData()
        }
      } catch (error) {
        console.error('订单页面加载商品详情失败:', error)
        console.log('订单页面API调用失败，使用模拟数据')
        this.loadMockGoodsData()
      }
    },

    // 加载模拟商品数据
    loadMockGoodsData() {
      this.goodsInfo = {
        id: this.goodsId,
        goodsName: '王老吉',
        goodsImage: 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png',
        originalPrice: 6.00,
        seckillPrice: 3.00
      }
    },

    // 加载默认地址
    async loadDefaultAddress() {
      try {
        console.log('加载默认地址...')
        const res = await getAddressBookDefault()
        console.log('默认地址API响应:', res)
        
        if (res.code === 1 && res.data) {
          this.selectedAddress = res.data
          console.log('使用后端地址数据:', this.selectedAddress)
        } else {
          console.log('后端地址数据获取失败，使用模拟地址')
          this.loadMockAddress()
        }
      } catch (error) {
        console.error('加载默认地址失败:', error)
        console.log('地址API调用失败，使用模拟地址')
        this.loadMockAddress()
      }
    },

    // 加载模拟地址
    loadMockAddress() {
      this.selectedAddress = {
        id: 1,
        consignee: '徐徐',
        phone: '18026714983',
        provinceName: '江苏省',
        cityName: '盐城市',
        districtName: '滨海县',
        detail: '八栋7b503房间'
      }
    },

    // 选择收货地址
    selectAddress() {
      // 设置返回地址
      this.$store.commit('setAddressBackUrl', '/pages/seckill/order/order')
      uni.navigateTo({
        url: '/pages/address/address?from=seckill'
      })
    },

    // 开始支付倒计时（15分钟）
    startPayCountdown() {
      let totalSeconds = 15 * 60 // 15分钟
      
      const updateCountdown = () => {
        if (totalSeconds <= 0) {
          this.clearCountdown()
          this.autoCancel()
          return
        }
        
        const minutes = Math.floor(totalSeconds / 60)
        const seconds = totalSeconds % 60
        
        this.payCountdown = { minutes, seconds }
        totalSeconds--
      }
      
      updateCountdown()
      this.countdownTimer = setInterval(updateCountdown, 1000)
    },

    // 清除倒计时
    clearCountdown() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
        this.countdownTimer = null
      }
    },

    // 自动取消订单
    autoCancel() {
      uni.showModal({
        title: '提示',
        content: '支付超时，订单已自动取消',
        showCancel: false,
        success: () => {
          uni.navigateBack()
        }
      })
    },

    // 取消订单
    cancelOrder() {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消此订单吗？',
        success: (res) => {
          if (res.confirm) {
            uni.navigateBack()
          }
        }
      })
    },

    // 提交订单
    async submitOrder() {
      // 验证地址
      if (!this.selectedAddress) {
        uni.showToast({
          title: '请选择收货地址',
          icon: 'none'
        })
        return
      }

      try {
        this.submitting = true
        this.loadingText = '提交订单中...'

        // 提交秒杀订单
        const orderData = {
          seckillGoodsId: this.goodsId,
          quantity: this.quantity,
          addressBookId: this.selectedAddress.id,
          remark: this.remark
        }

        const submitRes = await submitSeckillOrder(orderData)
        
        if (submitRes.code !== 1) {
          throw new Error(submitRes.msg || '订单提交失败')
        }

        const orderInfo = submitRes.data
        this.loadingText = '支付中...'

        // 调用支付接口
        const payRes = await paySeckillOrder({
          orderNumber: orderInfo.orderNumber,
          payMethod: 1 // 微信支付
        })

        if (payRes.code === 1) {
          this.submitting = false
          this.clearCountdown()
          
          // 支付成功，跳转到成功页面
          uni.redirectTo({
            url: `/pages/success/index?orderId=${orderInfo.orderId}&orderNumber=${orderInfo.orderNumber}&type=seckill`
          })
        } else {
          throw new Error(payRes.msg || '支付失败')
        }

      } catch (error) {
        console.error('提交订单失败:', error)
        this.submitting = false
        uni.showToast({
          title: error.message || '操作失败，请重试',
          icon: 'none'
        })
      }
    },

    // 格式化时间单位
    formatTimeUnit(time) {
      return time < 10 ? `0${time}` : `${time}`
    }
  }
}
</script>

<style lang="scss" scoped>
.seckill-order-container {
  width: 100%;
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.content {
  width: 100%;
}

.address-section {
  background: white;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.address-content, .no-address {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.address-info {
  flex: 1;
}

.address-row {
  display: flex;
  align-items: center;
  gap: 30rpx;
  margin-bottom: 10rpx;
}

.consignee {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.phone {
  font-size: 28rpx;
  color: #666;
}

.address-detail {
  font-size: 28rpx;
  color: #666;
  line-height: 1.4;
}

.no-address {
  color: #999;
}

.location-icon {
  width: 40rpx;
  height: 40rpx;
  margin-right: 20rpx;
}

.address-tip {
  flex: 1;
  font-size: 28rpx;
}

.arrow-icon {
  width: 24rpx;
  height: 24rpx;
}

.goods-section {
  background: white;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.seckill-tag {
  background: #ff6b35;
  color: white;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.goods-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.goods-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.goods-price {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.seckill-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #ff6b35;
}

.original-price {
  font-size: 24rpx;
  color: #999;
  text-decoration: line-through;
}

.goods-quantity {
  font-size: 28rpx;
  color: #666;
}

.countdown-section {
  background: linear-gradient(135deg, #ff6b35, #ff8a50);
  margin-bottom: 20rpx;
  padding: 30rpx;
  color: white;
  text-align: center;
}

.countdown-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;
}

.clock-icon {
  width: 32rpx;
  height: 32rpx;
  margin-right: 10rpx;
}

.countdown-title {
  font-size: 28rpx;
}

.countdown-time {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10rpx;
}

.time-block {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8rpx;
  padding: 12rpx 16rpx;
  margin: 0 5rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 70rpx;
}

.time-num {
  font-size: 36rpx;
  font-weight: bold;
  line-height: 1;
}

.time-unit {
  font-size: 20rpx;
  opacity: 0.8;
  line-height: 1;
  margin-top: 4rpx;
}

.time-separator {
  font-size: 36rpx;
  font-weight: bold;
  margin: 0 10rpx;
}

.countdown-tip {
  font-size: 24rpx;
  opacity: 0.8;
}

.order-info-section {
  background: white;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  &.total {
    padding-top: 20rpx;
    border-top: 2rpx solid #f0f0f0;
  }
}

.info-label {
  font-size: 28rpx;
  color: #666;
}

.info-value {
  font-size: 28rpx;
  color: #333;
}

.total-price {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff6b35;
}

.remark-section {
  background: white;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.remark-label {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}

.remark-input {
  width: 100%;
  min-height: 120rpx;
  background: #f9f9f9;
  border-radius: 12rpx;
  padding: 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
  color: #333;
  border: none;
  outline: none;
}

.remark-count {
  display: block;
  text-align: right;
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: white;
  padding: 20rpx 30rpx;
  box-sizing: border-box;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.action-buttons {
  display: flex;
  gap: 20rpx;
}

.cancel-btn, .pay-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: bold;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
  border: 2rpx solid #ddd;
}

.pay-btn {
  background: #ff6b35;
  color: white;
}

.loading-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: white;
  padding: 60rpx;
  border-radius: 20rpx;
}

.loading-icon {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 20rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #666;
}
</style>
