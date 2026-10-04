<template>
    <div class="student-list-container">
        <div class="page-header">
            <h2>学生管理</h2>
            <div class="header-actions">
                <el-button @click="importDialogVisible = true">批量导入</el-button>
                <el-button type="primary" @click="router.push('/admin/students/add')">添加学生</el-button>
            </div>
        </div>

        <el-dialog v-model="importDialogVisible" title="批量导入学生" width="520px">
            <p>下载模板并填写后上传 CSV。需要先创建对应的专业、年级和班级；导入采用全量校验，任一行有误都会取消本次导入。</p>
            <p><a href="/templates/student-import-template.csv" download>下载学生导入模板</a></p>
            <el-upload
                ref="uploadRef"
                accept=".csv,text/csv"
                :auto-upload="false"
                :limit="1"
                :file-list="uploadFiles"
                :on-change="handleFileChange"
                :on-remove="handleFileRemove"
            >
                <el-button>选择 CSV 文件</el-button>
                <template #tip>
                    <div class="el-upload__tip">最多 5 MB、5000 条；支持 UTF-8 和 GB18030 编码。</div>
                </template>
            </el-upload>
            <template #footer>
                <el-button @click="importDialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="importing" :disabled="!selectedFile" @click="handleImport">开始导入</el-button>
            </template>
        </el-dialog>

        <el-card class="filter-card">
            <el-form :inline="true" :model="filterForm" class="filter-form">
                <el-form-item label="学号">
                    <el-input v-model="filterForm.studentNo" placeholder="请输入学号" clearable />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="filterForm.realName" placeholder="请输入姓名" clearable />
                </el-form-item>
                <el-form-item label="专业">
                    <el-input v-model="filterForm.majorCode" placeholder="请输入专业" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <el-table ref="tableRef" :data="studentList" v-loading="loading" border
                :style="{ width: tableWidth + 'px' }">
                <el-table-column prop="studentNo" label="学号" :width="columnWidth.studentNo" align="center" />
                <el-table-column prop="realName" label="姓名" :width="columnWidth.realName" align="center" />
                <el-table-column prop="gender" label="性别" :width="columnWidth.gender" align="center">
                    <template #default="{ row }">
                        {{ row.gender === 1 ? '男' : '女' }}
                    </template>
                </el-table-column>
                <el-table-column prop="phone" label="手机号" :width="columnWidth.phone" align="center" />
                <el-table-column prop="email" label="邮箱" :width="columnWidth.email" align="center"
                    show-overflow-tooltip />
                <el-table-column prop="grade" label="年级" :width="columnWidth.grade" align="center" />
                <el-table-column prop="majorCode" label="专业代码" :width="columnWidth.majorCode" align="center" />
                <el-table-column prop="classNo" label="班级" :width="columnWidth.classNo" align="center" />
                <el-table-column prop="displayName" label="专业全称" :width="columnWidth.displayName" align="center"
                    show-overflow-tooltip />
                <el-table-column prop="status" label="状态" :width="columnWidth.status" align="center">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">
                            {{ getStatusText(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" :width="columnWidth.operation" fixed="right" align="center">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="router.push(`/admin/students/${row.id}`)">
                            查看
                        </el-button>
                        <el-button type="primary" link @click="router.push(`/admin/students/${row.id}/edit`)">
                            编辑
                        </el-button>
                        <el-popconfirm title="确定要删除这个学生吗？" @confirm="handleDelete(row.id)" confirm-button-text="确定"
                            cancel-button-text="取消" confirm-button-type="danger">
                            <template #reference>
                                <el-button type="danger" link>删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>

            <smart-pagination :total="total" :on-page-change="handlePageChange" />
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted, computed, nextTick, onUnmounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage, ElMessageBox } from 'element-plus'
    import { getStudentList, deleteStudent, importStudents, getStatusType, getStatusText } from '@/api/student'
    import SmartPagination from '@/components/common/SmartPagination.vue'
    import { useTableWidth } from '@/composables/useTableWidth'

    const router = useRouter()
    const loading = ref(false)
    const studentList = ref([])
    const total = ref(0)
    const importDialogVisible = ref(false)
    const importing = ref(false)
    const selectedFile = ref(null)
    const uploadFiles = ref([])
    const currentPage = ref(1)
    const pageSize = ref(20)

    // 定义每列的最小宽度
    const minColumnWidths = {
        studentNo: 120,
        realName: 100,
        gender: 80,
        phone: 120,
        email: 180,
        grade: 80,
        majorCode: 100,
        classNo: 80,
        displayName: 180,
        status: 100,
        operation: 180
    }

    // 使用表格宽度计算组合式函数
    const { tableRef, columnWidth, tableWidth } = useTableWidth(minColumnWidths)

    const filterForm = ref({
        studentNo: '',
        realName: '',
        majorCode: ''
    })

    const fetchStudents = async (page, size) => {
        loading.value = true;
        try {
            currentPage.value = page;
            pageSize.value = size;

            const response = await getStudentList({
                page,
                size,
                ...filterForm.value
            });

            if (response && response.data) {
                studentList.value = response.data.records || [];
                total.value = response.data.total || 0;

                if (studentList.value.length === 0 && currentPage.value > 1) {
                    await fetchStudents(currentPage.value - 1, pageSize.value);
                }
            }
        } catch (error) {
            console.error('获取学生列表失败：', error);
            ElMessage.error('获取学生列表失败');
        } finally {
            loading.value = false;
        }
    };

    const handleSearch = () => {
        fetchStudents(1, pageSize.value);
    };

    const resetFilter = () => {
        filterForm.value = {
            studentNo: '',
            realName: '',
            majorCode: ''
        };
        handleSearch();
    };

    const handleDelete = async (id) => {
        try {
            loading.value = true;
            await deleteStudent(id);
            ElMessage.success('删除成功');

            const currentPageRecords = studentList.value.length;

            if (currentPageRecords === 1 && currentPage.value > 1) {
                await fetchStudents(currentPage.value - 1, pageSize.value);
            } else {
                await fetchStudents(currentPage.value, pageSize.value);
            }
        } catch (error) {
            console.error('删除学生失败：', error);
            ElMessage.error(error.response?.data?.message || '删除失败');
        } finally {
            loading.value = false;
        }
    };

    const handlePageChange = ({ page, size }) => {
        fetchStudents(page, size);
    };

    const handleFileChange = (uploadFile, files) => {
        selectedFile.value = uploadFile.raw || null
        uploadFiles.value = files.slice(-1)
    }

    const handleFileRemove = () => {
        selectedFile.value = null
        uploadFiles.value = []
    }

    const handleImport = async () => {
        if (!selectedFile.value) return
        importing.value = true
        try {
            const response = await importStudents(selectedFile.value)
            const count = response?.data?.imported ?? 0
            ElMessage.success(`成功导入 ${count} 名学生`)
            importDialogVisible.value = false
            uploadFiles.value = []
            selectedFile.value = null
            await fetchStudents(1, pageSize.value)
        } catch (error) {
            ElMessage.error(error?.message || '导入失败，请检查文件内容')
        } finally {
            importing.value = false
        }
    }

    onMounted(() => {
        handleSearch();
    });
</script>

<style scoped>
    .student-list-container {
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
        flex-wrap: wrap;
        gap: 10px;
    }

    .header-actions {
        display: flex;
        gap: 10px;
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

    .filter-form :deep(.el-input) {
        width: 200px;
    }

    .table-card {
        margin-bottom: 20px;
        overflow-x: auto;
        padding: 20px;
    }

    .table-card :deep(.el-table) {
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

    .table-card :deep(.el-table--border) {
        border: 1px solid #EBEEF5;
    }

    .table-card :deep(.el-table--border th),
    .table-card :deep(.el-table--border td) {
        border-right: 1px solid #EBEEF5;
    }

    .table-card :deep(.el-table--border::after),
    .table-card :deep(.el-table--border::before) {
        background-color: #EBEEF5;
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

    .pagination-container {
        margin-top: 20px;
        display: flex;
        justify-content: center;
    }

    :deep(.el-button--link) {
        padding: 0 8px;
    }

    :deep(.el-tag) {
        min-width: 60px;
    }

    :deep(.el-popconfirm__main) {
        margin: 8px 0;
    }

    :deep(.el-button--link) {
        margin: 0 4px;
    }

    @media screen and (max-width: 768px) {
        .student-list-container {
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

        .filter-form :deep(.el-input) {
            width: 100%;
        }

        .pagination-container {
            overflow-x: auto;
        }
    }
</style>
