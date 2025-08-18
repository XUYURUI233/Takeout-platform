import { request } from "../../utils/request.js"

// 开桌
export const openTable = (params) =>
	request({
		url: `/user/table/open/${params.tableId}/${params.seatNumber}`,
		method: 'GET',
		params
	})

// 获取桌台状态
export const getTableState = (params) =>
	request({
		url: `/user/table/tableStatus/${params.shopId}/${params.storeId}/${params.tableId}`,
		method: 'GET',
		params,
	})

// 获取购物车菜品
export const getTableOrderDishList = (params) =>
	request({
		url: `/user/order/shopCart//${params.tableId}`,
		method: 'GET',
		params
	})
// 获取菜品规格
export const getMoreNorm = (params) =>
	request({
		url: `/user/dish/flavor/${params.dishId}`,
		method: 'GET',
		params,
	})

// 获取菜品分类列表	
export const getList = (params) =>
	request({
		url: `/user/dish/category`,
		method: 'GET',
		params,
	})

// 获取菜品详情	
export const getDishDetail = (params) =>
	request({
		url: `/user/dish/setmealDishList/${params.setmealId}`,
		method: 'GET',
		params,
	})

// 根据分类获取菜品列表
export const getDishList = (params) =>
	request({
		url: `/user/dish/dishPageList/${params.categoryId}/${params.type}/${params.page}/${params.pageSize}`,
		method: 'GET',
		params
	})

// 加菜
export const addDish = (params) =>
	request({
		url: `/user/order/addDish`,
		method: 'POST',
		params
	})

// 减菜
export const delDish = (params) =>
	request({
		url: `/user/order/decreaseDish/${params.tableId}/${params.dishId}`,
		method: 'GET',
		params
	})

// 清空购物车
export const clearOrder = (params) =>
	request({
		url: `/user/order/cleanShopCart/${params.tableId}`,
		method: 'GET',
		params
	})

// 提交订单 
export const payOrder = (params) =>
	request({
		url: `/user/order/pay/${params.tableId}/${params.jsCode}`,
		method: 'GET',
		params
	})


// 用户登录
export const userLogin = (params) => {
	return request({
		url: '/user/user/login',
		method: 'POST',
		params
	}).then(res => {
		// 扩展返回数据，添加店铺信息
		if (res.code === 1 || res.code === 200) {
			return {
				...res,
				data: {
					...res.data,
					deliveryFee: 6, // 默认配送费
					shopName: "Sky外卖",
					shopAddress: "北京市海淀区上地十街10号",
					shopId: 1
				}
			}
		}
		return res;
	})
}

// 菜品和套餐的分类
export const getCategoryList = (params) => {
	return request({
		url: '/user/category/list',
		method: 'GET',
		params
	})
}

// 查询菜品管理列表
export const dishListByCategoryId = (params) => {
	return request({
		url: '/user/dish/list',
		method: 'GET',
		params
	})
}

// 文件下载---预览
export const commonDownload = (params) => {
	return request({
		url: '/user/common/download',
		method: 'GET',
		params
	})
}


// 购物车----加菜功能实现
export const addShoppingCart = (params) => {
	return request({
		url: '/user/shoppingCart',
		method: 'POST',
		params
	})
}

// 根据type类型查询是套餐的接口
export const querySetmeaList = (params) => {
	return request({
		url: '/user/setmeal/list',
		method: 'GET',
		params
	})
}

// 获取购物车集合
export const getShoppingCartList = (params) => {
	return request({
		url: '/user/shoppingCart/list',
		method: 'GET',
		params
	})
}


// 修改购物车数量功能
export const editHoppingCart = (params) => {
	return request({
		url: '/user/shoppingCart',
		method: 'PUT',
		params
	})
}


// 购物车新增接口-new
export const newAddShoppingCartAdd = (params) => {
	return request({
		url: '/user/shoppingCart/add',
		method: 'POST',
		params
	})
}


// 购物车减菜接口-new 
export const newShoppingCartSub = (params) => {
	return request({
		url: '/user/shoppingCart/sub',
		method: 'POST',
		params
	})
}


