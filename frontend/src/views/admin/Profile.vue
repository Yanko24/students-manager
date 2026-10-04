<template>
    <div class="profile-container">
        <div class="page-header">
            <h2>个人信息</h2>
        </div>

        <el-card class="profile-card">
            <div class="profile-content">
                <!-- 左侧头像区域 -->
                <div class="avatar-section">
                    <el-upload class="avatar-uploader" action="/api/upload" :show-file-list="false"
                        :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                        <img v-if="profile.avatar" :src="profile.avatar" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                    <div class="upload-tip">点击上传头像</div>
                </div>

                <!-- 右侧信息表单 -->
                <div class="info-section">
                    <el-form :model="profile" label-width="100px" class="profile-form">
                        <el-form-item label="用户名">
                            <el-input v-model="profile.username" disabled />
                        </el-form-item>
                        <el-form-item label="姓名">
                            <el-input v-model="profile.name" />
                        </el-form-item>
                        <el-form-item label="手机号码">
                            <el-input v-model="profile.phone" />
                        </el-form-item>
                        <el-form-item label="邮箱">
                            <el-input v-model="profile.email" />
                        </el-form-item>
                        <el-form-item label="角色">
                            <el-tag type="success">{{ profile.role }}</el-tag>
                        </el-form-item>
                        <el-form-item label="创建时间">
                            <span>{{ formatDateTime(profile.createTime) }}</span>
                        </el-form-item>
                        <el-form-item label="最后登录">
                            <span>{{ formatDateTime(profile.lastLoginTime) }}</span>
                        </el-form-item>
                    </el-form>

                    <div class="form-actions">
                        <el-button type="primary" @click="saveProfile">保存修改</el-button>
                        <el-button @click="showPasswordDialog = true">修改密码</el-button>
                    </div>
                </div>
            </div>
        </el-card>

        <!-- 修改密码对话框 -->
        <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
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
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showPasswordDialog = false">取消</el-button>
                    <el-button type="primary" @click="updatePassword">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
    import { ref, reactive } from 'vue'
    import { ElMessage } from 'element-plus'
    import { Plus } from '@element-plus/icons-vue'
    import { formatDateTime } from '@/utils/dateUtils'

    const profile = reactive({
        username: 'admin',
        name: '管理员',
        phone: '13800138000',
        email: 'admin@example.com',
        role: '管理员',
        avatar: '',
        createTime: '2024-01-01 00:00:00',
        lastLoginTime: '2024-03-20 10:00:00'
    })

    const showPasswordDialog = ref(false)
    const passwordFormRef = ref(null)
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

    const handleAvatarSuccess = (response) => {
        profile.avatar = response.url
        ElMessage.success('头像上传成功')
    }

    const beforeAvatarUpload = (file) => {
        const isImage = file.type.startsWith('image/')
        const isLt2M = file.size / 1024 / 1024 < 2

        if (!isImage) {
            ElMessage.error('只能上传图片文件！')
            return false
        }
        if (!isLt2M) {
            ElMessage.error('图片大小不能超过 2MB！')
            return false
        }
        return true
    }

    const saveProfile = () => {
        // TODO: 实现保存个人信息功能
        ElMessage.success('个人信息保存成功')
    }

    const updatePassword = () => {
        passwordFormRef.value.validate((valid) => {
            if (valid) {
                // TODO: 实现修改密码功能
                ElMessage.success('密码修改成功')
                showPasswordDialog.value = false
            }
        })
    }
</script>

<style lang="scss" scoped>
    .profile-container {
        height: 100%;
        padding: 0;
        box-sizing: border-box;
        display: flex;
        flex-direction: column;

        .page-header {
            margin-bottom: 20px;

            h2 {
                margin: 0;
                font-size: 24px;
                color: #303133;
            }
        }

        .profile-card {
            flex: 1;
            display: flex;
            flex-direction: column;

            .profile-content {
                display: flex;
                gap: 40px;
                padding: 20px;
                height: 100%;
            }

            .avatar-section {
                display: flex;
                flex-direction: column;
                align-items: center;
                width: 200px;
                flex-shrink: 0;

                .avatar-uploader {
                    :deep(.el-upload) {
                        border: 1px dashed #d9d9d9;
                        border-radius: 50%;
                        cursor: pointer;
                        position: relative;
                        overflow: hidden;
                        transition: var(--el-transition-duration-fast);

                        &:hover {
                            border-color: var(--el-color-primary);
                        }
                    }
                }

                .avatar-uploader-icon {
                    font-size: 28px;
                    color: #8c939d;
                    width: 120px;
                    height: 120px;
                    text-align: center;
                    line-height: 120px;
                }

                .avatar {
                    width: 120px;
                    height: 120px;
                    border-radius: 50%;
                    display: block;
                }

                .upload-tip {
                    margin-top: 10px;
                    color: #909399;
                    font-size: 14px;
                }
            }

            .info-section {
                flex: 1;
                display: flex;
                flex-direction: column;

                .profile-form {
                    flex: 1;
                    max-width: 600px;
                }

                .form-actions {
                    margin-top: 20px;
                    text-align: left;
                }
            }
        }
    }

    @media screen and (max-width: 768px) {
        .profile-container {
            padding: 0;

            .profile-card {
                .profile-content {
                    flex-direction: column;
                    padding: 10px;
                    gap: 20px;
                }

                .avatar-section {
                    width: 100%;
                }

                .info-section {
                    .profile-form {
                        max-width: 100%;
                    }
                }
            }
        }
    }
</style>
