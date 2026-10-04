# Docker 部署

Docker 配置统一放在 `docker/` 目录。`backend.Dockerfile` 与 `frontend.Dockerfile` 分别定义镜像构建步骤，构建上下文分别指向 `backend/` 和 `frontend/`；单个生产 Compose 文件编排前端、后端、MySQL 和 Nginx。

文件职责：

- `docker-compose.yml`：前后端、MySQL、Nginx、网络和数据卷
- `backend.Dockerfile`、`frontend.Dockerfile`：前后端镜像构建步骤
- `db/init.sql`：生产环境数据库初始化，创建数据库表并插入首个管理员，不插入演示业务数据
- `.env.example`：首次配置模板；`.env`：本机 Compose 参数
- `nginx.conf`：生产入口的静态资源和 API 代理配置
- 项目根目录的 `.dockerignore`：两个镜像共用的 Docker 构建上下文忽略规则

Nginx 仅用于 Docker 部署入口；本地开发使用 Vite 代理 API，不启动 Docker Nginx。后端本地环境变量示例位于 [`backend/.env.local.example`](../backend/.env.local.example)，实际文件 `backend/.env.local` 不纳入版本管理。

## 首次配置

```bash
cp docker/.env.example docker/.env
```

编辑 `docker/.env`，设置强 `MYSQL_ROOT_PASSWORD`，并为 `SM4_KEY_BASE64` 填入 16 字节随机密钥的 Base64 值（可用 `openssl rand -base64 16` 生成）。密钥必须妥善备份，丢失后无法解密邮箱和手机号。不要将实际 `.env` 提交到代码仓库。

## 生产部署

```bash
cd docker
docker compose up -d --build
```

访问 `http://localhost`（可通过 `HTTP_PORT` 改端口）。容器包括 `nginx`、`frontend`、`backend`、`mysql`。前端静态文件由独立 Nginx 镜像提供，`/api/` 请求转发给 Spring Boot。

## 数据与初始化

MySQL 镜像使用 `mysql:8.4`，数据保存在 `students-manager-mysql-data` 命名卷中。Docker 挂载 `docker/db/`，其中的 `init.sql` 创建数据库表并插入首个管理员账号，不含演示学生、教师、学院或课程数据。Docker 只在空数据卷首次启动时执行它；项目仍处于首次发布前，暂不维护升级迁移脚本。现有数据卷会保留，不会自动套用初始化结构，也不会因重新构建容器而清空。若先前数据看起来缺失，请确认 Compose 实际使用的项目名及其绑定的数据卷；不要直接删除数据卷。

本地开发需要演示数据时，单独使用 [`backend/db/init.sql`](../backend/db/init.sql)。它包含管理员、教师、学生、课程等测试数据，不会被生产 Compose 挂载或执行。

首次管理员账号为 `admin`，默认密码为 `xiaoer`，登录后必须修改。数据库只保存 PBKDF2-HMAC-SM3 哈希，不保存明文密码。手机号和邮箱使用 SM4-GCM 加密；加密密钥只从 `.env` 注入，不存入 SQL。

若 MySQL 数据卷是在加入管理员初始化前创建的，Docker 不会重新执行初始化 SQL。保留数据卷的前提下，可在 `docker/` 目录手动幂等补入管理员，然后重启后端以加密空白联系方式：

```bash
docker compose exec -T mysql sh -c 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql -uroot' < db/init.sql
docker compose restart backend
```

学生批量录入：登录管理员账号后进入“学生管理”，点击“批量导入”并下载 CSV 模板。先创建学院及专业班级，再填写学生数据并上传。导入会先校验文件全部记录，全部合格后才写入；学号或专业班级无效时整批取消。每名新学生以学号作为账号，初始密码为 `xiaoer`，首次登录需修改密码。Excel 可另存为 CSV UTF-8 后上传。

这不代表整库数据都经过国密加密：学生个人信息等字段目前没有字段级加密，MySQL 数据卷也未配置磁盘加密；数据库连接目前使用普通 JDBC/TCP，未启用 TLS。部署到生产环境时，应使用独立强数据库凭据并从 `.env` 或密钥管理服务注入，按网络环境配置 MySQL TLS 与磁盘加密。

## 常用命令

```bash
docker compose ps
docker compose logs -f
docker compose down
```

`docker compose down` 不删除 MySQL 数据卷。要删除数据需要额外执行 `docker compose down -v`，请确认备份后再操作。
