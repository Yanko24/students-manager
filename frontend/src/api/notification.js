import request from '@/utils/request'

export const getNotifications = () => request({ url: '/notifications', method: 'get' })
export const getUnreadNotificationCount = () => request({ url: '/notifications/unread-count', method: 'get' })
export const markNotificationRead = id => request({ url: `/notifications/${id}/read`, method: 'put' })
export const getOperationAuditPage = params => request({ url: '/admin/operation-audits', method: 'get', params })
