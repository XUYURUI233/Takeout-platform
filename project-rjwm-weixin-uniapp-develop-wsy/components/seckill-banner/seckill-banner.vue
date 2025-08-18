<template>
  <view class="seckill-banner-container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 错误状态 -->
    <view v-else-if="showError" class="error-state">
      <text class="error-text">暂无秒杀活动</text>
      <view class="retry-btn" @tap="retryLoad">重试</view>
    </view>
    
    <!-- 秒杀轮播 -->
    <swiper
      v-else-if="seckillActivities.length > 0"
      class="seckill-swiper"
      :indicator-dots="true"
      :autoplay="true"
      :interval="5000"
      :duration="500"
      :circular="true"
      indicator-color="rgba(255, 255, 255, 0.3)"
      indicator-active-color="#ff6b35"
      @change="onSwiperChange"
    >
      <swiper-item 
        v-for="activity in seckillActivities" 
        :key="activity.id"
        @tap="goToSeckillDetail(activity)"
      >
        <view class="banner-item">
          <image 
            :src="activity.image || '../../static/bg.png'" 
            class="banner-bg"
            mode="aspectFill"
            @error="handleImageError"
          />
          <view class="banner-overlay">
            <view class="banner-content">
              <view class="activity-info">
                <text class="activity-title">{{ activity.name }}</text>
                <text class="activity-desc">{{ activity.description }}</text>
              </view>
              <view class="countdown-container">
                <view class="countdown-label">活动截止</view>
                <view class="countdown-time">
                  <view class="time-block" v-if="activity.countdown">
                    <text class="time-num">{{ formatTime(activity.countdown.hours) }}</text>
                    <text class="time-unit">时</text>
                  </view>
                  <text class="time-separator" v-if="activity.countdown">:</text>
                  <view class="time-block" v-if="activity.countdown">
                    <text class="time-num">{{ formatTime(activity.countdown.minutes) }}</text>
                    <text class="time-unit">分</text>
                  </view>
                  <text class="time-separator" v-if="activity.countdown">:</text>
                  <view class="time-block" v-if="activity.countdown">
                    <text class="time-num">{{ formatTime(activity.countdown.seconds) }}</text>
                    <text class="time-unit">秒</text>
                  </view>
                </view>
              </view>
            </view>
            <view class="banner-action">
              <text class="action-text">立即抢购</text>
              <text class="action-arrow">></text>
            </view>
          </view>
        </view>
      </swiper-item>
    </swiper>
  </view>
</template>

<script>
import { getActiveSeckillActivities } from '../../pages/api/api.js'

export default {
  name: 'SeckillBanner',
  data() {
    return {
      seckillActivities: [],
      currentIndex: 0,
      countdownTimer: null,
      loading: true,
      showError: false
    }
  },
  mounted() {
    console.log('🎯 秒杀横幅组件已挂载')
    console.log('🔍 组件初始状态:', {
      seckillActivitiesLength: this.seckillActivities.length,
      loading: this.loading,
      showError: this.showError
    })
    
    // 加载后端数据
    this.loadSeckillActivities()
    this.startCountdown()
  },
  beforeDestroy() {
    this.clearCountdown()
  },
  methods: {
    // 加载秒杀活动数据
    async loadSeckillActivities() {
      try {
        this.loading = true
        this.showError = false
        console.log('🔄 开始加载后端秒杀活动数据...')
        
        const res = await getActiveSeckillActivities()
        console.log('📡 API响应:', res)
        
        if (res.code === 1 && res.data && res.data.length > 0) {
          // 使用后端返回的真实数据
          this.seckillActivities = res.data.map(activity => ({
            ...activity,
            countdown: this.calculateCountdown(activity.endTime)
          }))
          this.showError = false
          console.log('✅ 成功加载后端数据:', this.seckillActivities)
        } else {
          // 后端无活动数据
          console.log('⚠️ 后端返回无活动数据')
          this.seckillActivities = []
          this.showError = true
        }
      } catch (error) {
        console.error('❌ 加载秒杀活动失败:', error)
        console.error('错误详情:', JSON.stringify(error))
        
        // 显示错误状态
        this.seckillActivities = []
        this.showError = true
      } finally {
        this.loading = false
        console.log('🔍 数据加载完成，最终状态:', {
          seckillActivitiesLength: this.seckillActivities.length,
          loading: this.loading,
          showError: this.showError,
          activities: this.seckillActivities
        })
      }
    },

    // 计算倒计时
    calculateCountdown(endTime) {
      try {
        const now = new Date().getTime()
        // 确保endTime是有效的时间格式
        const end = new Date(endTime.replace(/-/g, '/')).getTime()
        const diff = end - now

        if (diff <= 0) {
          return null
        }

        const hours = Math.floor(diff / (1000 * 60 * 60))
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
        const seconds = Math.floor((diff % (1000 * 60)) / 1000)

        return { hours, minutes, seconds }
      } catch (error) {
        console.error('计算倒计时失败:', error)
        return null
      }
    },

    // 格式化时间显示
    formatTime(time) {
      return time < 10 ? `0${time}` : `${time}`
    },

    // 开始倒计时
    startCountdown() {
      this.countdownTimer = setInterval(() => {
        this.seckillActivities = this.seckillActivities.map(activity => ({
          ...activity,
          countdown: this.calculateCountdown(activity.endTime)
        }))
      }, 1000)
    },

    // 清除倒计时
    clearCountdown() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
        this.countdownTimer = null
      }
    },

    // 轮播图切换事件
    onSwiperChange(e) {
      this.currentIndex = e.detail.current
    },

    // 跳转到秒杀详情页
    goToSeckillDetail(activity) {
      uni.navigateTo({
        url: `/pages/seckill/activity-detail/activity-detail?activityId=${activity.id}`
      })
    },

    // 处理图片加载错误
    handleImageError(e) {
      console.log('图片加载失败:', e)
      // 可以设置默认图片或其他处理逻辑
    },

    // 重试加载
    retryLoad() {
      console.log('🔄 用户点击重试，重新加载后端数据...')
      this.showError = false
      this.seckillActivities = []
      this.loadSeckillActivities()
    }
  }
}
</script>

