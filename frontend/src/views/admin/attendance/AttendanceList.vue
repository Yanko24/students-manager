<template>
    <div class="attendance-list-container">
        <div class="page-header">
            <h2>考勤管理</h2>
            <el-button type="primary" @click="router.push('/admin/attendance/add')">添加考勤</el-button>
        </div>

        <el-card class="filter-card">
            <el-form :model="filterForm" inline class="filter-form">
                <el-form-item label="学号">
                    <el-input v-model="filterForm.studentNo" placeholder="请输入学号" />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="filterForm.studentName" placeholder="请输入姓名" />
                </el-form-item>
                <el-form-item label="课程">
                    <el-input v-model="filterForm.courseName" placeholder="请输入课程名称" />
                </el-form-item>
                <el-form-item label="班级">
                    <el-input v-model="filterForm.className" placeholder="请输入学院、专业或班级" />
                </el-form-item>
                <el-form-item label="日期">
                    <el-date-picker v-model="filterForm.date" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" />
                </el-form-item>
                <el-form-item label="学期">
                    <el-input v-model="filterForm.semester" placeholder="如 2026-2027-1" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="filterForm.status" clearable placeholder="全部状态" style="width: 130px">
                        <el-option v-for="status in attendanceStatuses" :key="status" :label="status" :value="status" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <el-table :data="attendanceList" v-loading="loading" border stripe style="width: 100%">
                <el-table-column prop="studentNo" label="学号" width="120" />
                <el-table-column prop="studentName" label="姓名" width="120" />
                <el-table-column prop="courseName" label="课程" width="180" />
                <el-table-column prop="className" label="班级" min-width="210" />
                <el-table-column prop="teacherName" label="授课教师" width="120" />
                <el-table-column prop="classPeriod" label="节次" width="100" />
                <el-table-column prop="date" label="日期" width="120">
                    <template #default="{ row }">{{ formatDate(row.date) }}</template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="statusType(row.status)">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" />
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <record-view-link :to="{ name: 'AttendanceView', params: { id: row.id } }" />
                        <el-button type="primary" link
                            @click.stop="router.push({ name: 'AttendanceEdit', params: { id: row.id } })">编辑</el-button>
                        <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
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
import { showApiError } from "@/utils/errorHandler";
    import { ref, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage, ElMessageBox } from 'element-plus'
    import { getAttendanceList, deleteAttendance } from '@/api/attendance'
    import { formatDate } from '@/utils/dateUtils'
    import RecordViewLink from '@/components/common/RecordViewLink.vue'

    const router = useRouter()
    const loading = ref(false)
    const attendanceList = ref([])
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)

    const filterForm = ref({
        studentNo: '',
        studentName: '',
        courseName: '',
        className: '',
        semester: '',
        status: '',
        date: ''
    })
    const attendanceStatuses = ['正常', '迟到', '早退', '缺勤', '请假']
    const statusType = (status) => ({ 正常: 'success', 迟到: 'warning', 早退: 'warning', 缺勤: 'danger', 请假: 'info' }[status] || 'info')

    const fetchAttendanceList = async () => {
        loading.value = true
        try {
            const params = {
                page: currentPage.value,
                size: pageSize.value,
                ...filterForm.value
            }
            const response = await getAttendanceList(params)
            if (response && response.data) {
                attendanceList.value = response.data.records || []
                total.value = response.data.total || 0
            }
        } catch (error) {
            console.error('获取考勤列表失败：', error)
            showApiError(error, '获取考勤列表失败')
        } finally {
            loading.value = false
        }
    }

    const handleSearch = () => {
        currentPage.value = 1
        fetchAttendanceList()
    }

    const resetFilter = () => {
        filterForm.value = {
            studentNo: '',
            studentName: '',
            courseName: '',
            className: '',
            semester: '',
            status: '',
            date: ''
        }
        handleSearch()
    }

    const handleDelete = async (id) => {
        try {
            await ElMessageBox.confirm('删除后无法恢复，确定删除这条考勤记录吗？', '删除考勤', {
                confirmButtonText: '删除',
                cancelButtonText: '取消',
                type: 'warning',
                confirmButtonClass: 'el-button--danger'
            })
            await deleteAttendance(id)
            ElMessage.success('删除成功')
            fetchAttendanceList()
        } catch (error) {
            if (error !== 'cancel' && error !== 'close') {
                console.error('删除失败：', error)
                showApiError(error, '删除失败')
            }
        }
    }

    const handleSizeChange = (val) => {
        pageSize.value = val
        fetchAttendanceList()
    }

    const handleCurrentChange = (val) => {
        currentPage.value = val
        fetchAttendanceList()
    }

    onMounted(() => {
        fetchAttendanceList()
    })
</script>

<style scoped>
    .attendance-list-container {
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
        margin: 0;
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
        width: 100%;
        font-size: 14px;
    }

    .table-card :deep(.el-table th) {
        background-color: #f5f7fa;
        color: #606266;
        font-weight: 600;
    }

    .table-card :deep(.el-table td) {
        padding: 8px 0;
    }

    .pagination-container {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
        white-space: nowrap;
        overflow-x: auto;
    }

    @media screen and (max-width: 768px) {
        .attendance-list-container {
            padding: 0;
        }
    }
</style>
