import request from '@/utils/request'

export const getNotifications = params => request({ url: '/notifications', method: 'get', params })
export const getUnreadNotificationCount = () => request({ url: '/notifications/unread-count', method: 'get' })
export const markNotificationRead = id => request({ url: `/notifications/${id}/read`, method: 'put' })
export const markAllNotificationsRead = () => request({ url: '/notifications/read-all', method: 'put' })
export const publishAnnouncement = data => request({ url: '/notifications/admin/announcements', method: 'post', data })
export const getOperationAuditPage = params => request({ url: '/admin/operation-audits', method: 'get', params })