<style lang="scss" scoped>
.seckill-banner-container {
  width: 100%;
  height: 300rpx; // 调整为与父容器高度保持一致（两倍高度）
  position: relative;
  border-radius: 20rpx;
  overflow: hidden; // 恢复为hidden
  z-index: 1002 !important; // 设置高z-index
  // 移除调试样式
  // background-color: #e0e0e0;
  // border: 3rpx solid #00ff00;
}

.seckill-swiper {
  width: 100%;
  height: 100%;
  
  // 禁用原生滚动条
  ::-webkit-scrollbar {
    display: none !important;
    width: 0 !important;
    height: 0 !important;
    -webkit-appearance: none;
    background: transparent;
    color: transparent;
  }
}

.banner-item {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30rpx;
  box-sizing: border-box;
}

.banner-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.banner-overlay {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0.2) 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30rpx;
  box-sizing: border-box;
}

.banner-content {
  flex: 1;
  color: white;
}

.activity-info {
  margin-bottom: 20rpx;
}

.activity-title {
  font-size: 36rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 10rpx;
}

.activity-desc {
  font-size: 24rpx;
  opacity: 0.9;
  display: block;
}

.countdown-container {
  display: flex;
  align-items: center;
}

.countdown-label {
  font-size: 24rpx;
  margin-right: 20rpx;
  opacity: 0.8;
}

.countdown-time {
  display: flex;
  align-items: center;
}

.time-block {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8rpx;
  padding: 8rpx 12rpx;
  margin: 0 5rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 60rpx;
}

.time-num {
  font-size: 28rpx;
  font-weight: bold;
  line-height: 1;
}

.time-unit {
  font-size: 20rpx;
  opacity: 0.8;
  line-height: 1;
  margin-top: 2rpx;
}

.time-separator {
  font-size: 28rpx;
  font-weight: bold;
  margin: 0 5rpx;
}

.banner-action {
  display: flex;
  align-items: center;
  background: #ff6b35;
  border-radius: 50rpx;
  padding: 20rpx 30rpx;
  color: white;
}

.action-text {
  font-size: 28rpx;
  font-weight: bold;
  margin-right: 10rpx;
}

.action-arrow {
  font-size: 24rpx;
  font-weight: bold;
}

// 自定义指示器样式
:deep(.uni-swiper-dot) {
  width: 16rpx !important;
  height: 16rpx !important;
  border-radius: 50% !important;
  margin: 0 8rpx !important;
}

:deep(.uni-swiper-dot-active) {
  background-color: #ff6b35 !important;
}

// 测试横幅样式
.test-banner {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4CAF50, #66BB6A);
  border-radius: 20rpx;
  padding: 20rpx;
  box-sizing: border-box;
}

.test-text {
  color: white;
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.test-debug {
  color: white;
  font-size: 24rpx;
  opacity: 0.9;
}

// 加载状态样式
.loading-state {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff6b35, #ff8a50);
  border-radius: 20rpx;
}

.loading-text {
  color: white;
  font-size: 28rpx;
}

// 错误状态样式
.error-state {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff6b35, #ff8a50);
  border-radius: 20rpx;
  padding: 40rpx;
  box-sizing: border-box;
}

.error-text {
  color: white;
  font-size: 28rpx;
  margin-bottom: 20rpx;
  text-align: center;
}

.retry-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 12rpx 24rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.3);
}
</style>
