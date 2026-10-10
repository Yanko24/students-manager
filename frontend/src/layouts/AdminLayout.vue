<template>
    <div class="admin-layout">
        <el-container>
            <el-aside width="220px" class="sidebar">
                <div class="logo">
                    <img src="@/assets/images/logo.png" alt="Logo" />
                    <h1>知行教务</h1>
                </div>
                <el-menu :default-active="activeMenu" class="sidebar-menu" @select="handleMenuSelect">
                    <el-menu-item index="/admin/dashboard">
                        <el-icon><Monitor /></el-icon>
                        <span>数据总览</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/colleges">
                        <el-icon><OfficeBuilding /></el-icon>
                        <span>学院管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/majors">
                        <el-icon><Collection /></el-icon>
                        <span>专业管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/teachers">
                        <el-icon><UserFilled /></el-icon>
                        <span>教师管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/students">
                        <el-icon><User /></el-icon>
                        <span>学生管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/courses">
                        <el-icon><Reading /></el-icon>
                        <span>课程管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/curriculum-plans">
                        <el-icon><Reading /></el-icon>
                        <span>培养方案</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/academic-terms">
                        <el-icon><Calendar /></el-icon>
                        <span>学期管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/academic-risks">
                        <el-icon><Aim /></el-icon>
                        <span>学业预警</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/task-runs">
                        <el-icon><Document /></el-icon>
                        <span>定时任务</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/scores">
                        <el-icon><Document /></el-icon>
                        <span>成绩管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/attendance">
                        <el-icon><Calendar /></el-icon>
                        <span>考勤管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/student-status-changes">
                        <el-icon><Document /></el-icon>
                        <span>学籍审核</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/notifications">
                        <el-icon><Document /></el-icon>
                        <span>站内通知<span v-if="unreadNotificationCount">（{{ unreadNotificationCount > 99 ? '99+' : unreadNotificationCount }}）</span></span>
                    </el-menu-item>
                    <el-menu-item index="/admin/operation-audits">
                        <el-icon><Document /></el-icon>
                        <span>操作审计</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/profile">
                        <el-icon><User /></el-icon>
                        <span>个人信息</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/settings">
                        <el-icon><Setting /></el-icon>
                        <span>系统设置</span>
                    </el-menu-item>
                </el-menu>
            </el-aside>
            <el-container>
                <el-header>
                    <div class="header-content">
                        <div class="breadcrumb">
                            <el-breadcrumb separator="/">
                                <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
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
                            <el-dropdown trigger="click" @command="handleCommand" class="user-dropdown">
                                <div class="user-info">
                                    <el-avatar :size="32" :src="userStore.avatar || defaultAvatar" @error="() => true">
                                        {{ userStore.username?.charAt(0)?.toUpperCase() }}
                                    </el-avatar>
                                    <div class="user-detail">
                                        <span class="username">{{ userStore.username || '管理员' }}</span>
                                        <span class="role-tag">管理员</span>
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
                                        <el-dropdown-item command="settings">
                                            <el-icon>
                                                <Setting />
                                            </el-icon>系统设置
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
    import { useRouter, useRoute } from 'vue-router'
    import { useUserStore } from '@/stores/user'
    import { ElMessageBox } from 'element-plus'
    import {
        Monitor,
        User,
        UserFilled,
        Collection,
        Reading,
        Document,
        OfficeBuilding,
        Calendar,
        Setting,
        FullScreen,
        Aim,
        SwitchButton,
        CaretBottom
    } from '@element-plus/icons-vue'
    import defaultAvatar from '@/assets/images/default-avatar.png'
    import { useUnreadNotificationCount } from '@/composables/useUnreadNotificationCount'

    const router = useRouter()
    const route = useRoute()
    const userStore = useUserStore()
    const isFullscreen = ref(false)
    const { count: unreadNotificationCount } = useUnreadNotificationCount()

    const activeMenu = computed(() => route.path)

    const handleMenuSelect = (path) => {
        if (path && path !== route.path) {
            router.push(path)
        }
    }

    const currentRoute = computed(() => {
        const matched = route.matched
        if (matched.length > 1) {
            return matched[1].meta.title || ''
        }
        return ''
    })

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
    const handleCommand = (command) => {
        switch (command) {
            case 'profile':
                router.push('/admin/profile')
                break
            case 'settings':
                router.push('/admin/settings')
                break
            case 'logout':
                ElMessageBox.confirm('确定要退出登录吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(async () => {
                    userStore.logout()
                    await router.replace('/login')
                })
                break
        }
    }
</script>

<style scoped>
    .admin-layout {
        height: 100vh;
        display: flex;
    }

    .el-container {
        height: 100%;
    }

    .sidebar {
		background: linear-gradient(180deg, #24344d 0%, #1f2d43 100%);
        color: #fff;
        height: 100vh;
        overflow-y: auto;
        position: fixed;
        left: 0;
        top: 0;
        transition: width 0.3s;
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
		background-color: rgba(9, 18, 32, .22);
		border-bottom: 1px solid rgba(255, 255, 255, .07);
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
        background-color: transparent;
    }

    .sidebar-menu :deep(.el-menu-item) {
        color: #bfcbd9;
		height: 46px;
		line-height: 46px;
		margin: 4px 10px;
		border-radius: 8px;
        padding: 0 20px;
        display: flex;
        align-items: center;
    }

    .sidebar-menu :deep(.el-menu-item.is-active) {
		background: rgba(83, 145, 232, .18);
		color: #9bc2ff;
    }

    .sidebar-menu :deep(.el-menu-item:hover) {
		background-color: rgba(255, 255, 255, .07);
    }

    .sidebar-menu :deep(.el-icon) {
        margin-right: 12px;
        font-size: 18px;
    }

    .el-header {
        background-color: #fff;
		border-bottom: 1px solid #e9edf3;
		padding: 0 24px;
		box-shadow: 0 2px 10px rgba(24, 39, 75, .025);
        height: 60px;
        display: flex;
        align-items: center;
        position: fixed;
        top: 0;
        right: 0;
        left: 220px;
        z-index: 1000;
    }

    .header-content {
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .breadcrumb {
        font-size: 14px;
    }

    .header-right {
        display: flex;
        align-items: center;
        gap: 16px;
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
        color: var(--el-color-success);
    }

    .el-main {
        margin-top: 60px;
        margin-left: 220px;
		padding: 24px;
		background-color: #f3f5f8;
        min-height: calc(100vh - 60px);
    }

    @media screen and (max-width: 768px) {
        .sidebar {
            width: 64px;
        }

        .logo h1 {
            display: none;
        }

        .sidebar-menu :deep(.el-menu-item span) {
            display: none;
        }

        .el-header {
            left: 64px;
			padding: 0 16px;
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
