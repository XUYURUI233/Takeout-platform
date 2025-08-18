<template>
	<view class="navBar">
		<!-- 个人中心 -->
		<view @click="myCenterFun" class="person-box" :style="{top:ht + 'px'}">
			<image class="test_image" src="../../../static/center.png"></image>
			<span class="person-title">个人中心</span>
		</view>
		<!-- 返回按钮 -->
		<view @click="goBack" class="back-box" :style="{top:(ht + 60) + 'px'}">
			<image class="back_image" src="../../../static/btn_back.png"></image>
			<span class="back-title">返回</span>
		</view>
		<image class="index_bg" src="../../../static/bg.png"></image>
		<!-- <view class="leftNav" >
			<image class="logo" src="/static/logo.png"></image>
		</view> -->
	</view>
</template>

<script>
export default {
	computed: {
		// ht: function () {
		// 	let res = uni.getMenuButtonBoundingClientRect()
		// 	let num = 24
		// 	if(/iPhone.*/.test(uni.getSystemInfoSync().model)){
		// 		 num = res.top * 1.6
		// 	} else {
		// 		num = res.top * 2
		// 	}
		// 	return num
		// }
		ht: function () {
			let res = uni.getMenuButtonBoundingClientRect() 
			return res.top +5
		}
	},
	methods: {
		myCenterFun () {
			uni.navigateTo({
				url: '/pages/my/my'
			})
		},
		goBack () {
			uni.navigateBack({
				delta: 1
			})
		}
	}
}
</script>

<style src="./navbar.scss" lang="scss"></style>
