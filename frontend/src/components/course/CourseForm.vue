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

            <el-divider content-position="left">选课对象</el-divider>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="适用范围" prop="selectionScope">
                        <el-select v-model="formData.selectionScope" style="width: 100%" @change="handleScopeChange">
                            <el-option label="全校学生" value="ALL" />
                            <el-option label="指定学院" value="COLLEGE" />
                            <el-option label="指定专业" value="MAJOR" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col v-if="formData.selectionScope === 'COLLEGE'" :span="12">
                    <el-form-item label="适用学院" prop="selectionCollegeId">
                        <el-select v-model="formData.selectionCollegeId" filterable placeholder="请选择学院" style="width: 100%">
                            <el-option v-for="college in colleges" :key="college.id" :label="college.name" :value="college.id" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col v-if="formData.selectionScope === 'MAJOR'" :span="12">
                    <el-form-item label="适用专业" prop="selectionMajorCode">
                        <el-select v-model="formData.selectionMajorCode" filterable placeholder="请选择专业" style="width: 100%" @change="handleMajorChange">
                            <el-option v-for="major in majors" :key="major.code" :label="`${major.name}（${major.collegeName || '未分配学院'} · ${major.code}）`" :value="major.code" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="适用年级" prop="selectionGrade">
                        <el-input v-model="formData.selectionGrade" maxlength="4" placeholder="留空表示不限；例如 2025" />
                    </el-form-item>
                </el-col>
                <el-col :span="12" class="scope-hint">
                    <span>按学生档案中的入学年级匹配；专业范围会同时校验年级。</span>
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

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="选课容量" prop="maxStudents">
                        <el-input-number v-model="formData.maxStudents" :min="1" :max="500" style="width: 100%" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="选课状态" prop="selectionOpen">
                        <el-switch v-model="formData.selectionOpen" :active-value="1" :inactive-value="0"
                            active-text="开放" inactive-text="关闭" />
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
import { showApiError } from "@/utils/errorHandler";
    import { ref, onMounted, reactive } from 'vue'
    import { ElMessage } from 'element-plus'
    import { getTeacherList } from '@/api/teacher'
    import { getCourseById } from '@/api/course'
    import { getCollegeList } from '@/api/college'
    import { getAllMajorsList } from '@/api/major'

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
    const colleges = ref([])
    const majors = ref([])

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
        maxStudents: 60,
        selectionOpen: 1,
        selectionScope: 'ALL',
        selectionCollegeId: null,
        selectionMajorCode: '',
        selectionGrade: '',
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
        ],
        selectionCollegeId: [{
            validator: (_, value, callback) => {
                if (formData.selectionScope === 'COLLEGE' && !value) callback(new Error('请选择适用学院'))
                else callback()
            },
            trigger: 'change'
        }],
        selectionMajorCode: [{
            validator: (_, value, callback) => {
                if (formData.selectionScope === 'MAJOR' && !value) callback(new Error('请选择适用专业'))
                else callback()
            },
            trigger: 'change'
        }],
        selectionGrade: [{ pattern: /^$|^\d{4}$/, message: '适用年级请填写4位入学年份', trigger: 'blur' }]
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

    const handleScopeChange = () => {
        if (formData.selectionScope !== 'COLLEGE') formData.selectionCollegeId = null
        if (formData.selectionScope !== 'MAJOR') formData.selectionMajorCode = ''
    }

    const handleMajorChange = (majorCode) => {
        formData.selectionCollegeId = majors.value.find((major) => major.code === majorCode)?.collegeId ?? null
    }

    // 加载课程数据
    const loadCourseData = async (id) => {
        const response = await getCourseById(id)
        if (response?.code !== 200 || !response.data) throw new Error(response?.message || '获取课程信息失败')
        Object.assign(formData, response.data)
    }

    onMounted(async () => {
        try {
            const [teacherResponse, collegeResponse, majorResponse] = await Promise.all([
                getTeacherList({ page: 1, size: 100 }),
                getCollegeList({ page: 1, size: 100 }),
                getAllMajorsList()
            ])
            if (teacherResponse?.code === 200) teachers.value = teacherResponse.data?.records || []
            if (collegeResponse?.code === 200) colleges.value = collegeResponse.data?.records || []
            if (majorResponse?.code === 200) {
                const uniqueMajors = new Map()
                for (const major of majorResponse.data?.records || []) uniqueMajors.set(major.code, major)
                majors.value = [...uniqueMajors.values()]
            }
            if (props.isEdit) await loadCourseData(props.id)
        } catch (error) {
            showApiError(error, error.message || '加载课程信息失败')
        }
    })
</script>

<style lang="scss" scoped>
    .course-form-container {
        padding: 20px;
    }

    .scope-hint { display: flex; align-items: center; color: #909399; font-size: 13px; }
</style>
