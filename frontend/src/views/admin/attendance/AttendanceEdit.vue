<template>
    <div class="attendance-edit-container">
        <div class="page-header">
            <h2>编辑考勤</h2>
        </div>

        <el-card class="form-card">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
                <el-form-item label="学号" prop="studentNo">
                    <el-input v-model="form.studentNo" placeholder="请输入学号" disabled />
                </el-form-item>
                <el-form-item label="课程" prop="courseId">
                    <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%" disabled>
                        <el-option v-for="course in courseList" :key="course.id" :label="course.courseName"
                            :value="course.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="日期" prop="date">
                    <el-date-picker v-model="form.date" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" style="width: 100%" />
                </el-form-item>
                <el-form-item label="节次">
                    <el-input v-model="form.classPeriod" placeholder="如：第1-2节" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                        <el-option label="正常" value="正常" />
                        <el-option label="迟到" value="迟到" />
                        <el-option label="早退" value="早退" />
                        <el-option label="缺勤" value="缺勤" />
                        <el-option label="请假" value="请假" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSubmit">保存</el-button>
                    <el-button @click="router.back()">取消</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getAttendanceById, updateAttendance } from '@/api/attendance'
    import { getCourseList } from '@/api/course'

    const router = useRouter()
    const route = useRoute()
    const formRef = ref(null)
    const courseList = ref([])

    const form = ref({
        studentNo: '',
        courseId: '',
        date: '',
        classPeriod: '',
        status: '',
        remark: ''
    })

    const rules = {
        date: [
            { required: true, message: '请选择日期', trigger: 'change' }
        ],
        status: [
            { required: true, message: '请选择状态', trigger: 'change' }
        ]
    }

    const fetchAttendance = async () => {
        try {
            const response = await getAttendanceById(route.params.id)
            if (response?.code === 200 && response.data) {
                Object.assign(form.value, response.data)
            }
        } catch (error) {
            console.error('获取考勤详情失败：', error)
            ElMessage.error('获取考勤详情失败')
        }
    }

    const fetchCourses = async () => {
        try {
            const response = await getCourseList({ page: 1, size: 1000 })
            if (response?.code === 200 && response.data) {
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
            await updateAttendance(route.params.id, form.value)
            ElMessage.success('保存成功')
            router.push('/admin/attendance')
        } catch (error) {
            console.error('保存失败：', error)
            ElMessage.error('保存失败')
        }
    }

    onMounted(() => {
        fetchAttendance()
        fetchCourses()
    })
</script>

<style scoped>
    .attendance-edit-container {
        padding: 20px;
        width: 100%;
        margin: 0 auto;
        box-sizing: border-box;
    }

    .page-header {
        margin-bottom: 20px;
    }

    .page-header h2 {
        margin: 0;
        font-size: 24px;
        color: #303133;
    }

    .form-card {
        max-width: 800px;
        margin: 0 auto;
    }

    @media screen and (max-width: 768px) {
        .attendance-edit-container {
            padding: 10px;
        }
    }
</style>
