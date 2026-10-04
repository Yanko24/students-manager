<template>
    <div class="score-form-container">
        <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
            <el-form-item label="学号" prop="studentNo">
                <el-input v-model="formData.studentNo" placeholder="请输入学号" />
            </el-form-item>
            <el-form-item label="课程" prop="courseId">
                <el-select v-model="formData.courseId" placeholder="请选择课程" style="width: 100%">
                    <el-option v-for="course in courseList" :key="course.id" :label="course.name" :value="course.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="成绩" prop="score">
                <el-input-number v-model="formData.score" :min="0" :max="100" :precision="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="考试时间" prop="examTime">
                <el-date-picker v-model="formData.examTime" type="datetime" placeholder="请选择考试时间" style="width: 100%" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
                <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleSubmit">提交</el-button>
                <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
    import { ref, reactive, onMounted } from 'vue'
    import { ElMessage } from 'element-plus'
    import { getCourseList } from '@/api/course'

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
    const courseList = ref([])

    const formData = reactive({
        studentNo: '',
        courseId: '',
        score: 0,
        examTime: '',
        remark: ''
    })

    const rules = {
        studentNo: [
            { required: true, message: '请输入学号', trigger: 'blur' }
        ],
        courseId: [
            { required: true, message: '请选择课程', trigger: 'change' }
        ],
        score: [
            { required: true, message: '请输入成绩', trigger: 'blur' }
        ],
        examTime: [
            { required: true, message: '请选择考试时间', trigger: 'change' }
        ]
    }

    const fetchCourses = async () => {
        try {
            const response = await getCourseList({ page: 1, size: 1000 })
            if (response && response.data) {
                courseList.value = response.data.records || []
            }
        } catch (error) {
            console.error('获取课程列表失败：', error)
            ElMessage.error('获取课程列表失败')
        }
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

    const loadScoreData = async (id) => {
        // TODO: 调用获取成绩详情接口
        // 模拟数据
        formData.studentNo = '2023001'
        formData.courseId = '1'
        formData.score = 85
        formData.examTime = '2024-01-15 09:00:00'
        formData.remark = '考试成绩良好'
    }

    onMounted(() => {
        fetchCourses()
        if (props.isEdit) {
            loadScoreData(props.id)
        }
    })
</script>

<style lang="scss" scoped>
    .score-form-container {
        padding: 20px;
    }
</style>