<template>
    <div class="pagination-container" v-show="total > 0">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="pageSizes"
            :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'

    const props = defineProps({
        total: {
            type: Number,
            required: true
        },
        onPageChange: {
            type: Function,
            required: true
        }
    })

    const currentPage = ref(1)
    const pageSize = ref(10)
    const pageSizes = [10, 20, 50, 100]

    const handleSizeChange = (size) => {
        pageSize.value = size
        currentPage.value = 1
        emitPageChange()
    }

    const handleCurrentChange = (page) => {
        currentPage.value = page
        emitPageChange()
    }

    const resetToFirstPage = () => {
        currentPage.value = 1
        emitPageChange()
    }

    const emitPageChange = () => {
        props.onPageChange({
            page: currentPage.value,
            size: pageSize.value
        })
    }

    onMounted(emitPageChange)
    defineExpose({ resetToFirstPage })
</script>

<style scoped>
    .pagination-container {
        margin-top: 20px;
        display: flex;
        justify-content: center;
        white-space: nowrap;

        :deep(.el-pagination) {
            display: flex;
            align-items: center;
            flex-wrap: nowrap;
        }

        @media screen and (max-width: 768px) {
            overflow-x: auto;
            padding: 0 10px;
        }
    }
</style>
