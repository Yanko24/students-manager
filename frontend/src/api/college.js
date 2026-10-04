import request from "@/utils/request";

// 获取学院列表
export function getCollegeList(params) {
	return request({
		url: "/colleges",
		method: "get",
		params,
	});
}

// 获取学院详情
export function getCollegeById(id) {
	return request({
		url: `/colleges/${id}`,
		method: "get",
	});
}

// 创建学院
export function createCollege(data) {
	return request({
		url: "/colleges",
		method: "post",
		data,
	});
}

// 更新学院信息
export function updateCollege(id, data) {
	return request({
		url: `/colleges/${id}`,
		method: "put",
		data,
	});
}

// 删除学院
export function deleteCollege(id) {
	return request({
		url: `/colleges/${id}`,
		method: "delete",
	});
}
