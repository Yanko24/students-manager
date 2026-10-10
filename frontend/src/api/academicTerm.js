import request from '@/utils/request'

export function getAcademicTerms() {
  return request({ url: '/admin/academic-terms', method: 'get' })
}

export function getAcademicTermOptions() {
  return request({ url: '/admin/academic-terms/options', method: 'get' })
}

export function getCurrentAcademicTerm() {
  return request({ url: '/admin/academic-terms/current', method: 'get' })
}

export function createAcademicTerm(data) {
  return request({ url: '/admin/academic-terms', method: 'post', data })
}

export function updateAcademicTerm(id, data) {
  return request({ url: `/admin/academic-terms/${id}`, method: 'put', data })
}

export function setCurrentAcademicTerm(id) {
  return request({ url: `/admin/academic-terms/${id}/current`, method: 'put' })
}

export function deleteAcademicTerm(id) {
  return request({ url: `/admin/academic-terms/${id}`, method: 'delete' })
}
