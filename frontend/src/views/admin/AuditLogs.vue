<template>
    <div class="page-container">
        <div class="page-header"><h2>操作审计</h2></div>
        <el-card class="filter-card"><el-form inline :model="filters"><el-form-item label="操作人"><el-input v-model="filters.actor" clearable placeholder="账号" @keyup.enter="search"/></el-form-item><el-form-item label="操作类型"><el-input v-model="filters.action" clearable placeholder="输入操作代码，如 SCORE_PUBLISH" @keyup.enter="search"/></el-form-item><el-form-item><el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item></el-form></el-card>
        <el-card class="table-card"><el-table :data="rows" v-loading="loading" border stripe style="width: 100%">
            <el-table-column prop="actor" label="操作人" min-width="130"/>
            <el-table-column prop="action" label="操作类型" min-width="220"><template #default="{ row }">{{ auditActionLabel(row.action) }}</template></el-table-column>
            <el-table-column prop="entityType" label="业务对象" min-width="180"><template #default="{ row }">{{ auditEntityLabel(row.entityType) }}</template></el-table-column>
            <el-table-column prop="entityId" label="记录编号" min-width="120"/>
            <el-table-column prop="summary" label="操作说明" min-width="360"/>
            <el-table-column prop="createTime" label="操作时间" min-width="180">
                <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
            </el-table-column>
        </el-table><smart-pagination :total="total" :on-page-change="pageChange"/></el-card>
    </div>
</template>
<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getOperationAuditPage } from '@/api/notification'
import SmartPagination from '@/components/common/SmartPagination.vue'
import { showApiError } from '@/utils/errorHandler'
import { formatDateTime } from '@/utils/dateUtils'
import { auditActionLabel, auditEntityLabel } from '@/utils/auditLabels'
const rows=ref([]),total=ref(0),loading=ref(false),page=ref(1),size=ref(20),filters=reactive({actor:'',action:''})
const load=async()=>{loading.value=true;try{const r=await getOperationAuditPage({page:page.value,size:size.value,...filters});rows.value=r?.data?.records||[];total.value=r?.data?.total||0}catch(e){showApiError(e,'获取操作审计失败')}finally{loading.value=false}}
const search=()=>{page.value=1;load()}
const reset=()=>{filters.actor='';filters.action='';search()}
const pageChange=state=>{page.value=state.page;size.value=state.size;load()}
onMounted(load)
</script>
<style scoped>.page-header{margin-bottom:20px}.page-header h2{margin:0}.filter-card{margin-bottom:20px}</style>
