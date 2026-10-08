const notificationRoutes = {
  COURSE_ASSIGNED: { teacher: '/teacher/courses', admin: '/admin/courses' },
  COURSE_UPDATED: { teacher: '/teacher/courses', admin: '/admin/courses' },
  COURSE_ASSIGNMENT_CHANGED: { teacher: '/teacher/courses', admin: '/admin/courses' },
  COURSE_SELECTION_REVIEW: { student: '/student/courses', admin: '/admin/courses' },
  SCORE_PUBLISHED: { student: '/student/scores', admin: '/admin/scores' },
  STUDENT_STATUS_REVIEW: { student: '/student/status-changes', admin: '/admin/student-status-changes' },
  ANNOUNCEMENT_HOME: { student: '/student/dashboard', teacher: '/teacher/dashboard' },
  ANNOUNCEMENT_COURSES: { student: '/student/courses', teacher: '/teacher/courses' },
  ANNOUNCEMENT_SCORES: { student: '/student/scores', teacher: '/teacher/dashboard' },
  ANNOUNCEMENT_ATTENDANCE: { student: '/student/attendance', teacher: '/teacher/attendance' },
  ANNOUNCEMENT_STATUS: { student: '/student/status-changes', teacher: '/teacher/dashboard' },
}

export function getNotificationRoute(type, role) {
  return notificationRoutes[type]?.[role] || null
}
