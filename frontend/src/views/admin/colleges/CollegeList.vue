<template>
    <div class="college-list-container">
        <div class="page-header">
            <h2>学院管理</h2>
            <el-button type="primary" @click="router.push('/admin/colleges/add')">添加学院</el-button>
        </div>

        <el-card class="filter-card">
            <el-form :inline="true" :model="filterForm" class="filter-form">
                <el-form-item label="学院代码">
                    <el-input v-model="filterForm.code" placeholder="请输入学院代码" clearable />
                </el-form-item>
                <el-form-item label="学院名称">
                    <el-input v-model="filterForm.name" placeholder="请输入学院名称" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <el-table :data="collegeList" v-loading="loading" border style="width: 100%">
                <el-table-column prop="code" label="学院代码" min-width="120" align="center" />
                <el-table-column prop="name" label="学院名称" min-width="150" align="center" />
                <el-table-column prop="description" label="描述" min-width="200" align="center" show-overflow-tooltip />
                <el-table-column prop="createTime" label="创建时间" min-width="180" align="center" />
                <el-table-column prop="updateTime" label="更新时间" min-width="180" align="center" />
                <el-table-column label="操作" min-width="180" fixed="right" align="center">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="router.push(`/admin/colleges/${row.id}`)">
                            查看
                        </el-button>
                        <el-button type="primary" link @click="router.push(`/admin/colleges/${row.id}/edit`)">
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
    import { getCollegeList, deleteCollege } from '@/api/college'
    import SmartPagination from '@/components/common/SmartPagination.vue'

    const router = useRouter()
    const loading = ref(false)
    const collegeList = ref([])
    const total = ref(0)

    const filterForm = ref({
        code: '',
        name: ''
    })

    const fetchColleges = async (page, size) => {
        loading.value = true;
        try {
            console.log('开始获取学院列表，参数：', {
                page,
                size,
                ...filterForm.value
            });

            const response = await getCollegeList({
                page,
                size,
                ...filterForm.value
            });

            console.log('获取学院列表响应：', response);
            if (response && response.data) {
                collegeList.value = response.data.records || [];
                total.value = response.data.total || 0;
            }
        } catch (error) {
            console.error('获取学院列表失败：', error);
            ElMessage.error('获取学院列表失败');
        } finally {
            loading.value = false;
        }
    };

    const handleSearch = () => {
        handlePageChange({ page: 1, size: 20 });
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
            console.log('开始删除学院，ID：', id);
            await deleteCollege(id);
            console.log('删除学院成功');
            ElMessage.success('删除成功');
            handleSearch();
        } catch (error) {
            console.error('删除学院失败：', error);
            ElMessage.error('删除失败');
        }
    };

    const handlePageChange = ({ page, size }) => {
        fetchColleges(page, size);
    };

    onMounted(() => {
        handleSearch();
    });
</script>

<style scoped>
    .college-list-container {
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
        .college-list-container {
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