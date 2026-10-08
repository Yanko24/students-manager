<template>
  <div class="course-list-page">
    <header class="page-header">
      <div><h2>我的课程</h2><p>查看分配给你的授课课程、班级和上课安排。</p></div>
    </header>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filters" @submit.prevent="search">
        <el-form-item label="课程代码"><el-input v-model="filters.code" clearable placeholder="输入课程代码" @keyup.enter="search" /></el-form-item>
        <el-form-item label="课程名称"><el-input v-model="filters.name" clearable placeholder="输入课程名称" @keyup.enter="search" /></el-form-item>
        <el-form-item label="学期"><el-input v-model="filters.semester" clearable placeholder="例如 2026-2027-1" @keyup.enter="search" /></el-form-item>
        <el-form-item><el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card" v-loading="loading">
      <el-table :data="courseList" stripe>
        <el-table-column prop="code" label="课程代码" width="130" fixed="left" />
        <el-table-column prop="sectionCode" label="教学班" width="100" />
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="college" label="所属学院" min-width="170" />
        <el-table-column prop="semester" label="学期" width="150" />
        <el-table-column label="学分 / 学时" width="125" align="center"><template #default="{ row }">{{ row.credit ?? '—' }} / {{ row.hours ?? '—' }}</template></el-table-column>
        <el-table-column prop="type" label="课程类型" width="110" />
        <el-table-column label="选课人数" width="110" align="center"><template #default="{ row }">{{ row.selectedCount ?? 0 }} / {{ row.maxStudents ?? '—' }}</template></el-table-column>
        <el-table-column label="上课安排" min-width="180"><template #default="{ row }">{{ scheduleSummary(row.schedules) }}</template></el-table-column>
        <el-table-column label="课程状态" width="110"><template #default="{ row }"><el-tag :type="row.status === 2 ? 'info' : 'success'">{{ row.statusText || '未开课' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="130" fixed="right"><template #default="{ row }"><el-button type="primary" link @click="openAttendance(row)">查看考勤</el-button></template></el-table-column>
        <template #empty><el-empty :description="errorMessage || '暂无分配课程'" /></template>
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="loadCourses" @current-change="loadCourses" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMyTeachingCourses } from '@/api/teacher'
import { showApiError } from '@/utils/errorHandler'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const errorMessage = ref('')
const courseList = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const filters = reactive({ code: '', name: '', semester: '' })

const loadCourses = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    const response = await getMyTeachingCourses({ page: page.value, size: size.value, ...filters })
    if (response?.code !== 200) throw new Error(response?.message || '获取授课课程失败')
    courseList.value = response.data?.records || []
    total.value = Number(response.data?.total || 0)
  } catch (error) {
    courseList.value = []
    total.value = 0
    errorMessage.value = error?.message || '获取授课课程失败'
    showApiError(error, errorMessage.value)
  } finally {
    loading.value = false
  }
}

const search = () => { page.value = 1; loadCourses() }
const reset = () => { filters.code = ''; filters.name = ''; filters.semester = ''; search() }
const scheduleSummary = schedules => {
  if (!schedules?.length) return '暂未安排'
  return schedules.map(item => `周${['', '一', '二', '三', '四', '五', '六', '日'][item.dayOfWeek]} 第${item.startPeriod}-${item.endPeriod}节`).join('；')
}
const openAttendance = course => router.push({ path: '/teacher/attendance', query: { course: course.name, courseId: course.id } })

onMounted(() => {
  if (route.query.courseName) filters.name = String(route.query.courseName)
  loadCourses()
})
</script>

<style scoped lang="scss">
.page-header { margin-bottom: 18px; }.page-header h2 { margin: 0; font-size: 24px; }.page-header p { margin: 7px 0 0; color: var(--el-text-color-secondary); }
.filter-card { margin-bottom: 18px; }.filter-card :deep(.el-form-item) { margin-bottom: 0; }.list-card :deep(.el-card__body) { min-width: 0; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
</style>
