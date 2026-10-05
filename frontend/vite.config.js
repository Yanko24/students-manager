import { fileURLToPath } from "node:url";
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
	plugins: [vue()],
	build: {
		outDir: "../backend/src/main/resources/static",
		emptyOutDir: true,
		// ECharts is lazy-loaded with the admin dashboard; 505 kB raw is ~171 kB gzip.
		chunkSizeWarningLimit: 600,
		rollupOptions: {
			output: {
				manualChunks(id) {
					// ECharts contains class inheritance across its packages. Keep the
					// whole ECharts runtime together to avoid cross-chunk init-order errors.
					if (/node_modules\/(echarts|zrender|vue-echarts)\//.test(id)) return "charts";
					if (id.includes("node_modules/element-plus/")) return "element-plus";
					if (id.includes("node_modules/@element-plus/icons-vue/")) return "element-icons";
					if (/node_modules\/(vue|vue-router|pinia|@vue)\//.test(id)) return "framework";
				},
			},
		},
	},
	resolve: {
		alias: {
			"@": fileURLToPath(new URL("./src", import.meta.url)),
		},
	},
	server: {
		port: 3000,
		proxy: {
			"/api": {
				target: "http://localhost:8080",
			},
		},
	},
});
