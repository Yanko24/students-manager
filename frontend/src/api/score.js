import request from "@/utils/request";

// 获取成绩列表
export function getScoreList(params) {
	return request({
		url: "/scores",
		method: "get",
		params,
	});
}

// 获取成绩分布统计，period 支持 semester 或 year
export function getScoreDistribution(period = "semester") {
	return request({
		url: "/scores/distribution",
		method: "get",
		params: { period },
	});
}

// 获取成绩详情
export function getScoreById(id) {
	return request({
		url: `/scores/${id}`,
		method: "get",
	});
}

// 创建成绩
export function createScore(data) {
	return request({
		url: "/scores",
		method: "post",
		data,
	});
}

// 更新成绩信息
export function updateScore(id, data) {
	return request({
		url: `/scores/${id}`,
		method: "put",
		data,
	});
}

// 删除成绩
export function deleteScore(id) {
	return request({
		url: `/scores/${id}`,
		method: "delete",
	});
}

// 批量导入成绩
export function importScores(data) {
	return request({
		url: "/scores/import",
		method: "post",
		data,
		headers: {
			"Content-Type": "multipart/form-data",
		},
		timeout: 120000,
	});
}

// 导出成绩
export function exportScores(params) {
	return request({
		url: "/scores/export",
		method: "get",
		params,
		responseType: "blob",
	});
}
