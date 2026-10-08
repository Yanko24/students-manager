<template>
    <div class="score-form-container">
        <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
            <el-form-item label="学生" prop="studentId">
                <el-select v-model="formData.studentId" placeholder="按学号或姓名搜索" filterable remote
                    :remote-method="searchStudents" :loading="studentLoading" style="width: 100%">
                    <el-option v-for="student in students" :key="student.id"
                        :label="`${student.realName}（${student.studentNo}）`" :value="student.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="课程" prop="courseId">
                <el-select v-model="formData.courseId" placeholder="按课程代码或名称搜索" filterable remote
                    :remote-method="searchCourses" :loading="courseLoading" style="width: 100%">
                    <el-option v-for="course in courses" :key="course.id"
                        :label="`${course.name}（${course.code}）`" :value="course.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="学期" prop="semester">
                <el-input v-model="formData.semester" placeholder="例如：2026-2027-1" />
            </el-form-item>
            <el-form-item label="考试类型" prop="attemptType">
                <el-select v-model="formData.attemptType" style="width: 100%">
                    <el-option label="正常考试" value="REGULAR" />
                    <el-option label="补考" value="MAKEUP" />
                    <el-option label="重修" value="RETAKE" />
                </el-select>
            </el-form-item>
            <el-form-item label="考试次数" prop="attemptNo">
                <el-input-number v-model="formData.attemptNo" :min="1" :max="10" :precision="0" style="width: 100%" />
            </el-form-item>
            <el-form-item label="成绩" prop="score">
                <el-input-number v-model="formData.score" :min="0" :max="100" :precision="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="考试时间" prop="examTime">
                <el-date-picker v-model="formData.examTime" type="datetime" placeholder="请选择考试时间" style="width: 100%" />
            </el-form-item>
            <el-form-item label="评语" prop="comment">
                <el-input v-model="formData.comment" type="textarea" :rows="3" placeholder="请输入评语" />
            </el-form-item>
            <el-form-item v-if="isEdit" label="更正原因" prop="changeReason">
                <el-input v-model="formData.changeReason" type="textarea" :rows="2" placeholder="说明本次成绩更正原因" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleSubmit">提交</el-button>
                <el-button @click="emit('cancel')">取消</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
    import { ref, reactive, onMounted } from 'vue'
    import { ElMessage } from 'element-plus'
    import { getStudentList } from '@/api/student'
    import { getCourseList } from '@/api/course'
    import { getScoreById } from '@/api/score'
    import { useUnsavedChanges } from '@/composables/useUnsavedChanges'

    const props = defineProps({ id: { type: String, default: '' }, isEdit: { type: Boolean, default: false } })
    const emit = defineEmits(['submit', 'cancel'])
    const formRef = ref(null)
    const students = ref([])
    const courses = ref([])
    const studentLoading = ref(false)
    const courseLoading = ref(false)
    const formData = reactive({ studentId: null, courseId: null, score: 0, grade: '', semester: '', attemptType: 'REGULAR', attemptNo: 1, changeReason: '', examTime: '', comment: '' })
    const initialSnapshot = ref('')
    const initialized = ref(false)
    const { markClean } = useUnsavedChanges(() => initialized.value && JSON.stringify(formData) !== initialSnapshot.value)
    const setInitialSnapshot = () => { initialSnapshot.value = JSON.stringify(formData); initialized.value = true }
    defineExpose({ markClean })
    const rules = {
        studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
        score: [{ required: true, message: '请输入成绩', trigger: 'blur' }],
        semester: [{ required: true, message: '请输入学期', trigger: 'blur' }],
        examTime: [{ required: true, message: '请选择考试时间', trigger: 'change' }],
        changeReason: [{ required: true, message: '请填写成绩更正原因', trigger: 'blur' }]
    }

    const searchStudents = async (query = '') => {
        studentLoading.value = true
        try {
            const params = { page: 1, size: 50 }
            if (query) params[(/\d/.test(query) ? 'studentNo' : 'realName')] = query
            const response = await getStudentList(params)
            if (response?.code === 200) students.value = response.data?.records || []
        } catch (error) {
            showApiError(error, error.message || '加载学生列表失败')
        } finally {
            studentLoading.value = false
        }
    }

    const searchCourses = async (query = '') => {
        courseLoading.value = true
        try {
            const params = { page: 1, size: 50 }
            if (query) params[/^[a-z0-9-]+$/i.test(query) ? 'code' : 'name'] = query
            const response = await getCourseList(params)
            if (response?.code === 200) courses.value = response.data?.records || []
        } catch (error) {
            showApiError(error, error.message || '加载课程列表失败')
        } finally {
            courseLoading.value = false
        }
    }

    const handleSubmit = async () => {
        try {
            await formRef.value.validate()
            const examTime = formData.examTime instanceof Date
                ? new Date(formData.examTime.getTime() - formData.examTime.getTimezoneOffset() * 60000).toISOString().slice(0, 19)
                : formData.examTime
            emit('submit', { ...formData, examTime })
        } catch (error) {
            if (error) console.error('成绩表单验证失败：', error)
        }
    }

    onMounted(async () => {
        try {
            if (props.isEdit) {
                const response = await getScoreById(props.id)
                if (response?.code !== 200 || !response.data) throw new Error(response?.message || '获取成绩信息失败')
                Object.assign(formData, response.data)
                formData.changeReason = ''
                formData.examTime = response.data.examTime ? new Date(response.data.examTime) : ''
                await Promise.all([searchStudents(response.data.studentNo), searchCourses(response.data.courseCode)])
            } else {
                await Promise.all([searchStudents(''), searchCourses('')])
            }
            setInitialSnapshot()
        } catch (error) {
            showApiError(error, error.message || '加载成绩信息失败')
        }
    })
</script>

<style lang="scss" scoped>
    .score-form-container { padding: 20px; }
</style>
