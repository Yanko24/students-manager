<template>
    <section class="admin-record-page record-detail-page">
        <header class="page-header">
            <h2>{{ title }}</h2>
            <div class="header-actions">
                <slot name="actions" />
                <el-button @click="router.push(backPath)">返回列表</el-button>
            </div>
        </header>

        <el-card class="record-card" v-loading="loading">
            <el-descriptions :column="2" border>
                <slot />
            </el-descriptions>
        </el-card>
    </section>
</template>

<script setup>
import { useRouter } from 'vue-router'

defineProps({
    title: { type: String, required: true },
    backPath: { type: String, required: true },
    loading: { type: Boolean, default: false }
})

const router = useRouter()
</script>
