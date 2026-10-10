<template>
    <div class="page-container">
        <div class="page-header"><h2>学籍异动</h2><el-button type="primary" :disabled="loading || Boolean(blockingRequest)" @click="dialogVisible = true">{{ blockingRequest ? '已有申请处理中' : '提交申请' }}</el-button></div>
        <el-alert :title="blockingNotice" :type="blockingRequest ? 'warning' : 'info'" :closable="false" show-icon />
        <el-card class="table-card">
            <el-table :data="rows" v-loading="loading" border stripe>
                <el-table-column label="申请类型" min-width="120"><template #default="{ row }">{{ typeLabel(row.changeType) }}</template></el-table-column>
                <el-table-column label="申请状态" min-width="110"><template #default="{ row }"><el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag></template></el-table-column>
                <el-table-column prop="targetMajorCode" label="转入专业" min-width="120"><template #default="{ row }">{{ row.targetMajorCode || '—' }}</template></el-table-column>
                <el-table-column label="期望生效日期" min-width="140"><template #default="{ row }">{{ formatDate(row.effectiveDate) }}</template></el-table-column>
                <el-table-column label="生效状态" min-width="110"><template #default="{ row }">{{ row.status !== 'APPROVED' ? '待审批' : row.appliedAt ? '已生效' : '等待生效日' }}</template></el-table-column>
                <el-table-column prop="reason" label="申请原因" min-width="220" show-overflow-tooltip />
                <el-table-column prop="reviewComment" label="审核意见" min-width="220" show-overflow-tooltip />
                <el-table-column label="提交时间" min-width="180"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
            </el-table>
        </el-card>
        <el-dialog v-model="dialogVisible" title="提交学籍异动申请" width="560px" destroy-on-close>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
                <el-form-item label="异动类型" prop="changeType"><el-select v-model="form.changeType" placeholder="请选择" style="width:100%"><el-option label="休学" value="SUSPENSION"/><el-option label="复学" value="RETURN"/><el-option label="转专业" value="MAJOR_TRANSFER"/><el-option label="退学" value="WITHDRAWAL"/></el-select></el-form-item>
                <el-form-item v-if="form.changeType === 'MAJOR_TRANSFER'" label="转入专业班级" prop="targetMajorKey"><el-select v-model="form.targetMajorKey" filterable remote :remote-method="searchMajors" :loading="majorLoading" placeholder="搜索专业" style="width:100%"><el-option v-for="m in majors" :key="`${m.code}-${m.grade}-${m.classNo}`" :label="`${m.name}（${m.code} · ${m.grade}级${m.classNo}班）`" :value="`${m.code}~${m.classNo}`"/></el-select></el-form-item>
                <el-form-item label="期望生效日期" prop="effectiveDate"><el-date-picker v-model="form.effectiveDate" type="date" value-format="YYYY-MM-DD" style="width:100%"/></el-form-item>
                <el-form-item label="申请原因" prop="reason"><el-input v-model="form.reason" type="textarea" :rows="4" maxlength="1000" show-word-limit/></el-form-item>
            </el-form>
            <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="submitting" @click="submit">提交审核</el-button></template>
        </el-dialog>
    </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { applyStatusChange, getMyStatusChanges } from '@/api/studentStatusChange'
import { getAllMajors } from '@/api/major'
import { showApiError } from '@/utils/errorHandler'
import { formatDate, formatDateTime } from '@/utils/dateUtils'

const rows = ref([]), loading = ref(false), submitting = ref(false), dialogVisible = ref(false), formRef = ref(), majors = ref([]), majorLoading = ref(false)
const form = reactive({ changeType: '', targetMajorKey: '', effectiveDate: '', reason: '' })
const blockingRequest = computed(() => rows.value.find(row => row.status === 'PENDING' || (row.status === 'APPROVED' && !row.appliedAt)))
const blockingNotice = computed(() => {
    const request = blockingRequest.value
    if (!request) return '休学、复学、转专业和退学申请须由管理员审核，通过后才会更新学籍档案。'
    if (request.status === 'PENDING') return `你已有一条${typeLabel(request.changeType)}申请待审核，审核完成前不能重复提交。`
    return `你的${typeLabel(request.changeType)}申请已通过，预计 ${formatDate(request.effectiveDate)} 生效；生效前不能重复提交。`
})
const rules = { changeType: [{ required: true, message: '请选择异动类型', trigger: 'change' }], effectiveDate: [{ required: true, message: '请选择生效日期', trigger: 'change' }], reason: [{ required: true, message: '请填写申请原因', trigger: 'blur' }], targetMajorKey: [{ required: true, message: '请选择转入专业班级', trigger: 'change' }] }
const typeLabel = type => ({ SUSPENSION: '休学', RETURN: '复学', MAJOR_TRANSFER: '转专业', WITHDRAWAL: '退学' }[type] || type)
const statusLabel = status => ({ PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝', CANCELLED: '已取消' }[status] || status)
const statusType = status => ({ PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', CANCELLED: 'info' }[status] || 'info')
const load = async () => { loading.value = true; try { rows.value = (await getMyStatusChanges())?.data || [] } catch (e) { showApiError(e, '获取申请记录失败') } finally { loading.value = false } }
const searchMajors = async query => { majorLoading.value = true; try { const r = await getAllMajors({ page: 1, size: 100, name: query || undefined, status: 0 }); majors.value = r?.data?.records || r?.data?.list || [] } catch (e) { showApiError(e, '获取专业列表失败') } finally { majorLoading.value = false } }
watch(() => form.changeType, v => { if (v !== 'MAJOR_TRANSFER') form.targetMajorKey = '' })
const submit = async () => { try { await formRef.value.validate(); submitting.value = true; const [targetMajorCode, targetClassNo] = String(form.targetMajorKey || '').split('~'); await applyStatusChange({ changeType: form.changeType, targetMajorCode, targetClassNo, effectiveDate: form.effectiveDate, reason: form.reason }); ElMessage.success('申请已提交，等待管理员审核'); dialogVisible.value = false; Object.assign(form, { changeType: '', targetMajorKey: '', effectiveDate: '', reason: '' }); await load() } catch (e) { if (e !== false) showApiError(e, '提交申请失败') } finally { submitting.value = false } }
onMounted(load)
</script>

<style scoped>
.page-container{padding:0}.page-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:20px}.page-header h2{margin:0}.table-card{margin-top:20px}
</style>
