import { defineStore } from "pinia";
import { validateSession } from "@/api/auth";
import { advanceAuthSession } from "@/utils/authSession";

let sessionExpiryTimer;
let sessionValidationTimer;
let sessionValidationPending = false;

function startSessionValidation(validateImmediately = true) {
	window.clearInterval(sessionValidationTimer);
	const checkSession = () => {
		if (sessionValidationPending || !localStorage.getItem("token")) return;
		sessionValidationPending = true;
		validateSession().catch(() => {}).finally(() => {
			sessionValidationPending = false;
		});
	};
	if (validateImmediately) checkSession();
	sessionValidationTimer = window.setInterval(checkSession, 30_000);
}

function getTokenExpiresAt(token) {
	try {
		const payload = token.split(".")[1];
		if (!payload) return null;

		const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
		const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, "=");
		const binary = window.atob(padded);
		const bytes = Uint8Array.from(binary, (character) => character.charCodeAt(0));
		const claims = JSON.parse(new TextDecoder().decode(bytes));
		const expiration = Number(claims.exp);

		return Number.isFinite(expiration) ? expiration * 1000 : null;
	} catch {
		return null;
	}
}

function scheduleSessionExpiry(store) {
	window.clearTimeout(sessionExpiryTimer);
	const delay = store.tokenExpiresAt - Date.now();
	if (delay <= 0) {
		store.logout();
		window.dispatchEvent(new Event("auth:expired"));
		return;
	}

	sessionExpiryTimer = window.setTimeout(() => {
		if (store.token && store.tokenExpiresAt <= Date.now()) {
			store.logout();
			window.dispatchEvent(new Event("auth:expired"));
		}
	}, delay);
}

export const useUserStore = defineStore("user", {
	state: () => {
		try {
			const token = localStorage.getItem("token");
			const role = localStorage.getItem("userRole");
			const userInfoStr = localStorage.getItem("userInfo");
			const mustChangePassword = localStorage.getItem("mustChangePassword") === "true";
			const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null;

			return {
				userInfo,
				mustChangePassword,
				role,
				token,
				tokenExpiresAt: token ? getTokenExpiresAt(token) : null,
			};
		} catch (error) {
			console.error("初始化用户状态失败:", error);
			return {
				userInfo: null,
				mustChangePassword: false,
				role: null,
				token: null,
				tokenExpiresAt: null,
			};
		}
	},

	getters: {
		isLoggedIn: (state) => Boolean(state.token && state.tokenExpiresAt && state.tokenExpiresAt > Date.now()),
		getUserInfo: (state) => state.userInfo,
		getRole: (state) => state.role,
	},

	actions: {
		restoreSession() {
			if (!this.token) return;
			if (!this.tokenExpiresAt || this.tokenExpiresAt <= Date.now()) {
				this.logout();
				window.dispatchEvent(new Event("auth:expired"));
				return;
			}
			scheduleSessionExpiry(this);
			startSessionValidation();
		},

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
			const tokenExpiresAt = getTokenExpiresAt(token);
			if (!tokenExpiresAt || tokenExpiresAt <= Date.now()) {
				this.logout();
				throw new Error("登录凭证无效或已过期");
			}

			this.token = token;
			this.tokenExpiresAt = tokenExpiresAt;
			advanceAuthSession();
			localStorage.setItem("token", token);
			scheduleSessionExpiry(this);
			startSessionValidation(false);
		},

		setMustChangePassword(required) {
			this.mustChangePassword = required;
			localStorage.setItem("mustChangePassword", String(required));
		},

		logout() {
			advanceAuthSession();
			window.clearTimeout(sessionExpiryTimer);
			window.clearInterval(sessionValidationTimer);
			sessionValidationPending = false;
			this.userInfo = null;
			this.role = null;
			this.token = null;
			this.tokenExpiresAt = null;
			this.mustChangePassword = false;
			localStorage.removeItem("token");
			localStorage.removeItem("userRole");
			localStorage.removeItem("userInfo");
			localStorage.removeItem("mustChangePassword");
		},
	},
});
