<template>
    <div class="teacher-form-container" v-loading="loading">
        <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" label-position="right">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="姓名" prop="name">
                        <el-input v-model="formData.name" placeholder="请输入教师姓名" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="工号" prop="teacherId">
                        <el-input v-model="formData.teacherId" placeholder="请输入教师工号" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="性别" prop="gender">
                        <el-radio-group v-model="formData.gender">
                            <el-radio :label="1">男</el-radio>
                            <el-radio :label="0">女</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="所属院系" prop="department">
                        <el-input v-model="formData.department" placeholder="请输入所属学院" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="职称" prop="title">
                        <el-select v-model="formData.title" placeholder="请选择职称" style="width: 100%">
                            <el-option label="教授" value="教授" />
                            <el-option label="副教授" value="副教授" />
                            <el-option label="讲师" value="讲师" />
                            <el-option label="助教" value="助教" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="状态" prop="status">
                        <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                            <el-option label="在职" :value="0" />
                            <el-option label="离职" :value="1" />
                            <el-option label="退休" :value="2" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="联系电话" prop="phone">
                        <el-input v-model="formData.phone" placeholder="请输入联系电话" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="电子邮箱" prop="email">
                        <el-input v-model="formData.email" placeholder="请输入电子邮箱" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-form-item label="入职日期" prop="entryDate">
                <el-date-picker v-model="formData.entryDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择入职日期" style="width: 100%" />
            </el-form-item>

            <el-form-item>
                <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
                <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
    import { ref, reactive, onMounted } from 'vue'
    import { ElMessage } from 'element-plus'
    import { getTeacherById } from '@/api/teacher'
    import { showApiError } from '@/utils/errorHandler'

    const props = defineProps({
        id: {
            type: String,
            default: ''
        },
        isEdit: {
            type: Boolean,
            default: false
        }
    })

    const emit = defineEmits(['submit', 'cancel'])

    const formRef = ref(null)
    const loading = ref(false)
    const saving = ref(false)

    const formData = reactive({
        name: '',
        teacherId: '',
        gender: 1,
        department: '',
        title: '',
        status: 0,
        phone: '',
        email: '',
        entryDate: '',
    })

    const rules = {
        name: [
            { required: true, message: '请输入教师姓名', trigger: 'blur' },
            { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        teacherId: [
            { required: true, message: '请输入教师工号', trigger: 'blur' },
            { pattern: /^[A-Z0-9]{4,20}$/, message: '工号请使用4到20位大写字母或数字', trigger: 'blur' }
        ],
        gender: [
            { required: true, message: '请选择性别', trigger: 'change' }
        ],
        department: [
            { required: true, message: '请选择所属院系', trigger: 'change' }
        ],
        title: [
            { required: true, message: '请选择职称', trigger: 'change' }
        ],
        status: [
            { required: true, message: '请选择状态', trigger: 'change' }
        ],
        phone: [
            { required: true, message: '请输入联系电话', trigger: 'blur' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
            { required: true, message: '请输入电子邮箱', trigger: 'blur' },
            { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        entryDate: [
            { required: true, message: '请选择入职日期', trigger: 'change' }
        ]
    }

    const handleSubmit = async () => {
        try {
            saving.value = true
            await formRef.value.validate()
            const teacherNumber = formData.teacherId.trim()
            emit('submit', {
                teacherNumber,
                title: formData.title,
                department: formData.department.trim(),
                hireDate: formData.entryDate || null,
                status: formData.status,
                user: {
                    username: teacherNumber,
                    role: 'teacher',
                    realName: formData.name.trim(),
                    gender: Number(formData.gender),
                    phone: formData.phone.trim(),
                    email: formData.email.trim()
                }
            })
        } catch (error) {
            if (error?.length) return
            showApiError(error, '教师信息校验失败')
        } finally {
            saving.value = false
        }
    }

    const handleCancel = () => {
        emit('cancel')
    }

    const loadTeacherData = async (id) => {
        loading.value = true
        try {
            const response = await getTeacherById(id)
            const teacher = response?.data
            if (!teacher) throw new Error('教师信息不存在')
            Object.assign(formData, {
                name: teacher.realName || '',
                teacherId: teacher.teacherNo || '',
                gender: Number(teacher.gender ?? 1),
                department: teacher.department || '',
                title: teacher.title || '',
                status: Number(teacher.status ?? 0),
                phone: teacher.phone || '',
                email: teacher.email || '',
                entryDate: teacher.hireDate || ''
            })
        } catch (error) {
            showApiError(error, '获取教师信息失败')
        } finally {
            loading.value = false
        }
    }

    onMounted(() => {
        if (props.isEdit) {
            loadTeacherData(props.id)
        }
    })
</script>

<style lang="scss" scoped>
    .teacher-form-container {
        padding: 20px;
    }
</style>
