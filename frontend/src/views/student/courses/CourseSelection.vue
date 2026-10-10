<template>
  <div class="page-container">
    <div class="page-heading">
      <div>
        <h2>选课中心</h2>
        <p>浏览开放课程并办理选课或退选；待审核、已选和候补申请都会计入学期学分上限。</p>
      </div>
      <el-tag type="info" effect="plain">提交申请后由管理员审核</el-tag>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filters" @submit.prevent="search">
        <el-form-item label="课程名称">
          <el-input v-model="filters.courseName" clearable placeholder="输入课程名称" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="filters.semester" clearable placeholder="如 2026-2027-1" @keyup.enter="search" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询课程</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table :data="courses" v-loading="loading" border>
        <el-table-column prop="code" label="课程代码" min-width="120" fixed="left" />
        <el-table-column prop="sectionCode" label="教学班号" width="100" />
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="college" label="开课单位" min-width="170">
          <template #default="{ row }">{{ row.college || '暂未安排' }}</template>
        </el-table-column>
        <el-table-column prop="teacher" label="授课教师" min-width="120">
          <template #default="{ row }">{{ row.teacher || '暂未安排' }}</template>
        </el-table-column>
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="type" label="课程类型" min-width="110" />
        <el-table-column prop="semester" label="学期" min-width="130" />
        <el-table-column label="上课安排" min-width="250">
          <template #default="{ row }">
            <div v-if="row.schedules?.length" class="schedule-list">
              <div v-for="(schedule, index) in row.schedules" :key="schedule.id || index">{{ scheduleText(schedule) }}</div>
            </div>
            <span v-else class="muted">暂未安排</span>
          </template>
        </el-table-column>
        <el-table-column label="适用范围" min-width="200">
          <template #default="{ row }">
            <div>{{ scopeName(row) }}</div>
            <span v-if="row.selectionGrade" class="muted">限 {{ row.selectionGrade }} 级</span>
          </template>
        </el-table-column>
        <el-table-column label="选课条件" min-width="220">
          <template #default="{ row }">
            <div v-if="row.prerequisiteCourseCodes?.length">先修：{{ row.prerequisiteCourseCodes.join('、') }}</div>
            <div v-if="row.missingPrerequisiteCourseCodes?.length" class="rule-warning">
              未通过：{{ row.missingPrerequisiteCourseCodes.join('、') }}
            </div>
            <div>本学期已申请 {{ formatCredits(row.semesterSelectedCredits) }} / {{ formatCredits(row.semesterCreditLimit) }} 学分</div>
            <span v-if="!['approved', 'pending', 'waitlisted'].includes(row.selectionStatus) && selectionBlockReason(row)"
              class="rule-warning">{{ selectionBlockReason(row) }}</span>
            <span v-if="row.selectionStartAt || row.selectionEndAt" class="muted">
              {{ formatDateTime(row.selectionStartAt) || '立即' }} 至 {{ formatDateTime(row.selectionEndAt) || '不限' }}
            </span>
            <span v-if="row.dropDeadlineAt" class="muted">退选截止：{{ formatDateTime(row.dropDeadlineAt) }}</span>
            <span v-if="!row.prerequisiteCourseCodes?.length && !row.selectionStartAt && !row.selectionEndAt" class="muted">无额外限制</span>
          </template>
        </el-table-column>
        <el-table-column label="选课名额" min-width="150">
          <template #default="{ row }">
            <div class="capacity-cell">
              <span>{{ row.selectedCount || 0 }} / {{ row.maxStudents }}</span>
              <el-progress :percentage="capacityPercent(row)" :show-text="false" :stroke-width="5"
                :status="capacityPercent(row) >= 100 ? 'exception' : undefined" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="我的选课" min-width="145">
          <template #default="{ row }">
            <el-tag v-if="row.selectionStatus === 'approved'" type="success">已选</el-tag>
            <el-tag v-else-if="row.selectionStatus === 'pending'" type="warning">审核中</el-tag>
            <div v-else-if="row.selectionStatus === 'waitlisted'" class="waitlist-status">
              <el-tag type="info">候补中</el-tag>
              <span v-if="row.waitlistPosition">第 {{ row.waitlistPosition }} 位</span>
            </div>
            <el-tag v-else-if="row.selectionStatus === 'rejected'" type="danger">未通过</el-tag>
            <span v-else class="muted">未选择</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="125" fixed="right">
          <template #default="{ row }">
            <el-button v-if="['approved', 'pending', 'waitlisted'].includes(row.selectionStatus)"
              link type="danger" :loading="row.actionLoading" :disabled="!canDrop(row)" @click="drop(row)">
              {{ row.selectionStatus === 'waitlisted' ? '退出候补' : '退选' }}
            </el-button>
            <el-button v-else link type="primary" :loading="row.actionLoading"
              :disabled="!canSelect(row)"
              @click="select(row)">{{ isFull(row) ? '加入候补' : row.selectionStatus === 'rejected' ? '重新选课' : '选择课程' }}</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="当前没有开放选课的课程" /></template>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total"
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchCourses" @current-change="fetchCourses" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAvailableCourses, selectStudentCourse, dropStudentCourse } from '@/api/student'

const filters = reactive({ courseName: '', semester: '' })
const courses = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const scheduleText = (schedule) => {
  const day = ['一', '二', '三', '四', '五', '六', '日'][schedule.dayOfWeek - 1]
  const parity = ({ ALL: '每周', ODD: '单周', EVEN: '双周' })[schedule.weekParity] || '每周'
  return `周${day} ${schedule.startPeriod}-${schedule.endPeriod}节（${schedule.weekStart}-${schedule.weekEnd}周${parity}） ${schedule.classroom}`
}

