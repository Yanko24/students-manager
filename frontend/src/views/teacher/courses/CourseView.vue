<template>
    <div class="course-view-container">
        <div class="page-header">
            <h2>课程详情</h2>
            <el-button @click="router.push('/teacher/courses')">返回列表</el-button>
        </div>
        <el-card class="info-card" v-loading="loading">
            <template v-if="course">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="课程名称">{{ course.name }}</el-descriptions-item>
                    <el-descriptions-item label="课程代码">{{ course.code }}</el-descriptions-item>
                    <el-descriptions-item label="学分">{{ course.credit }}</el-descriptions-item>
                    <el-descriptions-item label="学时">{{ course.hours }}</el-descriptions-item>
                    <el-descriptions-item label="课程类型">{{ course.type }}</el-descriptions-item>
                    <el-descriptions-item label="开课学期">{{ course.semester }}</el-descriptions-item>
                    <el-descriptions-item label="课程描述">{{ course.description }}</el-descriptions-item>
                    <el-descriptions-item label="所属学院">{{ course.college || '—' }}</el-descriptions-item>
                    <el-descriptions-item label="授课教师">{{ course.teacher || '—' }}</el-descriptions-item>
                    <el-descriptions-item label="状态"><el-tag :type="courseStatusType">{{ course.statusText || '—' }}</el-tag></el-descriptions-item>
                    <el-descriptions-item label="创建时间">{{ formatDateTime(course.createTime) }}</el-descriptions-item>
                    <el-descriptions-item label="更新时间">{{ formatDateTime(course.updateTime) }}</el-descriptions-item>
                    <el-descriptions-item label="课程目标" :span="2">{{ course.objectives || '—' }}</el-descriptions-item>
                </el-descriptions>
                <div v-if="course.description" class="course-description"><h3>课程简介</h3><p>{{ course.description }}</p></div>
                <el-empty v-else class="not-available" description="暂无课程简介" />
            </template>
            <el-empty v-else-if="!loading" description="课程信息暂不可用" />
        </el-card>
    </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
    import { ref, computed, onMounted } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getCourseById } from '@/api/course'
    import { formatDateTime } from '@/utils/dateUtils'

    const route = useRoute()
    const router = useRouter()
    const loading = ref(true)
    const course = ref(null)
    const courseStatusType = computed(() => ({ 0: 'info', 1: 'success', 2: 'warning' }[course.value?.status] || 'info'))

    onMounted(async () => {
        try {
            const response = await getCourseById(route.params.id)
            if (response?.code === 200 && response.data) {
                course.value = response.data
            }
        } catch (error) {
            console.error('获取课程详情失败:', error)
            showApiError(error, error?.message || '获取课程详情失败')
        } finally {
            loading.value = false
        }
    })
</script>

<style scoped>
    .course-view-container {
        padding: 20px;
    }

    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    .info-card {
        max-width: 800px;
        margin: 0 auto;
    }

        .course-description { margin-top: 22px; color: var(--el-text-color-regular); line-height: 1.7; }
        .course-description h3 { margin: 0 0 8px; color: var(--el-text-color-primary); font-size: 16px; }
        .course-description p { margin: 0; white-space: pre-wrap; }
        .not-available { padding: 8px 0; }
</style>
