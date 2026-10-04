<template>
    <div class="students-container">
        <div class="page-header">
            <h2>学生管理</h2>
            <el-button type="primary" @click="handleExport">导出学生名单</el-button>
        </div>

        <!-- 搜索表单 -->
        <el-card class="search-card">
            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="课程">
                    <el-select v-model="searchForm.courseId" placeholder="请选择课程" clearable style="width: 200px">
                        <el-option label="计算机导论" value="CS101" />
                        <el-option label="C语言程序设计" value="CS102" />
                        <el-option label="数据结构" value="CS201" />
                    </el-select>
                </el-form-item>
                <el-form-item label="班级">
                    <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable style="width: 200px">
                        <el-option label="计算机科学与技术2班" value="CS2" />
                        <el-option label="软件工程1班" value="SE1" />
                        <el-option label="信息安全3班" value="IS3" />
                    </el-select>
                </el-form-item>
                <el-form-item label="学号">
                    <el-input v-model="searchForm.studentId" placeholder="请输入学号" clearable />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 学生列表 -->
        <el-card class="list-card">
            <el-table :data="studentList" v-loading="loading" border stripe>
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="studentId" label="学号" width="120" />
                <el-table-column prop="name" label="姓名" width="100" />
                <el-table-column prop="gender" label="性别" width="80" align="center" />
                <el-table-column prop="class" label="班级" width="150" />
                <el-table-column prop="course" label="课程" width="150" />
                <el-table-column prop="attendance" label="出勤率" width="100" align="center">
                    <template #default="{ row }">
                        <el-progress :percentage="row.attendance"
                            :status="row.attendance >= 80 ? 'success' : 'warning'" />
                    </template>
                </el-table-column>
                <el-table-column prop="score" label="成绩" width="100" align="center">
                    <template #default="{ row }">
                        <span :class="{ 'text-danger': row.score < 60 }">{{ row.score || '未录入' }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button-group>
                            <el-button type="primary" link @click="handleViewDetail(row)">
                                详情
                            </el-button>
                            <el-button type="success" link @click="handleAttendance(row)">
                                考勤
                            </el-button>
                            <el-button type="warning" link @click="handleScore(row)">
                                成绩
                            </el-button>
                        </el-button-group>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </el-card>
    </div>
</template>

<script setup>
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'

    const router = useRouter()

    // 搜索表单
    const searchForm = ref({
        courseId: '',
        classId: '',
        studentId: '',
        name: ''
    })

    // 列表数据
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(100)

    // 模拟学生数据
    const studentList = ref([
        {
            studentId: '2024001',
            name: '张三',
            gender: '男',
            class: '计算机科学与技术2班',
            course: '计算机导论',
            attendance: 95,
            score: 88
        },
        {
            studentId: '2024002',
            name: '李四',
            gender: '女',
            class: '软件工程1班',
            course: 'C语言程序设计',
            attendance: 85,
            score: 92
        },
        {
            studentId: '2024003',
            name: '王五',
            gender: '男',
            class: '信息安全3班',
            course: '数据结构',
            attendance: 75,
            score: 65
        }
    ])

    // 方法
    const handleSearch = () => {
        loading.value = true
        // TODO: 调用搜索接口
        setTimeout(() => {
            loading.value = false
        }, 1000)
    }

    const handleReset = () => {
        searchForm.value = {
            courseId: '',
            classId: '',
            studentId: '',
            name: ''
        }
        handleSearch()
    }

    const handleExport = () => {
        // TODO: 实现导出功能
        ElMessage.success('导出成功')
    }

    const handleViewDetail = (row) => {
        router.push({
            path: '/teacher/students/detail',
            query: {
                studentId: row.studentId,
                courseId: row.course
            }
        })
    }

    const handleAttendance = (row) => {
        router.push({
            path: '/teacher/attendance',
            query: {
                studentId: row.studentId,
                courseId: row.course
            }
        })
    }

    const handleScore = (row) => {
        router.push({
            path: '/teacher/scores',
            query: {
                studentId: row.studentId,
                courseId: row.course
            }
        })
    }

    const handleSizeChange = (val) => {
        pageSize.value = val
        handleSearch()
    }

    const handleCurrentChange = (val) => {
        currentPage.value = val
        handleSearch()
    }
</script>

<style lang="scss" scoped>
    .students-container {
        .page-header {
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;

            h2 {
                margin: 0;
            }
        }

        .search-card {
            margin-bottom: 20px;
        }

        .list-card {
            .pagination-container {
                margin-top: 20px;
                display: flex;
                justify-content: flex-end;
            }
        }

        .text-danger {
            color: #f56c6c;
        }
    }
</style>