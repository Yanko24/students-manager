<template>
    <div class="college-edit-container admin-record-page">
        <div class="page-header">
            <h2>编辑学院</h2>
            <div class="header-actions"><el-button @click="router.push('/admin/colleges')">返回列表</el-button></div>
        </div>

        <el-card class="form-card record-card">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
                <el-form-item label="学院代码" prop="code">
                    <el-input v-model="form.code" placeholder="请输入学院代码" disabled />
                </el-form-item>
                <el-form-item label="学院名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入学院名称" />
                </el-form-item>
                <el-form-item label="学院简介" prop="description">
                    <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入学院简介" />
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
import { showApiError } from "@/utils/errorHandler";
    import { ref, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { getCollegeById, updateCollege } from '@/api/college'
    import { useUnsavedChanges } from '@/composables/useUnsavedChanges'

    const router = useRouter()
    const route = useRoute()
    const formRef = ref(null)

    const form = ref({
        code: '',
        name: '',
        description: ''
    })
    const initialSnapshot = ref('')
    const initialized = ref(false)
    const { markClean } = useUnsavedChanges(() => initialized.value && JSON.stringify(form.value) !== initialSnapshot.value)

    const rules = {
        name: [
            { required: true, message: '请输入学院名称', trigger: 'blur' }
        ]
    }

    const fetchCollege = async () => {
        try {
            const response = await getCollegeById(route.params.id)
            if (response && response.data) {
                Object.assign(form.value, response.data)
                initialSnapshot.value = JSON.stringify(form.value)
                initialized.value = true
            }
        } catch (error) {
            console.error('获取学院详情失败：', error)
            showApiError(error, '获取学院详情失败')
        }
    }

    const handleSubmit = async () => {
        try {
            await formRef.value.validate()
            await updateCollege(route.params.id, form.value)
            ElMessage.success('保存成功')
            markClean()
            router.push('/admin/colleges')
        } catch (error) {
            console.error('保存失败：', error)
            showApiError(error, '保存失败')
        }
    }

    onMounted(() => {
        fetchCollege()
    })
</script>

<style scoped>
    .college-edit-container {
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
        .college-edit-container {
            padding: 10px;
        }
    }
</style>
