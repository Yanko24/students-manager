<template>
  <div class="teaching-page" v-loading="courseLoading">
    <header class="page-header">
      <div class="title-group">
        <el-button circle plain aria-label="返回我的课程" @click="router.push('/teacher/courses')"><el-icon><ArrowLeft /></el-icon></el-button>
        <div>
          <h2>{{ course.name || '课程教学' }}</h2>
          <p>{{ course.code || '—' }} · 教学班 {{ course.sectionCode || '—' }} · {{ course.semester || '—' }} · {{ course.college || '学院未填写' }}</p>
        </div>
      </div>
      <el-button type="primary" plain @click="router.push({ path: '/teacher/attendance', query: { course: course.name } })">课程考勤</el-button>
    </header>

    <el-alert class="workflow-tip" type="info" :closable="false" show-icon>
      教师只能为已确认选课的学生录入成绩。提交后成绩进入管理员待发布列表，发布后学生才能查询；已发布成绩需联系管理员更正。
    </el-alert>

    <el-card class="teaching-card" v-loading="dataLoading">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane name="students" label="学生名单">
          <div class="tab-toolbar">
            <div><strong>已确认选课学生</strong><span>共 {{ rosterTotal }} 人</span></div>
            <el-input v-model="studentKeyword" clearable placeholder="按学号或姓名筛选" class="student-filter" @keyup.enter="searchStudents" @clear="searchStudents">
              <template #append><el-button @click="searchStudents">查询</el-button></template>
            </el-input>
          </div>
          <el-table :data="roster" stripe>
            <el-table-column prop="studentNo" label="学号" min-width="145" fixed="left" />
            <el-table-column prop="studentName" label="姓名" min-width="110" />
            <el-table-column prop="majorName" label="专业" min-width="170" />
            <el-table-column prop="grade" label="年级" width="100" />
            <el-table-column prop="classNo" label="班级" width="100" />
            <el-table-column label="选课时间" min-width="175"><template #default="{ row }">{{ formatDateTime(row.selectionDate) }}</template></el-table-column>
            <el-table-column label="操作" width="130" fixed="right"><template #default="{ row }"><el-button link type="primary" @click="openCreateScore(row)">录入成绩</el-button></template></el-table-column>
            <template #empty><el-empty description="暂无已确认选课学生" /></template>
          </el-table>
          <div class="pagination-row">
            <el-pagination v-model:current-page="rosterPage" v-model:page-size="rosterSize" :page-sizes="[10, 20, 50]" :total="rosterTotal" layout="total, sizes, prev, pager, next" @size-change="loadRoster" @current-change="loadRoster" />
          </div>
        </el-tab-pane>

        <el-tab-pane name="scores" label="成绩录入与提交">
          <div class="tab-toolbar">
            <div><strong>课程成绩</strong><span>待发布成绩由管理员统一审核发布，共 {{ scoreTotal }} 条</span></div>
            <el-button type="primary" plain @click="openBatchDialog">批量录入成绩</el-button>
            <el-form :inline="true" :model="scoreFilters" @submit.prevent="searchScores">
              <el-form-item label="学号"><el-input v-model="scoreFilters.studentNo" clearable placeholder="输入学号" @keyup.enter="searchScores" /></el-form-item>
              <el-form-item label="姓名"><el-input v-model="scoreFilters.studentName" clearable placeholder="输入姓名" @keyup.enter="searchScores" /></el-form-item>
              <el-form-item><el-button type="primary" @click="searchScores">查询</el-button><el-button @click="resetScoreSearch">重置</el-button></el-form-item>
            </el-form>
          </div>
          <el-table :data="scores" stripe>
            <el-table-column prop="studentNo" label="学号" min-width="145" fixed="left" />
            <el-table-column prop="studentName" label="姓名" min-width="110" />
            <el-table-column label="考试类型" min-width="130"><template #default="{ row }">{{ attemptTypeLabel(row.attemptType) }}（第{{ row.attemptNo }}次）</template></el-table-column>
            <el-table-column prop="score" label="成绩" width="100" align="center" />
            <el-table-column prop="gradePoint" label="绩点" width="90" align="center" />
            <el-table-column label="考试时间" min-width="175"><template #default="{ row }">{{ formatDateTime(row.examTime) }}</template></el-table-column>
            <el-table-column label="发布状态" width="115"><template #default="{ row }"><el-tag :type="row.publishStatus === 'PUBLISHED' ? 'success' : 'warning'">{{ row.publishStatus === 'PUBLISHED' ? '已发布' : '待发布' }}</el-tag></template></el-table-column>
            <el-table-column label="操作" width="170" fixed="right"><template #default="{ row }"><el-button link type="primary" :disabled="row.publishStatus !== 'DRAFT'" @click="openEditScore(row)">更正待发布成绩</el-button><el-button link @click="openScoreHistory(row)">历史</el-button></template></el-table-column>
            <template #empty><el-empty description="该课程还没有录入成绩" /></template>
          </el-table>
          <div class="pagination-row">
            <el-pagination v-model:current-page="scorePage" v-model:page-size="scoreSize" :page-sizes="[10, 20, 50]" :total="scoreTotal" layout="total, sizes, prev, pager, next" @size-change="loadScores" @current-change="loadScores" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="scoreDialogVisible" :title="editingScoreId ? '更正待发布成绩' : '提交学生成绩'" width="620px" destroy-on-close :close-on-click-modal="false" :before-close="confirmScoreDialogClose">
      <el-form ref="scoreFormRef" :model="scoreForm" :rules="scoreRules" label-width="105px">
        <el-form-item label="学生"><el-input :model-value="`${scoreForm.studentNo || ''} ${scoreForm.studentName || ''}`" disabled /></el-form-item>
        <el-form-item label="课程学期"><el-input :model-value="course.semester || ''" disabled /></el-form-item>
        <el-form-item label="考试类型" prop="attemptType">
          <el-select v-model="scoreForm.attemptType" class="full-width">
            <el-option label="正常考试" value="REGULAR" /><el-option label="补考" value="MAKEUP" /><el-option label="重修" value="RETAKE" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试次数" prop="attemptNo"><el-input-number v-model="scoreForm.attemptNo" :min="1" :max="10" :precision="0" /></el-form-item>
        <el-form-item label="成绩" prop="score"><el-input-number v-model="scoreForm.score" :min="0" :max="100" :precision="2" :step="1" /></el-form-item>
        <el-form-item label="考试时间" prop="examTime"><el-date-picker v-model="scoreForm.examTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择考试时间" class="full-width" /></el-form-item>
        <el-form-item v-if="editingScoreId" label="更正原因" prop="changeReason"><el-input v-model="scoreForm.changeReason" maxlength="500" show-word-limit placeholder="说明本次成绩更正原因" /></el-form-item>
        <el-form-item label="评语"><el-input v-model="scoreForm.comment" type="textarea" :rows="3" maxlength="1000" show-word-limit placeholder="选填" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="requestScoreDialogClose">取消</el-button><el-button type="primary" :loading="savingScore" @click="saveScore">{{ editingScoreId ? '提交更正' : '提交审核' }}</el-button></template>
    </el-dialog>

    <el-dialog v-model="batchDialogVisible" title="批量录入课程成绩" width="min(1180px, calc(100vw - 32px))" destroy-on-close :close-on-click-modal="false" :before-close="confirmBatchDialogClose">
      <el-alert class="batch-tip" type="info" :closable="false" show-icon>
        可跨页勾选学生，勾选状态会保留。右侧逐人填写成绩后一次提交；本批最多 200 人，任一记录不合法时整批不会写入。
      </el-alert>
      <el-form :inline="true" :model="batchMeta" class="batch-meta">
        <el-form-item label="考试类型"><el-select v-model="batchMeta.attemptType"><el-option label="正常考试" value="REGULAR" /><el-option label="补考" value="MAKEUP" /><el-option label="重修" value="RETAKE" /></el-select></el-form-item>
        <el-form-item label="考试次数"><el-input-number v-model="batchMeta.attemptNo" :min="1" :max="10" :precision="0" /></el-form-item>
        <el-form-item label="考试时间"><el-date-picker v-model="batchMeta.examTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择考试时间" /></el-form-item>
      </el-form>
      <div class="batch-layout">
        <section class="batch-roster">
          <div class="batch-section-heading"><strong>已确认选课学生</strong><span>共 {{ batchRosterTotal }} 人</span></div>
          <el-input v-model="batchKeyword" clearable placeholder="按学号或姓名搜索全部名单" @keyup.enter="searchBatchRoster" @clear="searchBatchRoster">
            <template #append><el-button @click="searchBatchRoster">搜索</el-button></template>
          </el-input>
          <el-table ref="batchRosterTableRef" v-loading="batchRosterLoading" :data="batchRoster" row-key="studentId" stripe height="390" @select="handleBatchSelect" @select-all="handleBatchSelectAll">
            <el-table-column type="selection" reserve-selection width="48" />
            <el-table-column prop="studentNo" label="学号" min-width="115" />
            <el-table-column prop="studentName" label="姓名" min-width="90" />
            <el-table-column prop="majorName" label="专业" min-width="130" />
          </el-table>
          <el-pagination v-model:current-page="batchRosterPage" v-model:page-size="batchRosterSize" :page-sizes="[10, 20, 50]" :total="batchRosterTotal" layout="total, prev, pager, next" @size-change="loadBatchRoster" @current-change="loadBatchRoster" />
        </section>
        <section class="batch-selected">
          <div class="batch-section-heading"><strong>本批成绩</strong><span>已选 {{ batchRows.length }} / 200 人</span></div>
          <el-table :data="batchRows" stripe height="435">
            <el-table-column prop="studentNo" label="学号" min-width="115" />
            <el-table-column prop="studentName" label="姓名" min-width="90" />
            <el-table-column label="成绩" width="150"><template #default="{ row }"><el-input-number v-model="row.score" :min="0" :max="100" :precision="2" :step="1" controls-position="right" /></template></el-table-column>
            <el-table-column label="评语" min-width="150"><template #default="{ row }"><el-input v-model="row.comment" maxlength="200" placeholder="选填" /></template></el-table-column>
            <el-table-column label="操作" width="72" fixed="right"><template #default="{ row }"><el-button link type="danger" @click="removeBatchStudent(row)">移除</el-button></template></el-table-column>
            <template #empty><el-empty description="请从左侧名单勾选学生" /></template>
          </el-table>
        </section>
      </div>
      <template #footer>
        <span class="batch-footer-hint">提交后所有成绩进入管理员待发布列表</span>
        <el-button @click="requestBatchDialogClose">取消</el-button>
        <el-button type="primary" :loading="savingBatch" :disabled="!batchCanSubmit" @click="submitBatchScores">提交 {{ batchRows.length }} 条成绩</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="historyVisible" title="成绩变更历史" size="520px">
      <el-empty v-if="!scoreHistory.length" description="暂无变更记录" />
      <el-timeline v-else>
        <el-timeline-item v-for="item in scoreHistory" :key="item.id" :timestamp="formatDateTime(item.createTime)" placement="top">
          <strong>{{ historyAction(item.action) }}</strong>
          <p v-if="item.oldScore !== null && item.oldScore !== undefined">{{ item.oldScore }} 分 → {{ item.newScore ?? '已删除' }} 分</p>
          <p>{{ item.reason || '未填写说明' }}</p>
          <small>操作人：{{ item.operator || '—' }}</small>
        </el-timeline-item>
      </el-timeline>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getMyTeachingCourse, getMyCourseStudents, getMyCourseScores, submitMyCourseScore, submitMyCourseScores, updateMyCourseScore, getMyCourseScoreHistory } from '@/api/teacher'
