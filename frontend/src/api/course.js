import request from "@/utils/request";

// 获取课程列表
export function getCourseList(params) {
	return request({
		url: "/courses",
		method: "get",
		params,
	});
}

// 获取课程详情
export function getCourseById(id) {
	return request({
		url: `/courses/${id}`,
		method: "get",
	});
}

// 创建课程
export function createCourse(data) {
	return request({
		url: "/courses",
		method: "post",
		data,
	});
}

// 更新课程信息
export function updateCourse(id, data) {
	return request({
		url: `/courses/${id}`,
		method: "put",
		data,
	});
}

// 删除课程
export function deleteCourse(id) {
	return request({
		url: `/courses/${id}`,
		method: "delete",
	});
}
