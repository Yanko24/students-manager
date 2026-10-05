# 学生管理系统

学生管理系统由 Vue 3 前端、Spring Boot 后端和 MySQL 数据库组成，提供管理员、教师和学生三类账号入口。项目支持本地开发，也支持使用 Docker Compose 部署。Docker 配置统一放在 `docker/`；本地开发不需要启动 Docker 或 Nginx。

## 功能概览

| 角色 | 主要功能 |
| --- | --- |
| 管理员 | 数据总览；学生、教师、学院、专业、课程、成绩和考勤管理；学生及成绩 CSV 导入/导出 |
| 教师 | 查看授课课程与相关考勤、查看和维护个人信息 |
| 学生 | 查看个人主页、已确认课程、个人成绩及成绩统计、考勤记录、个人信息 |

学生端课程和成绩接口按当前登录账号查询，不返回其他学生的数据。课程表/具体排课时间目前没有对应的数据表；旧的 `/student/schedule` 地址会转到“我的课程”。

## 技术栈

- 前端：Vue 3、Vite、Vue Router、Pinia、Element Plus、ECharts
- 后端：Java 11、Spring Boot 2.7、Spring Security、JWT、MyBatis-Plus
- 数据库：MySQL 8.4
- 安全：密码使用 PBKDF2-HMAC-SM3 哈希存储；手机号和邮箱使用 SM4-GCM 加密

## 目录结构

```text
students-manager/
├── backend/
│   ├── db/init.sql                 # 本地开发数据库结构与演示数据
│   ├── src/main/java/              # Spring Boot 源码
│   ├── src/main/resources/         # 应用配置、Mapper 配置及前端构建输出
│   ├── .env.local.example          # 可选的本地环境变量模板
│   └── pom.xml
├── frontend/
│   ├── public/templates/           # 学生、成绩 CSV 模板
│   ├── src/api/                    # 按业务领域组织的 API
│   ├── src/components/             # 可复用组件
│   ├── src/layouts/                # 管理员、教师、学生布局
│   ├── src/router/                 # 前端路由
│   ├── src/views/                  # 按角色和业务组织的页面
│   ├── vite.config.js
│   └── package.json
├── docker/
│   ├── db/init.sql                 # 生产数据库初始化结构及首个管理员
│   ├── backend.Dockerfile
│   ├── frontend.Dockerfile
│   ├── docker-compose.yml
│   └── nginx.conf
├── .dockerignore
└── .gitignore
```

## 本地开发

### 环境要求

- Java 11
- Maven 3.6+
- Node.js 20、npm
- MySQL 8.4

### 初始化本地数据库

在**新数据库**上执行一次开发初始化脚本。脚本会创建 `students_manager` 数据库、表结构和演示数据，包括管理员、教师、学生、课程、少量成绩及近两周的考勤记录：

```bash
mysql -u root -p < backend/db/init.sql
```

本地默认连接 `localhost:3306`，数据库 `students_manager`，开发配置中的用户名和密码为 `root` / `xiaoer`。如本机 MySQL 凭据不同，请修改 `backend/src/main/resources/application-dev.yml`，或通过本地环境变量覆盖。初始化 SQL 不用于升级已有数据库；不会自动重建或迁移已有数据。

### 启动前后端

准备两个终端。先启动后端：

```bash
cd backend
mvn spring-boot:run
```

再启动前端：

```bash
cd frontend
npm install
npm run dev
```

访问 <http://localhost:3000>。Vite 将 `/api` 请求代理到 `http://localhost:8080`。后端默认使用 `dev` profile；开发环境配置了本地 SM4 默认密钥。此默认值仅供本地开发，不要用于生产环境。若需覆盖本地配置，可复制 `backend/.env.local.example` 为 `backend/.env.local`，再按 [后端说明](backend/README.md) 加载变量。

### 构建前端并由 Spring Boot 提供页面

前端生产构建默认输出到 `backend/src/main/resources/static/`，不会自动复制到 `backend/target/classes/static/`。若使用 IDEA 直接运行 Spring Boot，构建后执行一次 Maven 编译/资源复制，再启动或重启后端：

