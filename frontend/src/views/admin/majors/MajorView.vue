<template>
    <div class="major-view">
        <div class="header">
            <h2>专业详情</h2>
            <el-button @click="handleBack">返回</el-button>
        </div>

        <el-descriptions :column="1" border>
            <el-descriptions-item label="专业名称">{{ majorInfo.majorName }}</el-descriptions-item>
            <el-descriptions-item label="专业代码">{{ majorInfo.majorCode }}</el-descriptions-item>
            <el-descriptions-item label="显示名称">{{ majorInfo.displayName }}</el-descriptions-item>
            <el-descriptions-item label="状态">
                <el-tag :type="getStatusType(majorInfo.status)">
                    {{ getStatusText(majorInfo.status) }}
                </el-tag>
            </el-descriptions-item>
        </el-descriptions>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getMajorById, getStatusType, getStatusText } from '@/api/major'

    const route = useRoute()
    const router = useRouter()
    const majorInfo = ref({
        majorName: '',
        majorCode: '',
        displayName: ''
    })

    const fetchMajorInfo = async () => {
        try {
            const response = await getMajorById(route.params.id)
            Object.assign(majorInfo.value, response.data)
        } catch (error) {
            console.error('获取专业信息失败:', error)
            ElMessage.error('获取专业信息失败')
        }
    }

    const handleBack = () => {
        router.push('/admin/majors')
    }

    onMounted(() => {
        fetchMajorInfo()
    })
</script>

<style scoped>
    .major-view {
        padding: 20px;
    }

    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    h2 {
        margin: 0;
    }
</style>