const isFull = (course) => Number(course.selectedCount || 0) >= Number(course.maxStudents || 0)
const canSelect = (course) => {
  const current = Number(course.semesterSelectedCredits || 0)
  const credit = Number(course.credit || 0)
  const limit = Number(course.semesterCreditLimit || 30)
  const missing = course.missingPrerequisiteCourseCodes || []
  const now = Date.now()
  const beforeStart = course.selectionStartAt && now < new Date(course.selectionStartAt).getTime()
  const afterEnd = course.selectionEndAt && now > new Date(course.selectionEndAt).getTime()
  return Number(course.selectionOpen) === 1 && Number(course.status) !== 2
    && missing.length === 0 && current + credit <= limit && !beforeStart && !afterEnd
}
const selectionBlockReason = (course) => {
  if (Number(course.selectionOpen) !== 1 || Number(course.status) === 2) return '课程当前未开放选课'
  if (course.missingPrerequisiteCourseCodes?.length) return '未满足先修课程要求'
  if (Number(course.semesterSelectedCredits || 0) + Number(course.credit || 0) > Number(course.semesterCreditLimit || 30)) return '超过学期学分上限'
  if (course.selectionStartAt && Date.now() < new Date(course.selectionStartAt).getTime()) return '尚未到选课开始时间'
  if (course.selectionEndAt && Date.now() > new Date(course.selectionEndAt).getTime()) return '选课时间已截止'
  return ''
}
const canDrop = (course) => course.selectionStatus === 'waitlisted' || !course.dropDeadlineAt
  || Date.now() <= new Date(course.dropDeadlineAt).getTime()
const capacityPercent = (course) => course.maxStudents > 0
  ? Math.min(100, Math.round((Number(course.selectedCount || 0) / course.maxStudents) * 100))
  : 0

function scopeName(course) {
  if (course.selectionScope === 'COLLEGE') return course.selectionCollegeName || '指定学院'
  if (course.selectionScope === 'MAJOR') return course.selectionMajorName || course.selectionMajorCode || '指定专业'
  return '全校开放'
}

function formatCredits(value) {
  const number = Number(value || 0)
  return Number.isInteger(number) ? String(number) : number.toFixed(1)
}

function formatDateTime(value) {
  return value ? String(value).replace('T', ' ').slice(0, 16) : ''
}

async function fetchCourses() {
  loading.value = true
  try {
    const { data } = await getAvailableCourses({
      page: page.value,
      size: size.value,
      courseName: filters.courseName.trim() || undefined,
      semester: filters.semester.trim() || undefined
    })
    courses.value = (data?.records || []).map((course) => ({ ...course, actionLoading: false }))
    total.value = data?.total || 0
  } catch (error) {
    courses.value = []
    total.value = 0
    showApiError(error, error?.response?.data?.message || error?.message || '获取开放课程失败')
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  fetchCourses()
}

function reset() {
  filters.courseName = ''
  filters.semester = ''
  search()
}

async function select(course) {
  course.actionLoading = true
  try {
    const response = await selectStudentCourse(course.id)
    if (response?.data === 'waitlisted') {
      ElMessage.success(`《${course.name}》已加入候补队列`)
    } else {
      ElMessage.success(`《${course.name}》选课申请已提交，等待管理员审核`)
    }
    await fetchCourses()
  } catch (error) {
    showApiError(error, error?.response?.data?.message || error?.message || '选课失败')
  } finally {
    course.actionLoading = false
  }
}

async function drop(course) {
  const leavingWaitlist = course.selectionStatus === 'waitlisted'
  try {
    const actionText = leavingWaitlist ? '退出候补' : '退选'
    await ElMessageBox.confirm(`确定${actionText}《${course.name}》吗？`, `确认${actionText}`, {
      confirmButtonText: `确认${actionText}`,
      cancelButtonText: '再想想',
      type: 'warning'
    })
    course.actionLoading = true
    await dropStudentCourse(course.id)
    ElMessage.success(leavingWaitlist ? '已退出候补队列' : '退选成功')
    await fetchCourses()
  } catch (error) {
    if (error === 'cancel' || error === 'close') return
    showApiError(error, error?.response?.data?.message || error?.message || '退选失败')
  } finally {
    course.actionLoading = false
  }
}

onMounted(fetchCourses)
</script>

<style scoped>
.page-container { display: grid; gap: 18px; }
.page-heading { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.page-heading h2 { margin: 0; font-size: 24px; }
.page-heading p { margin: 8px 0 0; color: var(--el-text-color-secondary); }
.filter-card :deep(.el-card__body) { padding-bottom: 2px; }
.table-card :deep(.el-card__body) { overflow-x: auto; }
.capacity-cell { display: grid; gap: 5px; min-width: 100px; }
.schedule-list { display: grid; gap: 4px; white-space: nowrap; }
.waitlist-status { display: flex; align-items: center; justify-content: center; gap: 6px; white-space: nowrap; }
.waitlist-status span { color: var(--el-text-color-secondary); font-size: 12px; }
.muted { color: var(--el-text-color-secondary); }
.rule-warning { color: var(--el-color-danger); }
.pagination { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }
@media (max-width: 640px) {
  .page-heading { align-items: flex-start; flex-direction: column; }
}
</style>
