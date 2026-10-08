<template>
    <record-detail-page title="成绩详情" back-path="/admin/scores" :loading="loading">
                <el-descriptions-item label="学号">{{ score.studentNo }}</el-descriptions-item>
                <el-descriptions-item label="学生姓名">{{ score.studentName }}</el-descriptions-item>
                <el-descriptions-item label="课程代码">{{ score.courseCode }}</el-descriptions-item>
                <el-descriptions-item label="课程名称">{{ score.courseName }}</el-descriptions-item>
                <el-descriptions-item label="学分">{{ score.credit }}</el-descriptions-item>
                <el-descriptions-item label="成绩">{{ score.score }}</el-descriptions-item>
                <el-descriptions-item label="绩点">{{ score.gradePoint }}</el-descriptions-item>
                <el-descriptions-item label="学期">{{ score.semester }}</el-descriptions-item>
                <el-descriptions-item label="考试类型">{{ attemptTypeLabel(score.attemptType) }}（第{{ score.attemptNo }}次）</el-descriptions-item>
                <el-descriptions-item label="发布状态">{{ score.publishStatus === 'PUBLISHED' ? '已发布' : '待发布' }}</el-descriptions-item>
                <el-descriptions-item label="考试时间">{{ formatDateTime(score.examTime) }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                    <el-tag :type="score.status === '合格' ? 'success' : 'danger'">
                        {{ score.status }}
                    </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="备注">{{ score.comment || score.remark || '无' }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatDateTime(score.createTime) }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ formatDateTime(score.updateTime) }}</el-descriptions-item>
                <el-descriptions-item label="成绩变更记录" :span="2">
                    <el-timeline v-if="history.length">
                        <el-timeline-item v-for="item in history" :key="item.id" :timestamp="formatDateTime(item.createTime)" placement="top">
                            <strong>{{ actionLabel(item.action) }}</strong> · 操作人 {{ item.operator }}<br />
                            <span v-if="item.oldScore !== null">{{ item.oldScore }} 分 → </span>{{ item.newScore ?? '已删除' }}<span v-if="item.newScore !== null"> 分</span>
                            · {{ attemptTypeLabel(item.newAttemptType || item.oldAttemptType) }}
                            <div>原因：{{ item.reason }}</div>
                        </el-timeline-item>
                    </el-timeline>
                    <span v-else>暂无变更记录</span>
                </el-descriptions-item>
    </record-detail-page>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
    import { ref, onMounted } from 'vue'
    import { useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
import { getScoreById, getScoreHistory } from '@/api/score'
    import { formatDateTime } from '@/utils/dateUtils'
    import RecordDetailPage from '@/components/common/RecordDetailPage.vue'

    const route = useRoute()
    const loading = ref(true)
    const history = ref([])

    const attemptTypeLabel = (type) => ({ REGULAR: '正常考试', MAKEUP: '补考', RETAKE: '重修' }[type] || '正常考试')
    const actionLabel = (action) => ({ CREATE: '录入成绩', UPDATE: '更正成绩', DELETE: '删除成绩', PUBLISH: '发布成绩' }[action] || action)

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
        loading.value = true
        try {
            const response = await getScoreById(route.params.id)
            if (response && response.data) {
                score.value = response.data
                const historyResponse = await getScoreHistory(route.params.id)
                history.value = historyResponse?.data || []
            }
        } catch (error) {
            console.error('获取成绩详情失败：', error)
            showApiError(error, '获取成绩详情失败')
        } finally {
            loading.value = false
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
