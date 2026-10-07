<template>
    <record-detail-page title="教师详情" back-path="/admin/teachers" :loading="loading">
        <el-descriptions-item label="姓名">{{ teacher?.realName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="工号">{{ teacher?.teacherNo || teacher?.teacherNumber || '—' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ genderText(teacher?.gender) }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ teacher?.phone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ teacher?.email || '—' }}</el-descriptions-item>
        <el-descriptions-item label="所属院系">{{ teacher?.department || '—' }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ teacher?.title || '—' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(teacher?.status)">{{ getStatusText(teacher?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ formatDate(teacher?.hireDate) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(teacher?.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDateTime(teacher?.updateTime) }}</el-descriptions-item>
    </record-detail-page>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
    import { ref, onMounted } from 'vue'
    import { useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getTeacherById, getTeacherList, getStatusText, getStatusType } from '@/api/teacher'
    import { formatDate, formatDateTime } from '@/utils/dateUtils'
    import RecordDetailPage from '@/components/common/RecordDetailPage.vue'

    const route = useRoute()
    const loading = ref(true)
    const teacher = ref(null)

    const genderText = (gender) => gender === 1 ? '男' : gender === 0 ? '女' : '—'

    onMounted(async () => {
        try {
            const [detailResponse, listResponse] = await Promise.all([
                getTeacherById(route.params.id),
                getTeacherList({ page: 1, size: 1000 })
            ])
            const listRecord = (listResponse?.data?.records || []).find((item) => String(item.id) === String(route.params.id))
            teacher.value = { ...(detailResponse?.data || {}), ...(listRecord || {}) }
        } catch (error) {
            console.error('获取教师详情失败:', error)
            showApiError(error, '获取教师详情失败')
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
