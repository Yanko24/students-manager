<template>
    <div class="student-edit-container admin-record-page">
        <div class="page-header">
            <h2>编辑学生</h2>
            <div class="header-actions">
                <el-button @click="router.push('/admin/students')">返回列表</el-button>
            </div>
        </div>

        <el-card class="edit-card record-card" v-loading="loading">
            <StudentForm ref="studentFormRef" :is-edit="true" :initial-data="studentInfo" @submit="handleSubmit" />
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getStudentById, updateStudent } from '@/api/student'
    import StudentForm from '@/components/student/StudentForm.vue'

    const router = useRouter()
    const route = useRoute()
    const loading = ref(false)
    const studentFormRef = ref(null)
    const studentInfo = ref({
        studentNo: '',
        realName: '',
        gender: '',
        status: '',
        grade: '',
        majorCode: '',
        classNo: '',
        displayName: '',
        phone: '',
        email: '',
        admissionDate: '',
        birthDate: '',
        address: '',
        remark: ''
    })

    // 获取学生详情
    const fetchStudentDetail = async () => {
        loading.value = true
        try {
            const response = await getStudentById(route.params.id)
            if (response && response.data) {
                // 确保性别是数字类型
                const data = {
                    ...response.data,
                    gender: parseInt(response.data.gender)
                }
                studentInfo.value = data
                console.log('获取学生详情成功:', data)
            }
        } catch (error) {
            console.error('获取学生详情失败:', error)
            ElMessage.error('获取学生详情失败')
        } finally {
            loading.value = false
        }
    }

    // 处理表单提交
    const handleSubmit = async (formData) => {
        loading.value = true
        try {
            const response = await updateStudent(route.params.id, formData)
            if (response && response.code === 200) {
                ElMessage.success('更新成功')
                router.push('/admin/students')
            } else {
                ElMessage.error(response.msg || '更新失败')
            }
        } catch (error) {
            console.error('更新学生信息失败:', error)
            ElMessage.error('更新学生信息失败')
        } finally {
            loading.value = false
        }
    }

    onMounted(() => {
        fetchStudentDetail()
    })
</script>

<style lang="scss" scoped>
    .student-edit-container {
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

        .edit-card {
            width: 100%;
            max-width: 1200px;
            margin: 0 auto;
            border-radius: 8px;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
        }
    }
</style>
