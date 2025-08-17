import request from '@/utils/request'

/**
 * 秒杀管理相关接口
 */

// 分页查询秒杀活动列表
export const getSeckillActivityPage = (params: any) => {
  return request({
    url: '/admin/seckill/activity/page',
    method: 'get',
    params
  })
}

// 新增秒杀活动
export const addSeckillActivity = (data: any) => {
  return request({
    url: '/admin/seckill/activity',
    method: 'post',
    data
  })
}

// 修改秒杀活动
export const updateSeckillActivity = (data: any) => {
  return request({
    url: '/admin/seckill/activity',
    method: 'put',
    data
  })
}

// 删除秒杀活动
export const deleteSeckillActivity = (params: any) => {
  return request({
    url: '/admin/seckill/activity',
    method: 'delete',
    params
  })
}

// 查询秒杀活动详情
export const getSeckillActivityById = (id: string) => {
  return request({
    url: `/admin/seckill/activity/${id}`,
    method: 'get'
  })
}

// 启用/停用秒杀活动
export const updateSeckillActivityStatus = (status: number, params: any) => {
  return request({
    url: `/admin/seckill/activity/status/${status}`,
    method: 'post',
    params
  })
}

// 查询可用商品列表（菜品和套餐）
export const getAvailableGoods = (params: any) => {
  return request({
    url: '/admin/seckill/goods/available',
    method: 'get',
    params
  })
}

// 修改秒杀商品信息
export const updateSeckillGoods = (data: any) => {
  return request({
    url: '/admin/seckill/goods',
    method: 'put',
    data
  })
}

// 删除秒杀商品
export const deleteSeckillGoods = (params: any) => {
  return request({
    url: '/admin/seckill/goods',
    method: 'delete',
    params
  })
}

// 查询秒杀订单列表
export const getSeckillOrderPage = (params: any) => {
  return request({
    url: '/admin/seckill/order/page',
    method: 'get',
    params
  })
}

// 查询秒杀订单详情
export const getSeckillOrderById = (id: string) => {
  return request({
    url: `/admin/seckill/order/details/${id}`,
    method: 'get'
  })
}

// 查询秒杀活动统计数据
export const getSeckillStatistics = (params: any) => {
  return request({
    url: '/admin/seckill/statistics',
    method: 'get',
    params
  })
}

