<template>
    <div class="course-view-container">
        <div class="page-header">
            <h2>课程详情</h2>
            <el-button @click="router.push('/admin/courses')">返回列表</el-button>
        </div>
        <el-card class="info-card" v-loading="loading">
            <template v-if="course">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="课程名称">{{ course.name }}</el-descriptions-item>
                    <el-descriptions-item label="课程代码">{{ course.code }}</el-descriptions-item>
                    <el-descriptions-item label="授课院系">{{ course.college }}</el-descriptions-item>
                    <el-descriptions-item label="学分">{{ course.credit }}</el-descriptions-item>
                    <el-descriptions-item label="学时">{{ course.hours }}</el-descriptions-item>
                    <el-descriptions-item label="课程类型">{{ course.type }}</el-descriptions-item>
                    <el-descriptions-item label="授课教师">{{ course.teacher || '未指定' }}</el-descriptions-item>
                    <el-descriptions-item label="开课学期">{{ course.semester }}</el-descriptions-item>
                    <el-descriptions-item label="课程简介" :span="2">{{ course.description }}</el-descriptions-item>
                    <el-descriptions-item label="教学目标" :span="2">{{ course.objectives }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="course.status === 0 ? 'info' : course.status === 1 ? 'success' : 'warning'">
                            {{ course.statusText }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">{{ formatDateTime(course.createTime) }}</el-descriptions-item>
                    <el-descriptions-item label="更新时间">{{ formatDateTime(course.updateTime) }}</el-descriptions-item>
                </el-descriptions>

            </template>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { getCourseById } from '@/api/course'
    import { formatDateTime } from '@/utils/dateUtils'

    const route = useRoute()
    const router = useRouter()
    const loading = ref(true)
    const course = ref(null)

    onMounted(async () => {
        try {
            const response = await getCourseById(route.params.id)
            if (response && response.data) {
                course.value = response.data
            }
        } catch (error) {
            console.error('获取课程详情失败:', error)
        } finally {
            loading.value = false
        }
    })
</script>

<style lang="scss" scoped>
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

    .section-title {
        margin: 20px 0 10px;
        font-size: 16px;
        font-weight: bold;
        color: #303133;
    }
</style>
