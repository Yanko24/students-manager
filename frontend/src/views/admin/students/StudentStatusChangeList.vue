<template>
    <div class="page-container">
        <div class="page-header"><h2>学籍异动审核</h2></div>
        <el-card class="filter-card"><el-form inline><el-form-item label="申请状态"><el-select v-model="status" clearable placeholder="全部" @change="load"><el-option label="待审核" value="PENDING"/><el-option label="已通过" value="APPROVED"/><el-option label="已拒绝" value="REJECTED"/></el-select></el-form-item></el-form></el-card>
        <el-card><el-table :data="rows" v-loading="loading" border stripe>
            <el-table-column prop="studentNo" label="学号" min-width="130" fixed="left"/>
            <el-table-column prop="studentName" label="姓名" min-width="110"/>
            <el-table-column label="异动类型" min-width="110"><template #default="{ row }">{{ typeLabel(row.changeType) }}</template></el-table-column>
            <el-table-column label="状态" min-width="100"><template #default="{ row }"><el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag></template></el-table-column>
            <el-table-column prop="targetMajorCode" label="转入专业代码" min-width="130"/>
            <el-table-column label="期望生效日期" min-width="140"><template #default="{ row }">{{ formatDate(row.effectiveDate) }}</template></el-table-column>
            <el-table-column label="生效状态" min-width="110"><template #default="{ row }">{{ row.status !== 'APPROVED' ? '待审批' : row.appliedAt ? '已生效' : '等待生效日' }}</template></el-table-column>
            <el-table-column prop="reason" label="申请原因" min-width="220" show-overflow-tooltip/>
            <el-table-column prop="reviewComment" label="审核意见" min-width="220" show-overflow-tooltip/>
            <el-table-column label="提交时间" min-width="180"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
            <el-table-column label="操作" width="180" fixed="right"><template #default="{ row }"><template v-if="row.status === 'PENDING'"><el-button type="success" link @click="review(row, true)">通过</el-button><el-button type="danger" link @click="review(row, false)">拒绝</el-button></template><span v-else>{{ row.reviewedBy || '—' }}</span></template></el-table-column>
        </el-table><smart-pagination :total="total" :on-page-change="pageChange"/></el-card>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStatusChangePage, reviewStatusChange } from '@/api/studentStatusChange'
import SmartPagination from '@/components/common/SmartPagination.vue'
import { showApiError } from '@/utils/errorHandler'
import { formatDate, formatDateTime } from '@/utils/dateUtils'
const rows=ref([]),total=ref(0),loading=ref(false),status=ref(''),page=ref(1),size=ref(20)
const typeLabel = type => ({ SUSPENSION:'休学', RETURN:'复学', MAJOR_TRANSFER:'转专业', WITHDRAWAL:'退学' }[type]||type)
const statusLabel = value => ({ PENDING:'待审核', APPROVED:'已通过', REJECTED:'已拒绝' }[value]||value)
const statusType = value => ({ PENDING:'warning', APPROVED:'success', REJECTED:'danger' }[value]||'info')
const load = async () => { loading.value=true; try { const r=await getStatusChangePage({page:page.value,size:size.value,status:status.value||undefined}); rows.value=r?.data?.records||[]; total.value=r?.data?.total||0 } catch(e){showApiError(e,'获取学籍异动申请失败')} finally{loading.value=false} }
const pageChange = state => { page.value=state.page; size.value=state.size; load() }
const review = async (row, approved) => { try { const {value:comment}=await ElMessageBox.prompt(`确认${approved?'通过':'拒绝'}${typeLabel(row.changeType)}申请吗？请输入审核意见。`, '审核学籍异动', { confirmButtonText:approved?'通过':'拒绝', cancelButtonText:'取消', inputPlaceholder:'审核意见', inputValidator:v=>!!v?.trim()||'审核意见不能为空' }); await reviewStatusChange(row.id,{approved,comment:comment.trim()}); ElMessage.success('审核完成'); await load() } catch(e){ if(e==='cancel'||e==='close')return; showApiError(e,'审核失败') } }
onMounted(load)
</script>

<style scoped>.page-header{margin-bottom:20px}.page-header h2{margin:0}.filter-card{margin-bottom:20px}</style>
