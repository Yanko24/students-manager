<template>
  <div class="profile-container">
    <header class="page-header">
      <div>
        <h2>个人信息</h2>
        <p>查看当前账号资料，管理登录密码。</p>
      </div>
      <el-button type="primary" @click="passwordDialogVisible = true">修改密码</el-button>
    </header>

    <el-card class="profile-card" v-loading="loading">
      <div class="profile-content">
        <section class="identity-panel">
          <el-avatar :size="112" :src="defaultAvatar">{{ initials }}</el-avatar>
          <h3>{{ profile.realName || roleLabel }}</h3>
          <span class="role-tag">{{ roleLabel }}</span>
          <span class="username">{{ profile.username || '—' }}</span>
        </section>

        <section class="details-panel">
          <div class="section-heading"><h3>账号信息</h3><span>当前登录账号的基本资料</span></div>
          <el-descriptions :column="2" border class="profile-descriptions">
            <el-descriptions-item v-for="field in accountFields" :key="field.label" :label="field.label">{{ field.value }}</el-descriptions-item>
          </el-descriptions>

          <div class="section-heading role-heading"><h3>{{ roleSectionTitle }}</h3><span>{{ roleSectionHint }}</span></div>
          <el-descriptions :column="2" border class="profile-descriptions">
            <el-descriptions-item v-for="field in roleFields" :key="field.label" :label="field.label">{{ field.value }}</el-descriptions-item>
          </el-descriptions>
          <el-alert class="profile-note" type="info" :closable="false" show-icon>
            <template #title>档案信息由管理员维护。如需更正，请联系管理员。</template>
          </el-alert>
        </section>
      </div>
    </el-card>

    <el-dialog v-model="passwordDialogVisible" title="修改登录密码" width="min(480px, calc(100vw - 32px))" :close-on-click-modal="false" :before-close="confirmPasswordDialogClose" @closed="resetPasswordForm">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top" @submit.prevent="submitPassword">
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input v-model="passwordForm.currentPassword" type="password" show-password autocomplete="current-password" @keyup.enter="focusNewPassword" />
        </el-form-item>
        <el-form-item label="新密码（8 到 72 位）" prop="newPassword">
          <el-input ref="newPasswordInput" v-model="passwordForm.newPassword" type="password" show-password autocomplete="new-password" @keyup.enter="focusConfirmPassword" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input ref="confirmPasswordInput" v-model="passwordForm.confirmPassword" type="password" show-password autocomplete="new-password" @keyup.enter="submitPassword" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="requestPasswordDialogClose">取消</el-button>
        <el-button type="primary" :loading="savingPassword" @click="submitPassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { changePassword, getCurrentUser } from '@/api/auth'
import { formatDate, formatDateTime } from '@/utils/dateUtils'
import defaultAvatar from '@/assets/images/default-avatar.png'
import { useUnsavedChanges } from '@/composables/useUnsavedChanges'

const userStore = useUserStore()
const loading = ref(false)
const profile = ref({ ...(userStore.userInfo || {}), role: userStore.role })
const roleLabel = computed(() => ({ admin: '管理员', teacher: '教师', student: '学生' }[userStore.role] || '用户'))
const initials = computed(() => (profile.value.realName || profile.value.name || profile.value.username || roleLabel.value).slice(0, 1).toUpperCase())
const roleSectionTitle = computed(() => ({ admin: '管理员信息', teacher: '任教信息', student: '学籍信息' }[userStore.role] || '档案信息'))
const roleSectionHint = computed(() => ({ admin: '系统账号状态与创建信息', teacher: '当前教师档案与任职情况', student: '当前学籍、班级与专业信息' }[userStore.role] || '档案信息'))
const showValue = (value) => value === null || value === undefined || value === '' ? '—' : value
const genderLabel = (gender) => gender === 1 ? '男' : gender === 0 ? '女' : '—'
const accountStatusLabel = computed(() => profile.value.accountStatus === 0 ? '正常' : profile.value.accountStatus === 1 ? '已停用' : '—')
const accountFields = computed(() => [
  { label: '姓名', value: showValue(profile.value.realName) },
  { label: '用户名', value: showValue(profile.value.username) },
  { label: '账号编号', value: showValue(profile.value.userId) },
  { label: '性别', value: genderLabel(profile.value.gender) },
  { label: '联系电话', value: showValue(profile.value.phone) },
  { label: '电子邮箱', value: showValue(profile.value.email) },
  { label: '账号状态', value: accountStatusLabel.value },
  { label: '创建时间', value: formatDateTime(profile.value.accountCreatedAt) || '—' },
])
const roleFields = computed(() => {
  if (userStore.role === 'student') return [
    { label: '学号', value: showValue(profile.value.studentNo) },
    { label: '所属学院', value: showValue(profile.value.collegeName) },
    { label: '专业', value: showValue(profile.value.majorName) },
    { label: '专业代码', value: showValue(profile.value.majorCode) },
    { label: '年级', value: profile.value.grade ? `${profile.value.grade}级` : '—' },
    { label: '班级', value: profile.value.classNo ? `${profile.value.classNo}班` : '—' },
    { label: '出生日期', value: formatDate(profile.value.birthDate) || '—' },
    { label: '入学日期', value: formatDate(profile.value.admissionDate) || '—' },
    { label: '学籍状态', value: showValue(profile.value.studentStatusText) },
    { label: '家庭住址', value: showValue(profile.value.address) },
  ]
  if (userStore.role === 'teacher') return [
    { label: '工号', value: showValue(profile.value.teacherNo) },
    { label: '所属学院', value: showValue(profile.value.department) },
    { label: '职称', value: showValue(profile.value.title) },
    { label: '入职日期', value: formatDate(profile.value.hireDate) || '—' },
    { label: '任职状态', value: profile.value.teacherStatus === 0 ? '在职' : profile.value.teacherStatus === 1 ? '离职' : profile.value.teacherStatus === 2 ? '退休' : '—' },
  ]
  return [
    { label: '当前角色', value: roleLabel.value },
    { label: '账号状态', value: accountStatusLabel.value },
    { label: '创建时间', value: formatDateTime(profile.value.accountCreatedAt) || '—' },
  ]
})

