import store from './../store'
import { baseUrl } from './env'
import * as mockData from './mockData.js'

// 是否使用模拟数据（当后端服务不可用时设置为true）
const USE_MOCK_DATA = false
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
				} else {
					// 默认返回成功响应
					mockResponse = {
						code: 200,
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
				const { data } = res
				if (data.code == 200 || data.code === 1) {
					// store.commit('setLodding', false)
					resolve(res.data)
				}else{
					console.warn('⚠️ 请求返回错误码:', data.code, data.msg)
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
				const error = {data:{msg:err.data}}
				// store.commit('setLodding', true)
				reject(error)
			}
		});
	})
	return requestRes
}

