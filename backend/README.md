# 后端说明

后端为知行教务提供 REST API、身份认证、数据库访问和 Spring Boot 静态页面托管能力。Docker 部署时后端只负责 API，前端文件由独立的 Nginx 容器提供。

## 技术栈与运行要求

- Java 17
- Spring Boot 4.1.1、Spring MVC、Spring Security 7
- MyBatis-Plus 3.5.17（Spring Boot 4 starter）
- Flyway 13.6.0（MySQL 支持模块单独引入）
- MySQL Connector/J 8.4.0，目标数据库 MySQL 8.4
- Maven 3.6.3 或更新版本
- Bouncy Castle：SM3、SM4-GCM 算法实现
- springdoc-openapi 3（OpenAPI 3 / Swagger UI）

## 后端职责

- 管理员 API：学生、教师、学院、专业、课程、成绩、考勤和统计数据。
- 教师 API：查询授课课程和本人负责课程的考勤信息。
- 学生 API：当前学生的个人档案、开放课程查询、选课与退选、已确认课程、个人成绩和考勤。
- JWT 登录、当前账号查询、修改密码及会话有效性检查；登出由前端清除本地令牌，当前没有服务端 logout API。
- 联系方式（手机号、邮箱）在数据库读写时进行 SM4-GCM 加解密。
- 首次登录强制修改初始密码；密码存储为 PBKDF2-HMAC-SM3 哈希，并兼容旧 BCrypt 格式登录。
- Spring Boot 可在本地从 `classpath:/static/` 提供前端构建结果。

## 配置文件与 Profile

配置文件位于 `src/main/resources/`：

| 文件 | 用途 |
| --- | --- |
| `application.yml` | 公共配置、默认 Profile、端口、JWT 有效期和文件上传限制 |
| `application-dev.yml` | 本地开发数据库连接、MyBatis SQL 日志、开发用 SM4 默认密钥；关闭 Flyway 自动 baseline，由版本化迁移创建新库结构 |
| `application-prod.yml` | 生产日志配置及公共 MyBatis 设置；数据库连接、Flyway baseline 和 SM4 密钥按生产流程显式设置 |

`application.yml` 当前默认激活 `dev` Profile。Docker Compose 会显式设置 `SPRING_PROFILES_ACTIVE=prod`。本机可在 IDEA 的运行配置中设置 Profile，也可在终端指定：

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

### 常用环境变量

| 变量 | 用途 | 示例/默认值 |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | Spring Profile | 本地 `dev`；Docker 为 `prod` |
| `SPRING_DATASOURCE_URL` | JDBC URL | 本地 `localhost:3306/students_manager` |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 | 本地和 Docker 均为 `students_manager_app` |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 | 本地配置默认为 `xiaoer`；生产环境必须注入 |
| `SM4_KEY_BASE64` | 16 字节 SM4 密钥的 Base64 字符串 | 本地 dev 配置有仅供开发的默认值；生产必须设置 |
| `JWT_SECRET` | JWT 签名密钥，至少 32 个随机字节 | dev 有仅供本地使用的默认值；prod 必须注入，可用 `openssl rand -hex 32` 生成 |
| `JWT_EXPIRATION` | JWT 有效期，毫秒 | `1800000`（30 分钟） |

生产 Profile 将 `JWT_SECRET` 作为必填配置，未设置时应用不会启动。轮换该密钥会使所有已签发的 JWT 失效。开发 Profile 的静态兜底值只允许本地使用，生产环境必须通过密钥管理服务或容器环境变量注入。

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

注意：模板目前只覆盖本地 SM4 密钥；数据库账号仍来自 `application-dev.yml`，需要时可在 `.env.local` 中自行增加 `SPRING_DATASOURCE_*` 变量。开发 Profile 有独立的本地 JWT 兜底密钥。

## 本地启动

先确保 MySQL 已启动，并由 MySQL 管理员创建空数据库及仅有该库权限的应用账号（默认账号 `students_manager_app`，本地密码 `xiaoer`）。首次启动后 Flyway 会自动创建表和初始管理员：

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS students_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; CREATE USER IF NOT EXISTS 'students_manager_app'@'%' IDENTIFIED BY 'xiaoer'; ALTER USER 'students_manager_app'@'%' IDENTIFIED BY 'xiaoer'; GRANT ALL PRIVILEGES ON students_manager.* TO 'students_manager_app'@'%'"
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

