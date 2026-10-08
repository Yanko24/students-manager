import request from "@/utils/request";

export function getCurriculumPlans(params) {
	return request({ url: "/admin/curriculum-plans", method: "get", params });
}

export function getCurriculumMajorOptions() {
	return request({ url: "/admin/curriculum-plans/major-options", method: "get" });
}

export function createCurriculumPlan(data) {
	return request({ url: "/admin/curriculum-plans", method: "post", data });
}

export function updateCurriculumPlan(id, data) {
	return request({ url: `/admin/curriculum-plans/${id}`, method: "put", data });
}

export function deleteCurriculumPlan(id) {
	return request({ url: `/admin/curriculum-plans/${id}`, method: "delete" });
}

export function getStudentCurriculumProgress() {
	return request({ url: "/student-portal/curriculum-progress", method: "get" });
}
