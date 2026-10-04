<template>
    <div class="teacher-list-container">
        <div class="page-header">
            <h2>教师管理</h2>
            <el-button type="primary" @click="router.push('/admin/teachers/add')">添加教师</el-button>
        </div>

        <el-card class="filter-card">
            <el-form :inline="true" :model="filterForm" class="filter-form">
                <el-form-item label="工号">
                    <el-input v-model="filterForm.teacherNo" placeholder="请输入工号" clearable />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="filterForm.realName" placeholder="请输入姓名" clearable />
                </el-form-item>
                <el-form-item label="所属院系">
                    <el-input v-model="filterForm.department" placeholder="请输入院系" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <el-table ref="tableRef" :data="teacherList" v-loading="loading" border stripe
                :style="{ width: tableWidth + 'px' }">
                <el-table-column prop="teacherNo" label="工号" :width="columnWidth.teacherNo" align="center" />
                <el-table-column prop="realName" label="姓名" :width="columnWidth.realName" align="center" />
                <el-table-column prop="gender" label="性别" :width="columnWidth.gender" align="center">
                    <template #default="{ row }">
                        {{ row.gender === 1 ? '男' : row.gender === 0 ? '女' : '—' }}
                    </template>
                </el-table-column>
                <el-table-column prop="phone" label="手机号" :width="columnWidth.phone" align="center" />
                <el-table-column prop="email" label="邮箱" :width="columnWidth.email" align="center"
                    show-overflow-tooltip />
                <el-table-column prop="department" label="所属院系" :width="columnWidth.department" align="center"
                    show-overflow-tooltip />
                <el-table-column prop="title" label="职称" :width="columnWidth.title" align="center" />
                <el-table-column prop="status" label="状态" :width="columnWidth.status" align="center">
                    <template #default="{ row }">
                        <span class="teacher-status-badge" :class="`status-${row.status}`">
                            {{ getStatusText(row.status) }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" :width="columnWidth.operation" align="center">
                    <template #default="{ row }">
                        <record-view-link :to="{ name: 'TeacherView', params: { id: row.id } }" />
                        <el-button type="primary" link @click.stop="router.push({ name: 'TeacherEdit', params: { id: row.id } })">
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
    import { ElMessage, ElMessageBox } from 'element-plus'
    import { getTeacherList, deleteTeacher, getStatusText } from '@/api/teacher'
    import SmartPagination from '@/components/common/SmartPagination.vue'
    import RecordViewLink from '@/components/common/RecordViewLink.vue'
    import { useTableWidth } from '@/composables/useTableWidth'

    const router = useRouter()
    const loading = ref(false)
    const teacherList = ref([])
    const total = ref(0)

    const minColumnWidths = {
        teacherNo: 120,
        realName: 100,
        gender: 80,
        phone: 120,
        email: 180,
        department: 150,
        title: 100,
        status: 120,
        operation: 180
    }
    const { tableRef, columnWidth, tableWidth } = useTableWidth(minColumnWidths)

    const filterForm = ref({
        teacherNo: '',
        realName: '',
        department: ''
    })

    const fetchTeachers = async (page, size) => {
        loading.value = true;
        try {
            console.log('开始获取教师列表，参数：', {
                page,
                size,
                ...filterForm.value
            });

            const response = await getTeacherList({
                page,
                size,
                ...filterForm.value
            });

            console.log('获取教师列表响应：', response);
            if (response?.code !== 200 || !response.data) {
                teacherList.value = [];
                total.value = 0;
                ElMessage.error(response?.message || '获取教师列表失败');
                return;
            }
            teacherList.value = response.data.records || [];
            total.value = response.data.total || 0;
        } catch (error) {
            console.error('获取教师列表失败：', error);
            ElMessage.error('获取教师列表失败');
        } finally {
            loading.value = false;
        }
    };

    const handleSearch = () => {
        handlePageChange({ page: 1, size: 20 });
    };

    const resetFilter = () => {
        filterForm.value = {
            teacherNo: '',
            realName: '',
            department: ''
        };
        handleSearch();
    };

    const handleDelete = async (id) => {
        try {
            await ElMessageBox.confirm('删除后无法恢复，确定删除这名教师吗？', '删除教师', {
                confirmButtonText: '删除',
                cancelButtonText: '取消',
                type: 'warning',
                confirmButtonClass: 'el-button--danger'
            })
            await deleteTeacher(id);
            ElMessage.success('删除成功');
            handleSearch();
        } catch (error) {
            if (error === 'cancel' || error === 'close') return
            ElMessage.error('删除失败');
        }
    };

    const handlePageChange = ({ page, size }) => {
        fetchTeachers(page, size);
    };

</script>

<style scoped>
    .teacher-list-container {
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

    .filter-form :deep(.el-input) {
        width: 200px;
    }

    .table-card {
        margin-bottom: 20px;
        overflow-x: auto;
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

    :deep(.el-button--link) {
        padding: 0 8px;
    }

    .teacher-status-badge {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        box-sizing: border-box;
        width: 72px;
        height: 28px;
        white-space: nowrap;
        flex-shrink: 0;
        border: 1px solid transparent;
        border-radius: 4px;
        line-height: 1;
    }

    .teacher-status-badge.status-0 {
        color: #67c23a;
        background-color: #f0f9eb;
        border-color: #e1f3d8;
    }

    .teacher-status-badge.status-1 {
        color: #e6a23c;
        background-color: #fdf6ec;
        border-color: #faecd8;
    }

    .teacher-status-badge.status-2 {
        color: #909399;
        background-color: #f4f4f5;
        border-color: #e9e9eb;
    }

    @media screen and (max-width: 768px) {
        .teacher-list-container {
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

        .filter-form :deep(.el-input) {
            width: 100%;
        }
    }
</style>
