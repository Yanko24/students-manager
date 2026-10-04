<template>
    <div class="pagination-container" v-if="total > 0">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="pageSizes"
            :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
    </div>
</template>

<script setup>
    import { ref, onMounted, onUnmounted, watch } from 'vue'

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
    const pageSize = ref(20)
    const pageSizes = ref([])

    // 计算最佳的每页显示数量
    const calculateOptimalPageSize = () => {
        // 获取视口高度
        const viewportHeight = window.innerHeight
        // 估算其他元素占用的高度（头部、搜索栏、分页器等）
        const otherElementsHeight = 250 // 预估值，包括页面头部、搜索栏、分页器等
        // 单行高度（包括padding和border）
        const rowHeight = 45 // 表格每行的高度，根据实际情况调整

        // 计算可用于显示表格的高度
        const availableHeight = viewportHeight - otherElementsHeight
        // 计算理论上可以显示的行数
        const theoreticalRows = Math.floor(availableHeight / rowHeight)

        // 将理论行数向下取整到最接近的5的倍数
        const optimalSize = Math.floor(theoreticalRows / 5) * 5

        // 确保页面大小在合理范围内
        const finalSize = Math.max(10, Math.min(100, optimalSize))

        // 生成分页选项
        const sizes = []
        sizes.push(finalSize)
        if (finalSize * 2 <= 100) sizes.push(finalSize * 2)
        if (finalSize * 3 <= 100) sizes.push(finalSize * 3)
        if (!sizes.includes(100)) sizes.push(100)

        pageSizes.value = sizes
        pageSize.value = finalSize
        return finalSize
    }

    const handleSizeChange = (size) => {
        pageSize.value = size
        currentPage.value = 1
        emitPageChange()
    }

    const handleCurrentChange = (page) => {
        currentPage.value = page
        emitPageChange()
    }

    const emitPageChange = () => {
        props.onPageChange({
            page: currentPage.value,
            size: pageSize.value
        })
    }

    // 监听窗口大小变化
    const handleResize = () => {
        calculateOptimalPageSize()
        emitPageChange()
    }

    onMounted(() => {
        calculateOptimalPageSize()
        window.addEventListener('resize', handleResize)
        emitPageChange()
    })

    onUnmounted(() => {
        window.removeEventListener('resize', handleResize)
    })
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