<template>
    <div class="course-add-container">
        <div class="page-header">
            <h2>添加课程</h2>
        </div>
        <el-card class="form-card">
            <course-form ref="courseFormRef" @submit="handleSubmit" @cancel="handleCancel" />
        </el-card>
    </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { createCourse } from '@/api/course'
    import CourseForm from '@/components/course/CourseForm.vue'

    const router = useRouter()
    const courseFormRef = ref(null)

    const handleSubmit = async (formData) => {
        try {
            await createCourse(formData)
            ElMessage.success('添加成功')
            courseFormRef.value?.markClean()
            router.push('/admin/courses')
        } catch (error) {
            console.error('添加课程失败:', error)
            showApiError(error, '添加课程失败')
        }
    }

    const handleCancel = () => {
        router.push('/admin/courses')
    }
</script>

<style scoped>
    .course-add-container {
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
        .course-add-container {
            padding: 10px;
        }
    }
</style>
