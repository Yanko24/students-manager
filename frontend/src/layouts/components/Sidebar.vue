<template>
    <el-menu :default-active="activeMenu" class="sidebar-menu" router>
        <!-- 管理员菜单 -->
        <template v-if="userRole === 'admin'">
            <el-menu-item index="/admin/dashboard">
                <el-icon>
                    <Odometer />
                </el-icon>
                <span>仪表盘</span>
            </el-menu-item>

            <el-menu-item index="/admin/colleges">
                <el-icon>
                    <School />
                </el-icon>
                <span>学院管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/majors">
                <el-icon>
                    <Collection />
                </el-icon>
                <span>专业管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/teachers">
                <el-icon>
                    <User />
                </el-icon>
                <span>教师管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/students">
                <el-icon>
                    <UserFilled />
                </el-icon>
                <span>学生管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/courses">
                <el-icon>
                    <Reading />
                </el-icon>
                <span>课程管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/scores">
                <el-icon>
                    <DocumentChecked />
                </el-icon>
                <span>成绩管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/attendance">
                <el-icon>
                    <Calendar />
                </el-icon>
                <span>考勤管理</span>
            </el-menu-item>
        </template>

        <!-- 教师菜单 -->
        <template v-if="userRole === 'teacher'">
            <el-menu-item index="/teacher/dashboard">
                <el-icon>
                    <Odometer />
                </el-icon>
                <span>工作台</span>
            </el-menu-item>

            <el-menu-item index="/teacher/courses">
                <el-icon>
                    <Reading />
                </el-icon>
                <span>我的课程</span>
            </el-menu-item>

            <el-menu-item index="/teacher/scores">
                <el-icon>
                    <DocumentChecked />
                </el-icon>
                <span>成绩录入</span>
            </el-menu-item>

            <el-menu-item index="/teacher/attendance">
                <el-icon>
                    <Calendar />
                </el-icon>
                <span>考勤记录</span>
            </el-menu-item>

            <el-menu-item index="/teacher/profile">
                <el-icon>
                    <User />
                </el-icon>
                <span>个人信息</span>
            </el-menu-item>
        </template>

        <!-- 学生菜单 -->
        <template v-if="userRole === 'student'">
            <el-menu-item index="/student/dashboard">
                <el-icon>
                    <Odometer />
                </el-icon>
                <span>我的主页</span>
            </el-menu-item>

            <el-menu-item index="/student/courses">
                <el-icon>
                    <Reading />
                </el-icon>
                <span>我的课程</span>
            </el-menu-item>

            <el-menu-item index="/student/scores">
                <el-icon>
                    <DocumentChecked />
                </el-icon>
                <span>我的成绩</span>
            </el-menu-item>

            <el-menu-item index="/student/attendance">
                <el-icon>
                    <Calendar />
                </el-icon>
                <span>考勤记录</span>
            </el-menu-item>

            <el-menu-item index="/student/profile">
                <el-icon>
                    <User />
                </el-icon>
                <span>个人信息</span>
            </el-menu-item>
        </template>
    </el-menu>
</template>

<script setup>
    import { computed } from 'vue'
    import { useRoute } from 'vue-router'
    import { useUserStore } from '@/stores/user'
    import {
        Odometer,
        School,
        Collection,
        User,
        UserFilled,
        Reading,
        DocumentChecked,
        Calendar
    } from '@element-plus/icons-vue'

    const route = useRoute()
    const userStore = useUserStore()
    const activeMenu = computed(() => route.path)
    const userRole = computed(() => userStore.role)
</script>

<style scoped>
    .sidebar-menu {
        height: 100%;
        border-right: none;
    }

    .sidebar-menu :deep(.el-menu-item) {
        height: 50px;
        line-height: 50px;
    }

    .sidebar-menu :deep(.el-menu-item.is-active) {
        background-color: var(--el-menu-hover-bg-color);
    }

    .sidebar-menu :deep(.el-menu-item:hover) {
        background-color: var(--el-menu-hover-bg-color);
    }

    .sidebar-menu :deep(.el-menu-item .el-icon) {
        width: 24px;
        height: 24px;
    }
</style>