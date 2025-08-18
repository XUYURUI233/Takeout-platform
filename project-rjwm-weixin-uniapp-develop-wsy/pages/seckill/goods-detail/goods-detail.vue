<template>
  <view class="goods-detail-container">
    <!-- 导航栏 -->
    <navBar :title="goodsInfo.goodsName || '秒杀商品'"></navBar>
    
    <view class="content" :style="{ paddingTop: ht + 'px' }">
      <!-- 商品信息 -->
      <view class="goods-info-section">
        <image 
          :src="goodsInfo.goodsImage || '/static/dish-default.png'" 
          class="goods-image"
          mode="aspectFill"
        />
        <view class="goods-details">
          <text class="goods-name">{{ goodsInfo.goodsName }}</text>
          <text class="goods-desc" v-if="goodsInfo.description">{{ goodsInfo.description }}</text>
          
          <!-- 价格信息 -->
          <view class="price-section">
            <view class="price-row">
              <text class="price-label">秒杀价：</text>
              <text class="seckill-price">￥{{ goodsInfo.seckillPrice }}</text>
              <text class="original-price">￥{{ goodsInfo.originalPrice }}</text>
            </view>
            <view class="discount-tag">
              <text class="discount-text">{{ getDiscountText() }}</text>
            </view>
          </view>

          <!-- 活动信息 -->
          <view class="activity-info-section">
            <view class="activity-row">
              <text class="activity-label">活动时间：</text>
              <text class="activity-time">{{ formatActivityTime() }}</text>
            </view>
            <view class="activity-row">
              <text class="activity-label">限购数量：</text>
              <text class="limit-text">每人限购{{ goodsInfo.limitCount }}件</text>
            </view>
            <view class="activity-row">
              <text class="activity-label">剩余库存：</text>
              <text class="stock-text">{{ goodsInfo.availableStock }}件</text>
            </view>
          </view>

          <!-- 倒计时 -->
          <view class="countdown-section" v-if="countdown">
            <text class="countdown-label">距离结束：</text>
            <view class="countdown-time">
              <view class="time-block">
                <text class="time-num">{{ formatTimeUnit(countdown.hours) }}</text>
                <text class="time-unit">时</text>
              </view>
              <text class="time-separator">:</text>
              <view class="time-block">
                <text class="time-num">{{ formatTimeUnit(countdown.minutes) }}</text>
                <text class="time-unit">分</text>
              </view>
              <text class="time-separator">:</text>
              <view class="time-block">
                <text class="time-num">{{ formatTimeUnit(countdown.seconds) }}</text>
                <text class="time-unit">秒</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 购买数量选择 -->
      <view class="quantity-section" v-if="canPurchase()">
        <view class="section-title">购买数量</view>
        <view class="quantity-selector">
          <view 
            class="quantity-btn" 
            :class="{ 'disabled': quantity <= 1 }"
            @tap="decreaseQuantity"
          >
            -
          </view>
          <input 
            class="quantity-input" 
            type="number" 
            v-model="quantity" 
            @blur="validateQuantity"
          />
          <view 
            class="quantity-btn" 
            :class="{ 'disabled': quantity >= maxQuantity }"
            @tap="increaseQuantity"
          >
            +
          </view>
        </view>
        <text class="quantity-tip">最多可购买{{ maxQuantity }}件</text>
      </view>

      <!-- 商品详情描述 -->
      <view class="description-section">
        <view class="section-title">商品详情</view>
        <view class="description-content">
          <text class="description-text">
            {{ goodsInfo.description || '暂无详细描述' }}
          </text>
        </view>
      </view>

      <!-- 操作按钮区域 -->
      <view class="action-buttons-section">
        <!-- 个人中心按钮 -->
        <view class="personal-center-button" @tap="goToPersonalCenter">
          <image class="button-icon" src="/static/center.png" mode="aspectFit"></image>
          <text class="button-text">个人中心</text>
        </view>
        
        <!-- 返回按钮 -->
        <view class="back-button" @tap="goBack">
          <image class="button-icon" src="/static/btn_back.png" mode="aspectFit"></image>
          <text class="button-text">返回主页</text>
        </view>
      </view>

      <!-- 底部购买按钮 -->
      <view class="bottom-action">
        <view 
          class="buy-button" 
          :class="{ 'disabled': !canPurchase() }"
          @tap="handlePurchase"
        >
          {{ getBuyButtonText() }}
        </view>
      </view>

      <!-- 加载状态 -->
      <view class="loading-mask" v-if="loading">
        <view class="loading-content">
          <image src="/static/loading.gif" class="loading-icon" />
          <text class="loading-text">加载中...</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import navBar from "../../common/Navbar/navbar.vue"
