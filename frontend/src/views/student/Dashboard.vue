<template>
  <div class="dashboard" v-loading="loading">
    <section class="welcome-card">
      <div><span class="eyebrow">学生服务中心</span><h2>欢迎回来，{{ profile.realName || '同学' }}</h2><p>{{ todayLabel }} · 学业信息一目了然</p></div>
      <el-button type="primary" plain class="course-selection-button" @click="router.push('/student/course-selection')">进入选课中心</el-button>
      <el-avatar :size="68">{{ (profile.realName || '同').slice(0, 1) }}</el-avatar>
    </section>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="12" :sm="6"><el-card shadow="never" class="summary-card"><div class="summary-label">已选课程</div><div class="summary-value">{{ courseTotal }}<small> 门</small></div><el-link type="primary" :underline="false" @click="router.push('/student/courses')">查看课程 →</el-link></el-card></el-col>
      <el-col :xs="12" :sm="6"><el-card shadow="never" class="summary-card"><div class="summary-label">成绩平均分</div><div class="summary-value">{{ decimal(scoreStats.averageScore) }}<small> 分</small></div><el-link type="primary" :underline="false" @click="router.push('/student/scores')">查看成绩 →</el-link></el-card></el-col>
      <el-col :xs="12" :sm="6"><el-card shadow="never" class="summary-card"><div class="summary-label">考勤出勤率</div><div class="summary-value">{{ decimal(attendanceStats.attendanceRate) }}<small>%</small></div><el-link type="primary" :underline="false" @click="router.push('/student/attendance')">查看考勤 →</el-link></el-card></el-col>
      <el-col :xs="12" :sm="6"><el-card shadow="never" class="summary-card"><div class="summary-label">培养方案完成度</div><div class="summary-value">{{ curriculum.planConfigured ? decimal(curriculum.completionRate) : '—' }}<small>{{ curriculum.planConfigured ? '%' : '' }}</small></div><el-link type="primary" :underline="false" @click="router.push('/student/curriculum-progress')">查看培养进度 →</el-link></el-card></el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="list-card">
          <template #header><div class="card-heading"><div><strong>我的课程</strong><span>已通过选课</span></div><el-button link type="primary" @click="router.push('/student/courses')">全部课程</el-button></div></template>
          <div v-for="course in courses" :key="course.id" class="list-row" @click="router.push(`/student/courses/${course.id}`)"><div class="row-main"><strong>{{ course.name }}</strong><span>{{ course.code }} · {{ course.semester }}</span></div><el-tag size="small" :type="course.status === 1 ? 'success' : 'info'">{{ course.statusText }}</el-tag></div>
          <el-empty v-if="!courses.length" description="暂无已确认课程" :image-size="72" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="list-card">
          <template #header><div class="card-heading"><div><strong>最近成绩</strong><span>最新公布的课程成绩</span></div><el-button link type="primary" @click="router.push('/student/scores')">全部成绩</el-button></div></template>
          <div v-for="score in scores" :key="score.id" class="list-row"><div class="row-main"><strong>{{ score.courseName }}</strong><span>{{ score.semester }} · {{ formatDateTime(score.examTime) }}</span></div><strong class="score" :class="Number(score.score) >= 60 ? 'pass' : 'fail'">{{ score.score ?? '—' }}</strong></div>
          <el-empty v-if="!scores.length" description="暂无成绩记录" :image-size="72" />
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never" class="attendance-card">
      <template #header><div class="card-heading"><div><strong>最近考勤</strong><span>个人考勤记录</span></div><el-button link type="primary" @click="router.push('/student/attendance')">全部考勤</el-button></div></template>
      <div class="attendance-list">
        <div v-for="record in attendance" :key="record.id" class="list-row"><div class="row-main"><strong>{{ record.courseName }}</strong><span>{{ formatDate(record.date) }} · {{ record.classPeriod || '节次未注明' }}</span></div><el-tag size="small" :type="attendanceType(record.status)">{{ record.status }}</el-tag></div>
      </div>
      <el-empty v-if="!attendance.length" description="暂无考勤记录" :image-size="72" />
    </el-card>
    <el-card shadow="never" class="notification-card">
      <template #header><div class="card-heading"><div><strong>近期通知</strong><span>选课审核、成绩发布和教务消息</span></div><el-button link type="primary" @click="router.push('/student/notifications')">全部通知</el-button></div></template>
      <button v-for="item in notifications" :key="item.id" class="notification-row" type="button" @click="openNotification(item)">
        <span class="notification-dot" :class="{ unread: !item.readAt }"></span>
        <span class="row-main"><strong>{{ item.title }}</strong><span>{{ item.message }}</span></span>
        <time>{{ formatDateTime(item.createTime) }}</time>
      </button>
      <el-empty v-if="!notifications.length" description="暂无通知" :image-size="60" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getCurrentUser } from '@/api/auth'
import { getMyScoreStatistics, getMyScores, getStudentCourses } from '@/api/student'
import { getStudentAttendance, getStudentAttendanceStatistics } from '@/api/attendance'
import { getStudentCurriculumProgress } from '@/api/curriculum'
import { getNotifications, markNotificationRead } from '@/api/notification'
import { getNotificationRoute } from '@/utils/notificationLinks'
import { formatDate, formatDateTime } from '@/utils/dateUtils'
import { isAuthSessionExpiredError } from '@/utils/errorHandler'

