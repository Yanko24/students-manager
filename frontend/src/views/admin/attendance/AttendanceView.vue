<template>
    <div class="attendance-view-container">
        <div class="page-header">
            <h2>考勤详情</h2>
        </div>

        <el-card class="detail-card">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="学号">{{ attendance.studentNo }}</el-descriptions-item>
                <el-descriptions-item label="姓名">{{ attendance.studentName }}</el-descriptions-item>
                <el-descriptions-item label="课程代码">{{ attendance.courseCode }}</el-descriptions-item>
                <el-descriptions-item label="课程名称">{{ attendance.courseName }}</el-descriptions-item>
                <el-descriptions-item label="日期">{{ attendance.date }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                    <el-tag
                        :type="attendance.status === '正常' ? 'success' : attendance.status === '迟到' ? 'warning' : 'danger'">
                        {{ attendance.status }}
                    </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="备注">{{ attendance.remark || '无' }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ attendance.createTime }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ attendance.updateTime }}</el-descriptions-item>
            </el-descriptions>

            <div class="action-buttons">
                <el-button type="primary"
                    @click="router.push(`/admin/attendance/${route.params.id}/edit`)">编辑</el-button>
                <el-button @click="router.back()">返回</el-button>
            </div>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getAttendanceById } from '@/api/attendance'

    const router = useRouter()
    const route = useRoute()

    const attendance = ref({
        studentNo: '',
        studentName: '',
        courseCode: '',
        courseName: '',
        date: '',
        status: '',
        remark: '',
        createTime: '',
        updateTime: ''
    })

    const fetchAttendance = async () => {
        try {
            const response = await getAttendanceById(route.params.id)
            if (response && response.data) {
                attendance.value = response.data
            }
        } catch (error) {
            console.error('获取考勤详情失败：', error)
            ElMessage.error('获取考勤详情失败')
        }
    }

    onMounted(() => {
        fetchAttendance()
    })
</script>

<style scoped>
    .attendance-view-container {
        padding: 20px;
        width: 100%;
        margin: 0 auto;
        box-sizing: border-box;
    }

    .page-header {
        margin-bottom: 20px;
    }

    .page-header h2 {
        margin: 0;
        font-size: 24px;
        color: #303133;
    }

    .detail-card {
        max-width: 800px;
        margin: 0 auto;
    }

    .action-buttons {
        margin-top: 20px;
        text-align: center;
    }

    @media screen and (max-width: 768px) {
        .attendance-view-container {
            padding: 10px;
        }
    }
</style>