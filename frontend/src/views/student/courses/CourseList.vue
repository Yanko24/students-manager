<template>
  <div class="page-container">
    <div class="page-heading"><div><h2>我的课程</h2><p>查看已确认选修的课程与授课信息</p></div></div>
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filters" @submit.prevent="search">
        <el-form-item label="课程名称"><el-input v-model="filters.courseName" clearable placeholder="输入课程名称" @keyup.enter="search" /></el-form-item>
        <el-form-item label="学期"><el-input v-model="filters.semester" clearable placeholder="如 2026-2027-1" @keyup.enter="search" /></el-form-item>
        <el-form-item><el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <el-table :data="courses" v-loading="loading" border>
        <el-table-column prop="code" label="课程代码" min-width="120" />
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="college" label="开课单位" min-width="180" />
        <el-table-column prop="credit" label="学分" width="90" />
        <el-table-column prop="hours" label="学时" width="90" />
        <el-table-column prop="type" label="课程类型" min-width="110" />
        <el-table-column prop="teacher" label="授课教师" min-width="120"><template #default="{ row }">{{ row.teacher || '暂未安排' }}</template></el-table-column>
        <el-table-column prop="semester" label="学期" min-width="130" />
        <el-table-column label="课程状态" width="110"><template #default="{ row }"><el-tag :type="courseStatusType(row.status)">{{ row.statusText }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="90" fixed="right"><template #default="{ row }"><el-button link type="primary" @click="router.push(`/student/courses/${row.id}`)">详情</el-button></template></el-table-column>
        <template #empty><el-empty description="暂无已确认的课程" /></template>
      </el-table>
      <div class="pagination"><el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchCourses" @current-change="fetchCourses" /></div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getStudentCourses } from '@/api/student'

const router = useRouter()
const filters = reactive({ courseName: '', semester: '' })
const courses = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const courseStatusType = (status) => ({ 0: 'info', 1: 'success', 2: 'warning' }[status] || 'info')

async function fetchCourses() {
  loading.value = true
  try {
    const { data } = await getStudentCourses({ page: page.value, size: size.value, courseName: filters.courseName.trim() || undefined, semester: filters.semester.trim() || undefined })
    courses.value = data?.records || []
    total.value = data?.total || 0
  } catch (error) {
    courses.value = []
    total.value = 0
    ElMessage.error(error?.response?.data?.message || '获取课程列表失败')
  } finally { loading.value = false }
}
function search() { page.value = 1; fetchCourses() }
function reset() { filters.courseName = ''; filters.semester = ''; search() }
onMounted(fetchCourses)
</script>

<style scoped>
.page-container { display: grid; gap: 18px; }
.page-heading { display: flex; justify-content: space-between; align-items: center; }
.page-heading h2 { margin: 0; font-size: 24px; }
.page-heading p { margin: 8px 0 0; color: var(--el-text-color-secondary); }
.filter-card :deep(.el-card__body) { padding-bottom: 2px; }
.table-card :deep(.el-card__body) { overflow-x: auto; }
.pagination { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
</style>
