# 后端说明

后端为学生管理系统提供 REST API、身份认证、数据库访问和 Spring Boot 静态页面托管能力。Docker 部署时后端只负责 API，前端文件由独立的 Nginx 容器提供。

## 技术栈与运行要求

- Java 11
- Spring Boot 2.7.18、Spring MVC、Spring Security
- MyBatis-Plus 3.5.3.1
- MySQL Connector/J 8.4.0，目标数据库 MySQL 8.4
- Maven 3.6 或更新版本
- Bouncy Castle：SM3、SM4-GCM 算法实现
- Springfox Swagger 3

## 后端职责

- 管理员 API：学生、教师、学院、专业、课程、成绩、考勤和统计数据。
- 教师 API：查询授课课程和本人负责课程的考勤信息。
- 学生 API：当前学生的个人档案、已确认课程、个人成绩和考勤。
- JWT 登录、当前账号查询、修改密码及会话有效性检查；登出由前端清除本地令牌，当前没有服务端 logout API。
- 联系方式（手机号、邮箱）在数据库读写时进行 SM4-GCM 加解密。
- 首次登录强制修改初始密码；密码存储为 PBKDF2-HMAC-SM3 哈希，并兼容旧 BCrypt 格式登录。
- Spring Boot 可在本地从 `classpath:/static/` 提供前端构建结果。

## 配置文件与 Profile

配置文件位于 `src/main/resources/`：

| 文件 | 用途 |
| --- | --- |
| `application.yml` | 公共配置、默认 Profile、端口、JWT 有效期和文件上传限制 |
| `application-dev.yml` | 本地开发数据库连接、MyBatis SQL 日志和开发用 SM4 默认密钥 |
| `application-prod.yml` | 生产日志配置及公共 MyBatis 设置；数据库连接和 SM4 密钥应通过环境变量提供 |

`application.yml` 当前默认激活 `dev` Profile。Docker Compose 会显式设置 `SPRING_PROFILES_ACTIVE=prod`。本机可在 IDEA 的运行配置中设置 Profile，也可在终端指定：

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

### 常用环境变量

| 变量 | 用途 | 示例/默认值 |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | Spring Profile | 本地 `dev`；Docker 为 `prod` |
| `SPRING_DATASOURCE_URL` | JDBC URL | 本地 `localhost:3306/students_manager` |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 | 本地 `root` |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 | 本地配置默认为 `xiaoer` |
| `SM4_KEY_BASE64` | 16 字节 SM4 密钥的 Base64 字符串 | 本地 dev 配置有仅供开发的默认值；生产必须设置 |
| `JWT_EXPIRATION` | JWT 有效期，毫秒 | `1800000`（30 分钟） |

**JWT 密钥说明：** 当前 `jwt.secret` 定义在 `application.yml`，尚未绑定到 `JWT_SECRET` 环境变量。生产打包前必须在配置中替换为至少 256 位的随机密钥，并妥善保管；不能假设在 `docker/.env` 添加 `JWT_SECRET` 就会生效。生产系统如需运行时从密钥管理服务注入，应先为应用补充对应的外部化配置。

SM4 密钥必须保持稳定。更换或丢失密钥会导致已加密的手机号和邮箱无法解密。不要将生产密钥放入 Git、镜像或 SQL 初始化脚本。

本地可选环境变量模板：[`.env.local.example`](.env.local.example)。实际的 `.env.local` 仅用于本地开发，不要提交。若通过终端加载（macOS/Linux）：

```bash
cd backend
cp .env.local.example .env.local
set -a
source .env.local
set +a
mvn spring-boot:run
```

注意：模板目前只覆盖本地 SM4 密钥；数据库账号仍来自 `application-dev.yml`，需要时可在 `.env.local` 中自行增加 `SPRING_DATASOURCE_*` 变量。

## 本地启动

先确保 MySQL 已启动并完成一次开发数据库初始化：

```bash
mysql -u root -p < db/init.sql
```

然后在 `backend/` 目录启动服务：

```bash
mvn spring-boot:run
```

默认地址为 `http://localhost:8080`。如果前端通过 Vite 运行，还需要在另一个终端启动 `frontend/` 的 Vite 服务；详见 [前端说明](../frontend/README.md)。

开发 Profile 使用 MyBatis `StdOutImpl` 输出 SQL、参数和结果；生产 Profile 使用 `NoLoggingImpl` 关闭 MyBatis SQL 控制台输出。请勿将开发 SQL 日志误认为生产日志级别。

## 构建和运行

构建可执行 JAR：

```bash
cd backend
mvn clean package -DskipTests
```

