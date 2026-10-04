<template>
    <div class="scores-container">
        <div class="page-header">
            <h2>成绩管理</h2>
            <el-button type="primary" @click="handleBatchImport">批量导入</el-button>
        </div>

        <!-- 搜索表单 -->
        <el-card class="search-card">
            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="课程">
                    <el-select v-model="searchForm.courseId" placeholder="请选择课程" clearable style="width: 200px">
                        <el-option label="计算机导论" value="CS101" />
                        <el-option label="C语言程序设计" value="CS102" />
                        <el-option label="数据结构" value="CS201" />
                    </el-select>
                </el-form-item>
                <el-form-item label="班级">
                    <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable style="width: 200px">
                        <el-option label="计算机科学与技术2班" value="CS2" />
                        <el-option label="软件工程1班" value="SE1" />
                        <el-option label="信息安全3班" value="IS3" />
                    </el-select>
                </el-form-item>
                <el-form-item label="学期">
                    <el-select v-model="searchForm.semester" placeholder="请选择学期" clearable style="width: 200px">
                        <el-option label="2023-2024-2" value="2023-2024-2" />
                        <el-option label="2023-2024-1" value="2023-2024-1" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 成绩列表 -->
        <el-card class="list-card">
            <el-table :data="scoreList" v-loading="loading" border stripe>
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="studentId" label="学号" width="120" />
                <el-table-column prop="name" label="姓名" width="100" />
                <el-table-column prop="class" label="班级" width="150" />
                <el-table-column prop="course" label="课程" width="150" />
                <el-table-column prop="semester" label="学期" width="120" align="center" />
                <el-table-column prop="usualScore" label="平时成绩" width="100" align="center" />
                <el-table-column prop="midtermScore" label="期中成绩" width="100" align="center" />
                <el-table-column prop="finalScore" label="期末成绩" width="100" align="center" />
                <el-table-column prop="totalScore" label="总评成绩" width="100" align="center">
                    <template #default="{ row }">
                        <span :class="{ 'text-danger': row.totalScore < 60 }">{{ row.totalScore || '未录入' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="grade" label="等级" width="80" align="center">
                    <template #default="{ row }">
                        <el-tag :type="getGradeType(row.grade)">
                            {{ row.grade || '未评定' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button-group>
                            <el-button type="primary" link @click="handleEdit(row)">
                                编辑
                            </el-button>
                            <el-button type="success" link @click="handleViewDetail(row)">
                                详情
                            </el-button>
                        </el-button-group>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </el-card>

        <!-- 成绩编辑对话框 -->
        <el-dialog v-model="dialogVisible" title="成绩录入" width="500px">
            <el-form :model="scoreForm" label-width="100px">
                <el-form-item label="学号">
                    <el-input v-model="scoreForm.studentId" disabled />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="scoreForm.name" disabled />
                </el-form-item>
                <el-form-item label="课程">
                    <el-input v-model="scoreForm.course" disabled />
                </el-form-item>
                <el-form-item label="平时成绩">
                    <el-input-number v-model="scoreForm.usualScore" :min="0" :max="100" style="width: 100%" />
                </el-form-item>
                <el-form-item label="期中成绩">
                    <el-input-number v-model="scoreForm.midtermScore" :min="0" :max="100" style="width: 100%" />
                </el-form-item>
                <el-form-item label="期末成绩">
                    <el-input-number v-model="scoreForm.finalScore" :min="0" :max="100" style="width: 100%" />
                </el-form-item>
                <el-form-item label="总评成绩">
                    <el-input-number v-model="scoreForm.totalScore" :min="0" :max="100" style="width: 100%" disabled />
                </el-form-item>
                <el-form-item label="等级">
                    <el-select v-model="scoreForm.grade" style="width: 100%">
                        <el-option label="A" value="A" />
                        <el-option label="B" value="B" />
                        <el-option label="C" value="C" />
                        <el-option label="D" value="D" />
                        <el-option label="F" value="F" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="scoreForm.remark" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSave">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
    import { ref, watch } from 'vue'
    import { ElMessage } from 'element-plus'

    // 搜索表单
    const searchForm = ref({
        courseId: '',
        classId: '',
        semester: ''
    })

    // 列表数据
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(100)

    // 模拟成绩数据
    const scoreList = ref([
        {
            studentId: '2024001',
            name: '张三',
            class: '计算机科学与技术2班',
            course: '计算机导论',
            semester: '2023-2024-2',
            usualScore: 85,
            midtermScore: 88,
            finalScore: 90,
            totalScore: 88,
            grade: 'A',
            remark: ''
        },
        {
            studentId: '2024002',
            name: '李四',
            class: '软件工程1班',
            course: 'C语言程序设计',
            semester: '2023-2024-2',
            usualScore: 78,
            midtermScore: 82,
            finalScore: 85,
            totalScore: 82,
            grade: 'B',
            remark: ''
        },
        {
            studentId: '2024003',
            name: '王五',
            class: '信息安全3班',
            course: '数据结构',
            semester: '2023-2024-2',
            usualScore: 65,
            midtermScore: 68,
            finalScore: 70,
            totalScore: 68,
            grade: 'C',
            remark: '需要加强练习'
        }
    ])

    // 对话框数据
    const dialogVisible = ref(false)
    const scoreForm = ref({
        studentId: '',
        name: '',
        course: '',
        usualScore: 0,
        midtermScore: 0,
        finalScore: 0,
        totalScore: 0,
        grade: '',
        remark: ''
    })

    // 监听成绩变化，自动计算总评
    watch(
        () => [scoreForm.value.usualScore, scoreForm.value.midtermScore, scoreForm.value.finalScore],
        ([usual, midterm, final]) => {
            // 总评 = 平时成绩*30% + 期中成绩*30% + 期末成绩*40%
            scoreForm.value.totalScore = Math.round(usual * 0.3 + midterm * 0.3 + final * 0.4)
        }
    )

    // 方法
    const handleSearch = () => {
        loading.value = true
        // TODO: 调用搜索接口
        setTimeout(() => {
            loading.value = false
        }, 1000)
    }

    const handleReset = () => {
        searchForm.value = {
            courseId: '',
            classId: '',
            semester: ''
        }
        handleSearch()
    }

    const handleBatchImport = () => {
        // TODO: 实现批量导入功能
        ElMessage.info('批量导入功能开发中')
    }

    const getGradeType = (grade) => {
        const typeMap = {
            'A': 'success',
            'B': 'success',
            'C': 'warning',
            'D': 'warning',
            'F': 'danger'
        }
        return typeMap[grade] || 'info'
    }

    const handleEdit = (row) => {
        scoreForm.value = { ...row }
        dialogVisible.value = true
    }

    const handleViewDetail = (row) => {
        // TODO: 实现查看详情功能
        ElMessage.info('查看详情功能开发中')
    }

    const handleSave = () => {
        // TODO: 调用保存接口
        ElMessage.success('保存成功')
        dialogVisible.value = false
    }

    const handleSizeChange = (val) => {
        pageSize.value = val
        handleSearch()
    }

    const handleCurrentChange = (val) => {
        currentPage.value = val
        handleSearch()
    }
</script>

<style lang="scss" scoped>
    .scores-container {
        .page-header {
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;

            h2 {
                margin: 0;
            }
        }

        .search-card {
            margin-bottom: 20px;
        }

        .list-card {
            .pagination-container {
                margin-top: 20px;
                display: flex;
                justify-content: flex-end;
            }
        }

        .text-danger {
            color: #f56c6c;
        }
    }
</style>