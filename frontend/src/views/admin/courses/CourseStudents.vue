<template>
    <div class="course-students-page">
        <div class="page-header">
            <div>
                <h2>选课审核与名单</h2>
                <p v-if="course">{{ course.code }} · {{ course.name }}</p>
            </div>
            <el-button @click="router.push({ name: 'CourseList' })">返回课程列表</el-button>
        </div>

        <el-card class="summary-card" v-if="course">
            <div class="summary-item"><span>授课教师</span><strong>{{ course.teacher || '未指定' }}</strong></div>
            <div class="summary-item"><span>学期</span><strong>{{ course.semester || '—' }}</strong></div>
            <div class="summary-item"><span>名额占用（待审核 + 已通过）</span><strong>{{ course.selectedCount ?? total }} / {{ course.maxStudents ?? 60 }}</strong></div>
        </el-card>

        <el-card class="table-card">
            <el-tabs v-model="activeStatus" @tab-change="handleStatusChange">
                <el-tab-pane label="待审核" name="pending" />
                <el-tab-pane label="候补队列" name="waitlisted" />
                <el-tab-pane label="已通过" name="approved" />
                <el-tab-pane label="已拒绝" name="rejected" />
                <el-tab-pane label="全部记录" name="all" />
            </el-tabs>
            <div v-if="activeStatus === 'pending'" class="review-toolbar">
                <span>已选 {{ selectedRows.length }} 条待审核申请</span>
                <div>
                    <el-button type="success" :disabled="!selectedRows.length" :loading="reviewing"
                        @click="review('APPROVE')">批量通过</el-button>
                    <el-button type="danger" plain :disabled="!selectedRows.length" :loading="reviewing"
                        @click="review('REJECT')">批量拒绝</el-button>
                </div>
            </div>
            <el-table ref="tableRef" :data="students" v-loading="loading" row-key="selectionId" border stripe
                @selection-change="handleSelectionChange">
                <el-table-column v-if="activeStatus === 'pending'" type="selection" width="52" align="center"
                    :selectable="(row) => row.selectionStatus === 'pending'" />
                <el-table-column prop="studentNo" label="学号" min-width="140" align="center" fixed="left" />
                <el-table-column prop="studentName" label="姓名" min-width="120" align="center" />
                <el-table-column prop="majorName" label="专业" min-width="180" align="center" />
                <el-table-column label="年级班级" min-width="140" align="center">
                    <template #default="{ row }">{{ row.grade || '—' }}级 {{ row.classNo || '' }}班</template>
                </el-table-column>
                <el-table-column label="申请时间" min-width="180" align="center">
                    <template #default="{ row }">{{ formatDateTime(row.selectionDate) }}</template>
                </el-table-column>
                <el-table-column label="审核状态" width="120" align="center">
                    <template #default="{ row }">
                        <el-tag :type="statusTag(row.selectionStatus)">{{ statusLabel(row.selectionStatus, row.waitlistPosition) }}</el-tag>
                    </template>
                </el-table-column>
                <template #empty>
                    <el-empty :description="emptyDescription" />
                </template>
            </el-table>
            <div class="pagination">
                <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total"
                    :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handlePageSizeChange" @current-change="handlePageChange" />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseById, getCourseSelections, reviewCourseSelections } from '@/api/course'
import { formatDateTime } from '@/utils/dateUtils'

const route = useRoute()
const router = useRouter()
const course = ref(null)
const students = ref([])
const total = ref(0)
const loading = ref(false)
const reviewing = ref(false)
const activeStatus = ref('pending')
const selectedRows = ref([])
const tableRef = ref(null)
const page = ref(1)
const size = ref(10)
const emptyDescription = computed(() => ({
    pending: '当前没有待审核申请',
    waitlisted: '当前没有候补申请',
    approved: '还没有通过的选课申请',
    rejected: '还没有拒绝记录',
    all: '这门课程还没有选课申请'
})[activeStatus.value])