import { formatDateTime } from '@/utils/dateUtils'
import { showApiError } from '@/utils/errorHandler'
import { useUnsavedChanges } from '@/composables/useUnsavedChanges'

const route = useRoute()
const router = useRouter()
const courseId = Number(route.params.courseId)
const courseLoading = ref(false)
const dataLoading = ref(false)
const savingScore = ref(false)
const activeTab = ref('students')
const course = ref({})
const roster = ref([])
const scores = ref([])
const rosterPage = ref(1)
const rosterSize = ref(20)
const rosterTotal = ref(0)
const scorePage = ref(1)
const scoreSize = ref(20)
const scoreTotal = ref(0)
const studentKeyword = ref('')
const scoreFilters = reactive({ studentNo: '', studentName: '' })
const scoreDialogVisible = ref(false)
const batchDialogVisible = ref(false)
const scoreFormRef = ref()
const editingScoreId = ref(null)
const selectedStudent = ref(null)
const historyVisible = ref(false)
const scoreHistory = ref([])
const batchRosterTableRef = ref()
const batchRoster = ref([])
const batchRosterPage = ref(1)
const batchRosterSize = ref(10)
const batchRosterTotal = ref(0)
const batchRosterLoading = ref(false)
const batchKeyword = ref('')
const batchRows = ref([])
const savingBatch = ref(false)
const batchMeta = reactive({ attemptType: 'REGULAR', attemptNo: 1, examTime: localDateTimeNow() })
const scoreFormBaseline = ref('')
const batchBaseline = ref('')
const scoreForm = reactive({ studentId: null, studentNo: '', studentName: '', attemptType: 'REGULAR', attemptNo: 1, score: null, examTime: '', comment: '', changeReason: '' })
const scoreRules = {
  attemptType: [{ required: true, message: '请选择考试类型', trigger: 'change' }],
  attemptNo: [{ required: true, message: '请填写考试次数', trigger: 'change' }],
  score: [{ required: true, message: '请填写成绩', trigger: 'change' }],
  examTime: [{ required: true, message: '请选择考试时间', trigger: 'change' }],
  changeReason: [{ required: true, message: '请填写更正原因', trigger: 'blur' }],
}
const scoreFormDirty = computed(() => scoreDialogVisible.value && JSON.stringify(scoreForm) !== scoreFormBaseline.value)
const batchFormDirty = computed(() => batchDialogVisible.value && batchSnapshot() !== batchBaseline.value)
const batchCanSubmit = computed(() => batchRows.value.length > 0 && batchRows.value.length <= 200 && batchMeta.examTime && batchRows.value.every(row => row.score !== null && row.score !== '' && Number(row.score) >= 0 && Number(row.score) <= 100))
useUnsavedChanges(() => scoreFormDirty.value || batchFormDirty.value)
const loadCourse = async () => {
  courseLoading.value = true
  try {
    const response = await getMyTeachingCourse(courseId)
    if (response?.code !== 200) throw new Error(response?.message || '获取课程信息失败')
    course.value = response.data || {}
  } catch (error) {
    showApiError(error, '获取课程信息失败')
    router.replace('/teacher/courses')
  } finally { courseLoading.value = false }
}