import { 
  getSeckillGoodsDetail,
  checkSeckillEligibility,
  submitSeckillOrder
} from "../../api/api.js"
import { mapState } from "vuex"

export default {
  name: 'SeckillGoodsDetail',
  components: {
    navBar
  },
  data() {
    return {
      goodsId: null,
      goodsInfo: {},
      quantity: 1,
      countdown: null,
      countdownTimer: null,
      loading: true,
      ht: 0 // 导航栏高度
    }
  },
  computed: {
    ...mapState(['token', 'userInfo']),
    
    // 最大可购买数量
    maxQuantity() {
      const remainingQuota = this.goodsInfo.limitCount - (this.goodsInfo.userPurchased || 0)
      return Math.min(remainingQuota, this.goodsInfo.availableStock || 0)
    }
  },
  onLoad(options) {
    if (options.goodsId) {
      this.goodsId = options.goodsId
      this.loadGoodsDetail()
    }
    this.getNavBarHeight()
  },
  onReady() {
    this.startCountdown()
  },
  onUnload() {
    this.clearCountdown()
  },
  methods: {
    // 获取导航栏高度
    getNavBarHeight() {
      const systemInfo = uni.getSystemInfoSync()
      this.ht = systemInfo.statusBarHeight + 44
    },

    // 加载商品详情
    async loadGoodsDetail() {
      try {
        this.loading = true
        console.log('加载商品详情，商品ID:', this.goodsId)
        
        const res = await getSeckillGoodsDetail(this.goodsId)
        console.log('商品详情API响应:', res)
        
        if (res.code === 1 && res.data) {
          this.goodsInfo = res.data
          console.log('使用后端商品详情数据:', this.goodsInfo)
        } else {
          console.log('后端商品详情获取失败，使用模拟数据')
          this.loadMockData()
        }
        
        this.loading = false
      } catch (error) {
        console.error('加载商品详情失败:', error)
        console.log('API调用失败，使用模拟数据')
        this.loadMockData()
        this.loading = false
      }
    },

    // 加载模拟数据
    loadMockData() {
      this.goodsInfo = {
        id: this.goodsId,
        activityId: 1,
        activityName: '限时秒杀',
        goodsType: 1,
        goodsId: 46,
        goodsName: '王老吉',
        goodsImage: 'https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png',
        originalPrice: 6.00,
        seckillPrice: 3.00,
        availableStock: 85,
        soldCount: 15,
        limitCount: 2,
        userPurchased: 0,
        canPurchase: true,
        startTime: '2025-08-20 10:00:00',
        endTime: '2025-08-20 12:00:00',
        remainingTime: 3600,
        description: '清热解毒的凉茶饮品'
      }
    },

    // 获取折扣文本
    getDiscountText() {
      if (!this.goodsInfo.originalPrice || !this.goodsInfo.seckillPrice) return ''
      const discount = Math.round((this.goodsInfo.seckillPrice / this.goodsInfo.originalPrice) * 10)
      return `${discount}折`
    },

    // 格式化活动时间
    formatActivityTime() {
      if (!this.goodsInfo.startTime || !this.goodsInfo.endTime) return ''
      const start = this.formatTime(this.goodsInfo.startTime)
      const end = this.formatTime(this.goodsInfo.endTime)
      return `${start} - ${end}`
    },

    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr) return ''
      // 修复日期解析问题，将横线替换为斜线
      const fixedTimeStr = timeStr.replace(/-/g, '/')
      const date = new Date(fixedTimeStr)
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      return `${month}-${day} ${hour}:${minute}`
    },

    // 判断是否可以购买
    canPurchase() {
      const now = new Date().getTime()
      // 修复日期解析问题，将横线替换为斜线
      const startTimeStr = this.goodsInfo.startTime ? this.goodsInfo.startTime.replace(/-/g, '/') : ''
      const endTimeStr = this.goodsInfo.endTime ? this.goodsInfo.endTime.replace(/-/g, '/') : ''
      const startTime = new Date(startTimeStr).getTime()
      const endTime = new Date(endTimeStr).getTime()
      
      return now >= startTime && 
             now <= endTime && 
             this.goodsInfo.availableStock > 0 && 
             (this.goodsInfo.userPurchased || 0) < this.goodsInfo.limitCount
    },

    // 获取购买按钮文本
    getBuyButtonText() {
      const now = new Date().getTime()
      // 修复日期解析问题，将横线替换为斜线
      const startTimeStr = this.goodsInfo.startTime ? this.goodsInfo.startTime.replace(/-/g, '/') : ''
      const endTimeStr = this.goodsInfo.endTime ? this.goodsInfo.endTime.replace(/-/g, '/') : ''
      const startTime = new Date(startTimeStr).getTime()
      const endTime = new Date(endTimeStr).getTime()
      
      if (now < startTime) {
        return '活动未开始'
      } else if (now > endTime) {
        return '活动已结束'
      } else if (this.goodsInfo.availableStock <= 0) {
        return '已抢完'
      } else if ((this.goodsInfo.userPurchased || 0) >= this.goodsInfo.limitCount) {
        return '已达限购'
      } else {
        return '立即购买'
      }
    },

    // 增加数量
    increaseQuantity() {
      if (this.quantity < this.maxQuantity) {
        this.quantity++
      }
    },

    // 减少数量
    decreaseQuantity() {
      if (this.quantity > 1) {
        this.quantity--
      }
    },

    // 验证数量
    validateQuantity() {
      if (this.quantity < 1) {
        this.quantity = 1
      } else if (this.quantity > this.maxQuantity) {
        this.quantity = this.maxQuantity
      }
    },

    // 处理购买
    async handlePurchase() {
      if (!this.canPurchase()) {
        return
      }

      try {
        // 检查登录状态
        if (!this.token) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          })
          return
        }

        // 检查购买资格
        const eligibilityRes = await checkSeckillEligibility({
          seckillGoodsId: this.goodsId,
          quantity: this.quantity
        })

        if (eligibilityRes.code !== 1) {
          uni.showToast({
            title: eligibilityRes.msg || '无法购买',
            icon: 'none'
          })
          return
        }

        // 跳转到订单确认页面
        uni.navigateTo({
          url: `/pages/seckill/order/order?goodsId=${this.goodsId}&quantity=${this.quantity}`
        })

      } catch (error) {
        console.error('处理购买失败:', error)
        uni.showToast({
          title: '操作失败，请重试',
          icon: 'none'
        })
      }
    },

    // 计算倒计时
    calculateCountdown() {
      if (!this.goodsInfo.endTime) return null
      
      const now = new Date().getTime()
      // 修复日期解析问题，将横线替换为斜线
      const endTimeStr = this.goodsInfo.endTime.replace(/-/g, '/')
      const end = new Date(endTimeStr).getTime()
      const diff = end - now

      if (diff <= 0) {
        return null
      }

      const hours = Math.floor(diff / (1000 * 60 * 60))
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
      const seconds = Math.floor((diff % (1000 * 60)) / 1000)

      return { hours, minutes, seconds }
    },

    // 开始倒计时
    startCountdown() {
      this.countdown = this.calculateCountdown()
      this.countdownTimer = setInterval(() => {
        this.countdown = this.calculateCountdown()
        if (!this.countdown) {
          this.clearCountdown()
          // 活动结束，刷新页面
          this.loadGoodsDetail()
        }
      }, 1000)
    },

    // 清除倒计时
    clearCountdown() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
        this.countdownTimer = null
      }
    },

    // 格式化时间单位
    formatTimeUnit(time) {
      return time < 10 ? `0${time}` : `${time}`
    },

    // 跳转个人中心
    goToPersonalCenter() {
      uni.navigateTo({
        url: '/pages/my/my'
      })
    },

    // 返回主页
    goBack() {
      uni.redirectTo({
        url: '/pages/index/index'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.goods-detail-container {
  width: 100%;
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.content {
  width: 100%;
}

.goods-info-section {
  background: white;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.goods-image {
  width: 100%;
  height: 400rpx;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
}

.goods-details {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.goods-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.goods-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.4;
}

.price-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.price-label {
  font-size: 28rpx;
  color: #333;
}

.seckill-price {
  font-size: 48rpx;
  font-weight: bold;
  color: #ff6b35;
}

.original-price {
  font-size: 28rpx;
  color: #999;
  text-decoration: line-through;
}

.discount-tag {
  background: #ff6b35;
  color: white;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.activity-info-section {
  background: #f9f9f9;
  padding: 20rpx;
  border-radius: 12rpx;
}

.activity-row {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.activity-label {
  font-size: 26rpx;
  color: #666;
  width: 140rpx;
}

.activity-time, .limit-text, .stock-text {
  font-size: 26rpx;
  color: #333;
}

.countdown-section {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff6b35, #ff8a50);
  border-radius: 16rpx;
  padding: 30rpx;
  color: white;
}

.countdown-label {
  font-size: 28rpx;
  margin-right: 20rpx;
}

.countdown-time {
  display: flex;
  align-items: center;
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
  font-size: 32rpx;
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
  font-size: 32rpx;
  font-weight: bold;
  margin: 0 10rpx;
}

.quantity-section {
  background: white;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.quantity-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 30rpx;
  margin-bottom: 20rpx;
}

.quantity-btn {
  width: 60rpx;
  height: 60rpx;
  border: 2rpx solid #ddd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  
  &.disabled {
    border-color: #f0f0f0;
    color: #ccc;
  }
}

.quantity-input {
  width: 120rpx;
  height: 60rpx;
  text-align: center;
  border: 2rpx solid #ddd;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.quantity-tip {
  font-size: 24rpx;
  color: #999;
  text-align: center;
}

.description-section {
  background: white;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.description-content {
  background: #f9f9f9;
  padding: 20rpx;
  border-radius: 12rpx;
}

.description-text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
}

.action-buttons-section {
  background: white;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.personal-center-button,
.back-button {
  width: 300rpx;
  height: 80rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.personal-center-button {
  background: #ff6b35;
  color: white;
  border: 2rpx solid #ff6b35;
  
  &:active {
    background: #e55a2b;
  }
}

.back-button {
  background: #f5f5f5;
  color: #666;
  border: 2rpx solid #ddd;
  
  &:active {
    background: #e0e0e0;
  }
}

.button-icon {
  width: 32rpx;
  height: 32rpx;
}

.button-text {
  font-size: 28rpx;
  font-weight: 500;
}

.bottom-action {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: white;
  padding: 20rpx 30rpx;
  box-sizing: border-box;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.buy-button {
  width: 100%;
  height: 88rpx;
  background: #ff6b35;
  color: white;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &.disabled {
    background: #ccc;
    color: #999;
  }
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
