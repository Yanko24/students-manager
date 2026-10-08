<template>
    <div class="college-add-container">
        <div class="page-header">
            <h2>添加学院</h2>
        </div>

        <el-card class="form-card">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
                <el-form-item label="学院代码" prop="code">
                    <el-input v-model="form.code" placeholder="请输入学院代码" />
                </el-form-item>
                <el-form-item label="学院名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入学院名称" />
                </el-form-item>
                <el-form-item label="学院简介" prop="description">
                    <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入学院简介" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSubmit">提交</el-button>
                    <el-button @click="router.back()">取消</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { createCollege } from '@/api/college'
    import { useUnsavedChanges } from '@/composables/useUnsavedChanges'

    const router = useRouter()
    const formRef = ref(null)

    const form = ref({
        code: '',
        name: '',
        description: ''
    })
    const initialSnapshot = JSON.stringify(form.value)
    const { markClean } = useUnsavedChanges(() => JSON.stringify(form.value) !== initialSnapshot)

    const rules = {
        code: [
            { required: true, message: '请输入学院代码', trigger: 'blur' }
        ],
        name: [
            { required: true, message: '请输入学院名称', trigger: 'blur' }
        ]
    }

    const handleSubmit = async () => {
        try {
            await formRef.value.validate()
            await createCollege(form.value)
            ElMessage.success('添加成功')
            markClean()
            router.push('/admin/colleges')
        } catch (error) {
            console.error('添加失败：', error)
            showApiError(error, '添加失败')
        }
    }
</script>

<style scoped>
    .college-add-container {
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
        .college-add-container {
            padding: 10px;
        }
    }
</style>