```bash
cd frontend
npm run build

cd ../backend
mvn -DskipTests compile
```

然后访问 <http://localhost:8080>。也可以在 IDEA 中重新构建项目，让 Maven 资源同步到运行时 classpath。前端直接打包和本地开发均不需要 Nginx。

## Docker 部署

首次部署前配置 Docker 环境文件，并设置强数据库密码和独立的 SM4 密钥：

```bash
cp docker/.env.example docker/.env
openssl rand -base64 16
```

将生成的 Base64 密钥填入 `docker/.env` 的 `SM4_KEY_BASE64`，并设置 `MYSQL_ROOT_PASSWORD`。密钥必须稳定并妥善备份；丢失或更换密钥后，已有加密邮箱和手机号将无法解密。不要提交真实的 `docker/.env`。

启动服务：

```bash
cd docker
docker compose up -d --build
```

访问 <http://localhost>。默认 HTTP 端口为 80，可通过 `HTTP_PORT` 修改；MySQL 映射到主机的默认端口为 3308，可通过 `MYSQL_PORT` 修改。Compose 启动 MySQL 8.4、Spring Boot 后端、前端静态站点和 Nginx。Nginx 对外提供前端页面并代理 `/api/` 请求。

常用运维命令（在 `docker/` 目录执行）：

```bash
docker compose ps
docker compose logs -f
docker compose down
```

`docker compose down` 会保留命名卷中的数据库数据。仅在确认备份后，才使用 `docker compose down -v` 删除数据卷。完整说明见 [Docker 部署文档](docker/README.md)。

## 初始化数据与账号

| 场景 | 初始化文件 | 数据内容 |
| --- | --- | --- |
| 本地开发 | [`backend/db/init.sql`](backend/db/init.sql) | 表结构及演示业务数据 |
| Docker 部署 | [`docker/db/init.sql`](docker/db/init.sql) | 表结构及首个管理员，不含演示学生、教师、课程等数据 |

Docker MySQL 只会在**空数据目录首次初始化**时执行 `docker/db/init.sql`；已存在的数据卷不会因重建容器而重新初始化。两个 SQL 文件都是初始化脚本，不是数据库升级脚本。

Docker 初始管理员账号为 `admin`，密码为 `xiaoer`，首次登录必须修改密码。本地开发初始化脚本中的演示用户初始密码也为 `xiaoer`，用户名分别是管理员账号、教师工号或学生学号；首次登录同样需要修改。生产环境不会自动生成演示账号。CSV 模板位于 `frontend/public/templates/`；学生批量导入前需先建立对应学院及专业班级。

## API 文档与会话

- Swagger UI：<http://localhost:8080/swagger-ui/index.html>
- 后端默认端口：8080
- JWT 默认有效期：30 分钟，可用 `JWT_EXPIRATION` 覆盖，单位为毫秒
- 后端重启后，之前签发的 token 会失效，用户需要重新登录

## 安全说明

- 数据库保存带随机盐的 PBKDF2-HMAC-SM3 口令哈希，不保存明文密码。
- 邮箱和手机号使用 SM4-GCM 加密，密钥通过 `SM4_KEY_BASE64` 提供；生产密钥须与数据库分开保存。
- 当前 JWT 签名密钥仍配置在 `backend/src/main/resources/application.yml`，尚未从 Docker `.env` 外置；生产打包前必须替换该默认值。添加 `JWT_SECRET` 环境变量目前不会覆盖它。
- 部分业务 API 目前只要求登录，尚未全部按管理员/教师/学生角色细分；前端路由权限不等同于后端授权。
- 目前没有为所有个人信息字段、数据库数据卷或 JDBC 连接启用额外加密。生产部署应使用专用数据库账号、强密码，并按部署环境配置数据库 TLS 和磁盘加密。
- 不要将 `backend/.env.local`、`docker/.env`、密钥或生产数据提交到仓库。

## 相关文档

- [后端说明](backend/README.md)：构建、环境变量、接口和数据库详情
- [前端说明](frontend/README.md)：前端目录、键盘操作和构建说明
- [Docker 部署说明](docker/README.md)：Compose 配置、卷、初始化及运维
