<template>
    <record-detail-page title="学生详情" back-path="/admin/students" :loading="loading">
                <el-descriptions-item label="学号">{{ studentInfo.studentNo }}</el-descriptions-item>
                <el-descriptions-item label="姓名">{{ studentInfo.realName }}</el-descriptions-item>
                <el-descriptions-item label="性别">{{ studentInfo.gender === 1 ? '男' : '女' }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                    <el-tag :type="getStatusType(studentInfo.status)">
                        {{ getStatusText(studentInfo.status) }}
                    </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="年级">{{ studentInfo.grade }}</el-descriptions-item>
                <el-descriptions-item label="专业代码">{{ studentInfo.majorCode }}</el-descriptions-item>
                <el-descriptions-item label="班级">{{ studentInfo.classNo }}</el-descriptions-item>
                <el-descriptions-item label="专业全称">{{ studentInfo.displayName }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ studentInfo.phone }}</el-descriptions-item>
                <el-descriptions-item label="邮箱">{{ studentInfo.email }}</el-descriptions-item>
                <el-descriptions-item label="入学日期">{{ formatDate(studentInfo.admissionDate) }}</el-descriptions-item>
                <el-descriptions-item label="出生日期">{{ formatDate(studentInfo.birthDate) }}</el-descriptions-item>
                <el-descriptions-item label="地址" :span="2">{{ studentInfo.address }}</el-descriptions-item>
                <el-descriptions-item label="创建人">{{ studentInfo.createBy }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatDateTime(studentInfo.createTime) }}</el-descriptions-item>
                <el-descriptions-item label="更新人">{{ studentInfo.updateBy || '无' }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ formatDateTime(studentInfo.updateTime) }}</el-descriptions-item>
                <el-descriptions-item label="备注" :span="2">{{ studentInfo.remark || '无' }}</el-descriptions-item>
    </record-detail-page>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getStudentById, getStatusType, getStatusText } from '@/api/student'
    import { formatDate, formatDateTime } from '@/utils/dateUtils'
    import RecordDetailPage from '@/components/common/RecordDetailPage.vue'

    const route = useRoute()
    const loading = ref(false)
    const studentInfo = ref({})

    // 获取学生详情
    const fetchStudentDetail = async () => {
        loading.value = true
        try {
            const response = await getStudentById(route.params.id)
            if (response && response.data) {
                studentInfo.value = response.data
                console.log('获取学生详情成功:', response.data)
            }
        } catch (error) {
            console.error('获取学生详情失败:', error)
            ElMessage.error('获取学生详情失败')
        } finally {
            loading.value = false
        }
    }

    onMounted(() => {
        fetchStudentDetail()
    })
</script>

<style lang="scss" scoped>
    .student-view-container {
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

        .detail-card {
            width: 100%;
            max-width: 1200px;
            margin: 0 auto;
            border-radius: 8px;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

            :deep(.el-descriptions) {
                padding: 20px;
            }

            :deep(.el-descriptions__label) {
                width: 120px;
                background-color: #f5f7fa;
                color: #606266;
                font-weight: 600;
            }

            :deep(.el-descriptions__content) {
                color: #303133;
            }
        }
    }
</style>
