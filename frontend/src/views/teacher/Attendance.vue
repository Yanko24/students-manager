<template>
  <div class="attendance-container">
    <div class="page-header"><h2>课程考勤</h2></div>
  <el-card class="search-card filter-card">
      <el-form :inline="true" :model="filters" @submit.prevent="search">
        <el-form-item label="课程"><el-input v-model="filters.courseName" clearable placeholder="课程名称" @keyup.enter="search" /></el-form-item>
        <el-form-item label="班级"><el-input v-model="filters.className" clearable placeholder="学院、专业或班级" @keyup.enter="search" /></el-form-item>
        <el-form-item label="日期"><el-date-picker v-model="filters.date" type="date" value-format="YYYY-MM-DD" clearable placeholder="选择日期" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="全部状态" style="width: 130px">
            <el-option v-for="status in statuses" :key="status" :label="status" :value="status" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card">
      <el-table :data="attendanceList" v-loading="loading" border stripe>
        <el-table-column prop="studentNo" label="学号" width="130" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="className" label="班级" min-width="220" />
        <el-table-column prop="courseName" label="课程" min-width="160" />
        <el-table-column prop="date" label="日期" width="130"><template #default="{ row }">{{ formatDate(row.date) }}</template></el-table-column>
        <el-table-column prop="classPeriod" label="节次" width="110" />
        <el-table-column prop="status" label="状态" width="100"><template #default="{ row }"><el-tag :type="statusType(row.status)">{{ row.status }}</el-tag></template></el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <template #empty><el-empty description="没有符合条件的考勤记录" /></template>
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="[10, 20, 50]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchList" @current-change="fetchList" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTeacherAttendance } from '@/api/attendance'
import { formatDate } from '@/utils/dateUtils'

const route = useRoute()
const filters = reactive({
  courseName: typeof route.query.course === 'string' ? route.query.course : '',
  className: typeof route.query.class === 'string' ? route.query.class : '',
  date: '',
  status: ''
})
const statuses = ['正常', '迟到', '早退', '缺勤', '请假']
const attendanceList = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const statusType = (status) => ({ 正常: 'success', 迟到: 'warning', 早退: 'warning', 缺勤: 'danger', 请假: 'info' }[status] || 'info')

const fetchList = async () => {
  loading.value = true
  try {
    const params = Object.fromEntries(Object.entries(filters).map(([key, value]) => [key, value?.trim?.() || value || undefined]))
    const response = await getTeacherAttendance({ ...params, page: page.value, size: size.value })
    if (response?.code !== 200) throw new Error(response?.message || '查询考勤失败')
    attendanceList.value = response.data?.records || []
    total.value = response.data?.total || 0
  } catch (error) {
    console.error('查询教师考勤失败：', error)
    ElMessage.error(error?.message || '查询考勤失败')
  } finally {
    loading.value = false
  }
}

const search = () => { page.value = 1; fetchList() }
const reset = () => { Object.keys(filters).forEach((key) => { filters[key] = '' }); search() }
onMounted(fetchList)
</script>

<style scoped lang="scss">
.attendance-container { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 24px; }
.search-card { margin-bottom: 20px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
</style>
