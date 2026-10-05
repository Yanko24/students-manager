<template>
  <div class="course-list-page">
    <header class="page-header"><div><h2>我的课程</h2><p>按当前教师最近 100 条考勤记录汇总，仅显示已有考勤记录的课程。</p></div></header>
    <el-card class="filter-card">
      <el-form :inline="true" :model="filters" @submit.prevent="search">
        <el-form-item label="课程名称"><el-input v-model="filters.courseName" clearable placeholder="输入课程名称" @keyup.enter="search" /></el-form-item>
        <el-form-item><el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card class="list-card" v-loading="loading">
      <el-table :data="courseList" stripe>
        <el-table-column prop="courseName" label="课程名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="courseCode" label="课程代码" width="130" />
        <el-table-column prop="classCount" label="考勤班级数" width="120" align="center" />
        <el-table-column prop="attendanceCount" label="考勤记录数" width="120" align="center" />
        <el-table-column label="最近考勤日期" width="150"><template #default="{ row }">{{ formatDate(row.latestDate) || '—' }}</template></el-table-column>
        <el-table-column label="操作" width="120" fixed="right"><template #default="{ row }"><el-button type="primary" link @click="openAttendance(row)">查看考勤</el-button></template></el-table-column>
        <template #empty><el-empty :description="errorMessage || '暂无课程考勤记录'" /></template>
      </el-table>
      <div class="pagination-container"><el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="loadCourses" @current-change="loadCourses" /></div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTeacherAttendance } from '@/api/attendance'
import { formatDate } from '@/utils/dateUtils'

const router = useRouter()
const loading = ref(false)
const errorMessage = ref('')
const courseList = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const filters = reactive({ courseName: '' })

const loadCourses = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    const response = await getTeacherAttendance({ page: 1, size: 100, courseName: filters.courseName || undefined })
    if (response?.code !== 200) throw new Error(response?.message || '获取教师课程失败')
    const records = response.data?.records || []
    const grouped = new Map()
    records.forEach((record) => {
      const key = String(record.courseId || `${record.courseCode}-${record.courseName}`)
      const entry = grouped.get(key) || { id: key, courseId: record.courseId, courseCode: record.courseCode, courseName: record.courseName, classNames: new Set(), attendanceCount: 0, latestDate: '' }
      entry.attendanceCount += 1
      if (record.className) entry.classNames.add(record.className)
      if (record.date && (!entry.latestDate || String(record.date) > String(entry.latestDate))) entry.latestDate = record.date
      grouped.set(key, entry)
    })
    const allCourses = [...grouped.values()].map((course) => ({ ...course, classCount: course.classNames.size })).sort((a, b) => String(b.latestDate).localeCompare(String(a.latestDate)))
    total.value = allCourses.length
    const start = (page.value - 1) * size.value
    courseList.value = allCourses.slice(start, start + size.value)
  } catch (error) {
    courseList.value = []
    total.value = 0
    errorMessage.value = error?.message || '获取教师课程失败'
    ElMessage.error(errorMessage.value)
  } finally {
    loading.value = false
  }
}

const search = () => { page.value = 1; loadCourses() }
const reset = () => { filters.courseName = ''; search() }
const openAttendance = (course) => router.push({ path: '/teacher/attendance', query: { course: course.courseName } })
onMounted(loadCourses)
</script>

<style scoped lang="scss">
.page-header { margin-bottom: 18px; }.page-header h2 { margin: 0; font-size: 24px; }.page-header p { margin: 7px 0 0; color: var(--el-text-color-secondary); }
.filter-card { margin-bottom: 18px; }.filter-card :deep(.el-form-item) { margin-bottom: 0; }.list-card :deep(.el-card__body) { min-width: 0; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
</style>
