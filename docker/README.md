# Docker 部署与运维

本目录提供单机/开发验证用的生产 Compose 部署定义。容器包括 MySQL 8.4、Spring Boot 后端、提供静态页面的前端 Nginx，以及对外入口 Nginx。所有用户访问入口默认是 `http://localhost`。

本地源码开发通常**不需要 Docker**：MySQL 使用本机服务，后端在 IDEA 或 Maven 中运行，前端使用 Vite；Vite 将 `/api` 代理到后端。此目录面向需要 Docker 部署的场景。

## 架构与请求路径

```text
浏览器
  │ HTTP :80（宿主机 HTTP_PORT）
  ▼
nginx（入口代理） ── /api/* ──► backend（Spring Boot :8080）
  │ /                          ▲
  └────────────────────────────┘
                 frontend（Nginx :80，提供 Vue 静态文件）

backend ── JDBC ──► mysql（容器 :3306，宿主机 MYSQL_PORT）
```

`nginx` 和 `frontend` 是两个职责不同的容器：前者对宿主机发布端口并路由请求，后者只负责静态文件。前端构建使用 Node 24 LTS，静态站点和外部入口均使用 Nginx 1.30 Alpine。`/api/` 由入口 Nginx 转发至 Spring Boot，其余路径转发至前端静态站点。

## 文件职责

| 文件 | 职责 |
| --- | --- |
| `docker-compose.yml` | 编排四个容器、端口、网络、健康检查、环境变量和 MySQL 卷 |
| `backend.Dockerfile` | 使用 Maven/Java 17 构建并运行 Spring Boot JAR |
| `frontend.Dockerfile` | 使用 Node 24 LTS 构建 Vue 静态文件，再复制到 Nginx 镜像 |
| `nginx.conf` | 将 `/api/` 转给后端，其余请求转给前端 |
| `backend/src/main/resources/db/migration/V1__create_initial_schema.sql` | 唯一预发布基线，创建完整业务表及初始管理员，随 JAR 打包 |
| `.env.example` | 数据库密码、SM4 密钥、JWT 有效期及宿主机端口模板 |
| 项目根目录 `.dockerignore` | 排除 Git、node_modules、target、env、日志等构建上下文文件 |

Compose 中两个 Dockerfile 的 `build.context` 均为项目根目录（`..`），因此 Dockerfile 中的 `COPY backend/...` 与 `COPY frontend/...` 路径都相对于仓库根目录。Dockerfile 位于本目录，但构建上下文不是本目录。不要从不包含根目录 `.dockerignore` 的错误上下文构建。

## 环境要求

- Docker Engine
- Docker Compose v2（`docker compose` 子命令）
- 首次构建时可访问镜像仓库和 npm/Maven 依赖源
- 主机未占用将要映射的 HTTP 和 MySQL 端口

## 首次配置

在 `docker/` 目录创建本地环境文件：

```bash
cd docker
cp .env.example .env
```

#### 方法一：Python 标准库（推荐）

安装 Python 3 即可，无需安装额外包或使用 OpenSSL。在项目根目录或 `docker` 目录执行：

```bash
python3 -c 'import base64, secrets; print("SM4_KEY_BASE64=" + base64.b64encode(secrets.token_bytes(16)).decode("ascii")); print("APP_DB_PASSWORD=" + secrets.token_hex(32)); print("JWT_SECRET=" + secrets.token_hex(32)); print("MYSQL_ROOT_PASSWORD=" + secrets.token_hex(32))'
```

将输出的四行分别复制到 `docker/.env` 中同名变量。脚本调用 Python `secrets` 模块，从操作系统的安全随机数源取值，不依赖 OpenSSL。不要把命令输出粘贴到聊天、工单或提交记录中。

| 变量 | 随机材料与格式 | 用途 |
| --- | --- | --- |
| `SM4_KEY_BASE64` | 16 个随机字节的 Base64 编码；解码后必须恰好为 16 字节 | SM4-GCM 联系方式加密 |
| `APP_DB_PASSWORD` | 32 个随机字节的十六进制编码，即 64 个 `[0-9a-f]` 字符 | 应用数据库账号密码；十六进制可避免 SQL 引号转义问题 |
| `JWT_SECRET` | 32 个随机字节的十六进制编码，即 64 个 `[0-9a-f]` 字符 | JWT 签名；至少 32 字节随机材料 |
| `MYSQL_ROOT_PASSWORD` | 32 个随机字节的十六进制编码，即 64 个 `[0-9a-f]` 字符 | 正式部署的 MySQL root 密码；仅本地开发可保留示例值 `123456` |

