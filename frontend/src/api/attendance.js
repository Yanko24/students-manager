import request from "@/utils/request";

// 获取考勤列表
export function getAttendanceList(params) {
	return request({
		url: "/attendance",
		method: "get",
		params,
	});
}

// 管理员按学号、姓名、课程、班级、日期、状态和学期筛选，并分页读取
export function getAttendanceStatistics(period = "week") {
	return request({ url: "/attendance/statistics", method: "get", params: { period } });
}

export function getAttendanceTrend(period = "week") {
	return request({ url: "/attendance/trend", method: "get", params: { period } });
}

// 获取考勤详情
export function getAttendanceById(id) {
	return request({
		url: `/attendance/${id}`,
		method: "get",
	});
}

// 创建考勤记录
export function createAttendance(data) {
	return request({
		url: "/attendance",
		method: "post",
		data,
	});
}

// 更新考勤记录
export function updateAttendance(id, data) {
	return request({
		url: `/attendance/${id}`,
		method: "put",
		data,
	});
}

// 删除考勤记录
export function deleteAttendance(id) {
	return request({
		url: `/attendance/${id}`,
		method: "delete",
	});
}

// 获取教师所授课程的考勤记录
export function getTeacherAttendance(params) {
	return request({
		url: "/attendance/teacher",
		method: "get",
		params,
	});
}

// 获取学生的考勤记录
export function getStudentAttendance(params) {
	return request({
		url: "/attendance/student",
		method: "get",
		params,
	});
}

export function getStudentAttendanceStatistics(params = {}) {
	return request({ url: "/attendance/student/statistics", method: "get", params });
}
