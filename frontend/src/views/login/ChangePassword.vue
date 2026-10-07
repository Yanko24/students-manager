<template>
  <main class="reset-page">
    <el-card class="reset-card">
      <h2>首次登录，请修改默认密码</h2>
      <p>为了保护账户安全，修改密码后才能继续使用系统。</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input v-model="form.currentPassword" type="password" show-password autocomplete="current-password" />
        </el-form-item>
        <el-form-item label="新密码（至少 8 位）" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password autocomplete="new-password" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password autocomplete="new-password" />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">修改密码</el-button>
      </el-form>
    </el-card>
  </main>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { changePassword } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const rules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 72, message: '密码长度须为 8 到 72 位', trigger: 'blur' },
  ],
  confirmPassword: [{
    validator: (_rule, value, callback) => value === form.newPassword ? callback() : callback(new Error('两次输入的新密码不一致')),
    trigger: 'blur',
  }],
}

const submit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    const result = await changePassword(form)
    if (result?.code !== 200) throw new Error(result?.message || '修改密码失败')
    userStore.setMustChangePassword(false)
    ElMessage.success('密码已修改')
    await router.replace(`/${userStore.role}/dashboard`)
  } catch (error) {
    if (error?.message) showApiError(error, error.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.reset-page { min-height: 100vh; display: grid; place-items: center; background: #f0f2f5; padding: 24px; }
.reset-card { width: min(440px, 100%); }
h2 { margin: 0 0 8px; }
p { color: #606266; margin-bottom: 24px; }
.el-button { width: 100%; }
</style>