如果系统命令名是 `python` 而不是 `python3`，将上面的 `python3` 替换为 `python`。Windows PowerShell 若已安装 Python，也可运行同一条 `python -c '...'` 命令。

#### 方法二：密码管理器

可信密码管理器也可以生成和保存这些值。SM4 密钥要选择“生成随机字节/密钥”并生成恰好 16 字节，然后以 Base64 表示；不要直接把普通密码文本填入 `SM4_KEY_BASE64`。`APP_DB_PASSWORD` 和 `MYSQL_ROOT_PASSWORD` 建议使用 64 个十六进制字符（或确保只使用字母、数字、下划线和连字符，避免 SQL 特殊字符）；`JWT_SECRET` 使用至少 32 字节的高强度随机值，推荐 64 位十六进制字符。每个变量都应使用单独生成的随机值，不能互相复用。

#### 校验配置格式

保存 `.env` 后，可运行下面的检查。它只读取并校验长度/编码，不会输出密钥本身：

```bash
python3 -c 'from pathlib import Path; import base64; values = dict(line.split("=", 1) for line in Path(".env").read_text().splitlines() if line and not line.lstrip().startswith("#") and "=" in line); assert len(base64.b64decode(values["SM4_KEY_BASE64"], validate=True)) == 16, "SM4_KEY_BASE64 必须解码为 16 字节"; assert len(values["APP_DB_PASSWORD"]) >= 32, "APP_DB_PASSWORD 太短"; assert len(values["JWT_SECRET"].encode("utf-8")) >= 32, "JWT_SECRET 少于 32 字节"; print("密钥格式检查通过")'
```

如果使用 `python` 命令而不是 `python3`，也相应替换检查命令开头的 `python3`。

```dotenv
SM4_KEY_BASE64=填入第一条命令生成的Base64值
APP_DB_PASSWORD=填入第二条命令生成的64位十六进制值
JWT_SECRET=填入第三条命令生成的64位十六进制值
```

`APP_DB_USER` 默认是 `students_manager_app`，无需额外添加变量。本地 Docker 可保留 root 初始密码 `123456`；正式部署应为 `MYSQL_ROOT_PASSWORD` 配置独立强随机值。应用通过 `APP_DB_USER` / `APP_DB_PASSWORD` 使用受限数据库账号，不以 root 连接。Compose 会将 `APP_DB_USER` 和 `APP_DB_PASSWORD` 映射为 MySQL 容器所需的 `MYSQL_USER` 和 `MYSQL_PASSWORD`；后两者不是 `.env` 中需要配置的变量。

SM4 密钥必须 Base64 解码为**恰好 16 字节**。将它作为秘密材料妥善备份，并与数据库备份分开保管；不能随意更换或丢失，否则之前加密的手机号和邮箱无法解密。不要把真实 `.env` 提交到仓库或放入镜像。

### 环境变量

| 变量 | 默认值/要求 | 说明 |
| --- | --- | --- |
| `MYSQL_ROOT_PASSWORD` | `.env.example` 本地初始化默认值为 `123456`；Compose 未设置时也使用 `123456` | 本地开发使用；正式部署必须覆盖为强密码。仅首次初始化数据卷时生效 |
| `APP_DB_USER` | `students_manager_app` | 后端专用账号；权限限定在 `students_manager` 数据库 |
| `APP_DB_PASSWORD` | 必填，推荐 Python `secrets.token_hex(32)` 生成 | MySQL 首次初始化会创建该账号；已有数据卷需按下文升级步骤创建 |
| `JWT_SECRET` | 必填，推荐 Python `secrets.token_hex(32)` 生成 | 至少 32 字节随机密钥；更换后所有登录令牌失效 |
| `SM4_KEY_BASE64` | 必填，无默认值 | `backend` 服务启动必需；16 字节密钥的 Base64 |
| `JWT_EXPIRATION` | `1800000` | JWT 有效期，单位毫秒，默认 30 分钟 |
| `SPRING_FLYWAY_BASELINE_ON_MIGRATE` | `false` | 仅在接入经核对的既有数据库时，首次启动临时设为 `true`；成功后恢复 `false` |
| `HTTP_PORT` | `80` | 浏览器访问端口；端口冲突时修改 |
| `MYSQL_PORT` | `3308` | 主机连接 MySQL 的映射端口；容器内部仍为 3306 |

**权限说明：** MySQL 官方镜像仅在空数据目录首次初始化时，根据 Compose 映射进去的 `MYSQL_USER` / `MYSQL_PASSWORD` 创建应用账号。现有数据卷升级时，先备份，再按下一节创建账号并授权，然后重建后端；不要删除或重建数据库卷。