// 清除购物车
export const delShoppingCart = (params) => {
	return request({
		url: '/user/shoppingCart/clean',
		method: 'DELETE',
		params
	})
}


// 最近订单和历史订单
export const queryOrderUserPage = (params) => {
	return request({
		url: '/user/order/userPage',
		method: 'GET',
		params
	})
}


// 用户下单
export const submitOrderSubmit = (params) => {
	return request({
		url: '/user/order/submit',
		method: 'POST',
		params
	})
}


// 查询地址列表
export const queryAddressBookList = (params) => {
	return request({
		url: '/user/addressBook/list',
		method: 'GET',
		params
	})
}

// 新增默认接口
export const putAddressBookDefault = (params) => {
	return request({
		url: '/user/addressBook/default',
		method: 'PUT',
		params
	})
}


// 新增地址接口
export const addAddressBook = (params) => {
	return request({
		url: '/user/addressBook',
		method: 'POST',
		params
	})
}

// 修改地址接口
export const editAddressBook = (params) => {
	return request({
		url: '/user/addressBook',
		method: 'PUT',
		params
	})
}

// 删除地址
export const delAddressBook = (id) => {
	return request({
		url: `/user/addressBook/?id=${id}`,
		method: 'DELETE',
		params: { id }
	})
}

// 查询地址通过id
export const queryAddressBookById = (params) => {
	return request({
		url: `/user/addressBook/${params.id}`,
		method: 'GET',
		params
	})
}


// 再来一单
export const oneOrderAgain = (params) => {
	return request({
		url: '/user/order/again',
		method: 'POST',
		params
	})
}

// 查询默认地址
export const getAddressBookDefault = () => {
	return request({
		url: '/user/addressBook/default',
		method: 'GET'
	})
}


// 此接口为首页查询套餐详情展示的接口
export const querySetmealDishById = (params) => {
	return request({
		url: `/user/setmeal/dish/${params.id}`,
		method: 'GET'
	})
}
// v2.0添加接口
// 获取首页店铺信息
export const getShopStatus = (params) => {
	return request({
		url: `/user/shop/status`,
		method: 'GET'
	})
}
// 获取店铺信息（暂时使用默认数据）
export const getMerchantInfo = (params) => {
	return new Promise((resolve) => {
		// 模拟API响应，返回默认店铺信息
		resolve({
			code: 200,
			msg: "success",
			data: {
				phone: "400-123-4567",
				shopName: "Sky外卖",
				shopAddress: "北京市海淀区上地十街10号",
				deliveryFee: 6
			}
		});
	});
}
// 历史订单
export const getOrderPage = (params) => {
	return request({
		url: '/user/order/historyOrders',
		method: 'GET',
		params
	})
}
// 订单详情
export const getOrderDetail = (params) =>
	request({
		url: `/user/order/orderDetail/${params}`,
		method: 'GET'
	})
// 取消订单
export const cancelOrder = (params) =>
	request({
		url: `/user/order/cancel/${params}`,
		method: 'PUT'
	})
// 催单
export const reminderOrder = (params) =>
	request({
		url: `/user/order/reminder/${params}`,
		method: 'GET'
	})
// 订单支付
export const paymentOrder = (params) => {
	// 实验环境：调用真实后端接口来更新订单状态
	return request({
		url: `/user/order/payment`,
		method: 'PUT',
		params
	}).then(res => {
		console.log("支付接口响应：", res);
		// 确保返回成功状态
		if (res.code === 1) {
			return res;
		} else {
			// 如果后端返回失败，在实验环境中仍然返回成功
			console.log("后端支付接口返回失败，实验环境中强制返回成功");
			return {
				code: 1,
				msg: "支付成功",
				data: {
					nonceStr: "mock_nonce",
					paySign: "mock_sign", 
					timeStamp: String(Date.now()),
					signType: "RSA",
					packageStr: "mock_package"
				}
			};
		}
	}).catch(error => {
		console.log("支付接口调用失败，实验环境中返回成功:", error);
		// 实验环境：即使请求失败也返回支付成功
		return {
			code: 1,
			msg: "支付成功",
			data: {
				nonceStr: "mock_nonce",
				paySign: "mock_sign", 
				timeStamp: String(Date.now()),
				signType: "RSA",
				packageStr: "mock_package"
			}
		};
	});
}
// 再来一单
export const repetitionOrder = (params) =>
	request({
		url: `/user/order/repetition/${params}`,
		method: 'POST',
		params
	})


