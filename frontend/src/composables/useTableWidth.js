import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'

export function useTableWidth(minColumnWidths, padding = 40) {
    const tableRef = ref(null)
    const containerWidth = ref(0)

    // 计算总最小宽度
    const totalMinWidth = Object.values(minColumnWidths).reduce((sum, width) => sum + width, 0)

    // 动态计算列宽
    const columnWidth = computed(() => {
        if (containerWidth.value === 0 || containerWidth.value <= totalMinWidth) {
            return minColumnWidths
        }

        const extraWidth = containerWidth.value - totalMinWidth
        const totalWeight = Object.keys(minColumnWidths).length
        const extraPerColumn = Math.floor(extraWidth / totalWeight)

        return Object.entries(minColumnWidths).reduce((acc, [key, minWidth]) => {
            acc[key] = minWidth + extraPerColumn
            return acc
        }, {})
    })

    // 计算表格总宽度
    const tableWidth = computed(() => {
        return Math.max(totalMinWidth, containerWidth.value)
    })

    // 更新容器宽度
    const updateContainerWidth = () => {
        if (tableRef.value?.$el) {
            const parentWidth = tableRef.value.$el.parentElement.clientWidth
            containerWidth.value = parentWidth - padding // 减去内边距
        }
    }

    // 监听窗口大小变化
    const handleResize = () => {
        updateContainerWidth()
    }

    // 初始化
    const initializeWidth = () => {
        nextTick(() => {
            updateContainerWidth()
            window.addEventListener('resize', handleResize)
        })
    }

    // 清理
    const cleanupWidth = () => {
        window.removeEventListener('resize', handleResize)
    }

    onMounted(initializeWidth)
    onUnmounted(cleanupWidth)

    return {
        tableRef,
        columnWidth,
        tableWidth
    }
} 