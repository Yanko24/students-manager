<template>
  <div class="page-container">
    <div class="page-heading"><div><h2>学业预警</h2><p>按学期识别公开成绩不及格或缺勤较多的在读学生，供教务人员跟进。</p></div></div>
    <el-alert title="预警规则：当学期最新一次已发布成绩不及格、学期平均分低于 60 分，或缺勤达到 3 次时列入名单。预警仅供人工关注，不会自动改变学籍或发送通知。" type="warning" :closable="false" show-icon />
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filters" @submit.prevent="search">
        <el-form-item label="学期"><el-select v-model="filters.term" placeholder="选择学期" style="width: 190px" @change="search"><el-option v-for="term in terms" :key="term.id" :label="term.termCode" :value="term.termCode" /></el-select></el-form-item>
        <el-form-item label="学生"><el-input v-model="filters.keyword" clearable placeholder="学号、姓名或专业" @keyup.enter="search" /></el-form-item>
        <el-form-item><el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <el-table :data="rows" v-loading="loading" border>
        <el-table-column prop="studentNo" label="学号" min-width="140" fixed="left" />
        <el-table-column prop="studentName" label="姓名" min-width="120" />
        <el-table-column prop="majorName" label="专业" min-width="180" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="classNo" label="班级" width="90" />
        <el-table-column prop="failedCourseCount" label="不及格课程" width="130" />
        <el-table-column prop="averageScore" label="学期均分" width="120">
          <template #default="{ row }">{{ row.averageScore == null ? '暂无成绩' : Number(row.averageScore).toFixed(1) }}</template>
        </el-table-column>
        <el-table-column prop="absenceCount" label="缺勤次数" width="120" />
        <el-table-column prop="riskReasons" label="预警原因" min-width="250" />
        <template #empty><el-empty :description="filters.term ? '当前学期暂无预警学生' : '请先选择学期'" /></template>
      </el-table>
      <div class="pagination"><el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchRows" @current-change="fetchRows" /></div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { showApiError } from '@/utils/errorHandler'
import { getAcademicTerms, getCurrentAcademicTerm } from '@/api/academicTerm'
import { getAcademicRisks } from '@/api/academicRisk'

const terms = ref([])
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const filters = reactive({ term: '', keyword: '' })

async function loadTerms() {
  const [optionsResponse, currentResponse] = await Promise.all([getAcademicTerms(), getCurrentAcademicTerm()])
  terms.value = optionsResponse?.data || []
  filters.term = currentResponse?.data?.termCode || terms.value[0]?.termCode || ''
}
async function fetchRows() {
  if (!filters.term) { rows.value = []; total.value = 0; return }
  loading.value = true
  try {
    const response = await getAcademicRisks({ term: filters.term, page: page.value, size: size.value, keyword: filters.keyword.trim() || undefined })
    rows.value = response?.data?.records || []; total.value = response?.data?.total || 0
  } catch (error) { showApiError(error, '获取学业预警失败') }
  finally { loading.value = false }
}
function search() { page.value = 1; fetchRows() }
function reset() { filters.keyword = ''; search() }
onMounted(async () => { try { await loadTerms(); await fetchRows() } catch (error) { showApiError(error, '加载学期信息失败') } })
</script>

<style scoped>
.page-container { display: grid; gap: 18px; }
.page-heading h2 { margin: 0; font-size: 24px; }
.page-heading p { margin: 8px 0 0; color: var(--el-text-color-secondary); }
.pagination { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
</style>
