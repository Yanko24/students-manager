<template>
    <div class="course-list-container">
        <div class="page-header">
            <h2>我的课程</h2>
        </div>

        <el-card class="table-card">
            <el-table :data="courseList" v-loading="loading" border style="width: 100%">
                <el-table-column prop="name" label="课程名称" width="200" />
                <el-table-column prop="code" label="课程代码" width="120" />
                <el-table-column prop="credits" label="学分" width="80" />
                <el-table-column prop="hours" label="学时" width="80" />
                <el-table-column prop="type" label="课程类型" width="120" />
                <el-table-column prop="semester" label="开课学期" width="120" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
                            {{ row.status === 'active' ? '进行中' : '已结束' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="router.push(`/teacher/courses/${row.id}`)">
                            查看
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { getTeacherCourses } from '@/api/teacher'

    const router = useRouter()
    const loading = ref(false)
    const courseList = ref([])
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)

    // 获取课程列表
    const fetchCourseList = async () => {
        loading.value = true
        try {
            const response = await getTeacherCourses({
                page: currentPage.value,
                size: pageSize.value
            })
            if (response && response.data) {
                courseList.value = response.data.records || []
                total.value = response.data.total || 0
            }
        } catch (error) {
            console.error('获取课程列表失败:', error)
            courseList.value = []
            total.value = 0
        } finally {
            loading.value = false
        }
    }

    // 分页大小改变
    const handleSizeChange = (val) => {
        pageSize.value = val
        fetchCourseList()
    }

    // 页码改变
    const handleCurrentChange = (val) => {
        currentPage.value = val
        fetchCourseList()
    }

    onMounted(() => {
        fetchCourseList()
    })
</script>

<style scoped>
    .course-list-container {
        padding: 20px;
    }

    .page-header {
        margin-bottom: 20px;
    }

    .table-card {
        margin-bottom: 20px;
    }

    .pagination-container {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
    }
</style>