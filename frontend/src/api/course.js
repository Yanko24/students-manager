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

export function getCourseCatalogOptions() {
	return request({ url: "/courses/catalog-options", method: "get" });
}

// 获取课程已选学生名单
export function getCourseSelectedStudents(id, params) {
	return request({
		url: `/courses/${id}/students`,
		method: "get",
		params,
	});
}

// 获取课程选课申请及审核状态
export function getCourseSelections(id, params) {
	return request({
		url: `/courses/${id}/selections`,
		method: "get",
		params,
	});
}

// 批量通过或拒绝选课申请
export function reviewCourseSelections(id, data) {
	return request({
		url: `/courses/${id}/selections/review`,
		method: "post",
		data,
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
