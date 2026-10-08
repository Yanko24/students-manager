<template>
    <div class="student-add-container">
        <div class="page-header">
            <h2>新增学生</h2>
            <el-button @click="router.back()">
                <el-icon>
                    <Back />
                </el-icon>
                返回
            </el-button>
        </div>

        <el-card class="add-card" v-loading="loading">
            <StudentForm ref="studentFormRef" :is-edit="false" @submit="handleSubmit" />
        </el-card>
    </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { Back } from '@element-plus/icons-vue'
    import { ElMessage } from 'element-plus'
    import { createStudent } from '@/api/student'
    import StudentForm from '@/components/student/StudentForm.vue'

    const router = useRouter()
    const loading = ref(false)
    const studentFormRef = ref(null)

    // 处理表单提交
    const handleSubmit = async (formData) => {
        loading.value = true
        try {
            const response = await createStudent(formData)
            if (response && response.code === 200) {
                ElMessage.success('创建成功')
                studentFormRef.value?.markClean()
                router.push('/admin/students')
            } else {
                ElMessage.error(response.msg || '创建失败')
            }
        } catch (error) {
            console.error('创建学生失败:', error)
            showApiError(error, '创建学生失败')
        } finally {
            loading.value = false
        }
    }
</script>

<style lang="scss" scoped>
    .student-add-container {
        padding: 24px;
        min-height: calc(100vh - 84px);
        background-color: #f5f7fa;

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            padding: 0 16px;

            h2 {
                margin: 0;
                color: #303133;
                font-weight: 600;
                font-size: 20px;
            }
        }

        .add-card {
            width: 100%;
            max-width: 1200px;
            margin: 0 auto;
            border-radius: 8px;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
        }
    }
</style>
