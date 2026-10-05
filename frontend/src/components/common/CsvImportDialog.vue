<template>
    <el-dialog
        :model-value="modelValue"
        :title="title"
        width="640px"
        class="csv-import-dialog"
        :close-on-click-modal="!loading"
        :show-close="!loading"
        @update:model-value="$emit('update:modelValue', $event)"
        @closed="clearFile"
    >
        <div class="import-intro">
            <div class="intro-icon">表</div>
            <div>
                <h3>按模板整理数据，一次完成批量导入</h3>
                <p>{{ description }}</p>
            </div>
        </div>

        <div class="import-steps">
            <div class="step-item"><span>1</span><div><strong>下载模板</strong><small>使用系统提供的标准列名</small></div></div>
            <div class="step-item"><span>2</span><div><strong>填写数据</strong><small>保留表头，不要调整列顺序</small></div></div>
            <div class="step-item"><span>3</span><div><strong>上传导入</strong><small>系统校验通过后写入数据</small></div></div>
        </div>

        <div class="template-panel">
            <div>
                <strong>{{ templateName }}</strong>
                <p>CSV 文件 · 中文表头</p>
            </div>
            <el-button type="primary" plain tag="a" :href="templateHref" download>
                下载模板
            </el-button>
        </div>

        <div class="field-panel">
            <div class="panel-title">模板字段</div>
            <div class="field-tags">
                <el-tag v-for="field in fields" :key="field" effect="plain" type="info">{{ field }}</el-tag>
            </div>
        </div>

        <el-upload
            ref="uploadRef"
            class="csv-uploader"
            drag
            accept=".csv,text/csv"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-exceed="handleExceed"
            :on-remove="handleRemove"
        >
            <div class="upload-content">
                <el-icon class="upload-icon"><UploadFilled /></el-icon>
                <div class="upload-title">将 CSV 文件拖到此处，或 <em>点击选择</em></div>
                <div class="upload-tip">仅支持 .csv 文件，大小不超过 {{ maxSize }} MB</div>
            </div>
        </el-upload>

        <div class="import-notice">
            <div class="panel-title">导入须知</div>
            <ul>
                <li v-for="tip in tips" :key="tip">{{ tip }}</li>
            </ul>
        </div>

        <template #footer>
            <el-button :disabled="loading" @click="$emit('update:modelValue', false)">取消</el-button>
            <el-button type="primary" :loading="loading" :disabled="!selectedFile" @click="submit">
                校验并导入
            </el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

const props = defineProps({
    modelValue: { type: Boolean, required: true },
    title: { type: String, required: true },
    description: { type: String, required: true },
    templateName: { type: String, required: true },
    templateHref: { type: String, required: true },
    fields: { type: Array, default: () => [] },
    tips: { type: Array, default: () => [] },
    maxSize: { type: Number, default: 5 },
    loading: { type: Boolean, default: false },
})

const emit = defineEmits(['update:modelValue', 'submit'])
const uploadRef = ref()
const selectedFile = ref(null)

const handleFileChange = (file) => {
    const rawFile = file.raw
    if (!rawFile) return
    if (!rawFile.name.toLowerCase().endsWith('.csv')) {
        ElMessage.warning('请选择 CSV 格式文件')
        uploadRef.value?.clearFiles()
        selectedFile.value = null
        return
    }
    if (rawFile.size > props.maxSize * 1024 * 1024) {
        ElMessage.warning(`文件不能超过 ${props.maxSize} MB`)
        uploadRef.value?.clearFiles()
        selectedFile.value = null
        return
    }
    selectedFile.value = rawFile
}

const handleExceed = () => ElMessage.warning('一次只能选择一个文件，请先移除已选文件')
const handleRemove = () => { selectedFile.value = null }
const clearFile = () => {
    selectedFile.value = null
    uploadRef.value?.clearFiles()
}
const submit = () => {
    if (selectedFile.value) emit('submit', selectedFile.value)
}
</script>

<style scoped>
.import-intro { display: flex; align-items: flex-start; gap: 14px; margin-bottom: 20px; }
.intro-icon { display: grid; place-items: center; flex: 0 0 42px; height: 42px; border-radius: 12px; color: var(--el-color-primary); background: var(--el-color-primary-light-9); font-weight: 700; }
.import-intro h3 { margin: 0 0 6px; color: var(--el-text-color-primary); font-size: 16px; }
.import-intro p, .template-panel p { margin: 0; color: var(--el-text-color-secondary); font-size: 13px; line-height: 1.6; }
.import-steps { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-bottom: 18px; }
.step-item { display: flex; align-items: center; gap: 9px; min-width: 0; padding: 12px 10px; border-radius: 10px; background: var(--el-fill-color-light); }
.step-item > span { display: grid; place-items: center; flex: 0 0 24px; height: 24px; border-radius: 50%; color: #fff; background: var(--el-color-primary); font-size: 12px; }
.step-item strong, .step-item small { display: block; white-space: nowrap; }
.step-item strong { color: var(--el-text-color-primary); font-size: 13px; }
.step-item small { margin-top: 3px; color: var(--el-text-color-secondary); font-size: 11px; }
.template-panel { display: flex; justify-content: space-between; align-items: center; gap: 12px; padding: 14px 16px; border: 1px solid var(--el-border-color-lighter); border-radius: 10px; }
.template-panel strong, .panel-title { color: var(--el-text-color-primary); font-size: 14px; font-weight: 600; }
.field-panel { margin: 16px 0; }
.panel-title { margin-bottom: 9px; }
.field-tags { display: flex; flex-wrap: wrap; gap: 7px; }
.csv-uploader :deep(.el-upload), .csv-uploader :deep(.el-upload-dragger) { width: 100%; }
.csv-uploader :deep(.el-upload-dragger) { padding: 17px 12px; border-radius: 10px; }
.upload-icon { color: var(--el-color-primary); font-size: 28px; }
.upload-title { margin-top: 7px; color: var(--el-text-color-primary); font-size: 14px; }
.upload-title em { color: var(--el-color-primary); font-style: normal; }
.upload-tip { margin-top: 5px; color: var(--el-text-color-secondary); font-size: 12px; }
.import-notice { margin-top: 16px; padding: 12px 14px; border-radius: 9px; background: var(--el-color-warning-light-9); }
.import-notice .panel-title { margin-bottom: 6px; }
.import-notice ul { margin: 0; padding-left: 18px; color: var(--el-text-color-regular); font-size: 12px; line-height: 1.8; }
@media (max-width: 600px) {
    .import-steps { grid-template-columns: 1fr; }
    .step-item small { white-space: normal; }
    .template-panel { align-items: flex-start; flex-direction: column; }
}
</style>
