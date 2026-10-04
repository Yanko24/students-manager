<template>
    <div class="profile-container">
        <el-card class="profile-card">
            <template #header>
                <div class="card-header">
                    <span>个人信息</span>
                    <el-button type="primary" @click="handleEdit">编辑信息</el-button>
                </div>
            </template>

            <el-form :model="studentInfo" label-width="100px" class="profile-form">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="学号">
                            <el-input v-model="studentInfo.studentId" disabled />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="姓名">
                            <el-input v-model="studentInfo.name" disabled />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="性别">
                            <el-input v-model="studentInfo.gender" disabled />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="年龄">
                            <el-input v-model="studentInfo.age" disabled />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="班级">
                            <el-input v-model="studentInfo.class" disabled />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="专业">
                            <el-input v-model="studentInfo.major" disabled />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-form-item label="联系电话">
                    <el-input v-model="studentInfo.phone" disabled />
                </el-form-item>

                <el-form-item label="邮箱">
                    <el-input v-model="studentInfo.email" disabled />
                </el-form-item>

                <el-form-item label="家庭住址">
                    <el-input v-model="studentInfo.address" disabled />
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 编辑对话框 -->
        <el-dialog v-model="dialogVisible" title="编辑个人信息" width="50%">
            <el-form :model="editForm" label-width="100px">
                <el-form-item label="联系电话">
                    <el-input v-model="editForm.phone" />
                </el-form-item>
                <el-form-item label="邮箱">
                    <el-input v-model="editForm.email" />
                </el-form-item>
                <el-form-item label="家庭住址">
                    <el-input v-model="editForm.address" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSave">保存</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
    import { ref, reactive } from 'vue'
    import { ElMessage } from 'element-plus'

    const dialogVisible = ref(false)

    const studentInfo = reactive({
        studentId: '2024001',
        name: '张三',
        gender: '男',
        age: '20',
        class: '计算机2班',
        major: '计算机科学与技术',
        phone: '13800138000',
        email: 'zhangsan@example.com',
        address: '北京市海淀区'
    })

    const editForm = reactive({
        phone: '',
        email: '',
        address: ''
    })

    const handleEdit = () => {
        editForm.phone = studentInfo.phone
        editForm.email = studentInfo.email
        editForm.address = studentInfo.address
        dialogVisible.value = true
    }

    const handleSave = () => {
        studentInfo.phone = editForm.phone
        studentInfo.email = editForm.email
        studentInfo.address = editForm.address
        dialogVisible.value = false
        ElMessage.success('保存成功')
    }
</script>

<style scoped lang="scss">
    .profile-container {
        padding: 20px;

        .profile-card {
            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .profile-form {
                max-width: 800px;
                margin: 0 auto;
            }
        }
    }

    .dialog-footer {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
    }
</style>