<template>
  <div class="notification-page">
    <header class="page-header">
      <div>
        <h2>站内通知</h2>
        <p>集中查看课程、成绩和教务事项的处理进展。</p>
      </div>
      <div class="header-actions">
        <el-button v-if="isAdmin" type="primary" @click="announcementVisible = true">发布通知</el-button>
        <el-button :disabled="!unreadCount" @click="markAllRead">全部标为已读</el-button>
        <el-button @click="load">刷新</el-button>
      </div>
    </header>

    <el-card class="notification-card" v-loading="loading">
      <div class="list-toolbar">
        <el-radio-group v-model="filter" @change="changeFilter">
          <el-radio-button label="all">全部通知</el-radio-button>
          <el-radio-button label="unread">未读 <span v-if="unreadCount">({{ unreadCount }})</span></el-radio-button>
        </el-radio-group>
        <span class="result-count">共 {{ total }} 条</span>
      </div>

      <el-empty v-if="!loading && !items.length" :description="filter === 'unread' ? '没有未读通知' : '暂无通知'" />
      <div v-else class="notification-list">
        <article v-for="item in items" :key="item.id" class="notification-item" :class="{ 'is-unread': !item.readAt }">
          <span class="unread-dot" :class="{ visible: !item.readAt }" aria-label="未读" />
          <div class="notification-content">
            <div class="notification-heading">
              <div class="title-line">
                <strong>{{ item.title }}</strong>
                <el-tag size="small" effect="plain" :type="typeTone(item.notificationType)">{{ typeLabel(item.notificationType) }}</el-tag>
              </div>
              <time>{{ formatDateTime(item.createTime) }}</time>
            </div>
            <p>{{ item.message }}</p>
            <div class="item-actions">
              <el-button v-if="targetFor(item)" link type="primary" @click="openRelated(item)">查看相关内容</el-button>
              <el-button v-if="!item.readAt" link @click="markRead(item)">标为已读</el-button>
            </div>
          </div>
        </article>
      </div>

      <div v-if="total > pageSize" class="pagination-row">
        <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]"
          :total="total" layout="total, sizes, prev, pager, next" @current-change="load" @size-change="changePageSize" />
      </div>
    </el-card>

    <el-dialog v-model="announcementVisible" title="发布站内通知" width="560px" destroy-on-close>
      <el-form ref="announcementFormRef" :model="announcement" :rules="announcementRules" label-position="top">
        <el-form-item label="通知对象" prop="audience">
          <el-select v-model="announcement.audience" class="full-width">
            <el-option label="全体教师和学生" value="ALL" />
            <el-option label="全体教师" value="TEACHER" />
            <el-option label="全体学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="点击通知后打开" prop="target">
          <el-select v-model="announcement.target" class="full-width">
            <el-option label="工作台" value="HOME" />
            <el-option label="课程页面" value="COURSES" />
            <el-option label="成绩页面" value="SCORES" />
            <el-option label="考勤页面" value="ATTENDANCE" />
            <el-option label="学籍办理" value="STATUS" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="announcement.title" maxlength="200" show-word-limit placeholder="简要说明通知主题" />
        </el-form-item>
        <el-form-item label="通知内容" prop="message">
          <el-input v-model="announcement.message" type="textarea" :rows="5" maxlength="1000" show-word-limit placeholder="填写通知的具体内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="announcementVisible = false">取消</el-button>
        <el-button type="primary" :loading="publishing" @click="submitAnnouncement">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getNotifications, getUnreadNotificationCount, markAllNotificationsRead, markNotificationRead, publishAnnouncement } from '@/api/notification'
import { getNotificationRoute } from '@/utils/notificationLinks'
import { formatDateTime } from '@/utils/dateUtils'
import { showApiError } from '@/utils/errorHandler'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const role = computed(() => userStore.role)
const isAdmin = computed(() => role.value === 'admin')
const loading = ref(false)
const publishing = ref(false)
const announcementVisible = ref(false)
const announcementFormRef = ref()
const items = ref([])
const filter = ref('all')
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const unreadCount = ref(0)
const announcement = reactive({ audience: 'ALL', target: 'HOME', title: '', message: '' })
const announcementRules = {
  audience: [{ required: true, message: '请选择通知对象', trigger: 'change' }],
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  message: [{ required: true, message: '请输入通知内容', trigger: 'blur' }],
}

