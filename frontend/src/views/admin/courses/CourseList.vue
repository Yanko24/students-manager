<template>
    <div class="course-list-container">
        <div class="page-header">
            <h2>课程管理</h2>
            <el-button type="primary" @click="router.push('/admin/courses/add')">添加课程</el-button>
        </div>

        <el-card class="filter-card">
            <el-form :inline="true" :model="filterForm" class="filter-form">
                <el-form-item label="课程代码">
                    <el-input v-model="filterForm.code" placeholder="请输入课程代码" clearable />
                </el-form-item>
                <el-form-item label="课程名称">
                    <el-input v-model="filterForm.name" placeholder="请输入课程名称" clearable />
                </el-form-item>
                <el-form-item label="学院">
                    <el-input v-model="filterForm.college" placeholder="请输入学院" clearable />
                </el-form-item>
                <el-form-item label="学期">
                    <el-select v-model="filterForm.semester" placeholder="请选择学期" clearable>
                        <el-option label="2023-2024-1" value="2023-2024-1" />
                        <el-option label="2023-2024-2" value="2023-2024-2" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <el-table :data="courseList" v-loading="loading" border style="width: 100%">
                <el-table-column prop="code" label="课程代码" min-width="120" align="center" />
                <el-table-column prop="name" label="课程名称" min-width="150" align="center" />
                <el-table-column prop="college" label="所属学院" min-width="150" align="center" />
                <el-table-column prop="credit" label="学分" min-width="80" align="center" />
                <el-table-column prop="hours" label="学时" min-width="80" align="center" />
                <el-table-column prop="type" label="课程类型" min-width="100" align="center" />
                <el-table-column prop="teacher" label="授课教师" min-width="100" align="center" />
                <el-table-column prop="semester" label="学期" min-width="120" align="center" />
                <el-table-column prop="status" label="状态" min-width="80" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.status === '正常' ? 'success' : 'danger'" size="small">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" min-width="180" fixed="right" align="center">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="router.push(`/admin/courses/${row.id}`)">
                            查看
                        </el-button>
                        <el-button type="primary" link @click="router.push(`/admin/courses/${row.id}/edit`)">
                            编辑
                        </el-button>
                        <el-button type="danger" link @click="handleDelete(row.id)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <smart-pagination :total="total" :on-page-change="handlePageChange" />
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getCourseList, deleteCourse } from '@/api/course'
    import SmartPagination from '@/components/common/SmartPagination.vue'

    const router = useRouter()
    const loading = ref(false)
    const courseList = ref([])
    const total = ref(0)

    const filterForm = ref({
        code: '',
        name: '',
        college: '',
        semester: ''
    })

    const fetchCourses = async (page, size) => {
        loading.value = true
        try {
            console.log('开始获取课程列表，参数：', {
                page,
                size,
                ...filterForm.value
            })

            const response = await getCourseList({
                page,
                size,
                ...filterForm.value
            })

            console.log('获取课程列表响应：', response)
            if (response && response.data) {
                courseList.value = response.data.records || []
                total.value = response.data.total || 0
            }
        } catch (error) {
            console.error('获取课程列表失败：', error)
            ElMessage.error('获取课程列表失败')
        } finally {
            loading.value = false
        }
    }

    const handleSearch = () => {
        handlePageChange({ page: 1, size: 20 })
    }

    const resetFilter = () => {
        filterForm.value = {
            code: '',
            name: '',
            college: '',
            semester: ''
        }
        handleSearch()
    }

    const handleDelete = async (id) => {
        try {
            console.log('开始删除课程，ID：', id)
            await deleteCourse(id)
            console.log('删除课程成功')
            ElMessage.success('删除成功')
            handleSearch()
        } catch (error) {
            console.error('删除课程失败：', error)
            ElMessage.error('删除失败')
        }
    }

    const handlePageChange = ({ page, size }) => {
        fetchCourses(page, size)
    }

    onMounted(() => {
        handleSearch()
    })
</script>

<style scoped>
    .course-list-container {
        padding: 20px;
        width: 100%;
        margin: 0 auto;
        box-sizing: border-box;
    }

    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    .page-header h2 {
        margin: 0;
        font-size: 24px;
        color: #303133;
    }

    .filter-card {
        margin-bottom: 20px;
        background-color: #f5f7fa;
    }

    .filter-form {
        display: flex;
        flex-wrap: wrap;
        justify-content: flex-start;
        align-items: center;
        gap: 20px;
    }

    .filter-form :deep(.el-form-item) {
        margin-bottom: 0;
        margin-right: 0;
    }

    .filter-form :deep(.el-input),
    .filter-form :deep(.el-select) {
        width: 200px;
    }

    .table-card {
        margin-bottom: 20px;
        overflow-x: auto;
    }

    .table-card :deep(.el-table) {
        font-size: 14px;
        width: 100%;
    }

    .table-card :deep(.el-table th) {
        background-color: #f5f7fa;
        color: #606266;
        font-weight: 600;
    }

    .table-card :deep(.el-table td) {
        padding: 8px 0;
    }

    :deep(.el-button--link) {
        padding: 0 8px;
    }

    @media screen and (max-width: 768px) {
        .course-list-container {
            padding: 10px;
        }

        .page-header {
            flex-direction: column;
            align-items: flex-start;
        }

        .filter-form {
            flex-direction: column;
            align-items: stretch;
        }

        .filter-form :deep(.el-input),
        .filter-form :deep(.el-select) {
            width: 100%;
        }
    }
</style>