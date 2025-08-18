import store from './../store'
import { baseUrl } from './env'
import * as mockData from './mockData.js'

// 是否使用模拟数据（当后端服务不可用时设置为true）
const USE_MOCK_DATA = false  // 先尝试真实API，失败时自动降级到模拟数据
// 参数： url:请求地址  param：请求参数  method：请求方式 callBack：回调函数
export function request({url='', params={}, method='GET'}) {
	uni.getStorage({
		key: ''
	})
	const storeInfo = store.state
	let header = {
			'Accept': 'application/json',
			'Access-Control-Allow-Origin':'*',
			'Content-Type': 'application/json', 
			// 'shopid':storeInfo.storeInfo.shopId ?? '',
			// 'storeid':storeInfo.storeInfo.storeId ?? '',
			'authentication': storeInfo.token
		}
	
	// 添加调试日志
	console.log('🌐 网络请求调试信息:')
	console.log('📡 请求URL:', baseUrl + url)
	console.log('📋 请求参数:', params)
	console.log('🔧 请求方法:', method)
	console.log('📝 请求头:', header)
	
	// 如果启用模拟数据，直接返回模拟数据
	if (USE_MOCK_DATA) {
		console.log('🎭 使用模拟数据模式')
		return new Promise((resolve, reject) => {
			setTimeout(() => {
				let mockResponse = null
				
				// 根据URL返回对应的模拟数据
				if (url.includes('/user/shop/status')) {
					mockResponse = mockData.mockShopStatus
				} else if (url.includes('/user/user/login')) {
					mockResponse = mockData.mockUserLogin
				} else if (url.includes('/user/category/list')) {
					mockResponse = mockData.mockCategories
				} else if (url.includes('/user/dish/list')) {
					mockResponse = mockData.mockDishes
				} else if (url.includes('/user/shoppingCart/list')) {
					mockResponse = mockData.mockShoppingCart
				} else if (url.includes('/user/order/userPage')) {
					mockResponse = mockData.mockOrders
				} else if (url.includes('/user/addressBook/list')) {
					mockResponse = mockData.mockAddresses
				} 
				// ======================== 秒杀功能相关模拟数据路由 ========================
				else if (url.includes('/user/seckill/activity/active')) {
					mockResponse = mockData.mockSeckillActivities
				} else if (url.includes('/user/seckill/activity/') && url.includes('/goods')) {
					mockResponse = mockData.mockSeckillGoods
				} else if (url.includes('/user/seckill/goods/')) {
					mockResponse = mockData.mockSeckillGoodsDetail
				} else if (url.includes('/user/seckill/order/submit')) {
					mockResponse = {
						code: 1,
						msg: "success",
						data: {
							orderId: 100,
							orderNumber: "SK" + Date.now(),
							payExpireTime: new Date(Date.now() + 15 * 60 * 1000).toISOString().slice(0, 19).replace('T', ' '),
							totalAmount: 3.00,
							payTimeLimit: 900
						}
					}
				} else if (url.includes('/user/seckill/order/payment')) {
					mockResponse = {
						code: 1,
						msg: "success", 
						data: {
							nonceStr: "mock_nonce_" + Date.now(),
							paySign: "mock_sign_" + Date.now(),
							timeStamp: String(Date.now()),
							signType: "RSA",
							packageStr: "prepay_id=mock_prepay_" + Date.now()
						}
					}
				} else if (url.includes('/user/seckill/check/eligibility')) {
					mockResponse = {
						code: 1,
						msg: "success",
						data: {
							canPurchase: true,
							remainingQuota: 2,
							limitCount: 2,
							userPurchased: 0,
							availableStock: 85
						}
					}
				}
				else {
					// 默认返回成功响应
					mockResponse = {
						code: 1,
						msg: "success",
						data: null
					}
				}
				
				console.log('🎭 返回模拟数据:', mockResponse)
				resolve(mockResponse)
			}, 500) // 模拟网络延迟
		})
	}
	
	const requestRes = new Promise((resolve, reject) => {
		store.commit('setLodding', false)
		 uni.request({
			url: baseUrl+url, 
			data: params,
			header: header,
			method: method,
			success: (res) => {
				console.log('✅ 请求成功:', res)
				const { data, statusCode } = res
				
				// 处理HTTP状态码错误（如401）
				if (statusCode === 401 || statusCode === 403) {
					console.warn('⚠️ 认证失败，状态码:', statusCode)
					
					// 如果是秒杀相关API的认证失败，降级使用模拟数据
					if (url.includes('/user/seckill/')) {
						console.log('🎭 秒杀API认证失败，降级使用模拟数据')
						let mockResponse = null
						
						if (url.includes('/user/seckill/activity/active')) {
							mockResponse = mockData.mockSeckillActivities
						} else if (url.includes('/user/seckill/activity/') && url.includes('/goods')) {
							mockResponse = mockData.mockSeckillGoods
						} else if (url.includes('/user/seckill/goods/')) {
							mockResponse = mockData.mockSeckillGoodsDetail
						} else {
							mockResponse = { code: 1, msg: "success", data: null }
						}
						
						if (mockResponse) {
							console.log('🎭 返回降级模拟数据:', mockResponse)
							resolve(mockResponse)
							return
						}
					}
					
					reject(res)
					return
				}
				
				if (data && (data.code == 200 || data.code === 1)) {
					// store.commit('setLodding', false)
					resolve(res.data)
				}else{
					console.warn('⚠️ 请求返回错误码:', data ? data.code : '未知', data ? data.msg : '未知错误')
					// store.commit('setLodding', true)
					reject(res.data)
				}
			},
			fail: (err) => {
				console.error('❌ 请求失败:', err)
				console.error('🔍 错误详情:', {
					errMsg: err.errMsg,
					statusCode: err.statusCode,
					data: err.data
				})
				
				// 如果是秒杀相关API失败，尝试返回模拟数据
				if (url.includes('/user/seckill/')) {
					console.log('🎭 秒杀API失败，降级使用模拟数据')
					let mockResponse = null
					
					if (url.includes('/user/seckill/activity/active')) {
						mockResponse = mockData.mockSeckillActivities
					} else if (url.includes('/user/seckill/activity/') && url.includes('/goods')) {
						mockResponse = mockData.mockSeckillGoods
					} else if (url.includes('/user/seckill/goods/')) {
						mockResponse = mockData.mockSeckillGoodsDetail
					} else {
						mockResponse = { code: 1, msg: "success", data: null }
					}
					
					if (mockResponse) {
						console.log('🎭 返回降级模拟数据:', mockResponse)
						resolve(mockResponse)
						return
					}
				}
				
				const error = {data:{msg:err.data}}
				// store.commit('setLodding', true)
				reject(error)
			}
		});
	})
	return requestRes
}

