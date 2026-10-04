# 学生管理系统

学生管理系统由 Vue 3 前端、Spring Boot 后端和 MySQL 数据库组成。Docker 部署入口、镜像定义和 Nginx 配置统一放在 `docker/`。本地开发使用 Vite 代理前端 API，不需要 Nginx。

## 目录结构

```text
students/
├── backend/       # Spring Boot 后端、本地环境变量示例及开发数据库初始化脚本
│   └── db/init.sql
├── frontend/      # Vue 3 + Vite 前端
└── docker/        # Docker Compose、前后端 Dockerfile 和 Nginx 配置
```

## Docker 启动

先复制 Docker 配置模板并编辑数据库密码和生产 SM4 密钥：

```bash
cp docker/.env.example docker/.env
```

为 `SM4_KEY_BASE64` 设置 16 字节随机密钥的 Base64 值（可用 `openssl rand -base64 16` 生成）。妥善备份此密钥；丢失后无法解密已加密的邮箱和手机号。然后启动：

```bash
cd docker
docker compose up -d --build
```

访问 `http://localhost`。默认 HTTP 端口为 80，可在 `docker/.env` 中用 `HTTP_PORT` 修改。更多配置见 [Docker 部署说明](docker/README.md)。

## 数据库与初始账号

本地开发初始化脚本为 [`backend/db/init.sql`](backend/db/init.sql)，包含建表结构和教师、学生等演示数据。生产 Docker 使用 [`docker/db/init.sql`](docker/db/init.sql)，创建数据库表并初始化唯一管理员账号，不导入演示学生等业务数据。MySQL 官方镜像只会在数据目录为空时执行生产初始化脚本；已有 Docker 数据卷会保留原数据，不会自动重置或升级。当前尚未维护数据库升级迁移脚本。

首次管理员账号为 `admin`，初始密码为 `xiaoer`，首次登录必须修改密码。密码使用带盐 PBKDF2-HMAC-SM3 哈希存储。管理员登录后可在学生管理页下载 CSV 模板并批量导入学生；导入前需先创建对应学院、专业和班级。手机号和邮箱使用 SM4-GCM 加密存储。其他个人信息字段、数据库文件和数据库网络连接目前没有额外加密。

## 开发文档

- [后端说明](backend/README.md)
- [前端说明](frontend/README.md)
- [Docker 部署说明](docker/README.md)
