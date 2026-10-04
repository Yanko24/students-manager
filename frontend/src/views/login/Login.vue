<template>
    <div class="login-container">
        <el-card class="login-card">
            <div class="login-header">
                <img src="@/assets/images/logo.png" alt="Logo" class="logo" />
                <h2>学生管理系统</h2>
            </div>
            <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0">
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
                    <el-button type="primary" class="login-button" @click="handleLogin" :loading="loading">
                        登录
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, reactive } from 'vue'
    import { useRouter } from 'vue-router'
    import { useUserStore } from '@/stores/user'
    import { ElMessage } from 'element-plus'
    import { User, Lock } from '@element-plus/icons-vue'
    import { login } from '@/api/auth'

    const router = useRouter()
    const userStore = useUserStore()
    const loginFormRef = ref(null)
    const loading = ref(false)

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
    const handleLoginSuccess = (response) => {
        const { token, role, userId, name, mustChangePassword } = response.data

        // 存储用户信息
        userStore.setToken(token)
        userStore.setRole(role)
        userStore.setUserInfo({
            id: userId,
            name: name || loginForm.username,
            username: loginForm.username
        })
        userStore.setMustChangePassword(mustChangePassword)

        if (mustChangePassword) {
            router.push('/change-password')
            ElMessage.warning('首次登录请先修改默认密码')
            return
        }

        // 根据角色跳转
        const redirectMap = {
            admin: '/admin/dashboard',
            teacher: '/teacher/dashboard',
            student: '/student/dashboard'
        }
        router.push(redirectMap[role])
        ElMessage.success('登录成功')
    }

    // 处理登录失败
    const handleLoginError = (error) => {
        console.error('登录失败:', error)
        const errorMessage = error.response?.data?.message || error.message || '登录失败，请检查网络连接或联系管理员'
        ElMessage.error(errorMessage)
    }

    const handleLogin = () => {
        loginFormRef.value.validate(async (valid) => {
            if (!valid) return

            loading.value = true
            try {
                const response = await login(loginForm)

                if (response?.code === 200 && response?.data) {
                    handleLoginSuccess(response)
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
        background-color: #f0f2f5;

        .login-card {
            width: 400px;
            padding: 20px;

            .login-header {
                text-align: center;
                margin-bottom: 30px;

                .logo {
                    width: 64px;
                    height: 64px;
                    margin-bottom: 16px;
                }

                h2 {
                    margin: 0;
                    font-size: 24px;
                    color: #303133;
                }
            }

            .login-button {
                width: 100%;
            }
        }
    }
</style>
