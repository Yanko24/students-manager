import "./assets/main.css";
import { createApp } from "vue";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import { ElMessage } from "element-plus";
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import App from "./App.vue";
import router from "./router";
import { useUserStore } from "./stores/user";
import { installEnterShortcuts } from "./utils/keyboardShortcuts";

const app = createApp(App);
const pinia = createPinia();

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component);
}

app.use(pinia);
app.use(router);
app.use(ElementPlus);

const userStore = useUserStore(pinia);
let authRedirecting = false;
window.addEventListener("auth:expired", async () => {
	userStore.logout();
	if (authRedirecting || router.currentRoute.value.path === "/login") return;

	authRedirecting = true;
	try {
		await router.replace("/login");
		ElMessage.warning("登录已失效，请重新登录");
	} catch (error) {
		console.error("跳转登录页失败，执行页面级跳转：", error);
		window.location.replace("/login");
	} finally {
		authRedirecting = false;
	}
});
userStore.restoreSession();
installEnterShortcuts();

app.mount("#app");
