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
                <el-form-item label="学院">
                    <el-input v-model="filterForm.collegeName" placeholder="请输入学院" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <el-table :data="teacherList" v-loading="loading" border style="width: 100%">
                <el-table-column prop="teacherNo" label="工号" min-width="120" align="center" />
                <el-table-column prop="realName" label="姓名" min-width="100" align="center" />
                <el-table-column prop="gender" label="性别" min-width="80" align="center">
                    <template #default="{ row }">
                        {{ row.gender === 0 ? '男' : '女' }}
                    </template>
                </el-table-column>
                <el-table-column prop="phone" label="手机号" min-width="120" align="center" />
                <el-table-column prop="email" label="邮箱" min-width="180" align="center" show-overflow-tooltip />
                <el-table-column prop="collegeName" label="所属学院" min-width="150" align="center" show-overflow-tooltip />
                <el-table-column prop="title" label="职称" min-width="100" align="center" />
                <el-table-column prop="status" label="状态" min-width="80" align="center">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)" size="small">
                            {{ getStatusText(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" min-width="180" fixed="right" align="center">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="router.push(`/admin/teachers/${row.id}`)">
                            查看
                        </el-button>
                        <el-button type="primary" link @click="router.push(`/admin/teachers/${row.id}/edit`)">
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
    import { getTeacherList, deleteTeacher, getStatusType, getStatusText } from '@/api/teacher'
    import SmartPagination from '@/components/common/SmartPagination.vue'

    const router = useRouter()
    const loading = ref(false)
    const teacherList = ref([])
    const total = ref(0)

    const filterForm = ref({
        teacherNo: '',
        realName: '',
        collegeName: ''
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
            if (response && response.data) {
                teacherList.value = response.data.records || [];
                total.value = response.data.total || 0;
            }
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
            collegeName: ''
        };
        handleSearch();
    };

    const handleDelete = async (id) => {
        try {
            console.log('开始删除教师，ID：', id);
            await deleteTeacher(id);
            console.log('删除教师成功');
            ElMessage.success('删除成功');
            handleSearch();
        } catch (error) {
            console.error('删除教师失败：', error);
            ElMessage.error('删除失败');
        }
    };

    const handlePageChange = ({ page, size }) => {
        fetchTeachers(page, size);
    };

    onMounted(() => {
        handleSearch();
    });
</script>

<style scoped>
    .teacher-list-container {
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

    :deep(.el-tag) {
        min-width: 60px;
    }

    @media screen and (max-width: 768px) {
        .teacher-list-container {
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
    }
</style>