<template>
    <div class="dashboard-container">
        <el-row :gutter="20">
            <el-col :span="24">
                <el-card class="welcome-card">
                    <div class="welcome-content">
                        <el-avatar :size="64" :src="userAvatar" />
                        <div class="welcome-text">
                            <h2>欢迎回来，{{ studentName }}</h2>
                            <p>今天是 {{ currentDate }}</p>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" class="mt-4">
            <el-col :span="8">
                <el-card class="info-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <span>今日课程</span>
                            <el-button text>查看全部</el-button>
                        </div>
                    </template>
                    <div class="course-list">
                        <div v-for="course in todayCourses" :key="course.id" class="course-item">
                            <div class="course-info">
                                <h4>{{ course.name }}</h4>
                                <p>{{ course.time }}</p>
                            </div>
                            <el-tag :type="course.status === '进行中' ? 'success' : 'info'">
                                {{ course.status }}
                            </el-tag>
                        </div>
                    </div>
                </el-card>
            </el-col>

            <el-col :span="8">
                <el-card class="info-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <span>最近成绩</span>
                            <el-button text>查看全部</el-button>
                        </div>
                    </template>
                    <div class="score-list">
                        <div v-for="score in recentScores" :key="score.id" class="score-item">
                            <div class="score-info">
                                <h4>{{ score.course }}</h4>
                                <p>考试时间：{{ formatDateTime(score.date) }}</p>
                            </div>
                            <div class="score-value" :class="score.score >= 60 ? 'pass' : 'fail'">
                                {{ score.score }}
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>

            <el-col :span="8">
                <el-card class="info-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <span>考勤记录</span>
                            <el-button text>查看全部</el-button>
                        </div>
                    </template>
                    <div class="attendance-list">
                        <div v-for="record in attendanceRecords" :key="record.id" class="attendance-item">
                            <div class="attendance-info">
                                <h4>{{ record.courseName }}</h4>
                                <p>{{ formatDate(record.date) }} {{ record.classPeriod }}</p>
                            </div>
                            <el-tag :type="record.status === '正常' ? 'success' : record.status === '迟到' || record.status === '早退' ? 'warning' : record.status === '请假' ? 'info' : 'danger'">
                                {{ record.status }}
                            </el-tag>
                        </div>
                        <el-empty v-if="attendanceRecords.length === 0" description="暂无考勤记录" :image-size="70" />
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { ElMessage } from 'element-plus'
    import { getStudentAttendance } from '@/api/attendance'
    import { formatDate, formatDateTime } from '@/utils/dateUtils'

    const studentName = ref('张三')
    const userAvatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')

    const currentDate = computed(() => {
        return new Date().toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            weekday: 'long'
        })
    })

    // 模拟数据
    const todayCourses = ref([
        { id: 1, name: '高等数学', time: '08:00 - 09:40', status: '进行中' },
        { id: 2, name: '大学英语', time: '10:00 - 11:40', status: '未开始' },
        { id: 3, name: '计算机基础', time: '14:00 - 15:40', status: '未开始' }
    ])

    const recentScores = ref([
        { id: 1, course: '高等数学', date: '2024-03-20', score: 85 },
        { id: 2, course: '大学英语', date: '2024-03-18', score: 92 },
        { id: 3, course: '计算机基础', date: '2024-03-15', score: 78 }
    ])

    const attendanceRecords = ref([])

    const fetchAttendanceRecords = async () => {
        try {
            const response = await getStudentAttendance({ page: 1, size: 3 })
            if (response?.code !== 200) throw new Error(response?.message || '获取考勤记录失败')
            attendanceRecords.value = response.data?.records || []
        } catch (error) {
            console.error('获取首页考勤记录失败：', error)
            ElMessage.error(error?.message || '获取考勤记录失败')
        }
    }

    onMounted(fetchAttendanceRecords)
</script>

<style scoped lang="scss">
    .dashboard-container {
        padding: 20px;

        .welcome-card {
            margin-bottom: 20px;

            .welcome-content {
                display: flex;
                align-items: center;
                gap: 20px;

                .welcome-text {
                    h2 {
                        margin: 0;
                        font-size: 24px;
                        color: var(--el-text-color-primary);
                    }

                    p {
                        margin: 8px 0 0;
                        color: var(--el-text-color-secondary);
                    }
                }
            }
        }

        .info-card {
            height: 100%;

            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .course-list,
            .score-list,
            .attendance-list {

                .course-item,
                .score-item,
                .attendance-item {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 12px 0;
                    border-bottom: 1px solid var(--el-border-color-lighter);

                    &:last-child {
                        border-bottom: none;
                    }

                    h4 {
                        margin: 0;
                        font-size: 16px;
                    }

                    p {
                        margin: 4px 0 0;
                        font-size: 14px;
                        color: var(--el-text-color-secondary);
                    }
                }
            }

            .score-value {
                font-size: 20px;
                font-weight: bold;

                &.pass {
                    color: var(--el-color-success);
                }

                &.fail {
                    color: var(--el-color-danger);
                }
            }
        }
    }

    .mt-4 {
        margin-top: 16px;
    }
</style>
