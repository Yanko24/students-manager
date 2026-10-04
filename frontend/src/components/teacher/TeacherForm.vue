<template>
    <div class="teacher-form-container">
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
                            <el-radio label="male">男</el-radio>
                            <el-radio label="female">女</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="所属院系" prop="department">
                        <el-select v-model="formData.department" placeholder="请选择院系" style="width: 100%">
                            <el-option label="计算机科学与技术" value="计算机科学与技术" />
                            <el-option label="软件工程" value="软件工程" />
                            <el-option label="信息安全" value="信息安全" />
                        </el-select>
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
                <el-date-picker v-model="formData.entryDate" type="date" placeholder="请选择入职日期" style="width: 100%" />
            </el-form-item>

            <el-form-item label="备注" prop="remark">
                <el-input v-model="formData.remark" type="textarea" rows="3" placeholder="请输入备注" />
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="handleSubmit">保存</el-button>
                <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
    import { ref, reactive, onMounted } from 'vue'
    import { ElMessage } from 'element-plus'

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

    const formData = reactive({
        name: '',
        teacherId: '',
        gender: 'male',
        department: '',
        title: '',
        status: 0,
        phone: '',
        email: '',
        entryDate: '',
        remark: ''
    })

    const rules = {
        name: [
            { required: true, message: '请输入教师姓名', trigger: 'blur' },
            { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        teacherId: [
            { required: true, message: '请输入教师工号', trigger: 'blur' },
            { pattern: /^[A-Z]{2}\d{6}$/, message: '工号格式为：2个大写字母+6个数字', trigger: 'blur' }
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
            await formRef.value.validate()
            emit('submit', formData)
        } catch (error) {
            console.error('表单验证失败：', error)
        }
    }

    const handleCancel = () => {
        emit('cancel')
    }

    const loadTeacherData = async (id) => {
        // TODO: 调用获取教师详情接口
        // 模拟数据
        formData.name = '张三'
        formData.teacherId = 'JS202301'
        formData.gender = 'male'
        formData.department = '计算机科学与技术'
        formData.title = '教授'
        formData.status = 0
        formData.phone = '13800138000'
        formData.email = 'zhangsan@example.com'
        formData.entryDate = '2023-01-01'
        formData.remark = '优秀教师'
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