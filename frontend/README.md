# 前端

学生管理系统前端，采用 Vue 3、Vite 和 Element Plus。本地执行 `npm run build` 时直接输出到后端的 classpath 静态资源目录；Docker 构建单独输出到 `dist/`，由 Nginx 提供静态页面并代理 API 请求。

常规表单支持键盘操作：焦点位于普通输入框时按 Enter，会触发当前表单的主操作（例如登录、搜索、保存或提交）。下拉选择、日期/自动完成控件以及多行文本框保留各自的键盘行为；删除等危险操作不会绑定 Enter。

## 技术栈

- Vue 3、Vue Router、Pinia
- Element Plus、ECharts
- Axios、Vite、Sass
- Node.js 20（与 Docker 构建镜像保持一致）

## 本地开发

```bash
cd frontend
npm install
npm run dev
```

Vite 开发服务默认监听 3000 端口；`/api` 请求代理到 `http://localhost:8080`，因此需要同时启动后端。`npm run build` 直接将前端构建到 `backend/src/main/resources/static/`，供 Spring Boot 从 classpath 提供静态页面。构建后通过 IDEA 启动后端时，如页面资源未更新，请重新构建 IDEA 项目或刷新 Maven，使资源复制到 `backend/target/classes/`。通过 `npm run preview` 可预览构建结果。

## Docker 部署

前端镜像由 [`docker/frontend.Dockerfile`](../docker/frontend.Dockerfile) 构建。生产部署由 Nginx 提供 `dist/` 静态文件，并将 `/api/` 请求转发至后端。完整启动说明见 [Docker 部署文档](../docker/README.md)。

## 主要目录

```text
frontend/
├── src/
│   ├── api/          # 按业务领域组织的后端 API 模块，如 student.js 对应学生接口
│   ├── assets/       # 图片和全局样式
│   ├── components/   # 可复用业务组件，按领域归类
│   ├── composables/  # Vue 组合式逻辑
│   ├── layouts/      # 管理员、教师、学生等页面框架
│   ├── router/       # 路由定义，页面组件从 views 中加载
│   ├── stores/       # Pinia 状态管理
│   ├── utils/        # 与页面和单一业务领域无关的通用工具
│   └── views/        # 路由级页面，按角色和业务领域归类
├── public/           # 原样复制的公共资源
├── vite.config.js    # Vite 开发代理和构建配置
└── package.json      # 依赖与脚本
```

页面放在 `views/<角色>/<业务领域>/`，复用组件放在 `components/<业务领域>/`，接口按业务领域对应到 `api/<领域>.js`。页面专属样式写在 Vue 文件的 `<style scoped>` 中；跨页面的重置、主题变量和 Element Plus 全局覆盖放在 `src/assets/main.css`。路径导入统一使用 `@/` 指向 `src/`。
