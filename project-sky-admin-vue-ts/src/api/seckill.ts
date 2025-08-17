import request from '@/utils/request'

/**
 * ��ɱ������ؽӿ�
 */

// ��ҳ��ѯ��ɱ��б�
export const getSeckillActivityPage = (params: any) => {
  return request({
    url: '/admin/seckill/activity/page',
    method: 'get',
    params
  })
}

// ������ɱ�
export const addSeckillActivity = (data: any) => {
  return request({
    url: '/admin/seckill/activity',
    method: 'post',
    data
  })
}

// �޸���ɱ�
export const updateSeckillActivity = (data: any) => {
  return request({
    url: '/admin/seckill/activity',
    method: 'put',
    data
  })
}

// ɾ����ɱ�
export const deleteSeckillActivity = (params: any) => {
  return request({
    url: '/admin/seckill/activity',
    method: 'delete',
    params
  })
}

// ��ѯ��ɱ�����
export const getSeckillActivityById = (id: string) => {
  return request({
    url: `/admin/seckill/activity/${id}`,
    method: 'get'
  })
}

// ����/ͣ����ɱ�
export const updateSeckillActivityStatus = (status: number, params: any) => {
  return request({
    url: `/admin/seckill/activity/status/${status}`,
    method: 'post',
    params
  })
}

// ��ѯ������Ʒ�б���Ʒ���ײͣ�
export const getAvailableGoods = (params: any) => {
  return request({
    url: '/admin/seckill/goods/available',
    method: 'get',
    params
  })
}

// �޸���ɱ��Ʒ��Ϣ
export const updateSeckillGoods = (data: any) => {
  return request({
    url: '/admin/seckill/goods',
    method: 'put',
    data
  })
}

// ɾ����ɱ��Ʒ
export const deleteSeckillGoods = (params: any) => {
  return request({
    url: '/admin/seckill/goods',
    method: 'delete',
    params
  })
}

// ��ѯ��ɱ�����б�
export const getSeckillOrderPage = (params: any) => {
  return request({
    url: '/admin/seckill/order/page',
    method: 'get',
    params
  })
}

// ��ѯ��ɱ��������
export const getSeckillOrderById = (id: string) => {
  return request({
    url: `/admin/seckill/order/details/${id}`,
    method: 'get'
  })
}

// ��ѯ��ɱ�ͳ������
export const getSeckillStatistics = (params: any) => {
  return request({
    url: '/admin/seckill/statistics',
    method: 'get',
    params
  })
}

