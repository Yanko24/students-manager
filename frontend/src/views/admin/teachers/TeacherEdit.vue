<template>
    <div class="teacher-edit-container admin-record-page">
        <div class="page-header">
            <h2>编辑教师</h2>
            <div class="header-actions"><el-button @click="handleCancel">返回列表</el-button></div>
        </div>
        <el-card class="form-card record-card">
            <teacher-form ref="teacherFormRef" :id="id" :is-edit="true" @submit="handleSubmit" @cancel="handleCancel" />
        </el-card>
    </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
import { ref } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { updateTeacher } from '@/api/teacher'
    import TeacherForm from '@/components/teacher/TeacherForm.vue'

    const route = useRoute()
    const router = useRouter()
    const id = route.params.id
    const teacherFormRef = ref(null)

    const handleSubmit = async (formData) => {
        try {
            await updateTeacher(id, formData)
            ElMessage.success('更新成功')
            teacherFormRef.value?.markClean()
            router.push('/admin/teachers')
        } catch (error) {
            console.error('更新教师失败:', error)
            showApiError(error, '更新教师失败')
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
