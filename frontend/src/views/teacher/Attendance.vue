<template>
    <div class="attendance-container">
        <div class="page-header">
            <h2>考勤管理</h2>
            <el-button type="primary" @click="handleBatchAttendance">批量考勤</el-button>
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
                <el-form-item label="日期">
                    <el-date-picker v-model="searchForm.date" type="date" placeholder="选择日期" clearable
                        style="width: 200px" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 考勤列表 -->
        <el-card class="list-card">
            <el-table :data="attendanceList" v-loading="loading" border stripe>
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="studentId" label="学号" width="120" />
                <el-table-column prop="name" label="姓名" width="100" />
                <el-table-column prop="class" label="班级" width="150" />
                <el-table-column prop="course" label="课程" width="150" />
                <el-table-column prop="date" label="日期" width="120" align="center" />
                <el-table-column prop="time" label="时间" width="120" align="center" />
                <el-table-column prop="status" label="状态" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="150" />
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button-group>
                            <el-button type="primary" link @click="handleEdit(row)">
                                编辑
                            </el-button>
                            <el-button type="danger" link @click="handleDelete(row)">
                                删除
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

        <!-- 考勤编辑对话框 -->
        <el-dialog v-model="dialogVisible" title="考勤记录" width="500px">
            <el-form :model="attendanceForm" label-width="100px">
                <el-form-item label="学号">
                    <el-input v-model="attendanceForm.studentId" disabled />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="attendanceForm.name" disabled />
                </el-form-item>
                <el-form-item label="课程">
                    <el-input v-model="attendanceForm.course" disabled />
                </el-form-item>
                <el-form-item label="日期">
                    <el-date-picker v-model="attendanceForm.date" type="date" style="width: 100%" />
                </el-form-item>
                <el-form-item label="时间">
                    <el-time-picker v-model="attendanceForm.time" style="width: 100%" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="attendanceForm.status" style="width: 100%">
                        <el-option label="正常" value="正常" />
                        <el-option label="迟到" value="迟到" />
                        <el-option label="早退" value="早退" />
                        <el-option label="缺勤" value="缺勤" />
                        <el-option label="请假" value="请假" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="attendanceForm.remark" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSave">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
    import { ref } from 'vue'
    import { ElMessage, ElMessageBox } from 'element-plus'

    // 搜索表单
    const searchForm = ref({
        courseId: '',
        classId: '',
        date: ''
    })

    // 列表数据
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(100)

    // 模拟考勤数据
    const attendanceList = ref([
        {
            studentId: '2024001',
            name: '张三',
            class: '计算机科学与技术2班',
            course: '计算机导论',
            date: '2024-03-20',
            time: '08:00',
            status: '正常',
            remark: ''
        },
        {
            studentId: '2024002',
            name: '李四',
            class: '软件工程1班',
            course: 'C语言程序设计',
            date: '2024-03-20',
            time: '08:15',
            status: '迟到',
            remark: '交通拥堵'
        },
        {
            studentId: '2024003',
            name: '王五',
            class: '信息安全3班',
            course: '数据结构',
            date: '2024-03-20',
            time: '08:00',
            status: '缺勤',
            remark: '请假'
        }
    ])

    // 对话框数据
    const dialogVisible = ref(false)
    const attendanceForm = ref({
        studentId: '',
        name: '',
        course: '',
        date: '',
        time: '',
        status: '',
        remark: ''
    })

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
            date: ''
        }
        handleSearch()
    }

    const handleBatchAttendance = () => {
        // TODO: 实现批量考勤功能
        ElMessage.info('批量考勤功能开发中')
    }

    const getStatusType = (status) => {
        const typeMap = {
            '正常': 'success',
            '迟到': 'warning',
            '早退': 'warning',
            '缺勤': 'danger',
            '请假': 'info'
        }
        return typeMap[status] || 'info'
    }

    const handleEdit = (row) => {
        attendanceForm.value = { ...row }
        dialogVisible.value = true
    }

    const handleDelete = (row) => {
        ElMessageBox.confirm('确定要删除该考勤记录吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            // TODO: 调用删除接口
            ElMessage.success('删除成功')
        })
    }

    const handleSave = () => {
        // TODO: 调用保存接口
        ElMessage.success('保存成功')
        dialogVisible.value = false
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
    .attendance-container {
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
    }
</style>