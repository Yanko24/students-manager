<template>
  <div class="page-container">
    <div class="page-heading"><div><h2>成绩查询</h2><p>查询个人课程成绩与学业统计</p></div><el-button :disabled="!scores.length" @click="exportScores">导出当前页</el-button></div>
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filters" @submit.prevent="search">
        <el-form-item label="课程名称"><el-input v-model="filters.courseName" clearable placeholder="输入课程名称" @keyup.enter="search" /></el-form-item>
        <el-form-item label="学期"><el-input v-model="filters.semester" clearable placeholder="如 2026-2027-1" @keyup.enter="search" /></el-form-item>
        <el-form-item><el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-row :gutter="18">
      <el-col :xs="24" :lg="17">
        <el-card shadow="never">
          <template #header><span class="card-title">成绩记录</span></template>
          <el-table :data="scores" v-loading="loading" border>
            <el-table-column prop="courseCode" label="课程代码" min-width="110" />
            <el-table-column prop="courseName" label="课程名称" min-width="160" />
            <el-table-column prop="credit" label="学分" width="80" />
            <el-table-column prop="score" label="成绩" width="95"><template #default="{ row }"><strong :class="Number(row.score) >= 60 ? 'pass' : 'fail'">{{ row.score }}</strong></template></el-table-column>
            <el-table-column prop="grade" label="等级" width="90"><template #default="{ row }"><el-tag :type="gradeType(row.grade)">{{ row.grade || '—' }}</el-tag></template></el-table-column>
            <el-table-column prop="gradePoint" label="绩点" width="80" />
            <el-table-column prop="semester" label="学期" min-width="130" />
            <el-table-column prop="examTime" label="考试时间" min-width="170"><template #default="{ row }">{{ formatDateTime(row.examTime) }}</template></el-table-column>
            <el-table-column prop="teacher" label="授课教师" min-width="110"><template #default="{ row }">{{ row.teacher || '—' }}</template></el-table-column>
            <template #empty><el-empty description="暂无成绩记录" /></template>
          </el-table>
          <div class="pagination"><el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchScores" @current-change="fetchScores" /></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="7">
        <el-card shadow="never" v-loading="statsLoading">
          <template #header><span class="card-title">学业统计<span class="scope-label">{{ filters.semester || '全部学期' }}</span></span></template>
          <div class="stats-grid">
            <div class="stat"><span>已出成绩</span><strong>{{ stats.scoreCount }}</strong><small>门课程</small></div>
            <div class="stat"><span>平均分</span><strong>{{ decimal(stats.averageScore) }}</strong><small>百分制</small></div>
            <div class="stat"><span>及格率</span><strong>{{ decimal(stats.passRate) }}%</strong><small>成绩 ≥ 60</small></div>
            <div class="stat"><span>优秀率</span><strong>{{ decimal(stats.excellentRate) }}%</strong><small>成绩 ≥ 90</small></div>
            <div class="stat wide"><span>已获得学分</span><strong>{{ decimal(stats.earnedCredits) }}</strong><small>仅统计及格课程</small></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyScores, getMyScoreStatistics } from '@/api/student'
import { formatDateTime } from '@/utils/dateUtils'

const filters = reactive({ courseName: '', semester: '' })
const scores = ref([])
const stats = reactive({ scoreCount: 0, averageScore: 0, passRate: 0, excellentRate: 0, earnedCredits: 0 })
const loading = ref(false)
const statsLoading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const decimal = (value) => Number(value || 0).toFixed(1).replace(/\.0$/, '')
const gradeType = (grade) => (['A+', 'A', '优秀'].includes(grade) ? 'success' : ['F', '不及格'].includes(grade) ? 'danger' : 'info')

async function fetchScores() {
  loading.value = true
  try {
    const { data } = await getMyScores({ page: page.value, size: size.value, courseName: filters.courseName.trim() || undefined, semester: filters.semester.trim() || undefined })
    scores.value = data?.records || []
    total.value = data?.total || 0
  } catch (error) {
    scores.value = []; total.value = 0
    ElMessage.error(error?.message || '获取成绩列表失败')
  } finally { loading.value = false }
}
async function fetchStats() {
  statsLoading.value = true
  try {
    const { data } = await getMyScoreStatistics({ semester: filters.semester.trim() || undefined })
    Object.assign(stats, data || {})
  } catch (error) {
    Object.assign(stats, { scoreCount: 0, averageScore: 0, passRate: 0, excellentRate: 0, earnedCredits: 0 })
    ElMessage.error(error?.message || '获取成绩统计失败')
  } finally { statsLoading.value = false }
}
function search() { page.value = 1; fetchScores(); fetchStats() }
function reset() { filters.courseName = ''; filters.semester = ''; search() }
function exportScores() {
  const quote = (v) => `"${String(v ?? '').replaceAll('"', '""')}"`
  const rows = [['课程代码', '课程名称', '学分', '成绩', '等级', '绩点', '学期', '考试时间'], ...scores.value.map(s => [s.courseCode, s.courseName, s.credit, s.score, s.grade, s.gradePoint, s.semester, formatDateTime(s.examTime)])]
  const csv = '\ufeff' + rows.map(row => row.map(quote).join(',')).join('\r\n')
  const url = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }))
  const link = document.createElement('a'); link.href = url; link.download = `成绩记录-第${page.value}页.csv`; link.click(); URL.revokeObjectURL(url)
}
onMounted(search)
</script>

<style scoped>
.page-container { display: grid; gap: 18px; }
.page-heading { display: flex; justify-content: space-between; align-items: center; }
.page-heading h2 { margin: 0; font-size: 24px; }
.page-heading p { margin: 8px 0 0; color: var(--el-text-color-secondary); }
.card-title { font-weight: 600; }
.scope-label { margin-left: 8px; color: var(--el-text-color-secondary); font-size: 13px; font-weight: 400; }
.pass { color: var(--el-color-success); }.fail { color: var(--el-color-danger); }
.pagination { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
.stats-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.stat { display: flex; flex-direction: column; gap: 6px; padding: 16px; border-radius: 10px; background: var(--el-fill-color-light); }
.stat span,.stat small { color: var(--el-text-color-secondary); }.stat strong { color: var(--el-color-primary); font-size: 25px; }.stat small { font-size: 12px; }
.stat.wide { grid-column: span 2; }
@media (max-width: 768px) { .page-heading { align-items: flex-start; gap: 10px; }.page-heading p { max-width: 60vw; } }
</style>