const passwordDialogVisible = ref(false)
const savingPassword = ref(false)
const passwordFormRef = ref()
const newPasswordInput = ref()
const confirmPasswordInput = ref()
const passwordForm = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const passwordFormDirty = computed(() => passwordDialogVisible.value && Object.values(passwordForm).some(value => value !== ''))
useUnsavedChanges(passwordFormDirty)
const passwordRules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 72, message: '密码长度须为 8 到 72 位', trigger: 'blur' },
    { validator: (_rule, value, callback) => value === passwordForm.currentPassword ? callback(new Error('新密码不能与当前密码相同')) : callback(), trigger: 'blur' },
  ],
  confirmPassword: [{ validator: (_rule, value, callback) => value === passwordForm.newPassword ? callback() : callback(new Error('两次输入的新密码不一致')), trigger: 'blur' }],
}

const loadProfile = async () => {
  loading.value = true
  try {
    const response = await getCurrentUser()
    if (response?.code !== 200 || !response.data) throw new Error(response?.message || '读取个人档案失败')
    profile.value = response.data
    userStore.setUserInfo({
      ...userStore.userInfo,
      id: response.data.userId,
      name: response.data.realName,
      username: response.data.username,
    })
  } catch (error) {
    showApiError(error, error?.message || '读取个人档案失败')
  } finally {
    loading.value = false
  }
}

const focusNewPassword = () => nextTick(() => newPasswordInput.value?.focus())
const focusConfirmPassword = () => nextTick(() => confirmPasswordInput.value?.focus())
const resetPasswordForm = () => {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}
const confirmPasswordDialogClose = async done => {
  if (passwordFormDirty.value) {
    try {
      await ElMessageBox.confirm('密码表单尚未提交，确定放弃本次输入吗？', '尚未保存', { type: 'warning', confirmButtonText: '放弃输入', cancelButtonText: '继续修改' })
    } catch { return }
  }
  done()
}
const requestPasswordDialogClose = () => confirmPasswordDialogClose(() => { passwordDialogVisible.value = false })
const submitPassword = async () => {
  if (savingPassword.value) return
  try {
    await passwordFormRef.value.validate()
    savingPassword.value = true
    const response = await changePassword({ ...passwordForm })
    if (response?.code !== 200) throw new Error(response?.message || '修改密码失败')
    userStore.setMustChangePassword(false)
    ElMessage.success('登录密码已更新')
    passwordDialogVisible.value = false
  } catch (error) {
    if (error?.message) showApiError(error, error.message)
  } finally {
    savingPassword.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped lang="scss">
.profile-container { display: flex; width: 100%; min-height: calc(100vh - 108px); flex-direction: column; gap: 18px; }
.page-header { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.page-header h2 { margin: 0; color: var(--el-text-color-primary); font-size: 24px; }
.page-header p { margin: 7px 0 0; color: var(--el-text-color-secondary); font-size: 14px; }
.profile-card { width: 100%; flex: 1; }
.profile-card :deep(.el-card__body) { min-height: 100%; box-sizing: border-box; }
.profile-content { display: grid; min-height: 100%; grid-template-columns: 210px minmax(0, 1fr); gap: 32px; padding: 12px; box-sizing: border-box; }
.identity-panel { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 20px 12px; border-right: 1px solid var(--el-border-color-lighter); }
.identity-panel h3 { margin: 4px 0 0; color: var(--el-text-color-primary); font-size: 20px; }
.role-tag { padding: 4px 12px; border-radius: 999px; background: var(--el-color-primary-light-9); color: var(--el-color-primary); font-size: 13px; }
.username { color: var(--el-text-color-secondary); font-size: 13px; }
.details-panel { min-width: 0; padding: 8px 12px 8px 0; }
.section-heading { display: flex; align-items: baseline; gap: 12px; margin: 2px 0 14px; }
.section-heading h3 { margin: 0; color: var(--el-text-color-primary); font-size: 17px; }
.section-heading span { color: var(--el-text-color-secondary); font-size: 13px; }
.role-heading { margin-top: 28px; }
.profile-descriptions { width: 100%; }
.profile-descriptions :deep(.el-descriptions__label) { width: 120px; }
.profile-descriptions :deep(.el-descriptions__content) { overflow-wrap: anywhere; }
.profile-note { margin-top: 20px; }
@media (max-width: 800px) {
  .profile-container { min-height: 0; }
  .profile-content { grid-template-columns: 1fr; gap: 16px; padding: 4px; }
  .identity-panel { border-right: 0; border-bottom: 1px solid var(--el-border-color-lighter); padding: 8px 8px 20px; }
  .details-panel { padding: 4px; }
  .profile-descriptions :deep(.el-descriptions__table) { min-width: 520px; }
  .profile-descriptions { overflow-x: auto; }
}
@media (max-width: 480px) { .page-header { align-items: flex-start; flex-direction: column; } }
</style>
