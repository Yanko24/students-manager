import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";
import Login from "@/views/login/Login.vue";
import AdminLayout from "@/layouts/AdminLayout.vue";
import StudentLayout from "@/layouts/StudentLayout.vue";
import TeacherLayout from "@/layouts/TeacherLayout.vue";
import TeacherDashboard from "@/views/teacher/Dashboard.vue";
import NotFound from "@/views/NotFound.vue";
import ChangePassword from "@/views/login/ChangePassword.vue";

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		{
			path: "/",
			redirect: "/login",
		},
		{
			path: "/login",
			name: "Login",
			component: Login,
			meta: { title: "登录", requiresAuth: false },
		},
		{
			path: "/change-password",
			name: "ChangePassword",
			component: ChangePassword,
			meta: { title: "首次登录修改密码", requiresAuth: true },
		},
		{
			path: "/admin",
			component: AdminLayout,
			meta: { requiresAuth: true, role: "admin" },
			children: [
				{
					path: "",
					redirect: "/admin/dashboard",
				},
				{
					path: "dashboard",
					name: "AdminDashboard",
					component: () => import("@/views/admin/Dashboard.vue"),
					meta: { title: "控制台", requiresAuth: true, role: "admin" },
				},
				{
					path: "students",
					name: "StudentList",
					component: () => import("@/views/admin/students/StudentList.vue"),
					meta: { title: "学生管理", requiresAuth: true, role: "admin" },
				},
				{
					path: "students/add",
					name: "StudentAdd",
					component: () => import("@/views/admin/students/StudentAdd.vue"),
					meta: { title: "添加学生", requiresAuth: true, role: "admin" },
				},
				{
					path: "students/:id/edit",
					name: "StudentEdit",
					component: () => import("@/views/admin/students/StudentEdit.vue"),
					meta: { title: "编辑学生", requiresAuth: true, role: "admin" },
				},
				{
					path: "students/:id",
					name: "StudentView",
					component: () => import("@/views/admin/students/StudentView.vue"),
					meta: { title: "学生详情", requiresAuth: true, role: "admin" },
				},
				{
					path: "teachers",
					name: "TeacherList",
					component: () => import("@/views/admin/teachers/TeacherList.vue"),
					meta: { title: "教师管理", requiresAuth: true, role: "admin" },
				},
				{
					path: "teachers/add",
					name: "TeacherAdd",
					component: () => import("@/views/admin/teachers/TeacherAdd.vue"),
					meta: { title: "添加教师", requiresAuth: true, role: "admin" },
				},
				{
					path: "teachers/:id/edit",
					name: "TeacherEdit",
					component: () => import("@/views/admin/teachers/TeacherEdit.vue"),
					meta: { title: "编辑教师", requiresAuth: true, role: "admin" },
				},
				{
					path: "teachers/:id",
					name: "TeacherView",
					component: () => import("@/views/admin/teachers/TeacherView.vue"),
					meta: { title: "教师详情", requiresAuth: true, role: "admin" },
				},
				{
					path: "majors",
					name: "MajorList",
					component: () => import("@/views/admin/majors/MajorList.vue"),
					meta: { title: "专业管理", requiresAuth: true, role: "admin" },
				},
				{
					path: "majors/add",
					name: "MajorAdd",
					component: () => import("@/views/admin/majors/MajorAdd.vue"),
					meta: { title: "添加专业", requiresAuth: true, role: "admin" },
				},
				{
					path: "majors/:id/edit",
					name: "MajorEdit",
					component: () => import("@/views/admin/majors/MajorEdit.vue"),
					meta: { title: "编辑专业", requiresAuth: true, role: "admin" },
				},
				{
					path: "majors/:id",
					name: "MajorView",
					component: () => import("@/views/admin/majors/MajorView.vue"),
					meta: { title: "专业详情", requiresAuth: true, role: "admin" },
				},
				{
					path: "courses",
					name: "CourseList",
					component: () => import("@/views/admin/courses/CourseList.vue"),
					meta: { title: "课程管理", requiresAuth: true, role: "admin" },
				},
				{
					path: "courses/add",
					name: "CourseAdd",
					component: () => import("@/views/admin/courses/CourseAdd.vue"),
					meta: { title: "添加课程", requiresAuth: true, role: "admin" },
				},
				{
					path: "courses/:id/edit",
					name: "CourseEdit",
					component: () => import("@/views/admin/courses/CourseEdit.vue"),
					meta: { title: "编辑课程", requiresAuth: true, role: "admin" },
				},
				{
					path: "courses/:id",
					name: "CourseView",
					component: () => import("@/views/admin/courses/CourseView.vue"),
					meta: { title: "课程详情", requiresAuth: true, role: "admin" },
				},
				{
					path: "attendance",
					name: "AttendanceList",
					component: () => import("@/views/admin/attendance/AttendanceList.vue"),
					meta: { title: "考勤管理", requiresAuth: true, roles: ["admin"] },
				},
				{
					path: "attendance/add",
					name: "AttendanceAdd",
					component: () => import("@/views/admin/attendance/AttendanceAdd.vue"),
					meta: { title: "添加考勤", requiresAuth: true, role: "admin" },
				},
				{
					path: "attendance/:id/edit",
					name: "AttendanceEdit",
					component: () => import("@/views/admin/attendance/AttendanceEdit.vue"),
					meta: { title: "编辑考勤", requiresAuth: true, role: "admin" },
				},
				{
					path: "attendance/:id",
					name: "AttendanceView",
					component: () => import("@/views/admin/attendance/AttendanceView.vue"),
					meta: { title: "考勤详情", requiresAuth: true, role: "admin" },
				},
				{
					path: "scores",
					name: "ScoreList",
					component: () => import("@/views/admin/scores/ScoreList.vue"),
					meta: { title: "成绩管理", requiresAuth: true, role: "admin" },
				},
				{
					path: "scores/add",
					name: "ScoreAdd",
					component: () => import("@/views/admin/scores/ScoreAdd.vue"),
					meta: { title: "添加成绩", requiresAuth: true, role: "admin" },
				},
				{
					path: "scores/:id/edit",
					name: "ScoreEdit",
					component: () => import("@/views/admin/scores/ScoreEdit.vue"),
					meta: { title: "编辑成绩", requiresAuth: true, role: "admin" },
				},
				{
					path: "scores/:id",
					name: "ScoreView",
					component: () => import("@/views/admin/scores/ScoreView.vue"),
					meta: { title: "成绩详情", requiresAuth: true, role: "admin" },
				},
				{
					path: "colleges",
					name: "CollegeList",
					component: () => import("@/views/admin/colleges/CollegeList.vue"),
					meta: { title: "学院管理", requiresAuth: true, role: "admin" },
				},
				{
					path: "colleges/add",
					name: "CollegeAdd",
					component: () => import("@/views/admin/colleges/CollegeAdd.vue"),
					meta: { title: "添加学院", requiresAuth: true, role: "admin" },
				},
				{
					path: "colleges/:id/edit",
					name: "CollegeEdit",
					component: () => import("@/views/admin/colleges/CollegeEdit.vue"),
					meta: { title: "编辑学院", requiresAuth: true, role: "admin" },
				},
				{
					path: "colleges/:id",
					name: "CollegeView",
					component: () => import("@/views/admin/colleges/CollegeView.vue"),
					meta: { title: "学院详情", requiresAuth: true, role: "admin" },
				},
				{
					path: "profile",
					name: "AdminProfile",
					component: () => import("@/views/admin/Profile.vue"),
					meta: { title: "个人信息", requiresAuth: true, roles: ["admin"] },
				},
				{
					path: "settings",
					name: "Settings",
					component: () => import("@/views/admin/Settings.vue"),
					meta: { title: "系统设置", requiresAuth: true, roles: ["admin"] },
				},
			],
		},
		{
			path: "/teacher",
			component: TeacherLayout,
			meta: { requiresAuth: true, role: "teacher" },
			children: [
				{
					path: "",
					redirect: "/teacher/courses",
				},
				{
					path: "courses",
					name: "TeacherCourseList",
					component: () => import("@/views/teacher/courses/CourseList.vue"),
					meta: { title: "我的课程", requiresAuth: true, role: "teacher" },
				},
				{
					path: "courses/:id",
					name: "TeacherCourseView",
					component: () => import("@/views/teacher/courses/CourseView.vue"),
					meta: { title: "课程详情", requiresAuth: true, role: "teacher" },
				},
				{
					path: "dashboard",
					name: "TeacherDashboard",
					component: TeacherDashboard,
					meta: { title: "控制台", requiresAuth: true, roles: ["teacher"] },
				},
				{
					path: "attendance",
					name: "TeacherAttendance",
					component: () => import("@/views/teacher/Attendance.vue"),
					meta: { title: "考勤管理", requiresAuth: true, roles: ["teacher"] },
				},
				{
					path: "profile",
					name: "TeacherProfile",
					component: () => import("@/views/teacher/Profile.vue"),
					meta: { title: "个人信息", requiresAuth: true, roles: ["teacher"] },
				},
			],
		},
		{
			path: "/student",
			component: StudentLayout,
			meta: { requiresAuth: true, role: "student" },
			children: [
				{
					path: "",
					redirect: "/student/courses",
				},
				{
					path: "courses",
					name: "StudentCourseList",
					component: () => import("@/views/student/courses/CourseList.vue"),
					meta: { title: "我的课程", requiresAuth: true, role: "student" },
				},
				{
					path: "courses/:id",
					name: "StudentCourseView",
					component: () => import("@/views/student/courses/CourseView.vue"),
					meta: { title: "课程详情", requiresAuth: true, role: "student" },
				},
				{
					path: "dashboard",
					name: "StudentDashboard",
					component: () => import("@/views/student/Dashboard.vue"),
					meta: { title: "控制台", requiresAuth: true, roles: ["student"] },
				},
				{
					path: "attendance",
					name: "StudentAttendance",
					component: () => import("@/views/student/Attendance.vue"),
					meta: { title: "我的考勤", requiresAuth: true, roles: ["student"] },
				},
				{
					path: "profile",
					name: "StudentProfile",
					component: () => import("@/views/student/Profile.vue"),
					meta: { title: "个人信息", requiresAuth: true, roles: ["student"] },
				},
			],
		},
		{
			path: "/:pathMatch(.*)*",
			name: "NotFound",
			component: NotFound,
			meta: { title: "404", requiresAuth: false },
		},
	],
});

// 路由守卫
router.beforeEach((to, from, next) => {
	const userStore = useUserStore();
	const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);
	const requiredRole = to.matched.find((record) => record.meta.role)?.meta.role;

	document.title = to.meta.title
		? `${to.meta.title} - 学生管理系统`
		: "学生管理系统";

	if (to.path === "/login") {
		if (userStore.isLoggedIn) {
			// 已登录用户访问登录页，重定向到对应角色的首页
			next(`/${userStore.role}/dashboard`);
		} else {
			next();
		}
		return;
	}

	if (userStore.mustChangePassword && to.path !== "/change-password") {
		next(userStore.isLoggedIn ? "/change-password" : "/login");
		return;
	}
	if (to.path === "/change-password" && !userStore.isLoggedIn) {
		next("/login");
		return;
	}

	if (requiresAuth) {
		if (!userStore.isLoggedIn) {
			next("/login");
			return;
		}

		if (requiredRole && userStore.role !== requiredRole) {
			next(`/${userStore.role}/dashboard`);
			return;
		}
	}

	next();
});

export default router;
