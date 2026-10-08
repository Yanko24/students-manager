# 前端说明

知行教务前端使用 Vue 3、Vite、Vue Router、Pinia、Element Plus 和 ECharts。开发时由 Vite 提供页面并代理 `/api` 请求；本地生产构建默认输出到 Spring Boot classpath 静态目录；Docker 构建则单独输出 `dist/` 并由 Nginx 提供服务。

## 环境要求

- Node.js 20（Docker 使用 Node 20.19.1）
- npm（依赖版本记录在 `package-lock.json`）
- 本地联调需同时启动后端和 MySQL；仅开发布局或纯前端组件时可以单独运行 Vite

## 安装与开发

在项目根目录执行：

```bash
cd frontend
npm ci
npm run dev
```

访问 <http://localhost:3000>。Vite 将请求 `/api/*` 转发到 `http://localhost:8080/api/*`；确保 Spring Boot 后端及 MySQL 已启动。前端 API 基础路径为相对路径 `/api`，开发与生产使用相同的请求地址。

如需升级或增删 npm 依赖，使用 `npm install <package>` 更新 `package.json` 和 `package-lock.json`；日常安装优先 `npm ci`，以锁文件安装一致版本。

## 构建与预览

```bash
npm run build
npm run preview
```

### 本地/IDEA 集成运行

默认 Vite `outDir` 是 `../backend/src/main/resources/static/`。如果希望直接通过 Spring Boot 的 8080 端口访问构建页面，构建之后还需将 Maven 资源复制到运行 classpath：

```bash
cd frontend
npm run build
cd ../backend
mvn -DskipTests compile
```

再启动或重启 IDEA 中的 Spring Boot 主类，访问 <http://localhost:8080>。若不执行 Maven 资源复制，`src/main/resources/static/` 和 `target/classes/static/` 可能不同步，浏览器会继续看到旧版页面或旧 chunk。

### Docker 构建

[`../docker/frontend.Dockerfile`](../docker/frontend.Dockerfile) 使用 `npm ci` 安装依赖，并通过 `npm run build -- --outDir dist` 覆盖默认输出目录。前端镜像将 `dist/` 复制到 Nginx 镜像，后端 JAR 不包含 Docker 部署用的前端静态站点。Docker 全流程见 [Docker 部署说明](../docker/README.md)。

## 页面与权限

| 路径前缀 | 页面内容 |
| --- | --- |
| `/login` | 用户登录；登录后根据账号角色进入对应控制台 |
| `/admin/*` | 管理员总览、档案与课程/成绩/考勤管理 |
| `/teacher/*` | 教师控制台、本人授课课程、课程学生名单、跨页批量成绩录入/更正、考勤、站内通知和个人信息 |
| `/student/*` | 学生主页、已确认课程、个人成绩、考勤和个人信息 |

Vue Router 根据登录状态和角色限制页面访问。API 的实际授权仍由 Spring Security 在后端执行，前端路由守卫不能替代后端权限检查。

学生端有“我的课程”列表和成绩查询，所用接口在服务端按当前登录账号限定数据。项目目前没有课程排课时间/教室数据；旧路由 `/student/schedule` 兼容跳转到“我的课程”。

## 键盘交互

应用通过 `src/utils/keyboardShortcuts.js` 为常见表单提供 Enter 快捷操作，例如登录、搜索、保存或提交。焦点位于下拉菜单、日期选择器、自动完成控件或多行文本框时保留控件默认行为；删除等危险操作不会被 Enter 自动触发。登录表单也支持 Enter 提交。

## 目录结构

```text
frontend/
├── public/                       # 原样复制的公开资源
│   └── templates/                # 学生/成绩 CSV 模板
├── src/
│   ├── api/                      # 后端 API 模块，按业务领域组织
│   ├── assets/                   # 图片、图标、全局 CSS/SCSS
│   ├── components/               # 可复用 UI 和业务组件
│   │   ├── common/
│   │   ├── course/
│   │   ├── major/
│   │   ├── score/
│   │   ├── student/
│   │   └── teacher/
│   ├── composables/              # Vue Composition API 复用逻辑
│   ├── layouts/                  # admin、teacher、student 页面外框
│   ├── router/                   # Vue Router 路由与导航守卫
│   ├── stores/                   # Pinia 状态，如登录用户与会话
│   ├── utils/                    # 请求、日期、键盘等通用工具
│   └── views/                    # 路由级页面，按角色与功能划分
├── index.html
├── vite.config.js                # alias、开发代理、构建输出和代码分块
├── package.json
└── package-lock.json
```

### 页面和模块的放置约定

- 页面放到 `src/views/<角色>/<功能>/`，由 `src/router/index.js` 加载。
- 面向多页面复用的组件放到 `src/components/<领域>/`；只被单页使用的简单子组件可留在页面内。
- 页面调用的接口放在 `src/api/<领域>.js`，不在视图中拼接重复的 API URL。
- 可跨页面复用的状态和逻辑分别放在 `src/stores/` 与 `src/composables/`。
- 单页样式使用 Vue SFC 的 `<style scoped>`；全局样式、重置和主题覆盖放在 `src/assets/`。
- `@/` 别名指向 `src/`，例如 `import request from '@/utils/request'`。

## 前端代码约定

- Vue 页面使用 Composition API 与 `<script setup>`。
- 数据分页、筛选和统计由后端接口提供；不要用静态演示数据冒充数据库结果。
- 所有页面展示日期和时间统一调用 `src/utils/dateUtils.js`：日期时间用 `formatDateTime(value)`，日期用 `formatDate(value)`。表格时间列使用作用域模板调用格式化函数，不直接绑定 `prop` 显示后端原值；新增页面也遵循此规则。
- 所有数据表格单元格默认不换行；列宽超出内容区时使用表格横向滚动查看，不通过换行压缩表格。
- API 错误通过 Axios 响应拦截器与页面提示处理；对需要呈现空状态的页面同步清空旧数据。
- 编辑表单和教师成绩录入在存在未保存改动时，会在应用内导航、关闭编辑弹窗或刷新/关闭浏览器前提示确认；保存失败保留当前输入，成功保存后正常离开。
- 登录凭据保存在浏览器本地状态中；过期或后端重启导致会话失效时，统一注销并跳回登录页。

## 常用命令

| 命令 | 作用 |
| --- | --- |
| `npm ci` | 按 lockfile 安装依赖 |
| `npm run dev` | 启动 Vite 开发服务器（默认 3000） |
| `npm run build` | 构建生产静态资源；默认写入后端 `static/` |
| `npm run preview` | 本地预览构建后的资源 |

## 常见问题

### API 返回 404

确认后端已启动、Vite 代理目标为 `localhost:8080`，并核对浏览器 Network 中失败的是前端路由还是 `/api/...` 请求。新增后端 Controller 后须重新编译并重启 Spring Boot；页面路由存在不代表后端 API 已在正在运行的进程中注册。

### IDEA 启动后页面没更新

`npm run build` 写入 `backend/src/main/resources/static/`。运行 classpath 可能仍使用旧的 `backend/target/classes/static/`；在 `backend/` 执行 `mvn -DskipTests compile`，重启后端并强制刷新浏览器。

### ECharts 图表运行时报类继承错误

Vite 配置将 `echarts`、`zrender` 与 `vue-echarts` 放在同一 `charts` chunk，避免依赖初始化顺序被拆散。更新分块配置后需重新构建，并从最新的 `index.html` 加载资源。

## 相关文档

- [项目总览与快速开始](../README.md)
- [后端接口、配置与数据库](../backend/README.md)
- [Docker 部署与运维](../docker/README.md)