## 启动与访问

在 `docker/` 目录（确保 `.env` 可被 Compose 自动读取）执行：

```bash
docker compose config
docker compose up -d --build
```

`docker compose config` 可先检查变量插值和 Compose 配置。首次构建会拉取基础镜像、安装 Maven/npm 依赖并创建镜像，所需时间取决于网络。

| 服务 | 容器端口 | 宿主机端口/访问方式 |
| --- | --- | --- |
| 入口 Nginx | 80 | `${HTTP_PORT:-80}`；浏览器打开 `http://localhost` |
| MySQL | 3306 | `${MYSQL_PORT:-3308}`；供宿主机客户端调试使用 |
| 后端 | 8080 | 仅 Docker 网络内部访问 |
| 前端静态 Nginx | 80 | 仅 Docker 网络内部访问 |

Compose 使用 `app-network` 网络，容器间通过 `mysql`、`backend`、`frontend` 服务名通信。MySQL 和后端有健康检查；入口 Nginx 等待前后端健康后启动。

## 数据库初始化与持久化

MySQL 镜像为 `mysql:8.4`。Compose 通过 `MYSQL_DATABASE` 创建 `students_manager` 空数据库；空数据卷首次初始化时，MySQL 镜像创建 `students_manager_app` 并授予该库权限。后端以应用账号连接并运行 Flyway，执行 [`V1__create_initial_schema.sql`](../backend/src/main/resources/db/migration/V1__create_initial_schema.sql)，一次创建当前 21 张表和初始管理员 `admin`。不包含演示学生、教师、学院、专业或课程。SQL 随 Spring Boot JAR 打包，运行位置为 `classpath:db/migration/`，因此不需要 MySQL 初始化 SQL 挂载。

数据库保存在 Compose 命名卷 `students-manager-mysql-data`。Docker Compose 通常会在实际卷名中添加项目名前缀，可通过 `docker volume ls` 确认。以下行为不会清空数据卷：

- `docker compose restart`
- `docker compose down` 后再次 `docker compose up`
- `docker compose up -d --build` 重建容器或镜像

Flyway 在每次后端启动时检查 `flyway_schema_history`。项目尚未正式发布，当前源码仅保留一个包含完整结构的 V1。此前使用过旧版本迁移的数据库，其迁移记录和 V1 校验和可能与当前文件不兼容，普通容器重启不会重置这些记录。需要保留数据时先备份，并制定核对后迁入新库的方案；仅可丢弃的开发库可在备份后重建。不要只清空 Flyway 历史表或用 `repair` 掩盖结构差异，也不要在未备份时删除数据卷。

#### 给已有数据卷创建应用账号

更改 Compose 环境变量不会修改已有 MySQL 用户。对现有数据卷升级时，先备份数据库，然后启动 MySQL 服务；使用 root 在该容器中执行一次以下命令创建/更新专用账号，再启动后端：

```bash
docker compose up -d mysql
docker compose exec -it mysql mysql -uroot -p
```

输入 root 密码后，在 MySQL 提示符执行以下语句。将密码占位文本替换为 `.env` 中 `APP_DB_PASSWORD` 的值：

```sql
CREATE USER IF NOT EXISTS 'students_manager_app'@'%' IDENTIFIED BY '替换为APP_DB_PASSWORD';
ALTER USER 'students_manager_app'@'%' IDENTIFIED BY '替换为APP_DB_PASSWORD';
GRANT ALL PRIVILEGES ON `students_manager`.* TO 'students_manager_app'@'%';
```

随后退出 MySQL 并启动服务：

```bash
docker compose up -d --build
```

账号主机部分为 `%`，用于兼容 Compose 网络地址变化；数据库标识符使用反引号精确限定到 `students_manager`，应用权限不会授予其它库。`APP_DB_PASSWORD` 使用十六进制随机值，避免 SQL 引号转义问题。新空卷不需要手工执行此步骤。

### 备份和恢复

备份 InnoDB 数据库到主机当前目录：

```bash
docker compose exec -T mysql sh -c 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysqldump --single-transaction --routines --events --triggers --no-tablespaces -uroot students_manager' > students_manager.sql
```

请将备份恢复到新建的空数据库或隔离的 MySQL 实例，不要直接覆盖仍在使用的库。导入 SQL 备份：

```bash
docker compose exec -T mysql sh -c 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql -uroot students_manager' < students_manager.sql
```

恢复后核对 Flyway 版本、管理员账号及关键业务表的记录数。已使用 MySQL 8.4 临时实例完成一次备份恢复演练，抽查用户、学生、教师、课程、成绩和迁移历史的行数与源库一致。备份文件应按机构策略加密、限制访问并设置保留周期；不要将其提交到 Git。

