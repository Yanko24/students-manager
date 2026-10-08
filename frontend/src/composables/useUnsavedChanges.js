import { onBeforeUnmount, onMounted, unref } from 'vue'
import { onBeforeRouteLeave } from 'vue-router'
import { ElMessageBox } from 'element-plus'

export function useUnsavedChanges(isDirty, options = {}) {
  const message = options.message || '当前有未保存的修改，确定离开并放弃吗？'
  const dirty = () => Boolean(typeof isDirty === 'function' ? isDirty() : unref(isDirty))
  let allowLeaveOnce = false

  onBeforeRouteLeave(async () => {
    if (allowLeaveOnce) {
      allowLeaveOnce = false
      return true
    }
    if (!dirty()) return true
    try {
      await ElMessageBox.confirm(message, '尚未保存', {
        type: 'warning',
        confirmButtonText: '放弃修改并离开',
        cancelButtonText: '继续编辑',
        distinguishCancelAndClose: true,
      })
      return true
    } catch {
      return false
    }
  })

  const handleBeforeUnload = event => {
    if (allowLeaveOnce || !dirty()) return
    event.preventDefault()
    event.returnValue = ''
  }

  onMounted(() => window.addEventListener('beforeunload', handleBeforeUnload))
  onBeforeUnmount(() => window.removeEventListener('beforeunload', handleBeforeUnload))

  return { markClean: () => { allowLeaveOnce = true } }
}
