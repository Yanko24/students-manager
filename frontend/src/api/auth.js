import request from "@/utils/request";

/**
 * 用户登录
 * @param {Object} data - 登录参数
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise<Object>} 登录结果
 */
export function login(data) {
	return request({
		url: "/auth/login",
		method: "post",
		data,
	});
}

export function changePassword(data) {
	return request({
		url: "/auth/change-password",
		method: "post",
		data,
	});
}

/**
 * 用户登出
 * @returns {Promise<Object>} 登出结果
 */
export function logout() {
	return request({
		url: "/auth/logout",
		method: "post",
	});
}

/**
 * 获取当前用户信息
 * @returns {Promise<Object>} 用户信息
 */
export function getCurrentUser() {
	return request({
		url: "/auth/current",
		method: "get",
	});
}
