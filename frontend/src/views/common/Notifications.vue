<template>
    <div class="page-container">
        <div class="page-header"><h2>站内通知</h2><el-button @click="load">刷新</el-button></div>
        <el-card><el-empty v-if="!loading && !items.length" description="暂无通知" />
            <el-timeline v-else>
                <el-timeline-item v-for="item in items" :key="item.id" :timestamp="formatDateTime(item.createTime)" placement="top" :type="item.readAt ? 'info' : 'primary'">
                    <el-card :class="{ unread: !item.readAt }"><div class="notification-title"><strong>{{ item.title }}</strong><el-tag v-if="!item.readAt" size="small" type="warning">未读</el-tag></div><p>{{ item.message }}</p><el-button v-if="!item.readAt" type="primary" link @click="markRead(item)">标记已读</el-button></el-card>
                </el-timeline-item>
            </el-timeline>
        </el-card>
    </div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotifications, markNotificationRead } from '@/api/notification'
import { formatDateTime } from '@/utils/dateUtils'
import { showApiError } from '@/utils/errorHandler'
const items=ref([]),loading=ref(false)
const load=async()=>{loading.value=true;try{items.value=(await getNotifications())?.data||[]}catch(e){showApiError(e,'获取通知失败')}finally{loading.value=false}}
const markRead=async item=>{try{await markNotificationRead(item.id);item.readAt=new Date().toISOString();ElMessage.success('已标记为已读')}catch(e){showApiError(e,'操作失败')}}
onMounted(load)
</script>
<style scoped>
.page-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:20px}.page-header h2{margin:0}.notification-title{display:flex;align-items:center;justify-content:space-between}.unread{border-color:var(--el-color-primary-light-5)}p{margin:12px 0;color:var(--el-text-color-regular)}
</style>
