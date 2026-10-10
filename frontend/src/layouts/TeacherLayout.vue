<template>
    <div class="teacher-layout">
        <el-container>
            <el-aside width="220px" class="sidebar" :class="{ 'is-collapse': isSidebarCollapsed }">
                <div class="logo">
                    <img src="@/assets/images/logo.png" alt="Logo" />
                    <h1>知行教务</h1>
                </div>
                <el-menu :default-active="activeMenu" class="sidebar-menu" :collapse="isSidebarCollapsed"
                    background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF" @select="handleMenuSelect">
                    <el-menu-item index="/teacher/dashboard">
                        <el-icon>
                            <Monitor />
                        </el-icon>
                        <template #title>控制台</template>
                    </el-menu-item>
                    <el-menu-item index="/teacher/courses">
                        <el-icon>
                            <Reading />
                        </el-icon>
                        <template #title>我的课程</template>
                    </el-menu-item>
                    <el-menu-item index="/teacher/attendance">
                        <el-icon>
                            <Calendar />
                        </el-icon>
                        <template #title>考勤管理</template>
                    </el-menu-item>
                    <el-menu-item index="/teacher/notifications">
                        <el-icon><Document /></el-icon>
                        <span>站内通知<span v-if="unreadNotificationCount">（{{ unreadNotificationCount > 99 ? '99+' : unreadNotificationCount }}）</span></span>
                    </el-menu-item>
                    <el-menu-item index="/teacher/profile">
                        <el-icon>
                            <User />
                        </el-icon>
                        <template #title>个人信息</template>
                    </el-menu-item>
                </el-menu>
            </el-aside>
            <el-container>
                <el-header>
                    <div class="header-content">
                        <div class="breadcrumb">
                            <el-icon class="toggle-sidebar" @click="toggleSidebar">
                                <component :is="isSidebarCollapsed ? 'Expand' : 'Fold'" />
                            </el-icon>
                            <el-breadcrumb separator="/">
                                <el-breadcrumb-item :to="{ path: '/teacher/dashboard' }">首页</el-breadcrumb-item>
                                <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
                            </el-breadcrumb>
                        </div>
                        <div class="header-right">
                            <!-- 全屏切换 -->
                            <el-tooltip :content="isFullscreen ? '退出全屏' : '全屏'" placement="bottom">
                                <el-icon class="header-icon" @click="toggleFullScreen">
                                    <FullScreen v-if="!isFullscreen" />
                                    <Aim v-else />
                                </el-icon>
                            </el-tooltip>

                            <!-- 用户信息 -->
                            <el-dropdown class="user-dropdown" trigger="click" @command="handleCommand">
                                <div class="user-info">
                                    <el-avatar :size="32" :src="userStore.userInfo?.avatar || defaultAvatar"
                                        @error="() => true">
                                        {{ userStore.userInfo?.name?.charAt(0)?.toUpperCase() }}
                                    </el-avatar>
                                    <div class="user-detail">
                                        <span class="username">{{ userStore.userInfo?.name || '教师' }}</span>
                                        <span class="role-tag">教师</span>
                                    </div>
                                    <el-icon class="el-icon--right">
                                        <CaretBottom />
                                    </el-icon>
                                </div>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item command="profile">
                                            <el-icon>
                                                <User />
                                            </el-icon>个人信息
                                        </el-dropdown-item>
                                        <el-dropdown-item command="attendance">
                                            <el-icon><Calendar /></el-icon>课程考勤
                                        </el-dropdown-item>
                                        <el-dropdown-item divided command="logout">
                                            <el-icon>
                                                <SwitchButton />
                                            </el-icon>退出登录
                                        </el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </div>
                </el-header>
                <el-main>
                    <router-view v-slot="{ Component }">
                        <transition name="fade" mode="out-in">
                            <component :is="Component" :key="$route.fullPath" />
                        </transition>
                    </router-view>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script setup>
    import { ref, computed } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { useUserStore } from '@/stores/user'
    import { ElMessageBox } from 'element-plus'
    import {
        Fold,
        Expand,
        FullScreen,
        Aim,
        User,
        Document,
        Calendar,
        SwitchButton,
        CaretBottom,
        DataLine,
        Reading
    } from '@element-plus/icons-vue'
    import defaultAvatar from '@/assets/images/default-avatar.png'
    import { useUnreadNotificationCount } from '@/composables/useUnreadNotificationCount'

    const route = useRoute()
    const router = useRouter()
    const userStore = useUserStore()
    const isSidebarCollapsed = ref(false)
    const isFullscreen = ref(false)
    const { count: unreadNotificationCount } = useUnreadNotificationCount()

    const toggleSidebar = () => {
        isSidebarCollapsed.value = !isSidebarCollapsed.value
    }

    // 处理全屏切换
    const toggleFullScreen = () => {
        if (!document.fullscreenElement) {
            document.documentElement.requestFullscreen()
            isFullscreen.value = true
        } else {
            document.exitFullscreen()
            isFullscreen.value = false
        }
    }

    // 处理下拉菜单命令
    const handleCommand = async (command) => {
        switch (command) {
            case 'profile':
                router.push('/teacher/profile')
                break
            case 'attendance':
                router.push('/teacher/attendance')
                break
            case 'logout':
                try {
                    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    })
                    userStore.logout()
                    router.push('/login')
                } catch {
                    // 用户取消退出
                }
                break
        }
    }

    const currentRoute = computed(() => {
        const matched = route.matched
        if (matched.length > 1) {
            return matched[1].meta.title || ''
        }
        return ''
    })

    const activeMenu = computed(() => route.path)

    const handleMenuSelect = (path) => {
        if (path && path !== route.path) router.push(path)
    }