项目尚未正式发布，目前保留两个迁移：V1 创建基础表结构和初始管理员；V2 将此前分散在多个版本中的预发布功能结构合并，包括选课规则、必修课程清单、学期管理和定时任务记录。这样，已经 baseline 到 V1 的开发库可以通过 V2 补齐结构并保留数据；全新数据库则依次运行 V1、V2。开发演示数据仍单独放在 `db/seed-dev.sql`。正式发布后，V1、V2 都应冻结，后续结构变更从 V3 开始。

Flyway 13 需要 Java 17，MySQL 支持通过单独的 `flyway-mysql` 模块提供。数据库迁移应先在独立的 MySQL 8.4 测试库验证，再用于生产；不要把 Maven 打包成功当成数据库兼容性验证。

### 本地开发

[`db/seed-dev.sql`](db/seed-dev.sql) 只包含本地开发演示数据，不创建数据库或表，也不重复插入管理员。表结构和初始管理员由 Flyway 创建。演示数据涵盖学院、专业、师生、课程排课、选课、培养方案及部分逐门必修清单、正常/补考/重修成绩、成绩变更历史、待审/已审学籍异动、操作审计和站内通知。该脚本仅执行一次。

如需演示数据，先创建空数据库并从 `backend/` 启动后端，让 Flyway 执行全部迁移；首次启动完成后按 Ctrl+C，导入演示数据，再次启动。后端启动时会加密演示用户的手机号和邮箱。

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS students_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; CREATE USER IF NOT EXISTS 'students_manager_app'@'%' IDENTIFIED BY 'xiaoer'; ALTER USER 'students_manager_app'@'%' IDENTIFIED BY 'xiaoer'; GRANT ALL PRIVILEGES ON students_manager.* TO 'students_manager_app'@'%'"
mvn spring-boot:run
# 首次启动完成后按 Ctrl+C，再执行：
mysql -u students_manager_app -p students_manager < db/seed-dev.sql
mvn spring-boot:run
```

不需要演示数据时，只创建数据库并启动后端即可；Flyway 会建表并创建初始管理员。

### Docker/生产初始化

Docker Compose 通过 `MYSQL_DATABASE` 创建空库，并由 MySQL 镜像创建应用数据库账号；后端启动后 Flyway 执行 `classpath:db/migration/V1__create_initial_schema.sql`，创建完整结构及初始管理员。迁移随后端 JAR 打包，不插入演示学生、教师、学院或课程，也不需要 Docker 专用的数据库初始化 SQL 挂载。

全新数据库不会预置教学学期。管理员登录后应先进入“学期管理”创建并启用学期，再将其中一个设为当前学期；课程和成绩共用该列表，创建教学班前需确保对应学期已启用。

#### 接入已有生产数据库

本次整理将原 V2–V5 合并为新的 V2，不修改 V1。只有 baseline 到 V1 的开发库会直接运行 V2，补齐缺失结构并保留已有业务数据。若数据库已经执行过旧 V2–V5，确认四个版本的结构都已完整应用后，先执行 Flyway `repair` 对齐迁移历史，再启动；`repair` 只修复迁移记录，不执行 SQL。若旧数据库未完整应用这些版本，不要直接 `repair`，应先核对结构后再决定补齐方案。不要对结构不一致或需要保留的数据直接执行 `repair`。

成绩管理支持正常考试、补考和重修，按学生、课程、学期和考试次数分别保存记录。教师可在“我的课程 → 学生与成绩”中查看已确认选课学生，支持跨页选择最多 200 名学生并批量提交成绩；后端在单个事务中校验课程归属、选课状态、成绩范围和重复考试次数，整批通过后才写入。教师提交的成绩默认为待发布，管理员可批量发布；学生端只展示已发布成绩。教师只能更正待发布成绩，已发布成绩由管理员处理。更正与删除要求填写原因，成绩详情保留操作人、时间、原因以及成绩和发布状态的变更历史。本地初始化的演示成绩为已发布状态。

系统包含学生学籍异动申请表。学生可申请休学、复学、转专业和退学，管理员审核并填写意见；已批准且到期的申请由每分钟执行的定时任务应用到学生档案，生效前不会提前修改专业或学籍状态。同时包含关键操作审计表和站内通知表。成绩发布、学籍异动新申请和审核结果会生成通知；课程新建、授课教师变更及授课信息/排课调整会通知相关教师。管理员可向全体教师、全体学生或两类用户发布站内通知，并指定通知打开的业务模块。通知中心支持分页、未读筛选、单条/批量已读和相关业务页面跳转；管理员、教师和学生侧栏会显示未读数。

若已有库的表、列与 V1 有差异，不要直接 baseline；应先备份并核对数据，再决定是否重建或编写兼容迁移。不要删除正式环境 Docker 数据卷来“升级”数据库。

## API 与权限概览

Swagger UI：<http://localhost:8080/swagger-ui/index.html>。受保护接口在 Swagger 的 **Authorize** 中填写 `Bearer <JWT>`。

| API 前缀 | 用途 | 主要授权 |
| --- | --- | --- |
| `/api/auth` | 登录、会话检查、当前账号、改密 | 登录公开；其余需认证；当前无服务端 logout API |
| `/api/students`、`/api/teachers` | 学生与教师管理档案 | 管理员 |
| `/api/colleges`、`/api/courses`、`/api/users` | 学院、课程与用户管理 | 管理员 |
| `/api/majors` | 专业查询与管理 | 查询需登录；创建、修改和删除仅管理员 |
| `/api/scores` | 管理端成绩查询、导入、修改、发布 | 管理员角色 |
| `/api/attendance` | 管理端、教师端、学生端考勤和统计 | 按角色分路径限制 |
| `/api/student-portal` | 开放课程查询、选课与退选、本人课程、成绩及成绩统计 | 学生角色；服务端按登录账号限定数据 |
| `/api/teacher-portal/courses` | 本人授课课程、已确认选课名单、课程成绩录入/更正/历史 | 教师角色；服务端按当前账号限定课程 |
| `/api/notifications` | 本人通知分页/未读数/已读操作；管理员通知群发 | 需登录；群发仅管理员 |
| `/api/admin/students` | 学生 CSV 批量导入 | 管理员 |
| `/api/health/check` | 服务健康检查 | 公开 |
| `/api/health/db` | 数据库连接检查 | 管理员 |

接口清单和字段以 Swagger、控制器 DTO/VO 为准。学生提交选课后进入待审核状态；待审核申请和已通过记录占用课程名额，候补申请不占名额，但会计入学期学分上限；学生可在课程退选截止前退选。名额满时，新申请进入候补队列；退选、拒绝申请或增加容量后，系统按申请时间递补为待审核，超过课程选课截止时间后停止递补。选课申请校验课程开放时间、选课范围、先修课程公开通过成绩、学籍状态、学期学分上限、重复申请及排课冲突。学分上限默认为每学期 30 学分，可通过 `COURSE_SELECTION_MAX_SEMESTER_CREDITS` 配置。管理员可为课程目录配置先修课程，并为教学班配置选课起止和退选截止时间。学生端会展示先修要求、缺少的先修课程、当前学期申请学分和选课时间窗。培养方案可配置逐门必修课，学生培养进度会按已发布成绩给出毕业条件预检查，不会自动修改学籍状态。学期由管理员集中维护，系统预置数据会从已有课程与成绩中回填学期编码。学业预警按学期检查最新公开成绩、平均分和缺勤情况；学籍异动生效任务每分钟运行，数据库命名锁避免多实例重复处理，管理员可查看运行结果和错误信息。操作审计页面将已知操作及业务对象代码转换为中文。

**权限边界：** 管理类学生、教师、学院、课程和用户接口仅管理员可用；学生门户和教师门户按角色隔离，门户数据由后端按当前账号限定；成绩、培养方案、学籍异动审核、审计和考勤接口有角色级授权。新增 Controller 或 API 时应明确配置角色规则，不能依赖前端隐藏菜单作为权限边界。

## 文件上传与 CSV

- 文件上传基础限制由 `application.yml` 中 `file.upload` 配置（默认最大 10 MB；JPEG、PNG、GIF）。
- 学生模板：[`../frontend/public/templates/student-import-template.csv`](../frontend/public/templates/student-import-template.csv)。导入前需创建学院、专业、年级和班级；整批校验通过后才写入。
- 成绩模板：[`../frontend/public/templates/score-import-template.csv`](../frontend/public/templates/score-import-template.csv)。中文表头为“学号、课程代码、成绩、学期、考试时间、评语”，同时兼容旧英文表头；考试时间格式为 `YYYY-MM-DDTHH:mm:ss`，成绩范围为 0–100。
- 学生批量导入和 `/api/scores/**` 管理成绩接口仅管理员可用；教师通过 `/api/teacher-portal/**` 操作本人课程成绩。

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

Flyway 只执行尚未成功的版本迁移。已有数据库接入前须按上文核对并 baseline；其它 schema 差异应在备份后新增向前迁移，不要为了升级直接删除数据卷。

## 相关文档

- [项目总览与快速开始](../README.md)
- [前端开发与构建](../frontend/README.md)
- [Docker 部署与运维](../docker/README.md)
