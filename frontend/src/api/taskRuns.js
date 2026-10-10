import request from '@/utils/request'

export function getScheduledTaskRuns(params) {
  return request({ url: '/admin/task-runs', method: 'get', params })
}
