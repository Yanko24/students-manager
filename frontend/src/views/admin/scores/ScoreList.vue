<template>
    <div class="score-list-container">
        <div class="page-header">
            <h2>成绩管理</h2>
            <div class="header-actions">
                <el-button type="primary" @click="importDialogVisible = true">导入成绩</el-button>
                <el-button type="success" @click="handleExport">导出成绩</el-button>
                <el-button type="warning" :disabled="selectedIds.length === 0" @click="handlePublish">发布所选（{{ selectedIds.length }}）</el-button>
                <el-button type="primary" @click="router.push('/admin/scores/add')">录入成绩</el-button>
            </div>
        </div>

        <csv-import-dialog
            v-model="importDialogVisible"
            title="批量导入成绩"
            description="使用中文模板填写成绩数据。学号和课程代码需已存在，导入前会校验整份文件。"
            template-name="成绩导入模板.csv"
            template-href="/templates/score-import-template.csv"
            :fields="['学号', '课程代码', '成绩', '学期', '考试类型', '考试次数', '考试时间', '评语']"
            :tips="scoreImportTips"
            :loading="importing"
            @submit="handleImport"
        />

        <el-card class="filter-card">
            <el-form :inline="true" :model="filterForm" class="filter-form">
                <el-form-item label="学号">
                    <el-input v-model="filterForm.studentNo" placeholder="请输入学号" clearable />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="filterForm.studentName" placeholder="请输入姓名" clearable />
                </el-form-item>
                <el-form-item label="课程">
                    <el-input v-model="filterForm.courseName" placeholder="请输入课程" clearable />
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
            <el-table :data="scoreList" v-loading="loading" border stripe style="width: 100%" @selection-change="selectedRows = $event">
                <el-table-column type="selection" width="48" align="center" :selectable="canPublishScore" />
                <el-table-column prop="studentNo" label="学号" min-width="120" align="center" fixed="left" />
                <el-table-column prop="studentName" label="姓名" min-width="100" align="center" />
                <el-table-column prop="courseNo" label="课程代码" min-width="120" align="center" />
                <el-table-column prop="courseName" label="课程名称" min-width="150" align="center" />
                <el-table-column prop="credit" label="学分" min-width="80" align="center" />
                <el-table-column prop="score" label="成绩" min-width="80" align="center">
                    <template #default="{ row }">
                        <span :style="{ color: getScoreColor(row.score) }">{{ row.score }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="gradePoint" label="绩点" min-width="80" align="center" />
                <el-table-column prop="semester" label="学期" min-width="120" align="center" />
                <el-table-column label="考试类型" min-width="100" align="center">
                    <template #default="{ row }">{{ attemptTypeLabel(row.attemptType) }}（第{{ row.attemptNo }}次）</template>
                </el-table-column>
                <el-table-column prop="examTime" label="考试时间" min-width="180" align="center">
                    <template #default="{ row }">{{ formatDateTime(row.examTime) }}</template>
                </el-table-column>
                <el-table-column prop="status" label="状态" min-width="80" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.status === '合格' ? 'success' : 'danger'" size="small">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="publishStatus" label="发布状态" min-width="100" align="center">
                    <template #default="{ row }"><el-tag :type="row.publishStatus === 'PUBLISHED' ? 'success' : 'warning'">{{ row.publishStatus === 'PUBLISHED' ? '已发布' : '待发布' }}</el-tag></template>
                </el-table-column>
                <el-table-column label="操作" min-width="180" fixed="right" align="center">
                    <template #default="{ row }">
                        <record-view-link :to="{ name: 'ScoreView', params: { id: row.id } }" />
                        <el-button type="primary" link @click.stop="router.push({ name: 'ScoreEdit', params: { id: row.id } })">
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
import { showApiError } from "@/utils/errorHandler";
import { ref, computed } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage, ElMessageBox } from 'element-plus'
import { getScoreList, deleteScore, exportScores, importScores, publishScores } from '@/api/score'
    import SmartPagination from '@/components/common/SmartPagination.vue'
    import RecordViewLink from '@/components/common/RecordViewLink.vue'
    import CsvImportDialog from '@/components/common/CsvImportDialog.vue'
    import { formatDateTime } from '@/utils/dateUtils'

    const router = useRouter()
    const loading = ref(false)
    const scoreList = ref([])
    const selectedRows = ref([])
    const selectedIds = computed(() => selectedRows.value.map(row => row.id))
    const total = ref(0)
    const importDialogVisible = ref(false)
    const importing = ref(false)
    const paginationRef = ref(null)
    const pageSize = ref(10)
    const scoreImportTips = [
        '成绩填写 0–100；学期示例：2026-2027-1。',
        '考试时间格式为 YYYY-MM-DDTHH:mm:ss，例如 2026-10-03T17:27:19。',
        '考试类型填写正常考试、补考或重修；考试次数填写 1–10，默认 1。最多导入 5000 条、文件不超过 5 MB；任意记录失败整批回滚。'
    ]

    const filterForm = ref({
        studentNo: '',
        studentName: '',
        courseName: '',
        semester: ''
    })

    const getScoreColor = (score) => {
        if (score >= 90) return '#67C23A'
        if (score >= 80) return '#409EFF'
        if (score >= 60) return '#E6A23C'
        return '#F56C6C'
    }

    const attemptTypeLabel = (type) => ({ REGULAR: '正常考试', MAKEUP: '补考', RETAKE: '重修' }[type] || '正常考试')
    const canPublishScore = (row) => row.publishStatus === 'DRAFT'

    const handlePublish = async () => {
        try {
            const { value: reason } = await ElMessageBox.prompt('请输入本次成绩发布说明。发布后学生即可查询。', '批量发布成绩', {
                confirmButtonText: '发布', cancelButtonText: '取消', inputPlaceholder: '例如：2026-2027 学年第一学期成绩',
                inputValidator: value => !!value?.trim() || '发布说明不能为空'
            })
            const response = await publishScores({ scoreIds: selectedIds.value, reason: reason.trim() })
            ElMessage.success(`已发布 ${response?.data?.published ?? selectedIds.value.length} 条成绩`)
            selectedRows.value = []
            handleSearch()
        } catch (error) {
            if (error === 'cancel' || error === 'close') return
            showApiError(error, '发布成绩失败')
        }
    }

    const fetchScores = async (page, size) => {
        loading.value = true;
        try {
            const response = await getScoreList({
                page,
                size,
                ...filterForm.value
            });

            if (response?.code === 200 && response.data) {
                scoreList.value = response.data.records || [];
                total.value = response.data.total || 0;
            } else {
                scoreList.value = [];
                total.value = 0;
                ElMessage.error(response?.message || '获取成绩列表失败');
            }
        } catch (error) {
            console.error('获取成绩列表失败：', error);
            showApiError(error, '获取成绩列表失败');
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
            studentNo: '',
            studentName: '',
            courseName: '',
            semester: ''
        };
        handleSearch();
    };

    const handleDelete = async (id) => {
        try {
            const { value: reason } = await ElMessageBox.prompt('请填写删除这条成绩的原因。', '删除成绩', {
                confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning',
                inputPlaceholder: '删除原因', inputValidator: value => !!value?.trim() || '删除原因不能为空',
                confirmButtonClass: 'el-button--danger'
            });
            await deleteScore(id, reason.trim());
            ElMessage.success('删除成功');
            handleSearch();
        } catch (error) {
            if (error === 'cancel' || error === 'close') return;
            showApiError(error, '删除失败');
        }
    };

    const handleImport = async (file) => {
        const formData = new FormData()
        formData.append('file', file)
        importing.value = true
        try {
            const response = await importScores(formData)
            const count = response?.data?.imported ?? 0
            ElMessage.success(`成功导入 ${count} 条成绩`)
            importDialogVisible.value = false
            handleSearch()
        } catch (error) {
            showApiError(error, error?.message || '导入失败，请检查文件内容')
        } finally {
            importing.value = false
        }
    }

    const handleExport = async () => {
        try {
            const response = await exportScores(filterForm.value);
            const blob = new Blob([response], { type: 'application/vnd.ms-excel' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', '成绩表.csv');
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('导出成绩失败：', error);
            showApiError(error, '导出失败');
        }
    };

    const handlePageChange = ({ page, size }) => {
        pageSize.value = size
        fetchScores(page, size);
    };

</script>

<style scoped>
.score-list-container {
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

.header-actions {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
}

.template-link {
    align-self: center;
    color: var(--el-color-primary);
    font-size: 14px;
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

    :deep(.el-button--link) {
        padding: 0 8px;
    }

    :deep(.el-tag) {
        min-width: 60px;
    }

    @media screen and (max-width: 768px) {
        .score-list-container {
            padding: 0;
        }

        .page-header {
            flex-direction: column;
            align-items: flex-start;
        }

        .header-actions {
            width: 100%;
            justify-content: flex-start;
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
