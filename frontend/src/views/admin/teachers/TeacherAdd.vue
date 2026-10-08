<template>
    <div class="teacher-add-container">
        <div class="page-header">
            <h2>添加教师</h2>
        </div>
        <el-card class="form-card">
            <teacher-form ref="teacherFormRef" @submit="handleSubmit" @cancel="handleCancel" />
        </el-card>
    </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { createTeacher } from '@/api/teacher'
    import TeacherForm from '@/components/teacher/TeacherForm.vue'

    const router = useRouter()
    const teacherFormRef = ref(null)

    const handleSubmit = async (formData) => {
        try {
            await createTeacher(formData)
            ElMessage.success('添加成功')
            teacherFormRef.value?.markClean()
            router.push('/admin/teachers')
        } catch (error) {
            console.error('添加教师失败:', error)
            showApiError(error, '添加教师失败')
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
