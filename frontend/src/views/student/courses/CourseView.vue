<template>
    <div class="course-view-container">
        <div class="page-header">
            <h2>课程详情</h2>
            <el-button @click="router.push('/student/courses')">返回列表</el-button>
        </div>
        <el-card class="info-card" v-loading="loading">
            <template v-if="course">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="课程名称">{{ course.name }}</el-descriptions-item>
                    <el-descriptions-item label="课程代码">{{ course.code }}</el-descriptions-item>
                    <el-descriptions-item label="学分">{{ course.credits }}</el-descriptions-item>
                    <el-descriptions-item label="学时">{{ course.hours }}</el-descriptions-item>
                    <el-descriptions-item label="课程类型">{{ course.type }}</el-descriptions-item>
                    <el-descriptions-item label="开课学期">{{ course.semester }}</el-descriptions-item>
                    <el-descriptions-item label="授课教师">{{ course.teacherName }}</el-descriptions-item>
                    <el-descriptions-item label="教师邮箱">{{ course.teacherEmail }}</el-descriptions-item>
                    <el-descriptions-item label="课程描述">{{ course.description }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="course.status === 'active' ? 'success' : 'danger'">
                            {{ course.status === 'active' ? '进行中' : '已结束' }}
                        </el-tag>
                    </el-descriptions-item>
                </el-descriptions>

                <div class="section-title">课程安排</div>
                <el-table :data="scheduleList" border style="width: 100%">
                    <el-table-column prop="weekday" label="星期" width="100" />
                    <el-table-column prop="startTime" label="开始时间" width="120" />
                    <el-table-column prop="endTime" label="结束时间" width="120" />
                    <el-table-column prop="location" label="上课地点" />
                </el-table>
            </template>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { getCourseById } from '@/api/course'

    const route = useRoute()
    const router = useRouter()
    const loading = ref(true)
    const course = ref(null)
    const scheduleList = ref([])

    onMounted(async () => {
        try {
            const response = await getCourseById(route.params.id)
            if (response && response.data) {
                course.value = response.data
                scheduleList.value = response.data.schedules || []
            }
        } catch (error) {
            console.error('获取课程详情失败:', error)
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

    .section-title {
        margin: 20px 0 10px;
        font-size: 16px;
        font-weight: bold;
        color: #303133;
    }
</style>