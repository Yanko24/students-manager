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
                    <el-descriptions-item label="所属学院">{{ course.college }}</el-descriptions-item>
                    <el-descriptions-item label="学分">{{ course.credit }}</el-descriptions-item>
                    <el-descriptions-item label="学时">{{ course.hours }}</el-descriptions-item>
                    <el-descriptions-item label="课程类型">{{ course.type }}</el-descriptions-item>
                    <el-descriptions-item label="授课教师">{{ course.teacher }}</el-descriptions-item>
                    <el-descriptions-item label="开课学期">{{ course.semester }}</el-descriptions-item>
                    <el-descriptions-item label="课程简介" :span="2">{{ course.description }}</el-descriptions-item>
                    <el-descriptions-item label="教学目标" :span="2">{{ course.objectives }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag
                            :type="course.status === '未开课' ? 'info' : course.status === '已开课' ? 'success' : 'danger'">
                            {{ course.status }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">{{ course.createTime }}</el-descriptions-item>
                    <el-descriptions-item label="更新时间">{{ course.updateTime }}</el-descriptions-item>
                </el-descriptions>

                <div class="section-title">学生列表</div>
                <el-table :data="studentList" border style="width: 100%">
                    <el-table-column prop="studentId" label="学号" width="120" />
                    <el-table-column prop="name" label="姓名" width="120" />
                    <el-table-column prop="majorName" label="专业" width="150" />
                    <el-table-column prop="class" label="班级" width="120" />
                    <el-table-column prop="status" label="状态" width="100">
                        <template #default="{ row }">
                            <el-tag :type="row.status === '在读' ? 'success' : 'danger'">
                                {{ row.status }}
                            </el-tag>
                        </template>
                    </el-table-column>
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
    const studentList = ref([])

    onMounted(async () => {
        try {
            const response = await getCourseById(route.params.id)
            if (response && response.data) {
                course.value = response.data
                studentList.value = response.data.students || []
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