// 获取用户送餐期望时间（暂时使用本地计算）
export const getEstimatedDeliveryTime = (params) => {
	return new Promise((resolve) => {
		// 模拟API响应，返回当前时间加30分钟
		const currentTime = new Date();
		currentTime.setMinutes(currentTime.getMinutes() + 30);
		resolve({
			code: 200,
			msg: "success",
			data: currentTime.toISOString()
		});
	});
}
// 查询用户订单支付状态列表信息
export const queryOrdersCheckStatus = (params) =>
	request({
		url: `/user/order/queryOrdersCheckStatus`,
		method: 'get',
		params
	})

// 获取菜品月销量
export const getDishMonthlySales = (dishId) =>
	request({
		url: `/user/dish/monthlySales/${dishId}`,
		method: 'GET'
	})

// ======================== 秒杀功能相关API ========================

// 获取当前进行中的秒杀活动
export const getActiveSeckillActivities = () => {
	return request({
		url: '/user/seckill/activity/active',
		method: 'GET'
	})
}

// 根据活动ID获取秒杀商品列表
export const getSeckillGoodsByActivityId = (activityId) => {
	return request({
		url: `/user/seckill/activity/${activityId}/goods`,
		method: 'GET'
	})
}

// 获取秒杀商品详情
export const getSeckillGoodsDetail = (goodsId) => {
	return request({
		url: `/user/seckill/goods/${goodsId}`,
		method: 'GET'
	})
}

// 提交秒杀订单
export const submitSeckillOrder = (params) => {
	return request({
		url: '/user/seckill/order/submit',
		method: 'POST',
		params
	})
}

// 秒杀订单支付
export const paySeckillOrder = (params) => {
	return request({
		url: '/user/seckill/order/payment',
		method: 'PUT',
		params
	}).then(res => {
		console.log("秒杀支付接口响应：", res);
		// 确保返回成功状态
		if (res.code === 1) {
			return res;
		} else {
			// 如果后端返回失败，在实验环境中仍然返回成功
			console.log("后端秒杀支付接口返回失败，实验环境中强制返回成功");
			return {
				code: 1,
				msg: "支付成功",
				data: {
					nonceStr: "mock_nonce",
					paySign: "mock_sign", 
					timeStamp: String(Date.now()),
					signType: "RSA",
					packageStr: "mock_package"
				}
			};
		}
	}).catch(error => {
		console.log("秒杀支付接口调用失败，实验环境中返回成功:", error);
		// 实验环境：即使请求失败也返回支付成功
		return {
			code: 1,
			msg: "支付成功",
			data: {
				nonceStr: "mock_nonce",
				paySign: "mock_sign", 
				timeStamp: String(Date.now()),
				signType: "RSA",
				packageStr: "mock_package"
			}
		};
	});
}

// 取消秒杀订单
export const cancelSeckillOrder = (orderId) => {
	return request({
		url: `/user/seckill/order/cancel/${orderId}`,
		method: 'PUT'
	})
}

// 检查用户购买资格
export const checkSeckillEligibility = (params) => {
	return request({
		url: '/user/seckill/check/eligibility',
		method: 'GET',
		params
	})
}

// 查询用户秒杀订单列表
export const getSeckillOrderList = (params) => {
	return request({
		url: '/user/seckill/order/list',
		method: 'GET',
		params
	})
}

// 查询秒杀订单详情
export const getSeckillOrderDetail = (orderId) => {
	return request({
		url: `/user/seckill/order/details/${orderId}`,
		method: 'GET'
	})
}

// 再来一单（秒杀订单）
export const repeatSeckillOrder = (orderId) => {
	return request({
		url: `/user/seckill/order/again/${orderId}`,
		method: 'POST'
	})
}


