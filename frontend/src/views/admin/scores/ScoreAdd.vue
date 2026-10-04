<template>
    <div class="score-add-container">
        <div class="page-header">
            <h2>录入成绩</h2>
        </div>
        <el-card class="form-card">
            <score-form @submit="handleSubmit" @cancel="handleCancel" />
        </el-card>
    </div>
</template>

<script setup>
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { createScore } from '@/api/score'
    import ScoreForm from '@/components/score/ScoreForm.vue'

    const router = useRouter()

    const handleSubmit = async (formData) => {
        try {
            await createScore(formData)
            ElMessage.success('录入成功')
            router.push('/admin/scores')
        } catch (error) {
            console.error('录入失败：', error)
            ElMessage.error('录入失败')
        }
    }

    const handleCancel = () => {
        router.push('/admin/scores')
    }
</script>

<style scoped>
    .score-add-container {
        padding: 20px;
    }

    .page-header {
        margin-bottom: 20px;
    }

    .page-header h2 {
        margin: 0;
        font-size: 24px;
        color: #303133;
    }

    .form-card {
        max-width: 800px;
        margin: 0 auto;
    }

    @media screen and (max-width: 768px) {
        .score-add-container {
            padding: 10px;
        }
    }
</style>