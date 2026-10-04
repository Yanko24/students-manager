<template>
    <div class="attendance-list-container">
        <div class="page-header">
            <h2>考勤管理</h2>
            <el-button type="primary" @click="router.push('/admin/attendance/add')">添加考勤</el-button>
        </div>

        <el-card class="filter-card">
            <el-form :model="filterForm" inline>
                <el-form-item label="学号">
                    <el-input v-model="filterForm.studentNo" placeholder="请输入学号" />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="filterForm.studentName" placeholder="请输入姓名" />
                </el-form-item>
                <el-form-item label="课程">
                    <el-input v-model="filterForm.courseName" placeholder="请输入课程名称" />
                </el-form-item>
                <el-form-item label="日期">
                    <el-date-picker v-model="filterForm.date" type="date" placeholder="请选择日期" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <el-table :data="attendanceList" v-loading="loading" border style="width: 100%">
                <el-table-column prop="studentNo" label="学号" width="120" />
                <el-table-column prop="studentName" label="姓名" width="120" />
                <el-table-column prop="courseName" label="课程" width="180" />
                <el-table-column prop="date" label="日期" width="120" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.status === '正常' ? 'success' : row.status === '迟到' ? 'warning' : 'danger'">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" />
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link
                            @click="router.push(`/admin/attendance/${row.id}`)">查看</el-button>
                        <el-button type="primary" link
                            @click="router.push(`/admin/attendance/${row.id}/edit`)">编辑</el-button>
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
    import { ref, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage, ElMessageBox } from 'element-plus'
    import { getAttendanceList, deleteAttendance } from '@/api/attendance'

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
        date: ''
    })

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
            ElMessage.error('获取考勤列表失败')
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
            date: ''
        }
        handleSearch()
    }

    const handleDelete = async (id) => {
        try {
            await ElMessageBox.confirm('确定要删除这条考勤记录吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            await deleteAttendance(id)
            ElMessage.success('删除成功')
            fetchAttendanceList()
        } catch (error) {
            if (error !== 'cancel') {
                console.error('删除失败：', error)
                ElMessage.error('删除失败')
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
    }

    .table-card {
        margin-bottom: 20px;
    }

    .pagination-container {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
    }

    @media screen and (max-width: 768px) {
        .attendance-list-container {
            padding: 10px;
        }
    }
</style>