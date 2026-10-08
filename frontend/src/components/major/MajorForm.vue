<template>
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" class="major-form">
        <el-form-item label="专业名称" prop="majorName">
            <el-input v-model="formData.majorName" placeholder="请输入专业名称" />
        </el-form-item>
        <el-form-item label="专业代码" prop="majorCode">
            <el-input v-model="formData.majorCode" placeholder="请输入专业代码" />
        </el-form-item>
        <el-form-item label="显示名称" prop="displayName">
            <el-input v-model="formData.displayName" placeholder="请输入显示名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态">
                <el-option label="正常" :value="0" />
                <el-option label="停招" :value="1" />
                <el-option label="撤销" :value="2" />
            </el-select>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="handleSubmit">保存</el-button>
            <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
    </el-form>
</template>

<script setup>
import { showApiError } from "@/utils/errorHandler";
    import { ref, defineProps, defineEmits, onMounted } from 'vue'
    import { ElMessage } from 'element-plus'
    import { getMajorById, updateMajor, createMajor } from '@/api/major'

    const props = defineProps({
        id: {
            type: [String, Number],
            default: null
        },
        initialData: {
            type: Object,
            default: () => ({})
        }
    })

    const emit = defineEmits(['submit', 'cancel', 'success'])

    const formRef = ref(null)
    const formData = ref({
        majorName: '',
        majorCode: '',
        displayName: '',
        status: 0
    })

    const rules = {
        majorName: [
            { required: true, message: '请输入专业名称', trigger: 'blur' }
        ],
        majorCode: [
            { required: true, message: '请输入专业代码', trigger: 'blur' }
        ],
        displayName: [
            { required: true, message: '请输入显示名称', trigger: 'blur' }
        ],
        status: [
            { required: true, message: '请选择状态', trigger: 'change' }
        ]
    }

    const fetchMajorInfo = async () => {
        if (!props.id) return

        try {
            const response = await getMajorById(props.id)
            Object.assign(formData.value, response.data)
        } catch (error) {
            console.error('获取专业信息失败:', error)
            showApiError(error, '获取专业信息失败')
        }
    }

    const handleSubmit = async () => {
        if (!formRef.value) return;
        await formRef.value.validate(async (valid) => {
            if (valid) {
                try {
                    if (props.id) {
                        await updateMajor(props.id, formData.value);
                    } else {
                        await createMajor(formData.value);
                    }
                    emit('success');
                } catch (error) {
                    console.error('提交专业表单失败：', error);
                    showApiError(error, '操作失败');
                }
            }
        });
    };

    const handleCancel = () => {
        emit('cancel')
    }

    onMounted(() => {
        if (props.initialData && Object.keys(props.initialData).length > 0) {
            Object.assign(formData.value, props.initialData)
        } else {
            fetchMajorInfo()
        }
    })
</script>

<style scoped>
    .major-form {
        max-width: 500px;
        margin: 0 auto;
    }
</style>
