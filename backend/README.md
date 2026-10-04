# 后端

本目录包含学生管理系统的 Spring Boot 服务、Maven 配置和数据库初始化 SQL。

## 技术栈

- Java 11、Spring Boot 2.7、Maven
- Spring Security、JWT
- MyBatis-Plus、MySQL 8.4
- SM3 口令哈希、SM4-GCM 联系方式字段加密

## 构建

```bash
cd backend
mvn clean package -DskipTests
```

生成的 JAR 位于 `target/`。直接启动后端时，需要先有可访问的 MySQL 数据库，并配置数据库连接和 `SM4_KEY_BASE64`：

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/students_manager?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8'
export SPRING_DATASOURCE_USERNAME='your-user'
export SPRING_DATASOURCE_PASSWORD='your-password'
export SM4_KEY_BASE64='your-base64-encoded-16-byte-key'
java -jar target/students-manager-1.0-SNAPSHOT.jar
```

服务默认监听 8080 端口。Docker 部署时由 Compose 提供数据库地址和密钥，参见 [Docker 部署说明](../docker/README.md)。

### 本地开发启动

本地开发默认 SM4 密钥已写入 `application-dev.yml`，因此直接启动 `dev` profile 即可，不依赖 Docker 配置，也不会启动 Nginx。也可以在 `backend/.env.local` 覆盖本地配置；首次使用时，在项目根目录执行：

```bash
cp backend/.env.local.example backend/.env.local
```

`application-dev.yml` 内的默认密钥与此前本地数据库使用的密钥一致。此默认密钥仅供本地开发，不能用于生产环境。密钥必须保持稳定；更换或丢失密钥会导致已有加密联系方式无法解密。`backend/.env.local.example` 供需要覆盖本地配置时使用，不得用于生产。

在 `backend/` 目录直接启动：

```bash
set -a
source .env.local
set +a
mvn spring-boot:run
```

`application-dev.yml` 默认连接本机 `localhost:3306` 的 `students_manager` 数据库，凭据为 `root` / `xiaoer`；若本机 MySQL 凭据不同，请相应修改该配置。前端开发服务器运行于 `http://localhost:3000`，由 Vite 将 `/api` 请求代理到本地后端 `http://localhost:8080`，不需要 Nginx。

## 数据库

[`db/init.sql`](db/init.sql) 用于本地开发，包含数据库结构及管理员、教师、学生等演示数据。生产 Docker 使用 [`../docker/db/init.sql`](../docker/db/init.sql)，创建数据库表并初始化 `admin` 管理员，不插入演示学生等业务数据。初始密码为 `xiaoer`，首次登录必须修改。首次发布前暂不维护版本升级迁移脚本；Docker 只在 MySQL 数据目录为空时运行其生产初始化脚本，已有数据库不会自动重建或升级。

管理员可在学生管理页面下载 CSV 模板批量导入学生。导入前需先建好匹配的专业、年级和班级。文件先整体校验，任何行不合格都会取消整个导入；通过后一次性写入。CSV 列和格式见 `frontend/public/templates/student-import-template.csv`。

## 密码与敏感字段

- 初始化用户的默认密码是 `xiaoer`，数据库仅保存 PBKDF2-HMAC-SM3 口令哈希，不保存明文。用户首次登录必须设置新密码。
- 手机号和邮箱使用 SM4-GCM 加密；密钥通过 `SM4_KEY_BASE64` 注入，需妥善备份并与数据库分开保管。
- 目前其他个人信息字段及数据库文件没有字段级/磁盘加密；数据库连接也未默认启用 TLS。

## 相关目录

```text
backend/
├── db/init.sql       # 本地开发初始化结构和演示数据
├── src/main/java/    # 后端源码
├── src/main/resources/# 应用配置及 MyBatis 映射
└── pom.xml           # Maven 构建配置
```
