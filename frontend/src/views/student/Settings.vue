<template>
    <div class="settings-container">
        <div class="page-header">
            <h2>个人设置</h2>
        </div>

        <el-card class="settings-card">
            <el-tabs v-model="activeTab">
                <!-- 账号设置 -->
                <el-tab-pane label="账号设置" name="account">
                    <el-form :model="accountSettings" label-width="120px">
                        <el-form-item label="用户名">
                            <el-input v-model="accountSettings.username" disabled />
                        </el-form-item>
                        <el-form-item label="学号">
                            <el-input v-model="accountSettings.studentId" disabled />
                        </el-form-item>
                        <el-form-item label="邮箱">
                            <el-input v-model="accountSettings.email" />
                        </el-form-item>
                        <el-form-item label="手机号">
                            <el-input v-model="accountSettings.phone" />
                        </el-form-item>
                    </el-form>
                </el-tab-pane>

                <!-- 密码设置 -->
                <el-tab-pane label="密码设置" name="password">
                    <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px">
                        <el-form-item label="原密码" prop="oldPassword">
                            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                        </el-form-item>
                        <el-form-item label="新密码" prop="newPassword">
                            <el-input v-model="passwordForm.newPassword" type="password" show-password />
                        </el-form-item>
                        <el-form-item label="确认密码" prop="confirmPassword">
                            <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                        </el-form-item>
                    </el-form>
                </el-tab-pane>

                <!-- 通知设置 -->
                <el-tab-pane label="通知设置" name="notification">
                    <el-form :model="notificationSettings" label-width="120px">
                        <el-form-item label="成绩通知">
                            <el-switch v-model="notificationSettings.scoreNotification" />
                        </el-form-item>
                        <el-form-item label="考勤通知">
                            <el-switch v-model="notificationSettings.attendanceNotification" />
                        </el-form-item>
                        <el-form-item label="课程通知">
                            <el-switch v-model="notificationSettings.courseNotification" />
                        </el-form-item>
                        <el-form-item label="系统通知">
                            <el-switch v-model="notificationSettings.systemNotification" />
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
            </el-tabs>

            <div class="form-actions">
                <el-button type="primary" @click="saveSettings">保存设置</el-button>
                <el-button @click="resetSettings">重置</el-button>
            </div>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, reactive } from 'vue'
    import { ElMessage } from 'element-plus'

    const activeTab = ref('account')
    const passwordFormRef = ref(null)

    // 账号设置
    const accountSettings = reactive({
        username: '张三',
        studentId: '2024001',
        email: 'zhangsan@example.com',
        phone: '13800138000'
    })

    // 密码设置
    const passwordForm = reactive({
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    })

    const passwordRules = {
        oldPassword: [
            { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
            { required: true, message: '请输入新密码', trigger: 'blur' },
            { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
        ],
        confirmPassword: [
            { required: true, message: '请确认新密码', trigger: 'blur' },
            {
                validator: (rule, value, callback) => {
                    if (value !== passwordForm.newPassword) {
                        callback(new Error('两次输入的密码不一致'))
                    } else {
                        callback()
                    }
                },
                trigger: 'blur'
            }
        ]
    }

    // 通知设置
    const notificationSettings = reactive({
        scoreNotification: true,
        attendanceNotification: true,
        courseNotification: true,
        systemNotification: true
    })

    // 方法
    const saveSettings = () => {
        if (activeTab.value === 'password') {
            passwordFormRef.value.validate((valid) => {
                if (valid) {
                    // TODO: 实现密码修改功能
                    ElMessage.success('密码修改成功')
                }
            })
        } else {
            // TODO: 实现其他设置保存功能
            ElMessage.success('设置保存成功')
        }
    }

    const resetSettings = () => {
        if (activeTab.value === 'password') {
            passwordFormRef.value.resetFields()
        } else {
            // TODO: 实现其他设置重置功能
            ElMessage.warning('确定要重置所有设置吗？')
        }
    }
</script>

<style lang="scss" scoped>
    .settings-container {
        .page-header {
            margin-bottom: 20px;

            h2 {
                margin: 0;
            }
        }

        .settings-card {
            .form-actions {
                margin-top: 20px;
                text-align: center;
            }
        }
    }
</style>