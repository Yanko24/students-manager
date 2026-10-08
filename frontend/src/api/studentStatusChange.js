import request from '@/utils/request'

export const getMyStatusChanges = () => request({ url: '/student-portal/status-changes', method: 'get' })
export const applyStatusChange = data => request({ url: '/student-portal/status-changes', method: 'post', data })
export const getStatusChangePage = params => request({ url: '/admin/student-status-changes', method: 'get', params })
export const reviewStatusChange = (id, data) => request({ url: `/admin/student-status-changes/${id}/review`, method: 'put', data })
