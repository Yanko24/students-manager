<template>
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" class="student-form">
        <el-row :gutter="20">
            <el-col :span="12">
                <el-form-item label="学号" prop="studentNo">
                    <el-input v-model="formData.studentNo" :disabled="true" placeholder="请先选择专业" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="姓名" prop="realName">
                    <el-input v-model="formData.realName" placeholder="请输入姓名" />
                </el-form-item>
            </el-col>
        </el-row>

        <el-row :gutter="20">
            <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="formData.gender">
                        <el-radio :value="1">男</el-radio>
                        <el-radio :value="0">女</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="状态" prop="status">
                    <el-select v-model="formData.status" placeholder="请选择状态">
                        <el-option label="在读" :value="0" />
                        <el-option label="休学" :value="1" />
                        <el-option label="退学" :value="2" />
                        <el-option label="毕业" :value="3" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>

        <el-row :gutter="20">
            <el-col :span="12">
                <el-form-item label="专业全称" prop="displayName">
                    <el-select v-model="formData.displayName" placeholder="请选择专业" @change="onMajorChange">
                        <el-option v-for="item in majorList" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="4">
                <el-form-item label="年级" prop="grade">
                    <el-input v-model="formData.grade" placeholder="年级" disabled />
                </el-form-item>
            </el-col>
            <el-col :span="4">
                <el-form-item label="专业代码" prop="majorCode">
                    <el-input v-model="formData.majorCode" placeholder="专业代码" disabled />
                </el-form-item>
            </el-col>
            <el-col :span="4">
                <el-form-item label="班级" prop="classNo">
                    <el-input v-model="formData.classNo" placeholder="班级" disabled />
                </el-form-item>
            </el-col>
        </el-row>

        <el-row :gutter="20">
            <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="formData.phone" placeholder="请输入手机号" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="formData.email" placeholder="请输入邮箱" />
                </el-form-item>
            </el-col>
        </el-row>

        <el-row :gutter="20">
            <el-col :span="12">
                <el-form-item label="入学日期" prop="admissionDate">
                    <el-date-picker v-model="formData.admissionDate" type="date" placeholder="请选择入学日期"
                        value-format="YYYY-MM-DD" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="出生日期" prop="birthDate">
                    <el-date-picker v-model="formData.birthDate" type="date" placeholder="请选择出生日期"
                        value-format="YYYY-MM-DD" />
                </el-form-item>
            </el-col>
        </el-row>

        <el-form-item label="地址" prop="address">
            <el-input v-model="formData.address" type="textarea" :rows="3" placeholder="请输入地址" />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
            <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>

        <el-form-item>
            <el-button type="primary" @click="handleSubmit">保存</el-button>
            <el-button @click="router.back()">取消</el-button>
        </el-form-item>
    </el-form>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
    import { ref, reactive, onMounted, watch } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getAllMajorsList } from '@/api/major'
    import { getStudentCount } from '@/api/student'
    import { useUnsavedChanges } from '@/composables/useUnsavedChanges'

    const props = defineProps({
        initialData: {
            type: Object,
            default: () => ({})
        },
        isEdit: {
            type: Boolean,
            default: false
        }
    })

    const emit = defineEmits(['submit'])
    const router = useRouter()
    const formRef = ref(null)
    const majorList = ref([])
    const formData = reactive({
        studentNo: '',
        realName: '',
        gender: '',
        status: '',
        grade: '',
        majorCode: '',
        classNo: '',
        displayName: '',
        phone: '',
        email: '',
        admissionDate: '',
        birthDate: '',
        address: '',
        remark: ''
    })
    const initialSnapshot = ref('')
    const initialized = ref(false)
    const { markClean } = useUnsavedChanges(() => initialized.value && JSON.stringify(formData) !== initialSnapshot.value)
    const setInitialSnapshot = () => { initialSnapshot.value = JSON.stringify(formData); initialized.value = true }
    defineExpose({ markClean })

    const rules = {
        studentNo: [
            { required: true, message: '请输入学号', trigger: 'blur' },
            { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        realName: [
            { required: true, message: '请输入姓名', trigger: 'blur' },
            { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        gender: [
            { required: true, message: '请选择性别', trigger: 'change' }
        ],
        status: [
            { required: true, message: '请选择状态', trigger: 'change' }
        ],
        grade: [
            { required: true, message: '请输入年级', trigger: 'blur' }
        ],
        majorCode: [
            { required: true, message: '请输入专业代码', trigger: 'blur' }
        ],
        classNo: [
            { required: true, message: '请输入班级', trigger: 'blur' }
        ],
        phone: [
            { required: true, message: '请输入手机号', trigger: 'blur' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
            { required: true, message: '请输入邮箱', trigger: 'blur' },
            { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        admissionDate: [
            { required: true, message: '请选择入学日期', trigger: 'change' }
        ],
        birthDate: [
            { required: true, message: '请选择出生日期', trigger: 'change' }
        ],
        address: [
            { required: true, message: '请输入地址', trigger: 'blur' }
        ]
    }

    // 获取专业列表
    const fetchMajorList = async () => {
        try {
            const response = await getAllMajorsList()
            if (response && response.data && response.data.records) {
                majorList.value = response.data.records.map(major => ({
                    value: `${major.code}-${major.grade}-${major.classNo}`,
                    label: major.displayName,
                    studentCount: major.studentCount
                }))
            }
        } catch (error) {
            console.error('获取专业列表失败:', error)
            showApiError(error, '获取专业列表失败')
        }
    }

    // 处理专业选择变化
    const onMajorChange = async (value) => {
        if (value) {
            const [code, grade, classNo] = value.split('-')

            // 更新专业相关字段
            formData.majorCode = code
            formData.grade = grade
            formData.classNo = classNo

            // 只在新增模式下生成学号，编辑模式下不做任何学号相关的操作
            if (!props.isEdit) {
                try {
                    const response = await getStudentCount({
                        majorCode: code,
                        grade: grade,
                        classNo: classNo
                    })

                    if (response && response.data) {
                        const studentCount = response.data || 0
                        const newStudentNo = `${code}${grade}${classNo}${String(studentCount + 1).padStart(2, '0')}`
                        formData.studentNo = newStudentNo
                    } else {
                        console.warn('获取学生总数失败')
                        ElMessage.warning('获取学生总数失败')
                    }
                } catch (error) {
                    console.error('生成学号失败:', error)
                    showApiError(error, '生成学号失败')
                }
            }
        } else {
            // 清空专业相关字段
            formData.majorCode = ''
            formData.grade = ''
            formData.classNo = ''

            // 只在新增模式下清空学号
            if (!props.isEdit) {
                formData.studentNo = ''
            }
        }
    }

    // 初始化表单数据
    onMounted(() => {
        fetchMajorList()
        if (props.initialData && Object.keys(props.initialData).length > 0) {

            // 设置学号（编辑模式下）
            if (props.isEdit && props.initialData.studentNo) {
                formData.studentNo = props.initialData.studentNo
            }

            // 设置其他字段
            const initialData = {
                ...props.initialData,
                gender: parseInt(props.initialData.gender)
            }
            Object.assign(formData, initialData)
            setInitialSnapshot()

            // 如果有初始数据，设置专业信息
            if (props.initialData.displayName) {
                // 等待专业列表加载完成
                const timer = setInterval(() => {
                    if (majorList.value.length > 0) {
                        const selectedMajor = majorList.value.find(m => m.label === props.initialData.displayName)
                        if (selectedMajor) {
                            const [code, grade, classNo] = selectedMajor.value.split('-')
                            formData.majorCode = code
                            formData.grade = grade
                            formData.classNo = classNo
                            formData.displayName = selectedMajor.label
                            clearInterval(timer)
                        }
                    }
                }, 100)
            }
        } else setInitialSnapshot()
    })

    // 监听 initialData 变化
    watch(() => props.initialData, (newVal) => {
        if (newVal && Object.keys(newVal).length > 0) {

            // 保持学号不变（编辑模式下）
            if (props.isEdit && newVal.studentNo) {
                formData.studentNo = newVal.studentNo
            }

            const initialData = {
                ...newVal,
                gender: parseInt(newVal.gender)
            }
            Object.assign(formData, initialData)
            setInitialSnapshot()
        }
    }, { deep: true })

    // 处理表单提交
    const handleSubmit = async () => {
        if (!formRef.value) return

        try {
            await formRef.value.validate()

            // 准备提交的数据
            const submitData = {
                ...formData,
                gender: String(formData.gender) // 确保gender是字符串类型
            }

            emit('submit', submitData)
        } catch (error) {
            console.error('表单验证失败:', error)
            showApiError(error, '请检查表单填写是否正确')
        }
    }
</script>

<style lang="scss" scoped>
    .student-form {
        padding: 20px;
        max-width: 1200px;
        margin: 0 auto;

        .el-form-item {
            margin-bottom: 22px;
        }

        .el-input,
        .el-select,
        .el-date-picker {
            width: 100%;
        }
    }
</style>
