import axios from "axios";
import { ElMessage } from "element-plus";
import { getAuthSessionRevision } from "@/utils/authSession";

// 创建 axios 实例
const request = axios.create({
	baseURL: "/api", // 前端与后端同源，由当前服务接收 API 请求
	timeout: 5000,
});

// 请求拦截器
request.interceptors.request.use(
	(config) => {
		// 从 localStorage 获取 token
		const token = localStorage.getItem("token");
		// 记下发起请求时的凭证，旧会话的迟到响应不能注销新会话。
		config._authToken = token;
		config._authSessionRevision = getAuthSessionRevision();
		if (token) {
			// 设置请求头，添加 Bearer 前缀
			config.headers["Authorization"] = `Bearer ${token}`;
		}
		return config;
	},
	(error) => {
		console.error("请求错误:", error);
		return Promise.reject(error);
	}
);

// 响应拦截器
request.interceptors.response.use(
	(response) => {
		// 检查响应数据结构
		if (response.data && typeof response.data === "object") {
			return response.data;
		}
		// 如果数据结构不符合预期，返回原始响应
		return response;
	},
	(error) => {
		console.error("响应错误:", error);
		if (error.response) {
			const isLoginRequest = /\/auth\/login(?:\?|$)/.test(error.config?.url || "");
			const responseToken = error.config?._authToken;
			const isUnauthorizedRequest = error.response.status === 401 && !isLoginRequest;
			const isCurrentSession = responseToken &&
				responseToken === localStorage.getItem("token") &&
				error.config?._authSessionRevision === getAuthSessionRevision();
			let triggeredSessionExpiry = false;
			if (isUnauthorizedRequest && isCurrentSession) {
				triggeredSessionExpiry = true;
				window.dispatchEvent(new Event("auth:expired"));
			}
			// Hide companion 401s after expiry. A late page request can also start after
			// logout has already cleared localStorage, so a missing token is expired too.
			const currentToken = localStorage.getItem("token");
			const sessionChanged =
				error.config?._authSessionRevision !== getAuthSessionRevision();
			const isExpiredSessionRequest = isUnauthorizedRequest && (
				triggeredSessionExpiry ||
				sessionChanged ||
				!currentToken ||
				Boolean(responseToken && responseToken !== currentToken)
			);
			if (isExpiredSessionRequest) {
				const responseData = error.response.data;
				return Promise.reject(
					responseData && typeof responseData === "object"
						? { ...responseData, authSessionExpired: true }
						: { message: String(responseData || ""), authSessionExpired: true }
				);
			}
			// 直接返回错误响应，让调用方处理具体的错误信息
			return Promise.reject(error.response.data);
		} else {
			// 网络错误
			return Promise.reject({
				code: -1,
				message: "连接到服务器失败",
			});
		}
	}
);

export default request;
