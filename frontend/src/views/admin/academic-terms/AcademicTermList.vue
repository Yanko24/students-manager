<template>
  <div class="page-container">
    <div class="page-heading">
      <div><h2>学期管理</h2><p>统一维护教学学期与当前学期，课程与成绩共用学期编码。</p></div>
      <el-button type="primary" @click="openCreate">新增学期</el-button>
    </div>

    <el-card shadow="never">
      <el-alert v-if="currentTerm" :title="`当前学期：${currentTerm.termCode}`" type="success" :closable="false" show-icon />
      <el-alert v-else title="尚未设置当前学期，请先新增并设置一个当前学期。" type="warning" :closable="false" show-icon />
      <el-table :data="terms" v-loading="loading" border class="term-table">
        <el-table-column prop="termCode" label="学期编码" min-width="180" />
        <el-table-column prop="academicYear" label="学年" width="150" />
        <el-table-column label="学期" width="100"><template #default="{ row }">{{ row.termNo === 1 ? '第一学期' : '第二学期' }}</template></el-table-column>
        <el-table-column label="起止日期" min-width="240">
          <template #default="{ row }">{{ row.startDate || '未设置' }} 至 {{ row.endDate || '未设置' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="130">
          <template #default="{ row }">
            <el-tag v-if="row.isCurrent" type="success">当前学期</el-tag>
            <el-tag v-else-if="row.isActive" type="info">已启用</el-tag>
            <el-tag v-else type="info" effect="plain">已停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button v-if="!row.isCurrent && row.isActive" link type="primary" @click="makeCurrent(row)">设为当前</el-button>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="暂无学期数据" /></template>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑学期' : '新增学期'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="学期编码" prop="termCode">
          <el-input v-model="form.termCode" placeholder="例如：2026-2027-1" :disabled="Boolean(editingId)" />
        </el-form-item>
        <el-form-item label="学期日期">
          <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" format="YYYY-MM-DD"
            range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.isActive" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
        </el-form-item>
        <el-alert title="学期编码格式为 YYYY-YYYY-1 或 YYYY-YYYY-2。已有课程或成绩的学期不可删除或改编码。" type="info" :closable="false" />
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { showApiError } from '@/utils/errorHandler'
import { createAcademicTerm, deleteAcademicTerm, getAcademicTerms, setCurrentAcademicTerm, updateAcademicTerm } from '@/api/academicTerm'

const terms = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const form = reactive({ termCode: '', isActive: 1 })
const dateRange = ref([])
const rules = { termCode: [{ required: true, pattern: /^\d{4}-\d{4}-[12]$/, message: '格式应为 YYYY-YYYY-1 或 YYYY-YYYY-2', trigger: 'blur' }] }
const currentTerm = computed(() => terms.value.find(term => term.isCurrent))

async function fetchTerms() {
  loading.value = true
  try { const response = await getAcademicTerms(); terms.value = response?.data || [] }
  catch (error) { showApiError(error, '获取学期列表失败') }
  finally { loading.value = false }
}
function resetForm() { Object.assign(form, { termCode: '', isActive: 1 }); dateRange.value = []; editingId.value = null }
function openCreate() { resetForm(); dialogVisible.value = true }
function openEdit(term) {
  resetForm(); editingId.value = term.id
  Object.assign(form, { termCode: term.termCode, isActive: term.isActive })
  dateRange.value = term.startDate && term.endDate ? [term.startDate, term.endDate] : []
  dialogVisible.value = true
}
async function save() {
  if (!await formRef.value?.validate().catch(() => false)) return
  saving.value = true
  try {
    const payload = { ...form, startDate: dateRange.value?.[0] || null, endDate: dateRange.value?.[1] || null }
    if (editingId.value) await updateAcademicTerm(editingId.value, payload)
    else await createAcademicTerm(payload)
    ElMessage.success('学期信息已保存'); dialogVisible.value = false; await fetchTerms()
  } catch (error) { showApiError(error, '保存学期失败') }
  finally { saving.value = false }
}
async function makeCurrent(term) {
  try {
    await ElMessageBox.confirm(`将“${term.termCode}”设为当前学期？`, '切换当前学期', { type: 'warning', confirmButtonText: '确认切换', cancelButtonText: '取消' })
    await setCurrentAcademicTerm(term.id); ElMessage.success('当前学期已更新'); await fetchTerms()
  } catch (error) { if (error !== 'cancel' && error !== 'close') showApiError(error, '切换当前学期失败') }
}
async function remove(term) {
  try {
    await ElMessageBox.confirm(`确定删除学期“${term.termCode}”吗？已关联课程或成绩的学期不能删除。`, '删除学期', { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' })
    await deleteAcademicTerm(term.id); ElMessage.success('学期已删除'); await fetchTerms()
  } catch (error) { if (error !== 'cancel' && error !== 'close') showApiError(error, '删除学期失败') }
}
onMounted(fetchTerms)
</script>

<style scoped>
.page-container { display: grid; gap: 18px; }
.page-heading { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.page-heading h2 { margin: 0; font-size: 24px; }
.page-heading p { margin: 8px 0 0; color: var(--el-text-color-secondary); }
.term-table { margin-top: 18px; }
</style>
