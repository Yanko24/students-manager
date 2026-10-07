<template>
    <div class="major-add">
        <h2>添加专业</h2>
        <major-form @submit="handleSubmit" @cancel="handleCancel" />
    </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { createMajor } from '@/api/major'
    import MajorForm from '@/components/major/MajorForm.vue'

    const router = useRouter()

    const handleSubmit = async (formData) => {
        try {
            await createMajor(formData)
            ElMessage.success('添加成功')
            router.push('/admin/majors')
        } catch (error) {
            console.error('添加专业失败:', error)
            showApiError(error, '添加专业失败')
        }
    }

    const handleCancel = () => {
        router.push('/admin/majors')
    }
</script>

<style scoped>
    .major-add {
        padding: 20px;
    }
</style>