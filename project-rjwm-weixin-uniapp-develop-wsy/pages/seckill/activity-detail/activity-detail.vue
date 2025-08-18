<template>
  <view class="activity-detail-container">
    <!-- 导航栏 -->
    <navBar :title="activityInfo.name || '秒杀活动'"></navBar>
    
    <view class="content" :style="{ paddingTop: ht + 'px' }">
      <!-- 活动信息头部 -->
      <view class="activity-header">
        <image 
          :src="activityInfo.image || '/static/seckill-default.png'" 
          class="activity-banner"
          mode="aspectFill"
        />
        <view class="activity-overlay">
          <view class="activity-info">
            <text class="activity-title">{{ activityInfo.name }}</text>
            <text class="activity-desc">{{ activityInfo.description }}</text>
            <view class="activity-time">
              <text class="time-label">活动时间：</text>
              <text class="time-range">
                {{ formatTime(activityInfo.startTime) }} - {{ formatTime(activityInfo.endTime) }}
              </text>
            </view>
          </view>
          <view class="countdown-box" v-if="countdown">
            <text class="countdown-label">距离结束</text>
            <view class="countdown-time">
              <text class="time-item">{{ formatTimeUnit(countdown.hours) }}</text>
              <text class="separator">:</text>
              <text class="time-item">{{ formatTimeUnit(countdown.minutes) }}</text>
              <text class="separator">:</text>
              <text class="time-item">{{ formatTimeUnit(countdown.seconds) }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 商品列表 -->
      <view class="goods-section">
        <view class="section-title">
          <text class="title-text">秒杀商品</text>
          <text class="goods-count">共{{ goodsList.length }}件商品</text>
        </view>
        
        <view class="goods-list">
          <view 
            class="goods-item" 
            v-for="goods in goodsList" 
            :key="goods.id"
            @tap="goToGoodsDetail(goods)"
          >
            <image :src="goods.goodsImage" class="goods-image" mode="aspectFill" />
            <view class="goods-info">
              <text class="goods-name">{{ goods.goodsName }}</text>
              <view class="price-info">
                <text class="seckill-price">￥{{ goods.seckillPrice }}</text>
                <text class="original-price">￥{{ goods.originalPrice }}</text>
              </view>
              <view class="stock-info">
                <text class="stock-text">剩余{{ goods.availableStock }}件</text>
                <view class="progress-bar">
                  <view 
                    class="progress-fill" 
                    :style="{ width: getStockPercentage(goods) + '%' }"
                  ></view>
                </view>
              </view>
              <view class="limit-info">
                <text class="limit-text">限购{{ goods.limitCount }}件</text>
                <text class="sold-text">已售{{ goods.soldCount }}件</text>
              </view>
            </view>
            <view class="goods-action">
              <view 
                class="buy-btn" 
                :class="{ 'disabled': !canPurchase(goods) }"
                @tap.stop="handlePurchase(goods)"
              >
                {{ getBuyButtonText(goods) }}
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="goodsList.length === 0 && !loading">
        <image src="/static/empty.png" class="empty-icon" />
        <text class="empty-text">暂无秒杀商品</text>
      </view>

      <!-- 加载状态 -->
      <view class="loading-state" v-if="loading">
        <image src="/static/loading.gif" class="loading-icon" />
        <text class="loading-text">加载中...</text>
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
    </view>
  </view>
</template>

<script>
import navBar from "../../common/Navbar/navbar.vue"
import { 
  getSeckillGoodsByActivityId,
  checkSeckillEligibility,
  getActiveSeckillActivities
} from "../../api/api.js"
import { mapState } from "vuex"

export default {
  name: 'SeckillActivityDetail',
  components: {
    navBar
  },
  data() {
    return {
      activityId: null,
      activityInfo: {},
      goodsList: [],
      countdown: null,
      countdownTimer: null,
      loading: true,
      ht: 0 // 导航栏高度
    }
  },
  computed: {
    ...mapState(['token', 'userInfo'])
  },
  onLoad(options) {
    if (options.activityId) {
      this.activityId = options.activityId
      this.loadActivityDetail()
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
      this.ht = systemInfo.statusBarHeight + 44 // 状态栏高度 + 导航栏高度
    },

    // 加载活动详情
    async loadActivityDetail() {
      try {
        this.loading = true
        console.log('加载活动详情，活动ID:', this.activityId)
        
        // 这里应该有获取活动详情的API，暂时使用活动列表API的第一个
        const activitiesRes = await getActiveSeckillActivities()
        if (activitiesRes.code === 1 && activitiesRes.data && activitiesRes.data.length > 0) {
          // 查找对应的活动
          const activity = activitiesRes.data.find(item => item.id == this.activityId)
          if (activity) {
            this.activityInfo = activity
            console.log('使用后端活动数据:', this.activityInfo)
          } else {
            console.log('未找到对应活动，使用第一个活动')
            this.activityInfo = activitiesRes.data[0]
          }
        } else {
          console.log('后端活动数据获取失败，没有活动数据')
          this.activityInfo = {
            name: '活动不存在',
            description: '该活动不存在或已结束',
            startTime: '',
            endTime: ''
          }
        }

        // 加载商品列表
        await this.loadGoodsList()
        
        this.loading = false
      } catch (error) {
        console.error('加载活动详情失败:', error)
        this.activityInfo = {
          name: '加载失败',
          description: '活动数据加载失败，请刷新重试',
          startTime: '',
          endTime: ''
        }
        await this.loadGoodsList()
        this.loading = false
      }
    },



    // 加载商品列表
    async loadGoodsList() {
      try {
        console.log('加载商品列表，活动ID:', this.activityId)
        const res = await getSeckillGoodsByActivityId(this.activityId)
        console.log('商品列表API响应:', res)
        
        if (res.code === 1 && res.data && res.data.length > 0) {
          this.goodsList = res.data
          console.log('使用后端商品数据:', this.goodsList)
        } else {
          console.log('后端商品数据获取失败，没有商品数据')
          this.goodsList = []
        }
      } catch (error) {
        console.error('加载商品列表失败:', error)
        console.log('API调用失败，没有商品数据')
        this.goodsList = []
      }
    },



    // 计算库存百分比
    getStockPercentage(goods) {
      const totalStock = goods.availableStock + goods.soldCount
      return totalStock > 0 ? Math.round((goods.soldCount / totalStock) * 100) : 0
    },

    // 判断是否可以购买
    canPurchase(goods) {
      const now = new Date().getTime()
      // 修复日期解析问题，将横线替换为斜线
      const startTimeStr = this.activityInfo.startTime ? this.activityInfo.startTime.replace(/-/g, '/') : ''
      const endTimeStr = this.activityInfo.endTime ? this.activityInfo.endTime.replace(/-/g, '/') : ''
      const startTime = new Date(startTimeStr).getTime()
      const endTime = new Date(endTimeStr).getTime()
      
      return now >= startTime && 
             now <= endTime && 
             goods.availableStock > 0 && 
             goods.userPurchased < goods.limitCount
    },

    // 获取购买按钮文本
    getBuyButtonText(goods) {
      const now = new Date().getTime()
      // 修复日期解析问题，将横线替换为斜线
      const startTimeStr = this.activityInfo.startTime ? this.activityInfo.startTime.replace(/-/g, '/') : ''
      const endTimeStr = this.activityInfo.endTime ? this.activityInfo.endTime.replace(/-/g, '/') : ''
      const startTime = new Date(startTimeStr).getTime()
      const endTime = new Date(endTimeStr).getTime()
      
      if (now < startTime) {
        return '未开始'
      } else if (now > endTime) {
        return '已结束'
      } else if (goods.availableStock <= 0) {
        return '已抢完'
      } else if (goods.userPurchased >= goods.limitCount) {
        return '已达限购'
      } else {
        return '立即抢购'
      }
    },

    // 处理购买
    async handlePurchase(goods) {
      if (!this.canPurchase(goods)) {
        return
      }

      try {
        // 检查购买资格
        const eligibilityRes = await checkSeckillEligibility({
          seckillGoodsId: goods.id,
          quantity: 1
        })

        if (eligibilityRes.code !== 1) {
          uni.showToast({
            title: eligibilityRes.msg || '无法购买',
            icon: 'none'
          })
          return
        }

        // 跳转到商品详情页
        this.goToGoodsDetail(goods)
      } catch (error) {
        console.error('检查购买资格失败:', error)
        // 直接跳转到商品详情页
        this.goToGoodsDetail(goods)
      }
    },

    // 跳转到商品详情页
    goToGoodsDetail(goods) {
      uni.navigateTo({
        url: `/pages/seckill/goods-detail/goods-detail?goodsId=${goods.id}`
      })
    },

    // 计算倒计时
    calculateCountdown() {
      const now = new Date().getTime()
      // 修复日期解析问题，将横线替换为斜线
      const endTimeStr = this.activityInfo.endTime ? this.activityInfo.endTime.replace(/-/g, '/') : ''
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
          this.loadActivityDetail()
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
.activity-detail-container {
  width: 100%;
  min-height: 100vh;
  background: #f5f5f5;
}

.content {
  width: 100%;
  padding-bottom: 40rpx;
}

.activity-header {
  position: relative;
  width: 100%;
  height: 400rpx;
  overflow: hidden;
}

.activity-banner {
  width: 100%;
  height: 100%;
}

.activity-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.3) 0%, rgba(0, 0, 0, 0.6) 100%);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 40rpx 30rpx;
  box-sizing: border-box;
  color: white;
}

.activity-info {
  flex: 1;
}

.activity-title {
  font-size: 48rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 20rpx;
}

.activity-desc {
  font-size: 28rpx;
  opacity: 0.9;
  display: block;
  margin-bottom: 30rpx;
  line-height: 1.4;
}

.activity-time {
  display: flex;
  align-items: center;
}

.time-label {
  font-size: 24rpx;
  opacity: 0.8;
}

.time-range {
  font-size: 24rpx;
  margin-left: 10rpx;
}

.countdown-box {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16rpx;
  padding: 20rpx;
}

.countdown-label {
  font-size: 24rpx;
  margin-right: 20rpx;
}

.countdown-time {
  display: flex;
  align-items: center;
}

.time-item {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 8rpx;
  padding: 8rpx 12rpx;
  font-size: 28rpx;
  font-weight: bold;
  min-width: 60rpx;
  text-align: center;
}

.separator {
  font-size: 28rpx;
  font-weight: bold;
  margin: 0 10rpx;
}

.goods-section {
  margin-top: 20rpx;
  background: white;
  padding: 30rpx;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.title-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.goods-count {
  font-size: 24rpx;
  color: #999;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.goods-item {
  display: flex;
  align-items: center;
  background: #f9f9f9;
  border-radius: 16rpx;
  padding: 20rpx;
  position: relative;
}

.goods-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
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

.price-info {
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

.stock-info {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.stock-text {
  font-size: 22rpx;
  color: #666;
}

.progress-bar {
  flex: 1;
  height: 8rpx;
  background: #f0f0f0;
  border-radius: 4rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #ff6b35;
  transition: width 0.3s ease;
}

.limit-info {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.limit-text, .sold-text {
  font-size: 22rpx;
  color: #666;
}

.goods-action {
  display: flex;
  align-items: center;
}

.buy-btn {
  background: #ff6b35;
  color: white;
  font-size: 24rpx;
  font-weight: bold;
  padding: 16rpx 32rpx;
  border-radius: 50rpx;
  text-align: center;
  min-width: 120rpx;
  
  &.disabled {
    background: #ccc;
    color: #999;
  }
}

.empty-state, .loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
}

.empty-icon, .loading-icon {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text, .loading-text {
  font-size: 28rpx;
  color: #999;
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
</style>