</script>

<style scoped>
    .teacher-layout {
        height: 100vh;
        display: flex;
    }

    .el-container {
        height: 100%;
        width: 100%;
    }

    .sidebar {
        background-color: #304156;
        color: #fff;
        height: 100vh;
        overflow-y: auto;
        position: fixed;
        left: 0;
        top: 0;
        transition: width 0.3s;
        z-index: 1001;
    }

    .sidebar.is-collapse {
        width: 64px !important;
    }

    .sidebar::-webkit-scrollbar {
        width: 6px;
    }

    .sidebar::-webkit-scrollbar-thumb {
        background-color: #4a5a6a;
        border-radius: 3px;
    }

    .sidebar::-webkit-scrollbar-track {
        background-color: #304156;
    }

    .logo {
        height: 60px;
        display: flex;
        align-items: center;
        padding: 0 20px;
        background-color: #2b2f3a;
    }

    .logo img {
        width: 32px;
        height: 32px;
        margin-right: 12px;
    }

    .logo h1 {
        color: #fff;
        font-size: 16px;
        margin: 0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .sidebar-menu {
        border-right: none;
    }

    .sidebar-menu :deep(.el-menu-item) {
        height: 50px;
        line-height: 50px;
        padding: 0 20px;
        display: flex;
        align-items: center;
    }

    .sidebar-menu :deep(.el-menu-item.is-active) {
        background-color: #263445;
    }

    .sidebar-menu :deep(.el-menu-item:hover) {
        background-color: #263445;
    }

    .sidebar-menu :deep(.el-menu-item .el-icon) {
        margin-right: 12px;
        font-size: 18px;
        width: auto;
        height: auto;
    }

    .el-header {
        background-color: #fff;
        border-bottom: 1px solid #e6e6e6;
        padding: 0 20px;
        height: 60px;
        position: fixed;
        top: 0;
        right: 0;
        left: 220px;
        z-index: 1000;
        transition: left 0.3s;
    }

    .header-content {
        height: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .breadcrumb {
        display: flex;
        align-items: center;
    }

    .header-right {
        display: flex;
        align-items: center;
        gap: 16px;
    }

    .toggle-sidebar {
        font-size: 20px;
        cursor: pointer;
        margin-right: 16px;
    }

    .el-main {
        margin-top: 60px;
        margin-left: 220px;
        padding: 24px;
        background-color: #f3f5f8;
        min-height: calc(100vh - 60px);
        transition: margin-left 0.3s;
    }

    .header-icon {
        font-size: 20px;
        cursor: pointer;
        color: var(--el-text-color-regular);
        transition: color 0.3s;
    }

    .header-icon:hover {
        color: var(--el-color-primary);
    }

    .notice-badge {
        cursor: pointer;
    }

    .user-dropdown {
        height: 100%;
        cursor: pointer;
        padding: 0 8px;
        border-radius: 4px;
    }

    .user-dropdown:hover {
        background: var(--el-fill-color-light);
    }

    .user-info {
        display: flex;
        align-items: center;
        gap: 8px;
    }

    .user-detail {
        display: flex;
        flex-direction: column;
        line-height: 1.2;
    }

    .username {
        font-size: 14px;
        color: var(--el-text-color-primary);
        margin-right: 8px;
    }

    .role-tag {
        font-size: 12px;
        color: var(--el-color-info);
    }

    @media screen and (max-width: 768px) {
        .sidebar {
            width: 64px !important;
        }

        .logo h1 {
            display: none;
        }

        .el-header {
            left: 64px;
        }

        .el-main {
            margin-left: 64px;
            padding: 16px;
        }

        .user-detail {
            display: none;
        }
    }
</style>
