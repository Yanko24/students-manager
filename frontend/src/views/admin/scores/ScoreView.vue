<template>
    <div class="score-view-container">
        <div class="page-header">
            <h2>成绩详情</h2>
        </div>

        <el-card class="detail-card">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="学号">{{ score.studentNo }}</el-descriptions-item>
                <el-descriptions-item label="学生姓名">{{ score.studentName }}</el-descriptions-item>
                <el-descriptions-item label="课程代码">{{ score.courseCode }}</el-descriptions-item>
                <el-descriptions-item label="课程名称">{{ score.courseName }}</el-descriptions-item>
                <el-descriptions-item label="学分">{{ score.credit }}</el-descriptions-item>
                <el-descriptions-item label="成绩">{{ score.score }}</el-descriptions-item>
                <el-descriptions-item label="绩点">{{ score.gradePoint }}</el-descriptions-item>
                <el-descriptions-item label="学期">{{ score.semester }}</el-descriptions-item>
                <el-descriptions-item label="考试时间">{{ formatDateTime(score.examTime) }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                    <el-tag :type="score.status === '合格' ? 'success' : 'danger'">
                        {{ score.status }}
                    </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="备注">{{ score.comment || score.remark || '无' }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatDateTime(score.createTime) }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ formatDateTime(score.updateTime) }}</el-descriptions-item>
            </el-descriptions>

            <div class="action-buttons">
                <el-button type="primary" @click="router.push(`/admin/scores/${route.params.id}/edit`)">编辑</el-button>
                <el-button @click="router.back()">返回</el-button>
            </div>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getScoreById } from '@/api/score'
    import { formatDateTime } from '@/utils/dateUtils'

    const router = useRouter()
    const route = useRoute()

    const score = ref({
        studentNo: '',
        studentName: '',
        courseCode: '',
        courseName: '',
        credit: 0,
        score: 0,
        gradePoint: 0,
        semester: '',
        examTime: '',
        status: '',
        remark: '',
        createTime: '',
        updateTime: ''
    })

    const fetchScore = async () => {
        try {
            const response = await getScoreById(route.params.id)
            if (response && response.data) {
                score.value = response.data
            }
        } catch (error) {
            console.error('获取成绩详情失败：', error)
            ElMessage.error('获取成绩详情失败')
        }
    }

    onMounted(() => {
        fetchScore()
    })
</script>

<style scoped>
    .score-view-container {
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
        .score-view-container {
            padding: 10px;
        }
    }
</style>
