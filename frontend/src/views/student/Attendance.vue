<template>
    <div class="attendance-container">
        <el-card class="attendance-card">
            <template #header>
                <div class="card-header">
                    <span>考勤记录</span>
                    <div class="header-actions">
                        <el-select v-model="currentSemester" placeholder="选择学期" style="width: 200px">
                            <el-option label="2023-2024学年第二学期" value="2023-2024-2" />
                            <el-option label="2023-2024学年第一学期" value="2023-2024-1" />
                        </el-select>
                        <el-select v-model="currentCourse" placeholder="选择课程" style="width: 200px">
                            <el-option label="全部课程" value="" />
                            <el-option label="高等数学" value="高等数学" />
                            <el-option label="大学英语" value="大学英语" />
                            <el-option label="计算机基础" value="计算机基础" />
                        </el-select>
                    </div>
                </div>
            </template>

            <el-table :data="attendanceList" style="width: 100%">
                <el-table-column prop="date" label="日期" width="120" />
                <el-table-column prop="courseName" label="课程名称" />
                <el-table-column prop="teacher" label="授课教师" />
                <el-table-column prop="time" label="上课时间" width="120" />
                <el-table-column prop="status" label="考勤状态" width="100">
                    <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)">
                            {{ scope.row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" />
            </el-table>

            <div class="summary-info">
                <el-row :gutter="20">
                    <el-col :span="6">
                        <div class="info-item">
                            <div class="label">总课时数</div>
                            <div class="value">{{ totalClasses }}</div>
                        </div>
                    </el-col>
                    <el-col :span="6">
                        <div class="info-item">
                            <div class="label">出勤课时</div>
                            <div class="value">{{ attendedClasses }}</div>
                        </div>
                    </el-col>
                    <el-col :span="6">
                        <div class="info-item">
                            <div class="label">迟到次数</div>
                            <div class="value">{{ lateCount }}</div>
                        </div>
                    </el-col>
                    <el-col :span="6">
                        <div class="info-item">
                            <div class="label">缺勤课时</div>
                            <div class="value">{{ absentClasses }}</div>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, computed } from 'vue'

    const currentSemester = ref('2023-2024-2')
    const currentCourse = ref('')

    const attendanceList = ref([
        {
            id: 1,
            date: '2024-03-20',
            courseName: '高等数学',
            teacher: '李老师',
            time: '1-2节',
            status: '正常',
            remark: ''
        },
        {
            id: 2,
            date: '2024-03-19',
            courseName: '大学英语',
            teacher: '王老师',
            time: '3-4节',
            status: '迟到',
            remark: '迟到10分钟'
        },
        {
            id: 3,
            date: '2024-03-18',
            courseName: '计算机基础',
            teacher: '张老师',
            time: '5-6节',
            status: '正常',
            remark: ''
        },
        {
            id: 4,
            date: '2024-03-17',
            courseName: '高等数学',
            teacher: '李老师',
            time: '1-2节',
            status: '缺勤',
            remark: '因病请假'
        },
        {
            id: 5,
            date: '2024-03-16',
            courseName: '大学英语',
            teacher: '王老师',
            time: '3-4节',
            status: '正常',
            remark: ''
        }
    ])

    const totalClasses = computed(() => {
        return attendanceList.value.length
    })

    const attendedClasses = computed(() => {
        return attendanceList.value.filter(record => record.status === '正常').length
    })

    const lateCount = computed(() => {
        return attendanceList.value.filter(record => record.status === '迟到').length
    })

    const absentClasses = computed(() => {
        return attendanceList.value.filter(record => record.status === '缺勤').length
    })

    const getStatusType = (status) => {
        switch (status) {
            case '正常':
                return 'success'
            case '迟到':
                return 'warning'
            case '缺勤':
                return 'danger'
            default:
                return 'info'
        }
    }
</script>

<style scoped lang="scss">
    .attendance-container {
        padding: 20px;

        .attendance-card {
            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;

                .header-actions {
                    display: flex;
                    gap: 16px;
                }
            }

            .summary-info {
                margin-top: 20px;
                padding-top: 20px;
                border-top: 1px solid var(--el-border-color-lighter);

                .info-item {
                    text-align: center;
                    padding: 16px;
                    background-color: var(--el-fill-color-light);
                    border-radius: 4px;

                    .label {
                        color: var(--el-text-color-secondary);
                        margin-bottom: 8px;
                    }

                    .value {
                        font-size: 24px;
                        font-weight: bold;
                        color: var(--el-text-color-primary);
                    }
                }
            }
        }
    }
</style>