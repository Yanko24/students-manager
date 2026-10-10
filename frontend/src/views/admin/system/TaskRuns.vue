<template>
  <div class="page-container">
    <div class="page-heading"><div><h2>定时任务</h2><p>仅展示有处理结果或执行失败的记录，定时检查的空跑不会占用列表。</p></div><el-button @click="fetchRows">刷新</el-button></div>
    <el-card shadow="never">
      <el-table :data="rows" v-loading="loading" border>
        <el-table-column prop="taskName" label="任务" min-width="190" />
        <el-table-column prop="startedAt" label="开始时间" min-width="190"><template #default="{ row }">{{ formatDateTime(row.startedAt) }}</template></el-table-column>
        <el-table-column prop="finishedAt" label="结束时间" min-width="190"><template #default="{ row }">{{ formatDateTime(row.finishedAt) || '运行中' }}</template></el-table-column>
        <el-table-column prop="status" label="结果" width="120"><template #default="{ row }"><el-tag :type="row.status === 'SUCCESS' ? 'success' : row.status === 'FAILED' ? 'danger' : 'warning'">{{ statusLabel(row.status) }}</el-tag></template></el-table-column>
        <el-table-column prop="processedCount" label="处理记录数" width="130" />
        <el-table-column prop="errorMessage" label="错误信息" min-width="300"><template #default="{ row }">{{ row.errorMessage || '—' }}</template></el-table-column>
        <template #empty><el-empty description="暂无定时任务运行记录" /></template>
      </el-table>
      <div class="pagination"><el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchRows" @current-change="fetchRows" /></div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { showApiError } from '@/utils/errorHandler'
import { getScheduledTaskRuns } from '@/api/taskRuns'
import { formatDateTime } from '@/utils/dateUtils'

const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const loading = ref(false)
const statusLabel = status => ({ RUNNING: '运行中', SUCCESS: '成功', FAILED: '失败' }[status] || status)

async function fetchRows() {
  loading.value = true
  try {
    const response = await getScheduledTaskRuns({ page: page.value, size: size.value })
    rows.value = response?.data?.records || []; total.value = response?.data?.total || 0
  } catch (error) { showApiError(error, '获取定时任务记录失败') }
  finally { loading.value = false }
}
onMounted(fetchRows)
</script>

<style scoped>
.page-container { display: grid; gap: 18px; }
.page-heading { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.page-heading h2 { margin: 0; font-size: 24px; }
.page-heading p { margin: 8px 0 0; color: var(--el-text-color-secondary); }
.pagination { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
</style>
