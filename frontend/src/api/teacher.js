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
