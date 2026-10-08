import { onBeforeUnmount, onMounted, ref } from 'vue'
import { getUnreadNotificationCount } from '@/api/notification'

export function useUnreadNotificationCount() {
  const count = ref(0)
  let timer

  const refresh = async () => {
    try {
      const response = await getUnreadNotificationCount()
      count.value = Number(response?.data?.count || 0)
    } catch {
      // Keep the layout usable if the notification counter is temporarily unavailable.
    }
  }

  onMounted(() => {
    refresh()
    timer = window.setInterval(refresh, 60_000)
    window.addEventListener('notifications:updated', refresh)
  })
  onBeforeUnmount(() => {
    window.clearInterval(timer)
    window.removeEventListener('notifications:updated', refresh)
  })

  return { count, refresh }
}
