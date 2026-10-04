<template>
    <div class="score-edit-container">
        <div class="page-header">
            <h2>编辑成绩</h2>
        </div>
        <el-card class="form-card">
            <score-form :id="id" :is-edit="true" @submit="handleSubmit" @cancel="handleCancel" />
        </el-card>
    </div>
</template>

<script setup>
    import { useRoute, useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { updateScore } from '@/api/score'
    import ScoreForm from '@/components/score/ScoreForm.vue'

    const route = useRoute()
    const router = useRouter()
    const id = route.params.id

    const handleSubmit = async (formData) => {
        try {
            await updateScore(id, formData)
            ElMessage.success('更新成功')
            router.push('/admin/scores')
        } catch (error) {
            console.error('更新失败：', error)
            ElMessage.error('更新失败')
        }
    }

    const handleCancel = () => {
        router.push('/admin/scores')
    }
</script>

<style scoped>
    .score-edit-container {
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
        .score-edit-container {
            padding: 10px;
        }
    }
</style>