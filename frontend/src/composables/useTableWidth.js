import { ref, computed, unref, onMounted, onUnmounted, nextTick } from 'vue'

// Measure source values instead of rendered cells: assigned widths never feed
// back into the next calculation, and Element Plus owns header/body alignment.
export function useTableWidth(minColumnWidths, rows, fieldMap = {}) {
    const tableRef = ref(null)
    const containerWidth = ref(0)
    const font = ref('14px sans-serif')
    let context

    const contentWidths = computed(() => {
        const currentFont = font.value
        if (context) context.font = currentFont
        return Object.fromEntries(Object.entries(minColumnWidths).map(([key, minimum]) => {
            const field = fieldMap[key] || key
            const width = (unref(rows) || []).reduce((largest, row) => {
                const value = typeof field === 'function' ? field(row) : row[field]
                if (value == null || typeof value === 'object') return largest
                const text = String(value)
                const measured = context ? context.measureText(text).width : [...text].reduce((sum, char) => sum + (/[^\x00-\x7f]/.test(char) ? 14 : 8), 0)
                return Math.max(largest, Math.ceil(measured) + 32)
            }, minimum)
            return [key, width]
        }))
    })

    const columnWidth = computed(() => {
        const widths = contentWidths.value
        const total = Object.values(widths).reduce((sum, width) => sum + width, 0)
        const extra = Math.max(0, containerWidth.value - total)
        const keys = Object.keys(widths)
        return Object.fromEntries(keys.map((key, index) => [key, widths[key] + Math.floor(extra / keys.length) + (index < extra % keys.length ? 1 : 0)]))
    })
    const tableWidth = computed(() => Object.values(columnWidth.value).reduce((sum, width) => sum + width, 0))

    const updateContainerWidth = () => {
        const element = tableRef.value?.$el
        if (!element) return
        containerWidth.value = Math.max(0, Math.floor(element.parentElement.clientWidth))
        const cell = element.querySelector('.cell')
        if (cell) {
            const style = getComputedStyle(cell)
            font.value = `${style.fontWeight} ${style.fontSize} ${style.fontFamily}`
        }
    }

    onMounted(async () => {
        context = document.createElement('canvas').getContext('2d')
        await nextTick()
        updateContainerWidth()
        window.addEventListener('resize', updateContainerWidth)
    })
    onUnmounted(() => window.removeEventListener('resize', updateContainerWidth))

    return { tableRef, columnWidth, tableWidth }
}
