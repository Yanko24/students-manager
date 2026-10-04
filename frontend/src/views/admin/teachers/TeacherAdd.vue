<template>
    <div class="teacher-add-container">
        <div class="page-header">
            <h2>添加教师</h2>
        </div>
        <el-card class="form-card">
            <teacher-form @submit="handleSubmit" @cancel="handleCancel" />
        </el-card>
    </div>
</template>

<script setup>
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { createTeacher } from '@/api/teacher'
    import TeacherForm from '@/components/teacher/TeacherForm.vue'

    const router = useRouter()

    const handleSubmit = async (formData) => {
        try {
            await createTeacher(formData)
            ElMessage.success('添加成功')
            router.push('/admin/teachers')
        } catch (error) {
            console.error('添加教师失败:', error)
            ElMessage.error('添加教师失败')
        }
    }

    const handleCancel = () => {
        router.push('/admin/teachers')
    }
</script>

<style scoped>
    .teacher-add-container {
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