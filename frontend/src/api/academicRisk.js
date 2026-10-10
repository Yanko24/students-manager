import request from '@/utils/request'

export function getAcademicRisks(params) {
  return request({ url: '/admin/academic-risks', method: 'get', params })
}
