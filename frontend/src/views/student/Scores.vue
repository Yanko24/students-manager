<template>
    <div class="scores-container">
        <div class="page-header">
            <h2>成绩查询</h2>
            <el-select v-model="currentSemester" placeholder="选择学期" class="semester-select">
                <el-option label="2023-2024学年第二学期" value="2023-2" />
                <el-option label="2023-2024学年第一学期" value="2023-1" />
            </el-select>
        </div>

        <el-row :gutter="20">
            <!-- 成绩列表 -->
            <el-col :span="16">
                <el-card class="score-list">
                    <template #header>
                        <div class="card-header">
                            <span>成绩列表</span>
                            <el-button type="primary" @click="exportScores">
                                <el-icon>
                                    <Download />
                                </el-icon>导出成绩单
                            </el-button>
                        </div>
                    </template>

                    <el-table :data="scoreList" style="width: 100%" v-loading="loading">
                        <el-table-column prop="courseName" label="课程名称" />
                        <el-table-column prop="credit" label="学分" width="80" />
                        <el-table-column prop="score" label="成绩" width="100">
                            <template #default="{ row }">
                                <span :class="{ 'pass': row.score >= 60, 'fail': row.score < 60 }">
                                    {{ row.score }}
                                </span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="grade" label="等级" width="100">
                            <template #default="{ row }">
                                <el-tag :type="getGradeTagType(row.grade)">{{ row.grade }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="examTime" label="考试时间" width="180">
                            <template #default="{ row }">{{ formatDateTime(row.examTime) }}</template>
                        </el-table-column>
                        <el-table-column prop="teacher" label="授课教师" width="120" />
                    </el-table>

                    <div class="pagination-container">
                        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                            :page-sizes="[10, 20, 50, 100]" :total="total"
                            layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                            @current-change="handleCurrentChange" />
                    </div>
                </el-card>
            </el-col>

            <!-- 成绩统计 -->
            <el-col :span="8">
                <el-card class="score-stats">
                    <template #header>
                        <div class="card-header">
                            <span>成绩统计</span>
                        </div>
                    </template>
                    <div class="stats-content">
                        <div class="stat-item">
                            <div class="label">平均分</div>
                            <div class="value">{{ averageScore }}</div>
                        </div>
                        <div class="stat-item">
                            <div class="label">总学分</div>
                            <div class="value">{{ totalCredits }}</div>
                        </div>
                        <div class="stat-item">
                            <div class="label">及格率</div>
                            <div class="value">{{ passRate }}%</div>
                        </div>
                        <div class="stat-item">
                            <div class="label">优秀率</div>
                            <div class="value">{{ excellentRate }}%</div>
                        </div>
                    </div>

                    <!-- 成绩分布图 -->
                    <div class="score-distribution">
                        <div class="chart-title">成绩分布</div>
                        <div class="chart-container">
                            <div v-for="(count, grade) in scoreDistribution" :key="grade" class="bar-item">
                                <div class="bar-label">{{ grade }}</div>
                                <div class="bar-wrapper">
                                    <div class="bar" :style="{ height: `${(count / maxCount) * 100}%` }"></div>
                                </div>
                                <div class="bar-value">{{ count }}</div>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
    import { ref, computed } from 'vue'
    import { Download } from '@element-plus/icons-vue'
    import { ElMessage } from 'element-plus'
    import { formatDateTime } from '@/utils/dateUtils'

    const currentSemester = ref('2023-2')
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(100)

    // 模拟成绩数据
    const scoreList = ref([
        {
            courseName: '高等数学',
            credit: 4,
            score: 85,
            grade: 'A',
            examTime: '2024-01-15 09:00',
            teacher: '张老师'
        },
        {
            courseName: '大学英语',
            credit: 3,
            score: 92,
            grade: 'A+',
            examTime: '2024-01-16 14:00',
            teacher: '李老师'
        },
        {
            courseName: '程序设计基础',
            credit: 3,
            score: 78,
            grade: 'B+',
            examTime: '2024-01-17 09:00',
            teacher: '王老师'
        }
    ])

    // 计算属性
    const averageScore = computed(() => {
        const total = scoreList.value.reduce((sum, course) => sum + course.score, 0)
        return (total / scoreList.value.length).toFixed(1)
    })

    const totalCredits = computed(() => {
        return scoreList.value.reduce((sum, course) => sum + course.credit, 0)
    })

    const passRate = computed(() => {
        const passCount = scoreList.value.filter(course => course.score >= 60).length
        return ((passCount / scoreList.value.length) * 100).toFixed(1)
    })

    const excellentRate = computed(() => {
        const excellentCount = scoreList.value.filter(course => course.score >= 90).length
        return ((excellentCount / scoreList.value.length) * 100).toFixed(1)
    })

    const scoreDistribution = computed(() => {
        const distribution = {
            'A+': 0,
            'A': 0,
            'B+': 0,
            'B': 0,
            'C+': 0,
            'C': 0,
            'D': 0,
            'F': 0
        }

        scoreList.value.forEach(course => {
            distribution[course.grade]++
        })

        return distribution
    })

    const maxCount = computed(() => {
        return Math.max(...Object.values(scoreDistribution.value))
    })

    // 方法
    const getGradeTagType = (grade) => {
        const types = {
            'A+': 'success',
            'A': 'success',
            'B+': 'warning',
            'B': 'warning',
            'C+': 'info',
            'C': 'info',
            'D': 'danger',
            'F': 'danger'
        }
        return types[grade] || 'info'
    }

    const exportScores = () => {
        ElMessage.success('成绩单导出成功')
    }

    const handleSizeChange = (val) => {
        pageSize.value = val
        // 重新加载数据
    }

    const handleCurrentChange = (val) => {
        currentPage.value = val
        // 重新加载数据
    }
</script>

<style lang="scss" scoped>
    .scores-container {
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

        .score-list {
            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .pagination-container {
                margin-top: 20px;
                display: flex;
                justify-content: flex-end;
            }

            .pass {
                color: var(--el-color-success);
                font-weight: bold;
            }

            .fail {
                color: var(--el-color-danger);
                font-weight: bold;
            }
        }

        .score-stats {
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

            .score-distribution {
                margin-top: 30px;
                padding: 20px;
                border-top: 1px solid var(--el-border-color-lighter);

                .chart-title {
                    font-size: 16px;
                    font-weight: bold;
                    margin-bottom: 20px;
                    text-align: center;
                }

                .chart-container {
                    display: flex;
                    justify-content: space-between;
                    align-items: flex-end;
                    height: 200px;
                    padding: 20px 0;

                    .bar-item {
                        flex: 1;
                        text-align: center;
                        margin: 0 5px;

                        .bar-label {
                            font-size: 12px;
                            color: var(--el-text-color-secondary);
                            margin-bottom: 5px;
                        }

                        .bar-wrapper {
                            height: 150px;
                            background-color: var(--el-color-primary-light-9);
                            border-radius: 4px;
                            position: relative;
                            overflow: hidden;

                            .bar {
                                position: absolute;
                                bottom: 0;
                                left: 0;
                                right: 0;
                                background-color: var(--el-color-primary);
                                transition: height 0.3s ease;
                            }
                        }

                        .bar-value {
                            font-size: 12px;
                            color: var(--el-text-color-secondary);
                            margin-top: 5px;
                        }
                    }
                }
            }
        }
    }
</style>
