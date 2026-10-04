<template>
    <div class="score-form-container">
        <div class="page-header">
            <h2>{{ isEdit ? '编辑成绩' : '添加成绩' }}</h2>
        </div>

        <el-card class="form-card">
            <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" label-position="right">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="学生" prop="studentId">
                            <el-select v-model="formData.studentId" placeholder="请选择学生" filterable remote
                                :remote-method="handleStudentSearch" :loading="studentLoading" style="width: 100%">
                                <el-option v-for="item in studentOptions" :key="item.id"
                                    :label="item.name + ' (' + item.id + ')'" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="课程" prop="courseId">
                            <el-select v-model="formData.courseId" placeholder="请选择课程" filterable remote
                                :remote-method="handleCourseSearch" :loading="courseLoading" style="width: 100%">
                                <el-option v-for="item in courseOptions" :key="item.id"
                                    :label="item.name + ' (' + item.code + ')'" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="成绩" prop="score">
                            <el-input-number v-model="formData.score" :min="0" :max="100" :precision="1"
                                style="width: 100%" @change="handleScoreChange" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="等级" prop="grade">
                            <el-input v-model="formData.grade" disabled />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="学期" prop="semester">
                            <el-select v-model="formData.semester" placeholder="请选择学期" style="width: 100%">
                                <el-option label="2023-2024-2" value="2023-2024-2" />
                                <el-option label="2023-2024-1" value="2023-2024-1" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="考试时间" prop="examTime">
                            <el-date-picker v-model="formData.examTime" type="datetime" placeholder="请选择考试时间"
                                style="width: 100%" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-form-item label="评语" prop="comment">
                    <el-input v-model="formData.comment" type="textarea" rows="3" placeholder="请输入评语" />
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="handleSubmit">保存</el-button>
                    <el-button @click="handleCancel">取消</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, computed, onMounted, reactive } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'

    const route = useRoute()
    const router = useRouter()
    const formRef = ref(null)

    // 判断是否为编辑模式
    const isEdit = computed(() => route.params.id !== undefined)

    // 表单数据
    const formData = reactive({
        studentId: '',
        courseId: '',
        score: 0,
        grade: '',
        semester: '',
        examTime: '',
        comment: ''
    })

    // 学生选项
    const studentLoading = ref(false)
    const studentOptions = ref([
        { id: '2021001', name: '张三' },
        { id: '2021002', name: '李四' },
        { id: '2021003', name: '王五' }
    ])

    // 课程选项
    const courseLoading = ref(false)
    const courseOptions = ref([
        { id: 1, code: 'CS101', name: '计算机导论' },
        { id: 2, code: 'CS102', name: 'C语言程序设计' },
        { id: 3, code: 'CS201', name: '数据结构' }
    ])

    // 表单验证规则
    const rules = {
        studentId: [
            { required: true, message: '请选择学生', trigger: 'change' }
        ],
        courseId: [
            { required: true, message: '请选择课程', trigger: 'change' }
        ],
        score: [
            { required: true, message: '请输入成绩', trigger: 'blur' }
        ],
        semester: [
            { required: true, message: '请选择学期', trigger: 'change' }
        ],
        examTime: [
            { required: true, message: '请选择考试时间', trigger: 'change' }
        ]
    }

    // 根据分数计算等级
    const calculateGrade = (score) => {
        if (score >= 90) return 'A+'
        if (score >= 85) return 'A'
        if (score >= 80) return 'B+'
        if (score >= 75) return 'B'
        if (score >= 70) return 'C+'
        if (score >= 60) return 'C'
        if (score >= 50) return 'D'
        return 'F'
    }

    // 方法
    const handleScoreChange = (value) => {
        formData.grade = calculateGrade(value)
    }

    const handleStudentSearch = (query) => {
        if (query) {
            studentLoading.value = true
            // TODO: 调用学生搜索接口
            setTimeout(() => {
                studentLoading.value = false
            }, 200)
        }
    }

    const handleCourseSearch = (query) => {
        if (query) {
            courseLoading.value = true
            // TODO: 调用课程搜索接口
            setTimeout(() => {
                courseLoading.value = false
            }, 200)
        }
    }

    const handleSubmit = async () => {
        if (!formRef.value) return

        await formRef.value.validate((valid, fields) => {
            if (valid) {
                // TODO: 调用保存接口
                ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
                router.push('/admin/scores')
            } else {
                console.error('表单验证失败:', fields)
            }
        })
    }

    const handleCancel = () => {
        router.back()
    }

    // 加载成绩数据
    const loadScoreData = async (id) => {
        // TODO: 调用获取成绩详情接口
        // 模拟数据
        formData.studentId = '2021001'
        formData.courseId = 1
        formData.score = 85
        formData.grade = 'A'
        formData.semester = '2023-2024-2'
        formData.examTime = new Date('2024-01-15 09:00')
        formData.comment = '该生在本课程中表现良好，理论知识掌握扎实，实践能力突出。'
    }

    onMounted(() => {
        if (isEdit.value) {
            loadScoreData(route.params.id)
        }
    })
</script>

<style lang="scss" scoped>
    .score-form-container {
        padding: 20px;

        .page-header {
            margin-bottom: 20px;

            h2 {
                margin: 0;
            }
        }

        .form-card {
            max-width: 1000px;
            margin: 0 auto;
        }
    }
</style>