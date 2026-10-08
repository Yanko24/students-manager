<template>
  <div class="page-container">
    <div class="page-heading">
      <div><h2>培养方案</h2><p>按专业和入学年级维护毕业学分要求</p></div>
      <el-button type="primary" @click="openCreate">新增方案</el-button>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filters" @submit.prevent="search">
        <el-form-item label="专业代码"><el-input v-model="filters.majorCode" clearable placeholder="输入专业代码" @keyup.enter="search" /></el-form-item>
        <el-form-item label="入学年级"><el-input v-model="filters.grade" maxlength="4" clearable placeholder="如 2023" @keyup.enter="search" /></el-form-item>
        <el-form-item><el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table :data="rows" v-loading="loading" border>
        <el-table-column prop="planName" label="方案名称" min-width="180" />
        <el-table-column label="专业" min-width="220"><template #default="{ row }">{{ majorLabel(row.majorCode, row.grade) }}</template></el-table-column>
        <el-table-column prop="grade" label="入学年级" width="110" />
        <el-table-column prop="totalCredits" label="毕业总学分" width="120" />
        <el-table-column prop="requiredCredits" label="必修学分" width="110" />
        <el-table-column prop="electiveCredits" label="选修学分" width="110" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="还没有培养方案，请先新增专业年级方案" /></template>
      </el-table>
      <div class="pagination"><el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchRows" @current-change="fetchRows" /></div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑培养方案' : '新增培养方案'" width="620px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="方案名称" prop="planName"><el-input v-model="form.planName" maxlength="100" placeholder="例如：计算机科学与技术 2023 级培养方案" /></el-form-item>
        <el-form-item label="专业年级" prop="majorKey">
          <el-select v-model="form.majorKey" filterable placeholder="选择专业和入学年级" style="width: 100%" :disabled="Boolean(editingId)">
            <el-option v-for="option in majorOptions" :key="option.key" :value="option.key" :label="option.label" />
          </el-select>
        </el-form-item>
        <el-form-item label="毕业总学分" prop="totalCredits"><el-input-number v-model="form.totalCredits" :min="1" :max="300" :precision="1" :step="1" /></el-form-item>
        <el-form-item label="必修学分" prop="requiredCredits"><el-input-number v-model="form.requiredCredits" :min="0" :max="300" :precision="1" :step="1" /></el-form-item>
        <el-form-item label="选修学分" prop="electiveCredits"><el-input-number v-model="form.electiveCredits" :min="0" :max="300" :precision="1" :step="1" /></el-form-item>
        <el-alert title="学生进度按已通过成绩统计；同一课程目录按一门课计学分，非必修课程计入选修学分。" type="info" :closable="false" />
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存方案</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { showApiError } from '@/utils/errorHandler'
import { createCurriculumPlan, deleteCurriculumPlan, getCurriculumMajorOptions, getCurriculumPlans, updateCurriculumPlan } from '@/api/curriculum'

const rows = ref([])
const majors = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const filters = reactive({ majorCode: '', grade: '' })
const form = reactive({ planName: '', majorKey: '', totalCredits: 160, requiredCredits: 100, electiveCredits: 20 })
const majorOptions = computed(() => {
  const unique = new Map()
  for (const item of majors.value) {
    if (item.status !== 0) continue
    const key = `${item.code}|${item.grade}`
    if (!unique.has(key)) unique.set(key, { key, code: item.code, grade: item.grade, label: `${item.name}（${item.code}） · ${item.grade}级` })
  }
  return [...unique.values()]
})
const rules = {
  planName: [{ required: true, message: '请输入方案名称', trigger: 'blur' }],
  majorKey: [{ required: true, message: '请选择专业年级', trigger: 'change' }],
  totalCredits: [{ required: true, message: '请设置毕业总学分', trigger: 'change' }]
}

function majorLabel(code, grade) {
  const major = majors.value.find(item => item.code === code && item.grade === grade)
  return major ? `${major.name}（${code}） · ${grade}级` : `${code} · ${grade}级`
}
async function loadMajors() {
  const response = await getCurriculumMajorOptions()
  majors.value = response?.data || []
}
async function fetchRows() {
  loading.value = true
  try {
    const response = await getCurriculumPlans({ page: page.value, size: size.value, majorCode: filters.majorCode || undefined, grade: filters.grade || undefined })
    rows.value = response?.data?.records || []
    total.value = response?.data?.total || 0
  } catch (error) {
    showApiError(error, '获取培养方案失败')
  } finally { loading.value = false }
}
function search() { page.value = 1; fetchRows() }
function reset() { filters.majorCode = ''; filters.grade = ''; search() }
function resetForm() {
  Object.assign(form, { planName: '', majorKey: '', totalCredits: 160, requiredCredits: 100, electiveCredits: 20 })
  editingId.value = null
}
function openCreate() { resetForm(); dialogVisible.value = true }
function openEdit(row) {
  resetForm()
  editingId.value = row.id
  Object.assign(form, { planName: row.planName, majorKey: `${row.majorCode}|${row.grade}`, totalCredits: row.totalCredits, requiredCredits: row.requiredCredits, electiveCredits: row.electiveCredits })
  dialogVisible.value = true
}
async function save() {
  if (!await formRef.value?.validate().catch(() => false)) return
  const [majorCode, grade] = form.majorKey.split('|')
  if (Number(form.requiredCredits || 0) + Number(form.electiveCredits || 0) > Number(form.totalCredits || 0)) {
    ElMessage.warning('必修和选修学分之和不能超过毕业总学分')
    return
  }
  saving.value = true
  try {
    const payload = { planName: form.planName, majorCode, grade, totalCredits: form.totalCredits, requiredCredits: form.requiredCredits, electiveCredits: form.electiveCredits }
    if (editingId.value) await updateCurriculumPlan(editingId.value, payload)
    else await createCurriculumPlan(payload)
    ElMessage.success('培养方案已保存')
    dialogVisible.value = false
    await fetchRows()
  } catch (error) { showApiError(error, '保存培养方案失败') }
  finally { saving.value = false }
}
async function remove(row) {
  try {
    await ElMessageBox.confirm(`删除“${row.planName}”后，该专业年级的学生将无法查看培养进度。确定继续吗？`, '删除培养方案', { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' })
    await deleteCurriculumPlan(row.id)
    ElMessage.success('培养方案已删除')
    await fetchRows()
  } catch (error) { if (error !== 'cancel' && error !== 'close') showApiError(error, '删除培养方案失败') }
}
onMounted(async () => { try { await loadMajors() } catch (error) { showApiError(error, '加载专业信息失败') } await fetchRows() })
</script>

<style scoped>
.page-container { display: grid; gap: 18px; }
.page-heading { display: flex; justify-content: space-between; align-items: center; gap: 16px; }
.page-heading h2 { margin: 0; font-size: 24px; }
.page-heading p { margin: 8px 0 0; color: var(--el-text-color-secondary); }
.pagination { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
</style>
