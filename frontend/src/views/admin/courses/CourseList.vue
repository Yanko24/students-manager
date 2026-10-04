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
                <el-form-item label="院系">
                    <el-input v-model="filterForm.department" placeholder="请输入院系" clearable />
                </el-form-item>
                <el-form-item label="学期">
                    <el-input v-model="filterForm.semester" placeholder="请输入学期" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <el-table ref="tableRef" :data="courseList" v-loading="loading" border stripe
                :style="{ width: tableWidth + 'px' }">
                <el-table-column prop="code" label="课程代码" :width="columnWidth.code" align="center" />
                <el-table-column prop="name" label="课程名称" :width="columnWidth.name" align="center"
                    show-overflow-tooltip />
                <el-table-column prop="college" label="所属学院" :width="columnWidth.college" align="center"
                    show-overflow-tooltip>
                    <template #default="{ row }">
                        <span class="single-line-cell" :title="row.college || ''">{{ row.college || '—' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="credit" label="学分" :width="columnWidth.credit" align="center" />
                <el-table-column prop="hours" label="学时" :width="columnWidth.hours" align="center" />
                <el-table-column prop="type" label="课程类型" :width="columnWidth.type" align="center" />
                <el-table-column prop="teacher" label="授课教师" :width="columnWidth.teacher" align="center"
                    show-overflow-tooltip />
                <el-table-column prop="semester" label="学期" :width="columnWidth.semester" align="center" />
                <el-table-column prop="statusText" label="状态" :width="columnWidth.status" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 0 ? 'info' : row.status === 1 ? 'success' : 'warning'" size="small">
                            {{ row.statusText }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" :width="columnWidth.operation" fixed="right" align="center">
                    <template #default="{ row }">
                        <record-view-link :to="{ name: 'CourseView', params: { id: row.id } }" />
                        <el-button type="primary" link @click.stop="router.push({ name: 'CourseEdit', params: { id: row.id } })">
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
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import RecordViewLink from '@/components/common/RecordViewLink.vue'
    import { ElMessage, ElMessageBox } from 'element-plus'
    import { getCourseList, deleteCourse } from '@/api/course'
    import SmartPagination from '@/components/common/SmartPagination.vue'
    import { useTableWidth } from '@/composables/useTableWidth'

    const router = useRouter()
    const loading = ref(false)
    const courseList = ref([])
    const total = ref(0)

    const minColumnWidths = {
        code: 120,
        name: 180,
        college: 280,
        credit: 80,
        hours: 80,
        type: 110,
        teacher: 120,
        semester: 140,
        status: 100,
        operation: 180
    }
    const { tableRef, columnWidth, tableWidth } = useTableWidth(minColumnWidths)

    const filterForm = ref({
        code: '',
        name: '',
        department: '',
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
            if (response?.code === 200 && response.data) {
                courseList.value = response.data.records || []
                total.value = response.data.total || 0
            } else {
                courseList.value = []
                total.value = 0
                ElMessage.error(response?.message || '获取课程列表失败')
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
            department: '',
            semester: ''
        }
        handleSearch()
    }

    const handleDelete = async (id) => {
        try {
            await ElMessageBox.confirm('删除后无法恢复，确定删除这门课程吗？', '删除课程', {
                confirmButtonText: '删除',
                cancelButtonText: '取消',
                type: 'warning',
                confirmButtonClass: 'el-button--danger'
            })
            await deleteCourse(id)
            ElMessage.success('删除成功')
            handleSearch()
        } catch (error) {
            if (error === 'cancel' || error === 'close') return
            ElMessage.error('删除失败')
        }
    }

    const handlePageChange = ({ page, size }) => {
        fetchCourses(page, size)
    }

</script>

<style scoped>
    .course-list-container {
        padding: 0;
        width: 100%;
        margin: 0 auto;
        box-sizing: border-box;
    }

    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        flex-wrap: wrap;
        gap: 10px;
    }

    .page-header h2 {
        margin: 0;
        font-size: 24px;
        color: #303133;
    }

    .filter-card {
        margin-bottom: 20px;
    }

    .filter-form {
        display: flex;
        flex-wrap: wrap;
        justify-content: flex-start;
        align-items: center;
        gap: 12px 16px;
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

    .table-card :deep(.el-table .cell) {
        white-space: nowrap !important;
        word-break: keep-all;
        overflow-wrap: normal;
    }

    .single-line-cell {
        display: inline-block;
        max-width: 100%;
        white-space: nowrap !important;
        word-break: keep-all;
        overflow-wrap: normal;
    }

    .table-card :deep(.el-table__fixed-right) {
        height: 100% !important;
        box-shadow: -2px 0 8px rgba(0, 0, 0, 0.15);
    }

    .table-card :deep(.el-table__fixed-right::before) {
        content: '';
        position: absolute;
        left: -1px;
        top: 0;
        bottom: 0;
        width: 1px;
        background-color: #EBEEF5;
        z-index: 1;
    }

    :deep(.el-button--link) {
        padding: 0 8px;
    }

    @media screen and (max-width: 768px) {
        .course-list-container {
            padding: 0;
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
