import request from "@/utils/request";

// 获取教师列表
export function getTeacherList(params) {
	return request({
		url: "/teachers",
		method: "get",
		params,
	});
}

// 获取教师详情
export function getTeacherById(id) {
	return request({
		url: `/teachers/${id}`,
		method: "get",
	});
}

// 创建教师
export function createTeacher(data) {
	return request({
		url: "/teachers",
		method: "post",
		data,
	});
}

// 更新教师信息
export function updateTeacher(id, data) {
	return request({
		url: `/teachers/${id}`,
		method: "put",
		data,
	});
}

// 删除教师
export function deleteTeacher(id) {
	return request({
		url: `/teachers/${id}`,
		method: "delete",
	});
}

// 获取教师所授课程
export function getTeacherCourses(id) {
	return request({
		url: `/teachers/${id}/courses`,
		method: "get",
	});
}

// 获取教师个人信息
export function getTeacherProfile() {
	return request({
		url: "/teachers/profile",
		method: "get",
	});
}

// 更新教师个人信息
export function updateTeacherProfile(data) {
	return request({
		url: "/teachers/profile",
		method: "put",
		data,
	});
}

// 修改教师密码
export function updateTeacherPassword(data) {
	return request({
		url: "/teachers/password",
		method: "put",
		data,
	});
}

// 获取教师考勤统计
export function getTeacherAttendanceStats(params) {
	return request({
		url: "/teachers/attendance/stats",
		method: "get",
		params,
	});
}

// 获取教师课程考勤详情
export function getTeacherCourseAttendance(courseId, params) {
	return request({
		url: `/teachers/courses/${courseId}/attendance`,
		method: "get",
		params,
	});
}

// 获取状态对应的标签类型
export const getStatusType = (status) => {
	switch (status) {
		case 0: // 在职
			return "success";
		case 1: // 离职
			return "warning";
		case 2: // 退休
			return "info";
		default:
			return "info";
	}
};

// 获取状态对应的文本
export const getStatusText = (status) => {
	switch (status) {
		case 0: // 在职
			return "在职";
		case 1: // 离职
			return "离职";
		case 2: // 退休
			return "退休";
		default:
			return "未知";
	}
};
