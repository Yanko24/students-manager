<template>
    <div class="course-form-container">
        <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" label-position="right">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="课程编号" prop="code">
                        <el-input v-model="formData.code" placeholder="请输入课程编号" :disabled="props.isEdit" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="课程名称" prop="name">
                        <el-input v-model="formData.name" placeholder="请输入课程名称" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="课程类型" prop="type">
                        <el-select v-model="formData.type" placeholder="请选择课程类型" style="width: 100%">
                            <el-option label="必修课" value="必修课" />
                            <el-option label="选修课" value="选修课" />
                            <el-option label="公共课" value="公共课" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="开课学期" prop="semester">
                        <el-input v-model="formData.semester" placeholder="例如：2026-2027-1" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="学分" prop="credit">
                        <el-input-number v-model="formData.credit" :min="1" :max="10" :step="1"
                            style="width: 100%" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="学时" prop="hours">
                        <el-input-number v-model="formData.hours" :min="16" :max="120" :step="2" style="width: 100%" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="授课教师" prop="teacherId">
                        <el-select v-model="formData.teacherId" placeholder="请选择教师" filterable style="width: 100%">
                            <el-option v-for="teacher in teachers" :key="teacher.id" :label="`${teacher.realName} (${teacher.teacherNo})`" :value="teacher.id" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="状态" prop="status">
                        <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                            <el-option label="未开课" :value="0" />
                            <el-option label="已开课" :value="1" />
                            <el-option label="已结课" :value="2" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-form-item label="课程简介" prop="description">
                <el-input v-model="formData.description" type="textarea" rows="3" placeholder="请输入课程简介" />
            </el-form-item>

            <el-form-item label="教学目标" prop="objectives">
                <el-input v-model="formData.objectives" type="textarea" rows="3" placeholder="请输入教学目标" />
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="handleSubmit">保存</el-button>
                <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
    import { ref, onMounted, reactive } from 'vue'
    import { ElMessage } from 'element-plus'
    import { getTeacherList } from '@/api/teacher'
    import { getCourseById } from '@/api/course'

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
    const teachers = ref([])

    // 表单数据
    const formData = reactive({
        code: '',
        name: '',
        type: '',
        semester: '',
        credit: 3,
        hours: 48,
        teacherId: null,
        status: 0,
        description: '',
        objectives: ''
    })

    // 表单验证规则
    const rules = {
        code: [
            { required: true, message: '请输入课程编号', trigger: 'blur' },
            { pattern: /^[A-Z]{2}\d{3}$/, message: '课程编号格式为：2个大写字母+3个数字', trigger: 'blur' }
        ],
        name: [
            { required: true, message: '请输入课程名称', trigger: 'blur' },
            { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        type: [
            { required: true, message: '请选择课程类型', trigger: 'change' }
        ],
        semester: [
            { required: true, message: '请选择开课学期', trigger: 'change' }
        ],
        credit: [
            { required: true, message: '请输入学分', trigger: 'blur' }
        ],
        hours: [
            { required: true, message: '请输入学时', trigger: 'blur' }
        ],
        teacherId: [
            { required: true, message: '请选择授课教师', trigger: 'change' }
        ],
        status: [
            { required: true, message: '请选择状态', trigger: 'change' }
        ]
    }

    // 提交表单
    const handleSubmit = async () => {
        if (!formRef.value) return

        await formRef.value.validate((valid, fields) => {
            if (valid) {
                emit('submit', formData)
            } else {
                console.error('表单验证失败:', fields)
            }
        })
    }

    // 取消
    const handleCancel = () => {
        emit('cancel')
    }

    // 加载课程数据
    const loadCourseData = async (id) => {
        const response = await getCourseById(id)
        if (response?.code !== 200 || !response.data) throw new Error(response?.message || '获取课程信息失败')
        Object.assign(formData, response.data)
    }

    onMounted(async () => {
        try {
            const response = await getTeacherList({ page: 1, size: 100 })
            if (response?.code === 200) teachers.value = response.data?.records || []
            if (props.isEdit) await loadCourseData(props.id)
        } catch (error) {
            ElMessage.error(error.message || '加载课程信息失败')
        }
    })
</script>

<style lang="scss" scoped>
    .course-form-container {
        padding: 20px;
    }
</style>