const loadRoster = async () => {
  dataLoading.value = true
  try {
    const response = await getMyCourseStudents(courseId, { page: rosterPage.value, size: rosterSize.value, keyword: studentKeyword.value.trim() || undefined })
    if (response?.code !== 200) throw new Error(response?.message || '获取学生名单失败')
    roster.value = response.data?.records || []
    rosterTotal.value = Number(response.data?.total || 0)
  } catch (error) { showApiError(error, '获取学生名单失败') } finally { dataLoading.value = false }
}

const loadScores = async () => {
  dataLoading.value = true
  try {
    const response = await getMyCourseScores(courseId, { page: scorePage.value, size: scoreSize.value, ...scoreFilters })
    if (response?.code !== 200) throw new Error(response?.message || '获取课程成绩失败')
    scores.value = response.data?.records || []
    scoreTotal.value = Number(response.data?.total || 0)
  } catch (error) { showApiError(error, '获取课程成绩失败') } finally { dataLoading.value = false }
}

const handleTabChange = tab => tab === 'scores' ? loadScores() : loadRoster()
const searchStudents = () => { rosterPage.value = 1; loadRoster() }
const searchScores = () => { scorePage.value = 1; loadScores() }
const resetScoreSearch = () => { scoreFilters.studentNo = ''; scoreFilters.studentName = ''; searchScores() }
const resetScoreForm = () => {
  Object.assign(scoreForm, { studentId: selectedStudent.value?.studentId ?? null, studentNo: selectedStudent.value?.studentNo ?? '', studentName: selectedStudent.value?.studentName ?? '', attemptType: 'REGULAR', attemptNo: 1, score: null, examTime: localDateTimeNow(), comment: '', changeReason: '' })
  scoreFormBaseline.value = JSON.stringify(scoreForm)
}
function localDateTimeNow() {
  const now = new Date()
  const pad = value => String(value).padStart(2, '0')
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}
const openCreateScore = student => {
  editingScoreId.value = null
  selectedStudent.value = student
  resetScoreForm()
  scoreDialogVisible.value = true
}
const openEditScore = row => {
  const student = { studentId: row.studentId, studentNo: row.studentNo, studentName: row.studentName }
  selectedStudent.value = student
  editingScoreId.value = row.id
  Object.assign(scoreForm, { ...student, attemptType: row.attemptType, attemptNo: row.attemptNo, score: Number(row.score), examTime: String(row.examTime || '').replace(' ', 'T'), comment: row.comment || '', changeReason: '' })
  scoreDialogVisible.value = true
  scoreFormBaseline.value = JSON.stringify(scoreForm)
}
const confirmDiscard = async () => {
  try {
    await ElMessageBox.confirm('当前内容尚未提交，确定放弃本次修改吗？', '尚未保存', { type: 'warning', confirmButtonText: '放弃修改', cancelButtonText: '继续编辑' })
    return true
  } catch { return false }
}
const closeWithConfirmation = async (visible, dirty, done) => {
  if (dirty.value && !(await confirmDiscard())) return
  if (typeof done === 'function') done()
  else visible.value = false
}
const confirmScoreDialogClose = done => closeWithConfirmation(scoreDialogVisible, scoreFormDirty, done)
const requestScoreDialogClose = () => confirmScoreDialogClose()
const batchSnapshot = () => JSON.stringify({ meta: batchMeta, rows: batchRows.value })
const resetBatchDialog = () => {
  batchRows.value = []
  batchKeyword.value = ''
  batchRosterPage.value = 1
  Object.assign(batchMeta, { attemptType: 'REGULAR', attemptNo: 1, examTime: localDateTimeNow() })
  batchBaseline.value = batchSnapshot()
}
const openBatchDialog = async () => {
  resetBatchDialog()
  batchDialogVisible.value = true
  await loadBatchRoster()
}
const loadBatchRoster = async () => {
  batchRosterLoading.value = true
  try {
    const response = await getMyCourseStudents(courseId, { page: batchRosterPage.value, size: batchRosterSize.value, keyword: batchKeyword.value.trim() || undefined })
    if (response?.code !== 200) throw new Error(response?.message || '获取学生名单失败')
    batchRoster.value = response.data?.records || []
    batchRosterTotal.value = Number(response.data?.total || 0)
  } catch (error) { showApiError(error, '获取学生名单失败') } finally { batchRosterLoading.value = false }
}
const searchBatchRoster = () => { batchRosterPage.value = 1; loadBatchRoster() }
const handleBatchSelect = (selection, row) => {
  const next = new Map(batchRows.value.map(item => [item.studentId, item]))
  if (selection.some(item => item.studentId === row.studentId)) {
    if (!next.has(row.studentId)) {
      if (next.size >= 200) {
        ElMessage.warning('每批最多选择200名学生')
        batchRosterTableRef.value?.toggleRowSelection(row, false)
      } else next.set(row.studentId, { ...row, score: null, comment: '' })
    }
  } else next.delete(row.studentId)
  batchRows.value = [...next.values()]
}
const handleBatchSelectAll = selection => {
  const next = new Map(batchRows.value.map(item => [item.studentId, item]))
  const selectedIds = new Set(selection.map(item => item.studentId))
  let limitReached = false
  for (const row of batchRoster.value) {
    if (selectedIds.has(row.studentId)) {
      if (!next.has(row.studentId)) {
        if (next.size >= 200) {
          limitReached = true
          batchRosterTableRef.value?.toggleRowSelection(row, false)
        } else next.set(row.studentId, { ...row, score: null, comment: '' })
      }
    } else next.delete(row.studentId)
  }
  batchRows.value = [...next.values()]
  if (limitReached) ElMessage.warning('每批最多选择200名学生，超出部分未加入本批')
}
const removeBatchStudent = row => {
  batchRows.value = batchRows.value.filter(item => item.studentId !== row.studentId)
  const visible = batchRoster.value.find(item => item.studentId === row.studentId)
  if (visible) batchRosterTableRef.value?.toggleRowSelection(visible, false)
}
const confirmBatchDialogClose = done => closeWithConfirmation(batchDialogVisible, batchFormDirty, done)
const requestBatchDialogClose = () => confirmBatchDialogClose()
const submitBatchScores = async () => {
  if (!batchCanSubmit.value) {
    ElMessage.warning('请为本批每位学生填写0到100之间的成绩，并选择考试时间')
    return
  }
  savingBatch.value = true
  try {
    const payload = batchRows.value.map(row => ({
      studentId: row.studentId,
      courseId,
      semester: course.value.semester,
      score: row.score,
      comment: row.comment || '',
      attemptType: batchMeta.attemptType,
      attemptNo: batchMeta.attemptNo,
      examTime: batchMeta.examTime,
    }))
    const response = await submitMyCourseScores(courseId, payload)
    if (response?.code !== 200) throw new Error(response?.message || '批量提交成绩失败')
    ElMessage.success(`已提交 ${payload.length} 条成绩，等待管理员发布`)
    batchBaseline.value = batchSnapshot()
    batchDialogVisible.value = false
    await loadScores()
    activeTab.value = 'scores'
  } catch (error) { showApiError(error, '批量提交成绩失败') } finally { savingBatch.value = false }
}
const saveScore = async () => {
  try { await scoreFormRef.value?.validate() } catch { return }
  savingScore.value = true
  try {
    const payload = { studentId: scoreForm.studentId, courseId, score: scoreForm.score, semester: course.value.semester, attemptType: scoreForm.attemptType, attemptNo: scoreForm.attemptNo, examTime: scoreForm.examTime, comment: scoreForm.comment, changeReason: scoreForm.changeReason }
    const response = editingScoreId.value
      ? await updateMyCourseScore(courseId, editingScoreId.value, payload)
      : await submitMyCourseScore(courseId, payload)
    if (response?.code !== 200) throw new Error(response?.message || '提交成绩失败')
    ElMessage.success(editingScoreId.value ? '成绩更正已提交管理员审核' : '成绩已提交管理员审核')
    scoreFormBaseline.value = JSON.stringify(scoreForm)
    scoreDialogVisible.value = false
    activeTab.value = 'scores'
    await loadScores()
  } catch (error) { showApiError(error, '提交成绩失败') } finally { savingScore.value = false }
}
const openScoreHistory = async row => {
  try {
    const response = await getMyCourseScoreHistory(courseId, row.id)
    if (response?.code !== 200) throw new Error(response?.message || '获取成绩历史失败')
    scoreHistory.value = response.data || []
    historyVisible.value = true
  } catch (error) { showApiError(error, '获取成绩历史失败') }
}
const attemptTypeLabel = type => ({ REGULAR: '正常考试', MAKEUP: '补考', RETAKE: '重修' }[type] || type || '正常考试')
const historyAction = action => ({ CREATE: '提交成绩', UPDATE: '更正成绩', PUBLISH: '管理员发布', DELETE: '删除成绩', SUBMIT: '提交审核' }[action] || action || '成绩变更')

