# 前端

学生管理系统前端，采用 Vue 3、Vite 和 Element Plus，生产构建输出到 `dist/`。Docker 部署时由 Nginx 提供静态页面并代理 API 请求。

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

Vite 开发服务默认监听 3000 端口；`/api` 请求代理到 `http://localhost:8080`，因此需要同时启动后端。可通过 `npm run build` 构建生产静态文件，通过 `npm run preview` 本地预览构建结果。

## Docker 部署

前端镜像由 [`docker/frontend.Dockerfile`](../docker/frontend.Dockerfile) 构建。生产部署由 Nginx 提供 `dist/` 静态文件，并将 `/api/` 请求转发至后端。完整启动说明见 [Docker 部署文档](../docker/README.md)。

## 主要目录

```text
frontend/
├── src/
│   ├── api/          # API 请求
│   ├── assets/       # 静态资源
│   ├── components/   # 组件
│   ├── router/       # 路由配置
│   ├── utils/        # 工具函数
│   └── views/        # 页面
├── public/           # 原样复制的公共资源
├── vite.config.js    # Vite 开发代理和构建配置
└── package.json      # 依赖与脚本
```