const router = useRouter()
const loading = ref(false)
const profile = ref({})
const courses = ref([])
const courseTotal = ref(0)
const scores = ref([])
const attendance = ref([])
const scoreStats = ref({ averageScore: 0 })
const attendanceStats = ref({ attendanceRate: 0 })
const curriculum = ref({ planConfigured: false, completionRate: 0 })
const notifications = ref([])
const todayLabel = new Intl.DateTimeFormat('zh-CN', { dateStyle: 'full' }).format(new Date())
const decimal = value => Number(value || 0).toFixed(1).replace(/\.0$/, '')
const attendanceType = status => ({ 正常: 'success', 迟到: 'warning', 早退: 'warning', 请假: 'info', 缺勤: 'danger' }[status] || 'info')

async function loadDashboard() {
  loading.value = true
  const results = await Promise.allSettled([
    getCurrentUser(),
    getStudentCourses({ page: 1, size: 5 }),
    getMyScores({ page: 1, size: 5 }),
    getMyScoreStatistics(),
    getStudentAttendance({ page: 1, size: 5 }),
    getStudentAttendanceStatistics(),
    getStudentCurriculumProgress(),
    getNotifications({ page: 1, size: 5 })
  ])
  const [user, courseResult, scoreResult, scoreStatsResult, attendanceResult, attendanceStatsResult, curriculumResult, notificationResult] = results
  if (user.status === 'fulfilled') profile.value = user.value?.data || {}
  if (courseResult.status === 'fulfilled') { courses.value = courseResult.value?.data?.records || []; courseTotal.value = courseResult.value?.data?.total || 0 }
  if (scoreResult.status === 'fulfilled') scores.value = scoreResult.value?.data?.records || []
  if (scoreStatsResult.status === 'fulfilled') scoreStats.value = scoreStatsResult.value?.data || { averageScore: 0 }
  if (attendanceResult.status === 'fulfilled') attendance.value = attendanceResult.value?.data?.records || []
  if (attendanceStatsResult.status === 'fulfilled') attendanceStats.value = attendanceStatsResult.value?.data || { attendanceRate: 0 }
  if (curriculumResult.status === 'fulfilled') curriculum.value = curriculumResult.value?.data || { planConfigured: false, completionRate: 0 }
  if (notificationResult.status === 'fulfilled') notifications.value = notificationResult.value?.data?.records || []
  const sessionExpired = results.some(item => item.status === 'rejected' && isAuthSessionExpiredError(item.reason))
  if (!sessionExpired && results.some(item => item.status === 'rejected')) {
    ElMessage.warning('部分首页数据暂时无法加载，可进入相应页面重试')
  }
  loading.value = false
}

async function openNotification(item) {
  try {
    if (!item.readAt) {
      await markNotificationRead(item.id)
      item.readAt = new Date().toISOString()
      window.dispatchEvent(new Event('notifications:updated'))
    }
  } catch {
    // The full notification page can still be opened if marking it read fails.
  }
  router.push(getNotificationRoute(item.notificationType, 'student') || '/student/notifications')
}
onMounted(loadDashboard)
</script>

<style scoped>
.dashboard { display: grid; gap: 18px; }
.welcome-card { display: flex; justify-content: space-between; align-items: center; padding: 28px 32px; color: #fff; border-radius: 12px; background: linear-gradient(120deg, #3478f6, #55a6f8); }
.course-selection-button { flex-shrink: 0; }
.welcome-card .eyebrow { font-size: 13px; opacity: .82; }.welcome-card h2 { margin: 8px 0; font-size: 25px; }.welcome-card p { margin: 0; opacity: .85; }
.summary-row,.content-row { row-gap: 16px; }.summary-card { height: 100%; }.summary-label { color: var(--el-text-color-secondary); }.summary-value { margin: 10px 0; font-size: 30px; font-weight: 700; color: var(--el-text-color-primary); }.summary-value small { font-size: 14px; font-weight: 400; color: var(--el-text-color-secondary); }
.card-heading { display: flex; justify-content: space-between; align-items: center; }.card-heading div { display: grid; gap: 5px; }.card-heading span { color: var(--el-text-color-secondary); font-size: 12px; }
.list-card { height: 100%; }.list-row { display: flex; justify-content: space-between; align-items: center; min-height: 62px; gap: 16px; border-bottom: 1px solid var(--el-border-color-lighter); }.list-row:last-child { border-bottom: 0; }.row-main { display: grid; gap: 6px; min-width: 0; }.row-main strong { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }.row-main span { color: var(--el-text-color-secondary); font-size: 13px; }.list-card .list-row { cursor: pointer; }.list-card .list-row:hover .row-main strong { color: var(--el-color-primary); }.score { font-size: 20px; }.pass { color: var(--el-color-success); }.fail { color: var(--el-color-danger); }
.notification-row { display: flex; align-items: center; width: 100%; gap: 12px; padding: 13px 0; border: 0; border-bottom: 1px solid var(--el-border-color-lighter); background: transparent; text-align: left; cursor: pointer; }.notification-row:last-of-type { border-bottom: 0; }.notification-dot { width: 8px; height: 8px; flex: 0 0 auto; border-radius: 50%; background: transparent; }.notification-dot.unread { background: var(--el-color-primary); }.notification-row .row-main { flex: 1; }.notification-row .row-main span { white-space: normal; overflow-wrap: anywhere; }.notification-row time { flex: 0 0 auto; color: var(--el-text-color-secondary); font-size: 12px; white-space: nowrap; }
@media (max-width: 640px) { .welcome-card { flex-wrap: wrap; gap: 16px; padding: 22px; }.course-selection-button { order: 3; width: 100%; } }
</style>
