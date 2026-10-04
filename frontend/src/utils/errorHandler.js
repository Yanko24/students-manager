import { ElMessage } from "element-plus";

/**
 * 统一处理API错误
 * @param {Error} error - 错误对象
 * @param {string} defaultMessage - 默认错误消息
 * @param {boolean} showMessage - 是否显示错误消息
 * @returns {string} 错误消息
 */
export const handleApiError = (
	error,
	defaultMessage = "操作失败",
	showMessage = true
) => {
	let errorMessage = defaultMessage;

	if (error.response) {
		// 服务器返回了错误状态码
		const status = error.response.status;
		const data = error.response.data;

		if (data && data.message) {
			errorMessage = data.message;
		} else {
			switch (status) {
				case 400:
					errorMessage = "请求参数错误";
					break;
				case 401:
					errorMessage = "未授权，请重新登录";
					break;
				case 403:
					errorMessage = "拒绝访问";
					break;
				case 404:
					errorMessage = "请求的资源不存在";
					break;
				case 500:
					errorMessage = "服务器内部错误";
					break;
				default:
					errorMessage = `请求失败 (${status})`;
			}
		}
	} else if (error.request) {
		// 请求已发出，但没有收到响应
		errorMessage = "网络错误，请检查您的网络连接";
	} else {
		// 请求配置出错
		errorMessage = error.message || defaultMessage;
	}

	if (showMessage) {
		ElMessage.error(errorMessage);
	}

	return errorMessage;
};
