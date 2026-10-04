<template>
  <div class="attendance-container">
    <el-card class="attendance-card">
      <template #header>
        <div class="card-header"><span>我的考勤</span></div>
      </template>

      <el-form :model="filters" inline class="filter-form" @submit.prevent="search">
        <el-form-item label="课程"><el-input v-model="filters.courseName" clearable placeholder="课程名称" @keyup.enter="search" /></el-form-item>
        <el-form-item label="学期"><el-input v-model="filters.semester" clearable placeholder="如 2026-2027-1" @keyup.enter="search" /></el-form-item>
        <el-form-item><el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item>
      </el-form>

      <el-table :data="attendanceList" v-loading="loading" border>
        <el-table-column prop="date" label="日期" width="130">
          <template #default="{ row }">{{ formatDate(row.date) }}</template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称" min-width="170" />
        <el-table-column prop="teacherName" label="授课教师" min-width="120" />
        <el-table-column prop="classPeriod" label="上课节次" width="120" />
        <el-table-column prop="status" label="考勤状态" width="110">
          <template #default="{ row }"><el-tag :type="statusType(row.status)">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <template #empty><el-empty description="暂无考勤记录" /></template>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="[10, 20, 50]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchList" @current-change="fetchList" />
      </div>

      <el-row :gutter="16" class="summary-info">
        <el-col :xs="12" :sm="6"><div class="info-item"><div class="label">考勤记录</div><div class="value">{{ stats.totalCount }}</div></div></el-col>
        <el-col :xs="12" :sm="6"><div class="info-item"><div class="label">出勤课时</div><div class="value">{{ attendedCount }}</div></div></el-col>
        <el-col :xs="12" :sm="6"><div class="info-item"><div class="label">迟到次数</div><div class="value">{{ stats.lateCount }}</div></div></el-col>
        <el-col :xs="12" :sm="6"><div class="info-item"><div class="label">缺勤次数</div><div class="value">{{ stats.absentCount }}</div></div></el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentAttendance, getStudentAttendanceStatistics } from '@/api/attendance'
import { formatDate } from '@/utils/dateUtils'

const filters = reactive({ courseName: '', semester: '' })
const attendanceList = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const stats = reactive({ totalCount: 0, presentCount: 0, lateCount: 0, earlyLeaveCount: 0, absentCount: 0, leaveCount: 0 })
const attendedCount = computed(() => Number(stats.presentCount) + Number(stats.lateCount) + Number(stats.earlyLeaveCount))

const statusType = (status) => ({ 正常: 'success', 迟到: 'warning', 早退: 'warning', 缺勤: 'danger', 请假: 'info' }[status] || 'info')

const queryParams = () => Object.fromEntries(Object.entries(filters).map(([key, value]) => [key, value.trim() || undefined]))

const fetchList = async () => {
  loading.value = true
  try {
    const response = await getStudentAttendance({ ...queryParams(), page: page.value, size: size.value })
    if (response?.code !== 200) throw new Error(response?.message || '查询考勤失败')
    attendanceList.value = response.data?.records || []
    total.value = response.data?.total || 0
  } catch (error) {
    console.error('查询学生考勤失败：', error)
    ElMessage.error(error?.message || '查询考勤失败')
  } finally {
    loading.value = false
  }
}

const fetchStats = async () => {
  try {
    const response = await getStudentAttendanceStatistics(queryParams())
    if (response?.code !== 200) throw new Error(response?.message || '读取考勤汇总失败')
    Object.assign(stats, response.data || {})
  } catch (error) {
    console.error('读取学生考勤汇总失败：', error)
  }
}

const search = () => {
  page.value = 1
  fetchList()
  fetchStats()
}

const reset = () => {
  filters.courseName = ''
  filters.semester = ''
  search()
}

onMounted(search)
</script>

<style scoped lang="scss">
.attendance-container { padding: 20px; }
.card-header { font-weight: 600; }
.filter-form { margin-bottom: 12px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
.summary-info { margin-top: 20px; padding-top: 20px; border-top: 1px solid var(--el-border-color-lighter); }
.info-item { margin-bottom: 12px; padding: 16px; text-align: center; background: var(--el-fill-color-light); border-radius: 8px; }
.label { margin-bottom: 8px; color: var(--el-text-color-secondary); }
.value { font-size: 22px; font-weight: 600; color: var(--el-text-color-primary); }
</style>
