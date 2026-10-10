const actionLabels = {
  SCORE_CREATE: '录入成绩',
  SCORE_UPDATE: '更正成绩',
  SCORE_DELETE: '删除成绩',
  SCORE_PUBLISH: '发布成绩',
  COURSE_SELECTION_APPROVE: '通过选课申请',
  COURSE_SELECTION_REJECT: '拒绝选课申请',
  STUDENT_STATUS_APPLY: '提交学籍异动申请',
  STUDENT_STATUS_APPROVE: '通过学籍异动申请',
  STUDENT_STATUS_REJECT: '拒绝学籍异动申请'
}

const entityLabels = {
  SCORE: '成绩',
  COURSE_SELECTION: '选课申请',
  STUDENT_STATUS_CHANGE: '学籍异动',
  STUDENT: '学生',
  TEACHER: '教师',
  COURSE: '课程',
  ATTENDANCE: '考勤',
  COLLEGE: '学院',
  MAJOR: '专业',
  USER: '用户'
}

const tokenLabels = {
  SCORE: '成绩', PUBLISH: '发布', CREATE: '新增', UPDATE: '修改', DELETE: '删除',
  COURSE: '课程', SELECTION: '选课', APPROVE: '通过', REJECT: '拒绝',
  STUDENT: '学生', STATUS: '学籍', CHANGE: '异动', APPLY: '申请',
  TEACHER: '教师', ATTENDANCE: '考勤', COLLEGE: '学院', MAJOR: '专业',
  USER: '用户', REVIEW: '审核', IMPORT: '导入', EXPORT: '导出'
}

function readableCode(value, dictionary) {
  if (!value) return '—'
  if (dictionary[value]) return dictionary[value]
  return String(value).split('_').map(token => tokenLabels[token] || token.toLowerCase()).join(' · ')
}

export const auditActionLabel = value => readableCode(value, actionLabels)
export const auditEntityLabel = value => readableCode(value, entityLabels)
