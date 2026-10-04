<template>
    <div class="courses-container">
        <div class="page-header">
            <h2>我的课程</h2>
            <el-select v-model="currentSemester" placeholder="选择学期" class="semester-select">
                <el-option label="2023-2024学年第二学期" value="2023-2" />
                <el-option label="2023-2024学年第一学期" value="2023-1" />
            </el-select>
        </div>

        <el-row :gutter="20">
            <!-- 课程列表 -->
            <el-col :span="16">
                <el-card class="course-list">
                    <template #header>
                        <div class="card-header">
                            <span>课程列表</span>
                            <el-radio-group v-model="viewMode" size="small">
                                <el-radio-button label="list">列表</el-radio-button>
                                <el-radio-button label="schedule">课表</el-radio-button>
                            </el-radio-group>
                        </div>
                    </template>

                    <!-- 列表视图 -->
                    <div v-if="viewMode === 'list'" class="list-view">
                        <el-table :data="courseList" style="width: 100%">
                            <el-table-column prop="name" label="课程名称" />
                            <el-table-column prop="teacher" label="授课教师" width="120" />
                            <el-table-column prop="credit" label="学分" width="80" />
                            <el-table-column prop="time" label="上课时间" width="180" />
                            <el-table-column prop="location" label="上课地点" width="150" />
                            <el-table-column label="操作" width="120" fixed="right">
                                <template #default="{ row }">
                                    <el-button type="primary" link @click="showCourseDetail(row)">
                                        详情
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>

                    <!-- 课表视图 -->
                    <div v-else class="schedule-view">
                        <el-table :data="scheduleData" style="width: 100%">
                            <el-table-column prop="time" label="时间" width="120" />
                            <el-table-column prop="monday" label="周一" />
                            <el-table-column prop="tuesday" label="周二" />
                            <el-table-column prop="wednesday" label="周三" />
                            <el-table-column prop="thursday" label="周四" />
                            <el-table-column prop="friday" label="周五" />
                            <el-table-column prop="saturday" label="周六" />
                            <el-table-column prop="sunday" label="周日" />
                        </el-table>
                    </div>
                </el-card>
            </el-col>

            <!-- 课程统计 -->
            <el-col :span="8">
                <el-card class="course-stats">
                    <template #header>
                        <div class="card-header">
                            <span>课程统计</span>
                        </div>
                    </template>
                    <div class="stats-content">
                        <div class="stat-item">
                            <div class="label">总课程数</div>
                            <div class="value">{{ courseList.length }}</div>
                        </div>
                        <div class="stat-item">
                            <div class="label">总学分</div>
                            <div class="value">{{ totalCredits }}</div>
                        </div>
                        <div class="stat-item">
                            <div class="label">必修课程</div>
                            <div class="value">{{ requiredCourses }}</div>
                        </div>
                        <div class="stat-item">
                            <div class="label">选修课程</div>
                            <div class="value">{{ electiveCourses }}</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 课程详情对话框 -->
        <el-dialog v-model="detailDialogVisible" title="课程详情" width="50%">
            <div v-if="selectedCourse" class="course-detail">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="课程名称">{{ selectedCourse.name }}</el-descriptions-item>
                    <el-descriptions-item label="授课教师">{{ selectedCourse.teacher }}</el-descriptions-item>
                    <el-descriptions-item label="学分">{{ selectedCourse.credit }}</el-descriptions-item>
                    <el-descriptions-item label="上课时间">{{ selectedCourse.time }}</el-descriptions-item>
                    <el-descriptions-item label="上课地点">{{ selectedCourse.location }}</el-descriptions-item>
                    <el-descriptions-item label="课程类型">{{ selectedCourse.type }}</el-descriptions-item>
                    <el-descriptions-item label="课程简介" :span="2">{{ selectedCourse.description }}</el-descriptions-item>
                </el-descriptions>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
    import { ref, computed } from 'vue'
    import { ElMessage } from 'element-plus'

    const currentSemester = ref('2023-2')
    const viewMode = ref('list')
    const detailDialogVisible = ref(false)
    const selectedCourse = ref(null)

    // 模拟课程数据
    const courseList = ref([
        {
            name: '高等数学',
            teacher: '张老师',
            credit: 4,
            time: '周一 1-2节',
            location: '教学楼A101',
            type: '必修',
            description: '本课程主要讲授高等数学的基本概念、理论和方法，包括函数、极限、导数、积分等内容。'
        },
        {
            name: '大学英语',
            teacher: '李老师',
            credit: 3,
            time: '周二 3-4节',
            location: '教学楼B203',
            type: '必修',
            description: '本课程旨在提高学生的英语听说读写能力，培养跨文化交际能力。'
        },
        {
            name: '程序设计基础',
            teacher: '王老师',
            credit: 3,
            time: '周三 5-6节',
            location: '实验楼C305',
            type: '必修',
            description: '本课程介绍程序设计的基本概念和方法，包括算法、数据结构、面向对象编程等。'
        }
    ])

    // 模拟课表数据
    const scheduleData = ref([
        {
            time: '第一节\n8:00-8:45',
            monday: '高等数学\n教学楼A101',
            tuesday: '',
            wednesday: '',
            thursday: '',
            friday: '',
            saturday: '',
            sunday: ''
        },
        {
            time: '第二节\n8:55-9:40',
            monday: '高等数学\n教学楼A101',
            tuesday: '',
            wednesday: '',
            thursday: '',
            friday: '',
            saturday: '',
            sunday: ''
        },
        {
            time: '第三节\n10:00-10:45',
            monday: '',
            tuesday: '大学英语\n教学楼B203',
            wednesday: '',
            thursday: '',
            friday: '',
            saturday: '',
            sunday: ''
        }
    ])

    // 计算属性
    const totalCredits = computed(() => {
        return courseList.value.reduce((sum, course) => sum + course.credit, 0)
    })

    const requiredCourses = computed(() => {
        return courseList.value.filter(course => course.type === '必修').length
    })

    const electiveCourses = computed(() => {
        return courseList.value.filter(course => course.type === '选修').length
    })

    // 方法
    const showCourseDetail = (course) => {
        selectedCourse.value = course
        detailDialogVisible.value = true
    }
</script>

<style lang="scss" scoped>
    .courses-container {
        padding: 20px;

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;

            h2 {
                margin: 0;
            }

            .semester-select {
                width: 200px;
            }
        }

        .course-list {
            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .schedule-view {
                :deep(.el-table__cell) {
                    white-space: pre-line;
                }
            }
        }

        .course-stats {
            .stats-content {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 20px;
                padding: 10px;

                .stat-item {
                    text-align: center;
                    padding: 15px;
                    background-color: var(--el-color-primary-light-9);
                    border-radius: 4px;

                    .label {
                        font-size: 14px;
                        color: var(--el-text-color-secondary);
                        margin-bottom: 5px;
                    }

                    .value {
                        font-size: 24px;
                        font-weight: bold;
                        color: var(--el-color-primary);
                    }
                }
            }
        }

        .course-detail {
            :deep(.el-descriptions__label) {
                width: 120px;
            }
        }
    }
</style>