const labelsByType = {
  COURSE_ASSIGNED: '授课安排', COURSE_UPDATED: '课程调整', COURSE_ASSIGNMENT_CHANGED: '授课安排',
  COURSE_SELECTION_REVIEW: '选课审核', SCORE_PUBLISHED: '成绩发布', STUDENT_STATUS_REVIEW: '学籍办理',
  ANNOUNCEMENT: '教务通知', ANNOUNCEMENT_HOME: '教务通知', ANNOUNCEMENT_COURSES: '教务通知',
  ANNOUNCEMENT_SCORES: '教务通知', ANNOUNCEMENT_ATTENDANCE: '教务通知', ANNOUNCEMENT_STATUS: '教务通知',
}
const targetFor = item => getNotificationRoute(item.notificationType, role.value)
const typeLabel = type => labelsByType[type] || '系统消息'
const typeTone = type => ({ SCORE_PUBLISHED: 'success', COURSE_SELECTION_REVIEW: 'warning', ANNOUNCEMENT: 'primary', ANNOUNCEMENT_HOME: 'primary', ANNOUNCEMENT_COURSES: 'primary', ANNOUNCEMENT_SCORES: 'primary', ANNOUNCEMENT_ATTENDANCE: 'primary', ANNOUNCEMENT_STATUS: 'primary' }[type] || 'info')

async function refreshUnreadCount() {
  try { unreadCount.value = Number((await getUnreadNotificationCount())?.data?.count || 0) } catch { /* badge refresh is best effort */ }
}

async function load() {
  loading.value = true
  try {
    const response = await getNotifications({ page: page.value, size: pageSize.value, unreadOnly: filter.value === 'unread' })
    const result = response?.data || {}
    items.value = result.records || []
    total.value = Number(result.total || 0)
    await refreshUnreadCount()
  } catch (error) {
    showApiError(error, '获取通知失败')
  } finally {
    loading.value = false
  }
}

function changeFilter() { page.value = 1; load() }
function changePageSize() { page.value = 1; load() }

async function markRead(item) {
  try {
    await markNotificationRead(item.id)
    item.readAt = new Date().toISOString()
    await refreshUnreadCount()
    window.dispatchEvent(new Event('notifications:updated'))
    if (filter.value === 'unread') load()
  } catch (error) { showApiError(error, '标记通知失败') }
}

async function markAllRead() {
  try {
    await markAllNotificationsRead()
    ElMessage.success('已将所有通知标为已读')
    window.dispatchEvent(new Event('notifications:updated'))
    await load()
  } catch (error) { showApiError(error, '批量标记失败') }
}

async function openRelated(item) {
  if (!item.readAt) await markRead(item)
  const target = targetFor(item)
  if (target) router.push({ path: target, query: { fromNotification: item.id } })
}

async function submitAnnouncement() {
  try { await announcementFormRef.value?.validate() } catch { return }
  publishing.value = true
  try {
    const response = await publishAnnouncement({ ...announcement })
    if (response?.code !== 200) throw new Error(response?.message || '发布通知失败')
    ElMessage.success('通知已发布')
    announcementVisible.value = false
    announcement.title = ''
    announcement.message = ''
    await load()
  } catch (error) { showApiError(error, '发布通知失败') } finally { publishing.value = false }
}

onMounted(() => {
  if (route.query.filter === 'unread') filter.value = 'unread'
  load()
})
</script>

<style scoped>
.notification-page { display: grid; gap: 18px; }
.page-header { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.page-header h2 { margin: 0; font-size: 24px; }
.page-header p { margin: 7px 0 0; color: var(--el-text-color-secondary); }
.header-actions { display: flex; flex-wrap: wrap; gap: 8px; }
.list-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-bottom: 16px; }
.result-count { color: var(--el-text-color-secondary); font-size: 13px; }
.notification-list { display: grid; }
.notification-item { display: flex; gap: 14px; padding: 18px 8px; border-bottom: 1px solid var(--el-border-color-lighter); }
.notification-item:last-child { border-bottom: 0; }
.notification-item.is-unread { background: var(--el-color-primary-light-9); }
.unread-dot { width: 8px; height: 8px; margin: 7px 2px 0; flex: 0 0 auto; border-radius: 50%; background: transparent; }
.unread-dot.visible { background: var(--el-color-primary); }
.notification-content { min-width: 0; flex: 1; }
.notification-heading,.title-line,.item-actions { display: flex; align-items: center; gap: 10px; }
.notification-heading { justify-content: space-between; }
.title-line { min-width: 0; flex-wrap: wrap; }
.title-line strong { color: var(--el-text-color-primary); font-size: 15px; }
.notification-heading time { flex: 0 0 auto; color: var(--el-text-color-secondary); font-size: 13px; }
.notification-content p { margin: 10px 0; color: var(--el-text-color-regular); line-height: 1.7; white-space: pre-wrap; overflow-wrap: anywhere; }
.item-actions { min-height: 24px; }
.pagination-row { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
.full-width { width: 100%; }
@media (max-width: 680px) { .page-header { align-items: flex-start; flex-direction: column; }.notification-heading { align-items: flex-start; flex-direction: column; gap: 7px; }.header-actions { width: 100%; }.list-toolbar { align-items: flex-start; flex-direction: column; } }
</style>