产物：`target/students-manager-1.0-SNAPSHOT.jar`。生产环境需显式使用 `prod` Profile 并注入数据库、SM4 配置；同时按上一节替换 JWT 签名密钥：

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/students_manager?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8'
export SPRING_DATASOURCE_USERNAME='your-user'
export SPRING_DATASOURCE_PASSWORD='your-strong-password'
export SM4_KEY_BASE64='your-base64-encoded-16-byte-key'
java -jar target/students-manager-1.0-SNAPSHOT.jar
```

数据库用户名和密码须按实际环境调整；上面连接 URL 中的 `useSSL=false` 只适用于本地示例，生产环境应按 MySQL TLS 配置调整。

## 前端静态资源与 IDEA

在 `frontend/` 目录执行 `npm run build` 后，Vite 将静态资源生成到 `backend/src/main/resources/static/`。Spring Boot/IDEA 运行 classpath 通常读取 `backend/target/classes/static/`，因此源目录更新后需要让 Maven/IDEA 再复制一次资源：

```bash
cd backend
mvn -DskipTests compile
```

在 IDEA 中也可重新构建项目或刷新 Maven。若只点击运行旧的 `target/classes`，页面可能还是上一次构建的内容，出现旧路由、缺失 chunk 或 404。Docker 构建不会使用后端的 `static/` 目录来部署前端；Docker 前端由 Nginx 镜像单独承载。

## 数据库初始化与样例

### 本地开发

[`db/init.sql`](db/init.sql) 在新数据库中创建完整表结构并初始化开发数据，包括用户、学院、专业、教师、学生、课程、选课、成绩和考勤。样例账号初始密码为 `xiaoer`，首次登录需修改。脚本适合本地开发和演示，不要将其当作生产升级脚本。

### Docker/生产初始化

[`../docker/db/init.sql`](../docker/db/init.sql) 用于 Docker MySQL 的空数据目录首次启动：创建表结构并插入首个管理员，不插入演示学生、教师、学院或课程。MySQL 官方镜像只会在数据目录为空时运行 `/docker-entrypoint-initdb.d/` 中的 SQL。

现有数据库不会自动升级；`CREATE TABLE IF NOT EXISTS` 不会为已有表补列。若应用报告 `Unknown column` 或 `Table doesn't exist`，应先比对实际库结构与当前初始化文件，再为已有库编写、备份并执行明确的升级脚本。直接重建数据卷会删除库中数据，不应作为常规升级方法。

两个初始化文件的说明：

- 开发：[`db/init.sql`](db/init.sql)
- Docker 生产：[`../docker/db/init.sql`](../docker/db/init.sql)
- 生产初始化文件由 Compose 挂载，不由后端应用自动执行。

## API 与权限概览

Swagger UI：<http://localhost:8080/swagger-ui/index.html>。受保护接口在 Swagger 的 **Authorize** 中填写 `Bearer <JWT>`。

| API 前缀 | 用途 | 主要授权 |
| --- | --- | --- |
| `/api/auth` | 登录、会话检查、当前账号、改密 | 登录公开；其余需认证；当前无服务端 logout API |
| `/api/students` | 学生档案和统计 | 管理员、教师、学生 |
| `/api/teachers` | 教师档案 | 管理员、教师 |
| `/api/colleges`、`/api/majors` | 学院与专业 | 需登录；当前未统一按角色细分 |
| `/api/courses`、`/api/scores`、`/api/users` | 课程、成绩和用户管理 | 需登录；当前未统一按角色细分 |
| `/api/attendance` | 管理端、教师端、学生端考勤和统计 | 按角色分路径限制 |
| `/api/student-portal` | 当前学生本人课程、课程详情、成绩及成绩统计 | 学生角色；服务端按登录账号限定数据 |
| `/api/admin/students` | 学生 CSV 批量导入 | 管理员 |
| `/api/health` | 服务与数据库健康检查 | 健康检查接口公开 |

接口清单和字段以 Swagger、控制器 DTO/VO 为准。学生课程仅显示已通过的选课记录；目前没有课程时间/教室排课数据表。

**权限现状：** `SecurityConfig` 对管理员导入、学生本人数据、教师考勤及学生考勤等部分接口实施了角色限制；其他没有匹配专用规则或方法级授权的接口目前只要求用户已登录。不要把“前端页面隐藏/限制入口”当作后端权限边界。公网部署前应进一步为未细分的业务 Controller 增加角色级授权。

## 文件上传与 CSV

- 文件上传基础限制由 `application.yml` 中 `file.upload` 配置（默认最大 10 MB；JPEG、PNG、GIF）。
- 学生模板：[`../frontend/public/templates/student-import-template.csv`](../frontend/public/templates/student-import-template.csv)。导入前需创建学院、专业、年级和班级；整批校验通过后才写入。
- 成绩模板：[`../frontend/public/templates/score-import-template.csv`](../frontend/public/templates/score-import-template.csv)。导入字段为 `studentNo,courseCode,score,semester,examTime,comment`；考试时间格式为 `YYYY-MM-DDTHH:mm:ss`。
- 学生批量导入在 `/api/admin/students/**` 下仅管理员可用。成绩通用接口当前只要求登录，尚未统一按管理员/教师/学生细分授权。

## 会话与安全

- JWT 默认 30 分钟失效；应用重启会更新服务端会话上下文，旧 JWT 会失效，需要重新登录。
- 初始化默认密码是 `xiaoer`。数据库保存 PBKDF2-HMAC-SM3 哈希，不保存明文；首次登录要求重置。
- 手机号和邮箱使用 SM4-GCM；加密字段由 TypeHandler/字段加密组件转换。
- 目前没有对全部个人信息字段启用字段加密，也没有为 JDBC 连接、数据库文件或数据卷自动启用 TLS/磁盘加密。
- 生产环境须使用强数据库凭据、随机且独立的 SM4 密钥、替换默认 JWT 签名密钥，并部署在受控网络中。

## 常见问题

### `SM4_KEY_BASE64` 缺失或联系方式无法解密

开发 Profile 有本地默认值。生产 Profile 不应依赖该值，需提供合法 Base64，解码后必须恰好为 16 字节。已有数据必须继续使用写入这些数据时的同一密钥。

### 启动后页面/API 仍是旧版本

Vite 输出在 `src/main/resources/static/`，IDEA 运行 classpath 常使用 `target/classes/`。重新构建前端后执行 `mvn -DskipTests compile`，再重启 Spring Boot 并强制刷新浏览器。

### 数据表或列缺失

初始化脚本只作用于新建数据库。已有数据库不会自动套用最新结构；备份后按 schema 编写升级 SQL，不要为了升级直接删除数据卷。

## 相关文档

- [项目总览与快速开始](../README.md)
- [前端开发与构建](../frontend/README.md)
- [Docker 部署与运维](../docker/README.md)
