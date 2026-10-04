<template>
    <record-detail-page title="学院详情" back-path="/admin/colleges" :loading="loading">
                <el-descriptions-item label="学院代码">{{ college.code }}</el-descriptions-item>
                <el-descriptions-item label="学院名称">{{ college.name }}</el-descriptions-item>
                <el-descriptions-item label="学院简介">{{ college.description || '无' }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatDateTime(college.createTime) }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ formatDateTime(college.updateTime) }}</el-descriptions-item>
    </record-detail-page>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getCollegeById } from '@/api/college'
    import { formatDateTime } from '@/utils/dateUtils'
    import RecordDetailPage from '@/components/common/RecordDetailPage.vue'

    const route = useRoute()
    const loading = ref(true)

    const college = ref({
        code: '',
        name: '',
        description: '',
        createTime: '',
        updateTime: ''
    })

    const fetchCollege = async () => {
        loading.value = true
        try {
            const response = await getCollegeById(route.params.id)
            if (response && response.data) {
                college.value = response.data
            }
        } catch (error) {
            console.error('获取学院详情失败：', error)
            ElMessage.error('获取学院详情失败')
        } finally {
            loading.value = false
        }
    }

    onMounted(() => {
        fetchCollege()
    })
</script>

<style scoped>
    .college-view-container {
        padding: 20px;
        width: 100%;
        margin: 0 auto;
        box-sizing: border-box;
    }

    .page-header {
        margin-bottom: 20px;
    }

    .page-header h2 {
        margin: 0;
        font-size: 24px;
        color: #303133;
    }

    .detail-card {
        max-width: 800px;
        margin: 0 auto;
    }

    .action-buttons {
        margin-top: 20px;
        text-align: center;
    }

    @media screen and (max-width: 768px) {
        .college-view-container {
            padding: 10px;
        }
    }
</style>
