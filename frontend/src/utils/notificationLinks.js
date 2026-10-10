const notificationRoutes = {
  ANNOUNCEMENT: { admin: '/admin/dashboard', teacher: '/teacher/dashboard', student: '/student/dashboard' },
  COURSE_ASSIGNED: { teacher: '/teacher/courses', admin: '/admin/courses' },
  COURSE_UPDATED: { teacher: '/teacher/courses', admin: '/admin/courses' },
  COURSE_ASSIGNMENT_CHANGED: { teacher: '/teacher/courses', admin: '/admin/courses' },
  COURSE_SELECTION_REVIEW: { student: '/student/courses', admin: '/admin/courses' },
  SCORE_SUBMITTED: { admin: '/admin/scores' },
  SCORE_PUBLISHED: { student: '/student/scores', admin: '/admin/scores' },
  STUDENT_STATUS_REVIEW: { student: '/student/status-changes', admin: '/admin/student-status-changes' },
  STUDENT_STATUS_PENDING: { admin: '/admin/student-status-changes' },
  STATUS_CHANGE_REVIEWED: { student: '/student/status-changes' },
  ANNOUNCEMENT_HOME: { admin: '/admin/dashboard', student: '/student/dashboard', teacher: '/teacher/dashboard' },
  ANNOUNCEMENT_COURSES: { admin: '/admin/courses', student: '/student/courses', teacher: '/teacher/courses' },
  ANNOUNCEMENT_SCORES: { admin: '/admin/scores', student: '/student/scores', teacher: '/teacher/dashboard' },
  ANNOUNCEMENT_ATTENDANCE: { admin: '/admin/attendance', student: '/student/attendance', teacher: '/teacher/attendance' },
  ANNOUNCEMENT_STATUS: { admin: '/admin/student-status-changes', student: '/student/status-changes', teacher: '/teacher/dashboard' },
}

export function getNotificationRoute(type, role) {
  return notificationRoutes[type]?.[role] || null
}
