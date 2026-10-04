# 后端

本目录包含学生管理系统的 Spring Boot 服务、Maven 配置和数据库初始化 SQL。

## 技术栈

- Java 11、Spring Boot 2.7、Maven
- Spring Security、JWT
- MyBatis-Plus、MySQL 8.4
- SM3 口令哈希、SM4-GCM 联系方式字段加密

JWT 登录凭证默认有效期为 30 分钟（`JWT_EXPIRATION=1800000`，单位毫秒）。本地启动可通过环境变量覆盖；Docker Compose 可在 `docker/.env` 中调整。每次后端重启也会使之前签发的 token 失效，需要重新登录。

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

[`db/init.sql`](db/init.sql) 是本地开发唯一的数据库初始化脚本，包含完整表结构及管理员、教师、学生、课程、少量成绩和考勤演示数据。成绩样例覆盖前 10 名学生和 3 门示例课程，最多 30 条；考勤样例为前 20 名学生、3 门课程近 14 天的记录，包含正常、迟到、早退、缺勤和请假状态。生产 Docker 使用 [`../docker/db/init.sql`](../docker/db/init.sql)，创建数据库表并初始化 `admin` 管理员，不插入演示学生等业务数据。初始密码为 `xiaoer`，首次登录必须修改。两个初始化脚本都只在新数据库初始化时执行；已有数据库不会自动重建或升级。

考勤管理支持按学号、姓名、课程、班级、学期、日期和状态分页筛选；管理员可新增、编辑和软删除考勤记录，教师只能查询自己所授课程的记录，学生只能查询自己的记录。首页出勤率和趋势基于数据库中的近 7 天或近 30 天数据计算，请假记录不计入出勤率分母。

课程与成绩管理现已提供分页查询、筛选、详情、新增、编辑和软删除。成绩支持 CSV 导入、导出；导入列名为 `studentNo,courseCode,score,semester,examTime,comment`，考试时间使用 `YYYY-MM-DDTHH:mm:ss` 格式。

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
