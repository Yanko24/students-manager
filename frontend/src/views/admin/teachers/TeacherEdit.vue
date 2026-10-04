<template>
    <div class="teacher-edit-container">
        <div class="page-header">
            <h2>编辑教师</h2>
        </div>
        <el-card class="form-card">
            <teacher-form :id="id" :is-edit="true" @submit="handleSubmit" @cancel="handleCancel" />
        </el-card>
    </div>
</template>

<script setup>
    import { useRoute, useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { updateTeacher } from '@/api/teacher'
    import TeacherForm from '@/components/teacher/TeacherForm.vue'

    const route = useRoute()
    const router = useRouter()
    const id = route.params.id

    const handleSubmit = async (formData) => {
        try {
            await updateTeacher(id, formData)
            ElMessage.success('更新成功')
            router.push('/admin/teachers')
        } catch (error) {
            console.error('更新教师失败:', error)
            ElMessage.error('更新教师失败')
        }
    }

    const handleCancel = () => {
        router.push('/admin/teachers')
    }
</script>

<style scoped>
    .teacher-edit-container {
        padding: 20px;
    }

    .page-header {
        margin-bottom: 20px;
    }

    .form-card {
        max-width: 800px;
        margin: 0 auto;
    }
</style>