<template>
  <div class="page-container">
    <div class="page-heading"><div><h2>课程详情</h2><p>课程内容与选课信息</p></div><el-button @click="router.push('/student/courses')">返回课程</el-button></div>
    <el-card v-loading="loading" shadow="never">
      <el-skeleton v-if="loading && !course" :rows="5" animated />
      <el-empty v-else-if="!course" description="课程信息不可用" />
      <template v-else>
        <div class="course-title"><div><span class="course-code">{{ course.code }}</span><h3>{{ course.name }}</h3></div><el-tag :type="statusType(course.status)">{{ course.statusText }}</el-tag></div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="开课单位">{{ course.college || '暂未填写' }}</el-descriptions-item>
          <el-descriptions-item label="授课教师">{{ course.teacher || '暂未安排' }}</el-descriptions-item>
          <el-descriptions-item label="课程类型">{{ course.type || '—' }}</el-descriptions-item>
          <el-descriptions-item label="开课学期">{{ course.semester || '—' }}</el-descriptions-item>
          <el-descriptions-item label="学分">{{ course.credit ?? '—' }}</el-descriptions-item>
          <el-descriptions-item label="学时">{{ course.hours ?? '—' }}</el-descriptions-item>
          <el-descriptions-item label="选课状态">{{ selectionStatus(course.selectionStatus) }}</el-descriptions-item>
          <el-descriptions-item label="选课时间">{{ formatDateTime(course.selectionDate) }}</el-descriptions-item>
        </el-descriptions>
        <section v-if="course.description" class="text-section"><h4>课程简介</h4><p>{{ course.description }}</p></section>
        <section v-if="course.objectives" class="text-section"><h4>教学目标</h4><p>{{ course.objectives }}</p></section>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyCourseById } from '@/api/student'
import { formatDateTime } from '@/utils/dateUtils'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const course = ref(null)
const statusType = (status) => ({ 0: 'info', 1: 'success', 2: 'warning' }[status] || 'info')
const selectionStatus = (status) => ({ approved: '已通过', pending: '审核中', rejected: '未通过' }[status] || '—')

onMounted(async () => {
  try {
    const response = await getMyCourseById(route.params.id)
    course.value = response?.data || null
  } catch (error) {
    showApiError(error, error?.response?.data?.message || '获取课程详情失败')
  } finally { loading.value = false }
})
</script>

<style scoped>
.page-container { display: grid; gap: 18px; }
.page-heading { display: flex; justify-content: space-between; align-items: center; }
.page-heading h2 { margin: 0; font-size: 24px; }
.page-heading p { margin: 8px 0 0; color: var(--el-text-color-secondary); }
.course-title { display: flex; justify-content: space-between; align-items: center; margin-bottom: 22px; }
.course-title h3 { margin: 6px 0 0; font-size: 21px; }
.course-code { color: var(--el-text-color-secondary); font-size: 13px; }
.text-section { margin-top: 24px; line-height: 1.8; }
.text-section h4 { margin: 0 0 8px; }
.text-section p { margin: 0; color: var(--el-text-color-regular); white-space: pre-wrap; }
</style>