数据库备份不包含 SM4 密钥。恢复加密的联系方式前，必须同时持有与数据匹配的 `SM4_KEY_BASE64`。

管理员由 Flyway V1 在新库初始化时建立；若管理员后来被删除，可走受控的账号恢复流程，不要重跑整个 schema 初始化。

## 初始账号和导入数据

- 初始管理员：用户名 `admin`，密码 `xiaoer`。
- 首次登录后必须立即修改密码。数据库中存储 PBKDF2-HMAC-SM3 哈希，不存密码明文。
- 新建学生由管理员在“学生管理”页面用 CSV 批量导入；学生账号为学号，初始密码 `xiaoer`，首次登录必须改密。
- 导入学生前先建学院、专业、年级和班级。模板位于 [`../frontend/public/templates/student-import-template.csv`](../frontend/public/templates/student-import-template.csv)。
- 成绩 CSV 模板位于 [`../frontend/public/templates/score-import-template.csv`](../frontend/public/templates/score-import-template.csv)，采用中文表头，考试时间格式为 `YYYY-MM-DDTHH:mm:ss`。开发演示数据请在本地使用 [`../backend/db/seed-dev.sql`](../backend/db/seed-dev.sql)，不要挂载到生产数据库。

## 常用运维命令

以下命令均在 `docker/` 目录执行：

```bash
docker compose ps
docker compose logs -f
docker compose logs -f backend
docker compose logs -f mysql
docker compose restart backend
docker compose stop
docker compose start
docker compose down
```

确认部署已备份且确实需要删除数据库时，才运行：

```bash
docker compose down -v
```

`-v` 会删除 Compose 管理的数据卷，包含 MySQL 中的业务数据；镜像和容器重建无法找回这些数据。

## 排错

### 容器无法启动或保持 `unhealthy`

```bash
docker compose ps
docker compose logs --tail=200 backend mysql frontend nginx
```

先看 `backend` 日志中的数据库连接和 SM4 配置错误，再确认 MySQL 的 health 状态。确认没有其他服务占用 `HTTP_PORT` / `MYSQL_PORT`。

### 刷新某个 Vue 子页面时返回 404

应用使用 Vue Router history 路由。API 请求和首页可访问，并不自动保证 Nginx 会将 `/admin/...`、`/student/...` 等深层 URL 回退到 `index.html`。如果从站内导航可以进入、但直接刷新子页面出现 Nginx 404，需要在提供静态文件的前端 Nginx 配置中增加 SPA fallback（例如将不存在的路径回退到 `/index.html`），然后重建前端镜像。当前 `docker/frontend.Dockerfile` 使用 Nginx 官方镜像默认站点配置，没有单独复制 SPA fallback 配置文件。

### 联系方式 SM4 解密失败

检查 `.env` 中的 `SM4_KEY_BASE64` 是否和写入该数据库时完全一致，且解码结果为 16 字节。不要通过更换 key 来处理旧密文；需要恢复原 key。

### 数据库缺表或缺列

检查后端启动日志和 `flyway_schema_history`，确认应用连接到预期的数据库。不要手工修改已执行的迁移文件；新增迁移版本，并先在备份/测试库验证。不要因为构建升级而运行 `docker compose down -v`。

### Docker Hub 访问失败、镜像拉取 403

这通常发生在基础镜像拉取阶段，而不是应用代码构建阶段。检查 Docker Engine 的镜像仓库网络/代理配置，确认 `mysql:8.4`、`nginx:1.30.5-alpine3.24`、`node:24.21.0-alpine3.24` 与 Maven/Temurin 基础镜像可访问。重新运行 `docker compose up -d --build` 前先检查 Docker Desktop 的镜像下载状态。

## 安全边界

- Compose 文件用于开发/单机部署示例，并非完整的公网生产加固方案。
- 初始管理员密码公开且会强制改密；部署后需更换为个人强密码。
- 手机号与邮箱使用 SM4-GCM，但其余个人信息字段并非都经过字段加密。
- MySQL 主机映射端口默认开放到宿主机；不需要宿主机直连时，应在受控部署中移除端口映射或限制防火墙来源。
- Nginx 配置当前提供 HTTP，没有配置 HTTPS 证书；对外部署应配置 TLS 终止、访问控制、备份和监控。
- 数据卷、备份文件、`.env` 和 SM4 密钥需要单独实施访问控制和加密存储。

## 其他说明

- [项目总览与本地开发](../README.md)
- [后端配置与 API](../backend/README.md)
- [前端构建与页面](../frontend/README.md)
