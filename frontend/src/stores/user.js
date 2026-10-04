import { defineStore } from "pinia";

export const useUserStore = defineStore("user", {
	state: () => {
		try {
			const token = localStorage.getItem("token");
			const role = localStorage.getItem("userRole");
			const userInfoStr = localStorage.getItem("userInfo");
			const mustChangePassword = localStorage.getItem("mustChangePassword") === "true";

			// 如果 userInfo 不存在或为空，返回 null
			const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null;

			return {
				userInfo,
				mustChangePassword,
				role,
				token,
			};
		} catch (error) {
			console.error("初始化用户状态失败:", error);
			// 如果解析失败，返回空状态
			return {
				userInfo: null,
				mustChangePassword: false,
				role: null,
				token: null,
			};
		}
	},

	getters: {
		isLoggedIn: (state) => !!state.token,
		getUserInfo: (state) => state.userInfo,
		getRole: (state) => state.role,
	},

	actions: {
		setUserInfo(userInfo) {
			try {
				this.userInfo = userInfo;
				localStorage.setItem("userInfo", JSON.stringify(userInfo));
			} catch (error) {
				console.error("存储用户信息失败:", error);
			}
		},

		setRole(role) {
			try {
				this.role = role;
				localStorage.setItem("userRole", role);
			} catch (error) {
				console.error("存储用户角色失败:", error);
			}
		},

		setToken(token) {
			try {
				this.token = token;
				localStorage.setItem("token", token);
			} catch (error) {
				console.error("存储token失败:", error);
			}
		},

		setMustChangePassword(required) {
			this.mustChangePassword = required;
			localStorage.setItem("mustChangePassword", String(required));
		},

		logout() {
			try {
				this.userInfo = null;
				this.role = null;
				this.token = null;
				localStorage.removeItem("token");
				localStorage.removeItem("userRole");
				localStorage.removeItem("userInfo");
				localStorage.removeItem("mustChangePassword");
			} catch (error) {
				console.error("登出失败:", error);
			}
		},
	},
});