onMounted(async () => {
  await loadCourse()
  if (course.value.id) await loadRoster()
})
</script>

<style scoped>
.teaching-page { display: grid; gap: 18px; }
.page-header,.title-group,.tab-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.title-group { justify-content: flex-start; }.title-group h2 { margin: 0; font-size: 24px; }.title-group p { margin: 7px 0 0; color: var(--el-text-color-secondary); }
.workflow-tip { line-height: 1.7; }
.tab-toolbar { margin-bottom: 16px; }.tab-toolbar > div:first-child { display: grid; gap: 5px; white-space: nowrap; }.tab-toolbar > div:first-child span { color: var(--el-text-color-secondary); font-size: 13px; }
.student-filter { width: 320px; }.pagination-row { display: flex; justify-content: flex-end; margin-top: 18px; overflow-x: auto; }.full-width { width: 100%; }
.batch-tip { margin-bottom: 16px; }.batch-meta { display: flex; flex-wrap: wrap; gap: 8px; }.batch-layout { display: grid; grid-template-columns: minmax(390px, 1fr) minmax(520px, 1.35fr); gap: 18px; }.batch-roster,.batch-selected { display: grid; align-content: start; gap: 12px; min-width: 0; }.batch-section-heading { display: flex; justify-content: space-between; align-items: center; gap: 12px; }.batch-section-heading span,.batch-footer-hint { color: var(--el-text-color-secondary); font-size: 13px; }.batch-selected :deep(.el-input-number) { width: 120px; }.batch-footer-hint { margin-right: auto; }
@media (max-width: 820px) { .page-header,.tab-toolbar { align-items: flex-start; flex-direction: column; }.tab-toolbar > div:first-child { white-space: normal; }.student-filter { width: 100%; }.tab-toolbar :deep(.el-form) { display: flex; flex-wrap: wrap; }.page-header > .el-button { align-self: flex-end; } }
@media (max-width: 980px) { .batch-layout { grid-template-columns: 1fr; }.batch-selected :deep(.el-table) { min-width: 0; } }
</style>
