<template>
    <record-detail-page title="专业详情" back-path="/admin/majors" :loading="loading">
        <el-descriptions-item label="专业名称">{{ majorInfo.name || '—' }}</el-descriptions-item>
        <el-descriptions-item label="专业代码">{{ majorInfo.code || '—' }}</el-descriptions-item>
        <el-descriptions-item label="所属学院">{{ majorInfo.collegeName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ majorInfo.grade || '—' }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ majorInfo.classNo || '—' }}</el-descriptions-item>
        <el-descriptions-item label="显示名称" :span="2">{{ majorInfo.displayName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="班主任">{{ majorInfo.headTeacherName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="班主任工号">{{ majorInfo.headTeacherNumber || '—' }}</el-descriptions-item>
        <el-descriptions-item label="学生人数">{{ majorInfo.studentCount ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(majorInfo.status)">{{ getStatusText(majorInfo.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(majorInfo.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDateTime(majorInfo.updateTime) }}</el-descriptions-item>
    </record-detail-page>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getMajorById, getStatusType, getStatusText } from '@/api/major'
    import { formatDateTime } from '@/utils/dateUtils'
    import RecordDetailPage from '@/components/common/RecordDetailPage.vue'

    const route = useRoute()
    const loading = ref(false)
    const majorInfo = ref({})

    const fetchMajorInfo = async () => {
        loading.value = true
        try {
            const response = await getMajorById(route.params.id)
            majorInfo.value = response?.data || {}
        } catch (error) {
            console.error('获取专业信息失败:', error)
            ElMessage.error('获取专业信息失败')
        } finally {
            loading.value = false
        }
    }

    onMounted(() => {
        fetchMajorInfo()
    })
</script>
