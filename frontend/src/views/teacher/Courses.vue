<template>
    <div class="courses-container">
        <div class="page-header">
            <h2>我的课程</h2>
        </div>

        <!-- 搜索表单 -->
        <el-card class="search-card">
            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="课程名称">
                    <el-input v-model="searchForm.name" placeholder="请输入课程名称" clearable />
                </el-form-item>
                <el-form-item label="学期">
                    <el-select v-model="searchForm.semester" placeholder="请选择学期" clearable style="width: 200px">
                        <el-option label="2023-2024-2" value="2023-2024-2" />
                        <el-option label="2023-2024-1" value="2023-2024-1" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 课程列表 -->
        <el-card class="list-card">
            <el-table :data="courseList" v-loading="loading" border stripe>
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="code" label="课程编号" width="120" />
                <el-table-column prop="name" label="课程名称" min-width="150" />
                <el-table-column prop="type" label="课程类型" width="100" align="center" />
                <el-table-column prop="credit" label="学分" width="80" align="center" />
                <el-table-column prop="hours" label="学时" width="80" align="center" />
                <el-table-column prop="class" label="班级" width="150" />
                <el-table-column prop="studentCount" label="学生人数" width="100" align="center" />
                <el-table-column prop="semester" label="开课学期" width="120" align="center" />
                <el-table-column prop="status" label="状态" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.status === '已开课' ? 'success' : 'info'">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="250" fixed="right">
                    <template #default="{ row }">
                        <el-button-group>
                            <el-button type="primary" link @click="handleViewStudents(row)">
                                查看学生
                            </el-button>
                            <el-button type="success" link @click="handleAttendance(row)">
                                考勤
                            </el-button>
                            <el-button type="warning" link @click="handleScores(row)">
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
        name: '',
        semester: ''
    })

    // 列表数据
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(100)

    // 模拟课程数据
    const courseList = ref([
        {
            code: 'CS101',
            name: '计算机导论',
            type: '必修课',
            credit: 3,
            hours: 48,
            class: '计算机科学与技术2班',
            studentCount: 30,
            semester: '2023-2024-2',
            status: '已开课'
        },
        {
            code: 'CS102',
            name: 'C语言程序设计',
            type: '必修课',
            credit: 4,
            hours: 64,
            class: '软件工程1班',
            studentCount: 35,
            semester: '2023-2024-2',
            status: '已开课'
        },
        {
            code: 'CS201',
            name: '数据结构',
            type: '必修课',
            credit: 4,
            hours: 64,
            class: '信息安全3班',
            studentCount: 32,
            semester: '2023-2024-2',
            status: '已开课'
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
            name: '',
            semester: ''
        }
        handleSearch()
    }

    const handleViewStudents = (row) => {
        router.push({
            path: '/teacher/students',
            query: {
                courseId: row.code
            }
        })
    }

    const handleAttendance = (row) => {
        router.push({
            path: '/teacher/attendance',
            query: {
                courseId: row.code,
                className: row.class
            }
        })
    }

    const handleScores = (row) => {
        router.push({
            path: '/teacher/scores',
            query: {
                courseId: row.code,
                className: row.class
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
    .courses-container {
        .page-header {
            margin-bottom: 20px;

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
    }
</style>