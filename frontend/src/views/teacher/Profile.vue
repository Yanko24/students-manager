<template>
    <div class="profile-container">
        <div class="page-header">
            <h2>个人信息</h2>
            <el-button type="primary" @click="handleEdit" v-if="!isEditing">编辑</el-button>
        </div>

        <el-card class="profile-card">
            <el-form :model="profileForm" label-width="100px" :disabled="!isEditing">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="工号">
                            <el-input v-model="profileForm.teacherId" disabled />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="姓名">
                            <el-input v-model="profileForm.name" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="性别">
                            <el-select v-model="profileForm.gender" style="width: 100%">
                                <el-option label="男" value="男" />
                                <el-option label="女" value="女" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="出生日期">
                            <el-date-picker v-model="profileForm.birthday" type="date" style="width: 100%" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="所属院系">
                            <el-select v-model="profileForm.department" style="width: 100%">
                                <el-option label="计算机学院" value="计算机学院" />
                                <el-option label="信息工程学院" value="信息工程学院" />
                                <el-option label="电子工程学院" value="电子工程学院" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="职称">
                            <el-select v-model="profileForm.title" style="width: 100%">
                                <el-option label="教授" value="教授" />
                                <el-option label="副教授" value="副教授" />
                                <el-option label="讲师" value="讲师" />
                                <el-option label="助教" value="助教" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-form-item label="联系电话">
                    <el-input v-model="profileForm.phone" />
                </el-form-item>

                <el-form-item label="电子邮箱">
                    <el-input v-model="profileForm.email" />
                </el-form-item>

                <el-form-item label="办公地点">
                    <el-input v-model="profileForm.office" />
                </el-form-item>

                <el-form-item label="个人简介">
                    <el-input v-model="profileForm.introduction" type="textarea" :rows="4" />
                </el-form-item>

                <el-form-item v-if="isEditing">
                    <el-button type="primary" @click="handleSave">保存</el-button>
                    <el-button @click="handleCancel">取消</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 修改密码对话框 -->
        <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
            <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
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
                    <el-button @click="passwordDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleChangePassword">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
    import { ref, reactive } from 'vue'
    import { ElMessage } from 'element-plus'

    // 编辑状态
    const isEditing = ref(false)

    // 个人信息表单
    const profileForm = reactive({
        teacherId: 'T2024001',
        name: '张老师',
        gender: '男',
        birthday: '1980-01-01',
        department: '计算机学院',
        title: '副教授',
        phone: '13800138000',
        email: 'zhang@example.com',
        office: '计算机楼A区301',
        introduction: '从事计算机科学与技术教学与研究工作20年，主要研究方向为人工智能和机器学习。'
    })

    // 密码修改对话框
    const passwordDialogVisible = ref(false)
    const passwordFormRef = ref(null)
    const passwordForm = reactive({
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    })

    // 密码验证规则
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

    // 方法
    const handleEdit = () => {
        isEditing.value = true
    }

    const handleSave = () => {
        // TODO: 调用保存接口
        ElMessage.success('保存成功')
        isEditing.value = false
    }

    const handleCancel = () => {
        // TODO: 重置表单
        isEditing.value = false
    }

    const handleChangePassword = () => {
        passwordFormRef.value.validate((valid) => {
            if (valid) {
                // TODO: 调用修改密码接口
                ElMessage.success('密码修改成功')
                passwordDialogVisible.value = false
            }
        })
    }
</script>

<style lang="scss" scoped>
    .profile-container {
        .page-header {
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;

            h2 {
                margin: 0;
            }
        }

        .profile-card {
            max-width: 800px;
            margin: 0 auto;
        }
    }
</style>