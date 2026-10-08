<template>
    <record-detail-page title="课程详情" back-path="/admin/courses" :loading="loading">
                    <template #actions>
                        <el-button type="primary" @click="router.push({ name: 'CourseStudents', params: { id: route.params.id } })">选课审核</el-button>
                    </template>
                    <el-descriptions-item label="课程名称">{{ course.name }}</el-descriptions-item>
                    <el-descriptions-item label="课程代码">{{ course.code }}</el-descriptions-item>
                    <el-descriptions-item label="教学班号">{{ course.sectionCode || '01' }}</el-descriptions-item>
                    <el-descriptions-item label="授课院系">{{ course.college }}</el-descriptions-item>
                    <el-descriptions-item label="学分">{{ course.credit }}</el-descriptions-item>
                    <el-descriptions-item label="学时">{{ course.hours }}</el-descriptions-item>
                    <el-descriptions-item label="课程类型">{{ course.type }}</el-descriptions-item>
                    <el-descriptions-item label="授课教师">{{ course.teacher || '未指定' }}</el-descriptions-item>
                    <el-descriptions-item label="开课学期">{{ course.semester }}</el-descriptions-item>
                    <el-descriptions-item label="上课安排" :span="2">
                        <div v-if="course.schedules?.length" class="schedule-list">
                            <div v-for="(schedule, index) in course.schedules" :key="schedule.id || index">{{ scheduleText(schedule) }}</div>
                        </div>
                        <span v-else>暂未安排</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="名额占用（待审核 + 已通过）">{{ course.selectedCount || 0 }} / {{ course.maxStudents || 60 }} 人</el-descriptions-item>
                    <el-descriptions-item label="适用范围">
                        {{ course.selectionScope === 'COLLEGE' ? `指定学院：${course.selectionCollegeName || '未设置'}` : course.selectionScope === 'MAJOR' ? `指定专业：${course.selectionMajorName || course.selectionMajorCode || '未设置'}` : '全校学生' }}
                        {{ course.selectionGrade ? `（${course.selectionGrade}级）` : '（不限年级）' }}
                    </el-descriptions-item>
                    <el-descriptions-item label="课程简介" :span="2">{{ course.description }}</el-descriptions-item>
                    <el-descriptions-item label="教学目标" :span="2">{{ course.objectives }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="course.status === 0 ? 'info' : course.status === 1 ? 'success' : 'warning'">
                            {{ course.statusText }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">{{ formatDateTime(course.createTime) }}</el-descriptions-item>
                    <el-descriptions-item label="更新时间">{{ formatDateTime(course.updateTime) }}</el-descriptions-item>
    </record-detail-page>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { getCourseById } from '@/api/course'
    import { formatDateTime } from '@/utils/dateUtils'
    import RecordDetailPage from '@/components/common/RecordDetailPage.vue'

    const route = useRoute()
    const router = useRouter()
    const loading = ref(true)
    const course = ref(null)

    const scheduleText = (schedule) => {
        const day = ['一', '二', '三', '四', '五', '六', '日'][schedule.dayOfWeek - 1]
        const parity = ({ ALL: '每周', ODD: '单周', EVEN: '双周' })[schedule.weekParity] || '每周'
        return `星期${day} 第 ${schedule.startPeriod}-${schedule.endPeriod} 节，第 ${schedule.weekStart}-${schedule.weekEnd} 周（${parity}），${schedule.classroom}`
    }

    onMounted(async () => {
        try {
            const response = await getCourseById(route.params.id)
            if (response && response.data) {
                course.value = response.data
            }
        } catch (error) {
            console.error('获取课程详情失败:', error)
        } finally {
            loading.value = false
        }
    })
</script>

<style scoped>
.schedule-list { display: grid; gap: 6px; }
</style>

<style lang="scss" scoped>
    .course-view-container {
        padding: 20px;
    }

    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    .info-card {
        max-width: 800px;
        margin: 0 auto;
    }

    .section-title {
        margin: 20px 0 10px;
        font-size: 16px;
        font-weight: bold;
        color: #303133;
    }
</style>
