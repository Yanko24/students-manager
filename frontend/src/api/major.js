import request from "@/utils/request";

// 获取所有专业列表（不分页）
export function getAllMajorsList() {
	return request({
		url: "/majors/list",
		method: "get",
	});
}

// 获取专业列表（分页）
export function getAllMajors(params) {
	return request({
		url: "/majors",
		method: "get",
		params,
	});
}

export function getMajorById(id) {
	return request({
		url: `/majors/${id}`,
		method: "get",
	});
}

export function createMajor(data) {
	return request({
		url: "/majors",
		method: "post",
		data,
	});
}

export function updateMajor(id, data) {
	return request({
		url: `/majors/${id}`,
		method: "put",
		data,
	});
}

export function deleteMajor(id) {
	return request({
		url: `/majors/${id}`,
		method: "delete",
	});
}

// 获取状态对应的标签类型
export const getStatusType = (status) => {
	switch (status) {
		case 0: // 正常
			return "success";
		case 1: // 停招
			return "warning";
		case 2: // 撤销
			return "danger";
		default:
			return "info";
	}
};

// 获取状态对应的文本
export const getStatusText = (status) => {
	switch (status) {
		case 0: // 正常
			return "正常";
		case 1: // 停招
			return "停招";
		case 2: // 撤销
			return "撤销";
		default:
			return "未知";
	}
};
