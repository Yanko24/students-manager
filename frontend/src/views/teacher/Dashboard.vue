<template>
    <div class="dashboard-container">
        <!-- 统计卡片 -->
        <el-row :gutter="20">
            <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                    <template #header>
                        <div class="card-header">
                            <span>我的课程</span>
                            <el-icon>
                                <Reading />
                            </el-icon>
                        </div>
                    </template>
                    <div class="card-content">
                        <div class="number">{{ stats.courses }}</div>
                        <div class="label">本学期课程数</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                    <template #header>
                        <div class="card-header">
                            <span>学生总数</span>
                            <el-icon>
                                <User />
                            </el-icon>
                        </div>
                    </template>
                    <div class="card-content">
                        <div class="number">{{ stats.students }}</div>
                        <div class="label">所教学生数</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                    <template #header>
                        <div class="card-header">
                            <span>待批作业</span>
                            <el-icon>
                                <Document />
                            </el-icon>
                        </div>
                    </template>
                    <div class="card-content">
                        <div class="number">{{ stats.homework }}</div>
                        <div class="label">待批改数量</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                    <template #header>
                        <div class="card-header">
                            <span>今日考勤</span>
                            <el-icon>
                                <Calendar />
                            </el-icon>
                        </div>
                    </template>
                    <div class="card-content">
                        <div class="number">{{ stats.attendance ?? '—' }}</div>
                        <div class="label">今日考勤记录</div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 待办事项和课程表 -->
        <el-row :gutter="20" class="mt-20">
            <el-col :span="16">
                <el-card class="schedule-card">
                    <template #header>
                        <div class="card-header">
                            <span>今日课程表</span>
                            <el-button type="primary" link>查看完整课表</el-button>
                        </div>
                    </template>
                    <el-table :data="schedule" style="width: 100%">
                        <el-table-column prop="time" label="时间" width="120" />
                        <el-table-column prop="course" label="课程" />
                        <el-table-column prop="class" label="班级" width="150" />
                        <el-table-column prop="location" label="地点" width="120" />
                        <el-table-column label="操作" width="120">
                            <template #default="{ row }">
                                <el-button type="primary" link @click="handleAttendance(row)">
                                    考勤
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card class="todo-card">
                    <template #header>
                        <div class="card-header">
                            <span>待办事项</span>
                        </div>
                    </template>
                    <el-timeline>
                        <el-timeline-item v-for="(todo, index) in todos" :key="index" :type="todo.type"
                            :timestamp="todo.time">
                            {{ todo.content }}
                        </el-timeline-item>
                    </el-timeline>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    import {
        Reading,
        User,
        Document,
        Calendar
    } from '@element-plus/icons-vue'
    import { ElMessage } from 'element-plus'
    import { getTeacherAttendance } from '@/api/attendance'

    const router = useRouter()

    // 统计数据
    const stats = ref({
        courses: 4,
        students: 120,
        homework: 8,
        attendance: null
    })

    // 课程表数据
    const schedule = ref([
        {
            time: '08:00-09:40',
            course: '计算机导论',
            class: '计算机科学与技术2班',
            location: '教学楼A101'
        },
        {
            time: '10:00-11:40',
            course: '数据结构',
            class: '软件工程1班',
            location: '教学楼B203'
        },
        {
            time: '14:00-15:40',
            course: 'C语言程序设计',
            class: '信息安全3班',
            location: '实验楼C305'
        }
    ])

    // 待办事项
    const todos = ref([
        {
            content: '批改计算机导论作业',
            time: '今天 10:00',
            type: 'warning'
        },
        {
            content: '录入数据结构期中成绩',
            time: '今天 14:00',
            type: 'primary'
        },
        {
            content: '处理软件工程1班考勤异常',
            time: '今天 16:00',
            type: 'danger'
        }
    ])

    const handleAttendance = (row) => {
        router.push({
            path: '/teacher/attendance',
            query: {
                course: row.course,
                class: row.class
            }
        })
    }

    const fetchTodayAttendance = async () => {
        const today = new Date()
        const date = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
        try {
            const response = await getTeacherAttendance({ date, page: 1, size: 1 })
            if (response?.code !== 200) throw new Error(response?.message || '获取今日考勤失败')
            stats.value.attendance = response.data?.total || 0
        } catch (error) {
            console.error('获取今日考勤失败：', error)
            ElMessage.error(error?.message || '获取今日考勤失败')
        }
    }

    onMounted(fetchTodayAttendance)
</script>

<style lang="scss" scoped>
    .dashboard-container {
        .mt-20 {
            margin-top: 20px;
        }

        .stat-card {
            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .card-content {
                text-align: center;
                padding: 20px 0;

                .number {
                    font-size: 24px;
                    font-weight: bold;
                    color: #409EFF;
                    margin-bottom: 10px;
                }

                .label {
                    color: #666;
                    font-size: 14px;
                }
            }
        }

        .schedule-card,
        .todo-card {
            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
        }
    }
</style>
