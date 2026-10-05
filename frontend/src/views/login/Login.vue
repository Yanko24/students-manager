<template>
    <div class="login-container">
        <el-card class="login-card">
            <div class="login-header">
                <img src="@/assets/images/logo.png" alt="Logo" class="logo" />
                <h2>学生管理系统</h2>
            </div>
            <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-width="0" @submit.prevent="handleLogin">
                <el-form-item prop="username">
                    <el-input v-model="loginForm.username" placeholder="用户名">
                        <template #prefix>
                            <el-icon>
                                <User />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="loginForm.password" type="password" placeholder="密码" show-password>
                        <template #prefix>
                            <el-icon>
                                <Lock />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" native-type="submit" class="login-button" :loading="loading">
                        登录
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
    import { useRouter } from 'vue-router'
    import { useUserStore } from '@/stores/user'
    import { ElMessage } from 'element-plus'
    import { User, Lock } from '@element-plus/icons-vue'
    import { login } from '@/api/auth'

    const router = useRouter()
    const userStore = useUserStore()
    const loginFormRef = ref(null)
    const loading = ref(false)

    const handleLoginKeydown = (event) => {
        if (event.key !== 'Enter' || event.isComposing) return
        event.preventDefault()
        event.stopPropagation()
        handleLogin()
    }

    let loginFormElement
    onMounted(() => {
        loginFormElement = loginFormRef.value?.$el
        loginFormElement?.addEventListener('keydown', handleLoginKeydown, true)
    })

    onBeforeUnmount(() => {
        loginFormElement?.removeEventListener('keydown', handleLoginKeydown, true)
    })

    const loginForm = reactive({
        username: '',
        password: ''
    })

    const rules = {
        username: [
            { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur' }
        ]
    }

    // 处理登录成功后的逻辑
    const handleLoginSuccess = async (response) => {
        const { token, role, userId, name, mustChangePassword } = response.data
        const normalizedRole = String(role || '').toLowerCase()

        // 存储用户信息
        userStore.setToken(token)
        userStore.setRole(normalizedRole)
        userStore.setUserInfo({
            id: userId,
            name: name || loginForm.username,
            username: loginForm.username
        })
        userStore.setMustChangePassword(mustChangePassword)

        const redirectMap = {
            admin: '/admin/dashboard',
            teacher: '/teacher/dashboard',
            student: '/student/dashboard'
        }
        const targetPath = mustChangePassword ? '/change-password' : redirectMap[normalizedRole]
        if (!targetPath) {
            userStore.logout()
            throw new Error('当前账号角色无效，请联系管理员')
        }

        await router.replace(targetPath)
        if (router.currentRoute.value.path !== targetPath) {
            userStore.logout()
            await router.replace('/login')
            throw new Error(`登录后页面跳转失败，当前页面为 ${router.currentRoute.value.fullPath}`)
        }

        if (mustChangePassword) ElMessage.warning('首次登录请先修改默认密码')
        else ElMessage.success('登录成功')
    }

    // 处理登录失败
    const handleLoginError = (error) => {
        console.error('登录失败:', error)
        const errorMessage = error.response?.data?.message || error.message || '登录失败，请检查网络连接或联系管理员'
        ElMessage.error(errorMessage)
    }

    const handleLogin = () => {
        if (loading.value) return

        loginFormRef.value.validate(async (valid) => {
            if (!valid) return

            loading.value = true
            try {
                const response = await login(loginForm)

                if (response?.code === 200 && response?.data) {
                    await handleLoginSuccess(response)
                } else {
                    handleLoginError({ response })
                }
            } catch (error) {
                handleLoginError(error)
            } finally {
                loading.value = false
            }
        })
    }
</script>

<style lang="scss" scoped>
    .login-container {
        height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 24px;
        background:
            radial-gradient(ellipse at 16% 12%, rgba(96, 156, 235, .20), transparent 32%),
            radial-gradient(ellipse at 86% 84%, rgba(67, 111, 177, .14), transparent 34%),
            #f3f6fb;

        .login-card {
            width: min(420px, 100%);
            padding: 26px 24px;
            border: 1px solid rgba(224, 231, 241, .9);
            border-radius: 16px;
            box-shadow: 0 20px 55px rgba(30, 56, 94, .12);

            .login-header {
                text-align: center;
                margin-bottom: 34px;

                .logo {
                    width: 60px;
                    height: 60px;
                    margin-bottom: 16px;
                }

                h2 {
                    margin: 0;
                    font-size: 23px;
                    color: #23334b;
                    letter-spacing: .4px;
                }
            }

            .login-button {
                width: 100%;
                height: 44px;
                font-size: 15px;
            }

            :deep(.el-input__wrapper) {
                min-height: 44px;
                padding: 0 13px;
            }
        }
    }
</style>
