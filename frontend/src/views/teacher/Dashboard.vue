<template>

  <div class="teacher-dashboard">
    <header class="page-header">
      <div>
        <h2>教师工作台</h2>
        <p>查看授课考勤概况，快速进入日常工作。</p>
      </div>
      <el-button type="primary" @click="router.push('/teacher/attendance')">查看考勤</el-button>
    </header>

    <el-row :gutter="16" class="summary-grid">
      <el-col v-for="item in summaryCards" :key="item.label" :xs="12" :sm="12" :md="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-icon" :class="item.tone"><el-icon><component :is="item.icon" /></el-icon></div>
          <div class="summary-copy"><span>{{ item.label }}</span><strong>{{ item.value }}</strong></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-grid">
      <el-col :xs="24" :lg="16">
        <el-card class="content-card" v-loading="loading">
          <template #header>
            <div class="card-heading"><div><h3>近期考勤</h3><span>当前教师负责课程的最近记录</span></div>
              <el-button link type="primary" @click="router.push('/teacher/attendance')">全部记录<el-icon><ArrowRight /></el-icon></el-button>
            </div>
          </template>
          <el-table :data="recentRecords" stripe>
            <el-table-column prop="date" label="日期" width="120"><template #default="{ row }">{{ formatDate(row.date) }}</template></el-table-column>
            <el-table-column prop="courseName" label="课程" min-width="150" show-overflow-tooltip />
            <el-table-column prop="className" label="班级" min-width="180" show-overflow-tooltip />
            <el-table-column prop="studentName" label="学生" width="100" />
            <el-table-column prop="status" label="考勤状态" width="110"><template #default="{ row }"><el-tag :type="statusType(row.status)" effect="light">{{ row.status }}</el-tag></template></el-table-column>
            <template #empty><el-empty description="暂无考勤记录" /></template>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card class="content-card shortcuts-card">
          <template #header><div class="card-heading"><div><h3>常用功能</h3><span>快速进入教师工作区</span></div></div></template>
          <button class="shortcut" type="button" @click="router.push('/teacher/courses')">
            <span class="shortcut-icon blue"><el-icon><Reading /></el-icon></span><span><strong>我的课程</strong><small>查看有考勤记录的授课课程</small></span><el-icon class="shortcut-arrow"><ArrowRight /></el-icon>
          </button>
          <button class="shortcut" type="button" @click="router.push('/teacher/attendance')">
            <span class="shortcut-icon green"><el-icon><Calendar /></el-icon></span><span><strong>课程考勤</strong><small>筛选并查看学生考勤</small></span><el-icon class="shortcut-arrow"><ArrowRight /></el-icon>
          </button>
          <button class="shortcut" type="button" @click="router.push('/teacher/profile')">
            <span class="shortcut-icon orange"><el-icon><User /></el-icon></span><span><strong>个人信息</strong><small>查看账号并修改登录密码</small></span><el-icon class="shortcut-arrow"><ArrowRight /></el-icon>
          </button>
        </el-card>
        <el-alert v-if="errorMessage" class="load-error" type="error" :title="errorMessage" show-icon :closable="false" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Calendar, Reading, User, Tickets, WarningFilled, Collection } from '@element-plus/icons-vue'
import { getTeacherAttendance } from '@/api/attendance'
import { formatDate } from '@/utils/dateUtils'
import { isAuthSessionExpiredError } from '@/utils/errorHandler'

const router = useRouter()
const loading = ref(false)
const errorMessage = ref('')
const recentRecords = ref([])
const total = ref(0)
const anomalies = ref(0)
const courses = computed(() => new Set(recentRecords.value.map((record) => record.courseId).filter(Boolean)).size)
const summaryCards = computed(() => [
  { label: '考勤记录', value: total.value, icon: Tickets, tone: 'blue' },
  { label: '异常记录（近100条）', value: anomalies.value, icon: WarningFilled, tone: 'orange' },
  { label: '近期涉及课程', value: courses.value, icon: Collection, tone: 'green' },
  { label: '最近展示', value: recentRecords.value.length, icon: Calendar, tone: 'purple' },
])
const statusType = (status) => ({ 正常: 'success', 迟到: 'warning', 早退: 'warning', 缺勤: 'danger', 请假: 'info' }[status] || 'info')

const loadDashboard = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    const response = await getTeacherAttendance({ page: 1, size: 100 })
    if (response?.code !== 200) throw new Error(response?.message || '获取教师考勤数据失败')
    const records = response.data?.records || []
    recentRecords.value = records.slice(0, 10)
    total.value = Number(response.data?.total || 0)
    anomalies.value = records.filter((record) => ['迟到', '早退', '缺勤'].includes(record.status)).length
  } catch (error) {
    if (!isAuthSessionExpiredError(error)) {
      errorMessage.value = error?.message || '获取教师工作台数据失败'
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadDashboard)
</script>

<style scoped lang="scss">
.teacher-dashboard { display: flex; flex-direction: column; gap: 18px; }
.page-header { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.page-header h2 { margin: 0; font-size: 24px; color: var(--el-text-color-primary); }
.page-header p { margin: 7px 0 0; color: var(--el-text-color-secondary); }
.summary-grid { row-gap: 16px; }
.summary-card :deep(.el-card__body) { display: flex; align-items: center; gap: 14px; min-height: 78px; }
.summary-icon,.shortcut-icon { display: grid; place-items: center; flex: 0 0 auto; border-radius: 12px; }
.summary-icon { width: 46px; height: 46px; font-size: 21px; }
.blue { color: #3478f6; background: #edf4ff; }.green { color: #20a779; background: #eaf8f2; }.orange { color: #e89324; background: #fff5e8; }.purple { color: #805ad5; background: #f3edff; }
.summary-copy { display: flex; flex-direction: column; gap: 5px; color: var(--el-text-color-secondary); font-size: 13px; }
.summary-copy strong { color: var(--el-text-color-primary); font-size: 25px; line-height: 1.1; }
.content-grid { row-gap: 16px; }
.content-card { height: 100%; }
.card-heading { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.card-heading h3 { margin: 0; color: var(--el-text-color-primary); font-size: 16px; }
.card-heading span { display: block; margin-top: 5px; color: var(--el-text-color-secondary); font-size: 12px; }
.shortcut { display: flex; align-items: center; width: 100%; padding: 14px 4px; border: 0; border-bottom: 1px solid var(--el-border-color-lighter); background: transparent; text-align: left; cursor: pointer; }
.shortcut:last-child { border-bottom: 0; }.shortcut-icon { width: 40px; height: 40px; margin-right: 12px; font-size: 18px; }
.shortcut > span:nth-child(2) { display: flex; flex: 1; flex-direction: column; gap: 5px; }.shortcut strong { color: var(--el-text-color-primary); font-size: 14px; }.shortcut small { color: var(--el-text-color-secondary); font-size: 12px; }
.shortcut-arrow { color: var(--el-text-color-placeholder); }.shortcut:hover strong,.shortcut:hover .shortcut-arrow { color: var(--el-color-primary); }
.load-error { margin-top: 16px; }
@media (max-width: 640px) { .page-header { align-items: flex-start; flex-direction: column; }.summary-card :deep(.el-card__body) { padding: 14px; gap: 10px; }.summary-icon { width: 38px; height: 38px; }.summary-copy strong { font-size: 21px; } }
</style>
