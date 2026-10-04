<template>
    <div class="teacher-view-container">
        <div class="page-header">
            <h2>教师详情</h2>
            <el-button @click="router.push('/admin/teachers')">返回列表</el-button>
        </div>
        <el-card class="info-card" v-loading="loading">
            <template v-if="teacher">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="姓名">{{ teacher.name }}</el-descriptions-item>
                    <el-descriptions-item label="工号">{{ teacher.teacherId }}</el-descriptions-item>
                    <el-descriptions-item label="性别">{{ teacher.gender }}</el-descriptions-item>
                    <el-descriptions-item label="手机号">{{ teacher.phone }}</el-descriptions-item>
                    <el-descriptions-item label="邮箱">{{ teacher.email }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="teacher.status === 'active' ? 'success' : 'danger'">
                            {{ teacher.status === 'active' ? '在职' : '离职' }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">{{ formatDateTime(teacher.createTime) }}</el-descriptions-item>
                    <el-descriptions-item label="更新时间">{{ formatDateTime(teacher.updateTime) }}</el-descriptions-item>
                </el-descriptions>
            </template>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { getTeacherById } from '@/api/teacher'
    import { formatDateTime } from '@/utils/dateUtils'

    const route = useRoute()
    const router = useRouter()
    const loading = ref(true)
    const teacher = ref(null)

    onMounted(async () => {
        try {
            const response = await getTeacherById(route.params.id)
            teacher.value = response.data
        } catch (error) {
            console.error('获取教师详情失败:', error)
        } finally {
            loading.value = false
        }
    })
</script>

<style scoped>
    .teacher-view-container {
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
</style>
