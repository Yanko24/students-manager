<template>
    <div class="college-view-container">
        <div class="page-header">
            <h2>学院详情</h2>
        </div>

        <el-card class="detail-card">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="学院代码">{{ college.code }}</el-descriptions-item>
                <el-descriptions-item label="学院名称">{{ college.name }}</el-descriptions-item>
                <el-descriptions-item label="学院简介">{{ college.description || '无' }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatDateTime(college.createTime) }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ formatDateTime(college.updateTime) }}</el-descriptions-item>
            </el-descriptions>

            <div class="action-buttons">
                <el-button type="primary" @click="router.push(`/admin/colleges/${route.params.id}/edit`)">编辑</el-button>
                <el-button @click="router.back()">返回</el-button>
            </div>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getCollegeById } from '@/api/college'
    import { formatDateTime } from '@/utils/dateUtils'

    const router = useRouter()
    const route = useRoute()

    const college = ref({
        code: '',
        name: '',
        description: '',
        createTime: '',
        updateTime: ''
    })

    const fetchCollege = async () => {
        try {
            const response = await getCollegeById(route.params.id)
            if (response && response.data) {
                college.value = response.data
            }
        } catch (error) {
            console.error('获取学院详情失败：', error)
            ElMessage.error('获取学院详情失败')
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
