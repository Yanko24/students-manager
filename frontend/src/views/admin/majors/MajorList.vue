<template>
    <div class="major-list-container">
        <div class="page-header">
            <h2>专业管理</h2>
            <el-button type="primary" @click="router.push('/admin/majors/add')">添加专业</el-button>
        </div>

        <el-card class="filter-card">
            <el-form :inline="true" :model="filterForm" class="filter-form">
                <el-form-item label="专业代码">
                    <el-input v-model="filterForm.code" placeholder="请输入专业代码" clearable />
                </el-form-item>
                <el-form-item label="专业名称">
                    <el-input v-model="filterForm.name" placeholder="请输入专业名称" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <el-table ref="tableRef" :data="majorList" v-loading="loading" border :style="{ width: tableWidth + 'px' }">
                <el-table-column prop="code" label="专业代码" :width="columnWidth.code" align="center" />
                <el-table-column prop="name" label="专业名称" :width="columnWidth.name" align="center" />
                <el-table-column prop="collegeName" label="所属学院" :width="columnWidth.collegeName" align="center"
                    show-overflow-tooltip />
                <el-table-column prop="displayName" label="专业全称" :width="columnWidth.displayName" align="center"
                    show-overflow-tooltip />
                <el-table-column prop="grade" label="年级" :width="columnWidth.grade" align="center" />
                <el-table-column prop="classNo" label="班级" :width="columnWidth.classNo" align="center" />
                <el-table-column prop="headTeacherName" label="班主任" :width="columnWidth.headTeacherName"
                    align="center" />
                <el-table-column prop="studentCount" label="学生人数" :width="columnWidth.studentCount" align="center" />
                <el-table-column prop="status" label="状态" :width="columnWidth.status" align="center">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)" size="small">
                            {{ getStatusText(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" :width="columnWidth.operation" fixed="right" align="center">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="router.push(`/admin/majors/${row.id}`)">
                            查看
                        </el-button>
                        <el-button type="primary" link @click="router.push(`/admin/majors/${row.id}/edit`)">
                            编辑
                        </el-button>
                        <el-button type="danger" link @click="handleDelete(row.id)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <smart-pagination ref="paginationRef" :total="total" :on-page-change="handlePageChange" />
        </el-card>
    </div>
</template>

<script setup>
    import { ref, computed, nextTick, onUnmounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getAllMajors, deleteMajor, getStatusType, getStatusText } from '@/api/major'
    import SmartPagination from '@/components/common/SmartPagination.vue'
    import { useTableWidth } from '@/composables/useTableWidth'

    const router = useRouter()
    const loading = ref(false)
    const majorList = ref([])
    const total = ref(0)
    const paginationRef = ref(null)
    const pageSize = ref(10)

    // 定义每列的最小宽度
    const minColumnWidths = {
        code: 100,
        name: 120,
        collegeName: 150,
        displayName: 180,
        grade: 80,
        classNo: 80,
        headTeacherName: 100,
        studentCount: 100,
        status: 100,
        operation: 180
    }

    // 使用表格宽度计算组合式函数
    const { tableRef, columnWidth, tableWidth } = useTableWidth(minColumnWidths)

    const filterForm = ref({
        code: '',
        name: ''
    })

    const fetchMajors = async (page, size) => {
        loading.value = true;
        try {
            console.log('开始获取专业列表，参数：', {
                page,
                size,
                code: filterForm.value.code,
                name: filterForm.value.name
            });

            const response = await getAllMajors({
                page,
                size,
                code: filterForm.value.code,
                name: filterForm.value.name
            });

            console.log('获取专业列表响应：', response);
            if (response && response.data) {
                majorList.value = response.data.records || [];
                total.value = response.data.total || 0;
            }
        } catch (error) {
            console.error('获取专业列表失败：', error);
            ElMessage.error('获取专业列表失败');
        } finally {
            loading.value = false;
        }
    };

    const handleSearch = () => {
        if (paginationRef.value) {
            paginationRef.value.resetToFirstPage()
        } else {
            handlePageChange({ page: 1, size: pageSize.value })
        }
    };

    const resetFilter = () => {
        filterForm.value = {
            code: '',
            name: ''
        };
        handleSearch();
    };

    const handleDelete = async (id) => {
        try {
            console.log('开始删除专业，ID：', id);
            await deleteMajor(id);
            console.log('删除专业成功');
            ElMessage.success('删除成功');
            handleSearch();
        } catch (error) {
            console.error('删除专业失败：', error);
            ElMessage.error('删除失败');
        }
    };

    const handlePageChange = ({ page, size }) => {
        pageSize.value = size
        fetchMajors(page, size);
    };

</script>

<style scoped>
    .major-list-container {
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

    @media screen and (max-width: 768px) {
        .major-list-container {
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
