<template>
    <div class="major-edit admin-record-page">
        <div class="page-header">
            <h2>编辑专业</h2>
            <div class="header-actions"><el-button @click="handleCancel">返回列表</el-button></div>
        </div>
        <el-card class="record-card">
            <major-form :id="id" @submit="handleSubmit" @cancel="handleCancel" />
        </el-card>
    </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
    import { useRoute, useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { updateMajor } from '@/api/major'
    import MajorForm from '@/components/major/MajorForm.vue'

    const route = useRoute()
    const router = useRouter()
    const id = route.params.id

    const handleSubmit = async (formData) => {
        try {
            await updateMajor(id, formData)
            ElMessage.success('更新成功')
            router.push('/admin/majors')
        } catch (error) {
            console.error('更新专业失败:', error)
            showApiError(error, '更新专业失败')
        }
    }

    const handleCancel = () => {
        router.push('/admin/majors')
    }
</script>

<style scoped>
    .major-edit {
        padding: 20px;
    }
</style>
