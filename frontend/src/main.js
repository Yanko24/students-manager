import "./assets/main.css";
import { createApp } from "vue";
import { createPinia } from "pinia";
import {
	ElAlert,
	ElAside,
	ElAvatar,
	ElBreadcrumb,
	ElBreadcrumbItem,
	ElButton,
	ElButtonGroup,
	ElCard,
	ElCheckbox,
	ElCheckboxGroup,
	ElCol,
	ElConfigProvider,
	ElContainer,
	ElDatePicker,
	ElDescriptions,
	ElDescriptionsItem,
	ElDialog,
	ElDropdown,
	ElDropdownItem,
	ElDropdownMenu,
	ElEmpty,
	ElForm,
	ElFormItem,
	ElHeader,
	ElIcon,
	ElInput,
	ElInputNumber,
	ElLoading,
	ElMain,
	ElMenu,
	ElMenuItem,
	ElMessage,
	ElOption,
	ElPagination,
	ElProgress,
	ElRadio,
	ElRadioButton,
	ElRadioGroup,
	ElResult,
	ElRow,
	ElSelect,
	ElSwitch,
	ElTabPane,
	ElTable,
	ElTableColumn,
	ElTabs,
	ElTag,
	ElTooltip,
	ElUpload,
} from "element-plus";
import "element-plus/dist/index.css";
import App from "./App.vue";
import router from "./router";
import { useUserStore } from "./stores/user";
import { installEnterShortcuts } from "./utils/keyboardShortcuts";

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);
[
	ElAlert,
	ElAside,
	ElAvatar,
	ElBreadcrumb,
	ElBreadcrumbItem,
	ElButton,
	ElButtonGroup,
	ElCard,
	ElCheckbox,
	ElCheckboxGroup,
	ElCol,
	ElConfigProvider,
	ElContainer,
	ElDatePicker,
	ElDescriptions,
	ElDescriptionsItem,
	ElDialog,
	ElDropdown,
	ElDropdownItem,
	ElDropdownMenu,
	ElEmpty,
	ElForm,
	ElFormItem,
	ElHeader,
	ElIcon,
	ElInput,
	ElInputNumber,
	ElMain,
	ElMenu,
	ElMenuItem,
	ElOption,
	ElPagination,
	ElProgress,
	ElRadio,
	ElRadioButton,
	ElRadioGroup,
	ElResult,
	ElRow,
	ElSelect,
	ElSwitch,
	ElTabPane,
	ElTable,
	ElTableColumn,
	ElTabs,
	ElTag,
	ElTooltip,
	ElUpload,
].forEach((component) => app.component(component.name, component));
app.directive("loading", ElLoading.directive);

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