function statusLabel(status, position) {
    if (status === 'waitlisted') return `候补第 ${position || '—'} 位`
    return ({ pending: '待审核', approved: '已通过', rejected: '已拒绝' })[status] || '未知'
}

function statusTag(status) {
    return ({ pending: 'warning', approved: 'success', rejected: 'danger' })[status] || 'info'
}

function handleSelectionChange(rows) {
    selectedRows.value = rows.filter((row) => row.selectionStatus === 'pending')
}

function handleStatusChange() {
    page.value = 1
    selectedRows.value = []
    tableRef.value?.clearSelection()
    fetchSelections()
}

function handlePageChange() {
    selectedRows.value = []
    fetchSelections()
}

function handlePageSizeChange() {
    page.value = 1
    selectedRows.value = []
    fetchSelections()
}

async function fetchSelections() {
    loading.value = true
    try {
        const response = await getCourseSelections(route.params.id, {
            page: page.value,
            size: size.value,
            status: activeStatus.value
        })
        if (response?.code !== 200 || !response.data) throw new Error(response?.message || '获取选课记录失败')
        students.value = response.data.records || []
        total.value = response.data.total || 0
    } catch (error) {
        students.value = []
        total.value = 0
        showApiError(error, error.message || '获取选课记录失败')
    } finally {
        loading.value = false
    }
}

async function review(action) {
    const selectionIds = selectedRows.value.map((row) => row.selectionId)
    if (!selectionIds.length) return
    const actionText = action === 'APPROVE' ? '通过' : '拒绝'
    try {
        await ElMessageBox.confirm(`确定${actionText}选中的 ${selectionIds.length} 条申请吗？`, `批量${actionText}`, {
            confirmButtonText: `确认${actionText}`,
            cancelButtonText: '取消',
            type: action === 'APPROVE' ? 'success' : 'warning'
        })
        reviewing.value = true
        const response = await reviewCourseSelections(route.params.id, { selectionIds, action })
        if (response?.code !== 200) throw new Error(response?.message || `批量${actionText}失败`)
        ElMessage.success(`已${actionText} ${response.data || selectionIds.length} 条申请`)
        selectedRows.value = []
        await Promise.all([fetchSelections(), refreshCourse()])
    } catch (error) {
        if (error === 'cancel' || error === 'close') return
        showApiError(error, error?.response?.data?.message || error.message || `批量${actionText}失败`)
    } finally {
        reviewing.value = false
    }
}

async function refreshCourse() {
    const response = await getCourseById(route.params.id)
    if (response?.code === 200) course.value = response.data
}

onMounted(async () => {
    try {
        const response = await getCourseById(route.params.id)
        if (response?.code !== 200 || !response.data) throw new Error(response?.message || '获取课程信息失败')
        course.value = response.data
    } catch (error) {
        showApiError(error, error.message || '获取课程信息失败')
    }
    fetchSelections()
})
</script>

<style scoped>
.course-students-page { width: 100%; }
.page-header { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-bottom: 20px; }
.page-header h2 { margin: 0; color: #303133; font-size: 24px; }
.page-header p { margin: 8px 0 0; color: #909399; }
.summary-card { margin-bottom: 20px; }
.summary-card :deep(.el-card__body) { display: flex; flex-wrap: wrap; gap: 36px; }
.summary-item { display: flex; flex-direction: column; gap: 6px; color: #909399; }
.summary-item strong { color: #303133; font-size: 16px; }
.table-card { margin-bottom: 20px; overflow-x: auto; }
.review-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-bottom: 16px; color: #606266; }
.pagination { display: flex; justify-content: flex-end; margin-top: 20px; overflow-x: auto; }
@media (max-width: 640px) { .page-header { align-items: flex-start; flex-direction: column; } }
@media (max-width: 640px) { .review-toolbar { align-items: flex-start; flex-direction: column; } }
</style>
