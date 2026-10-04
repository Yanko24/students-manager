-- Production schema and bootstrap administrator for a fresh deployment.
-- Docker MySQL runs this file only when initializing an empty data directory.
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS students_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE students_manager;

-- 设置数据库连接的字符集
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;
SET character_set_server = utf8mb4;
SET collation_connection = utf8mb4_unicode_ci;
SET collation_server = utf8mb4_unicode_ci;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username   VARCHAR(50)                          NOT NULL UNIQUE COMMENT '用户名',
    password   VARCHAR(255)                         NOT NULL COMMENT '密码',
    role       ENUM ('admin', 'teacher', 'student') NOT NULL COMMENT '角色：管理员、教师、学生',
    real_name  VARCHAR(50)                          NOT NULL COMMENT '真实姓名',
    gender     TINYINT                              NOT NULL COMMENT '性别：1-男，0-女',
    phone      VARCHAR(255)                         NOT NULL COMMENT 'SM4-GCM加密后的联系电话',
    email      VARCHAR(512)                         NOT NULL COMMENT 'SM4-GCM加密后的电子邮箱',
    status     TINYINT                              NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-禁用',
    must_change_password TINYINT                     NOT NULL DEFAULT 1 COMMENT '是否首次登录后必须修改密码',
    create_time DATETIME                             NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by  VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    update_by  VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT                              NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

-- 创建学院表
CREATE TABLE IF NOT EXISTS colleges
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name       VARCHAR(50) NOT NULL COMMENT '学院名称',
    code       VARCHAR(20) NOT NULL COMMENT '学院代码',
    description TEXT COMMENT '学院描述',
    status     TINYINT     NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-停办',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by  VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    update_by  VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='学院表';

-- 创建专业表（包含班级信息）
CREATE TABLE IF NOT EXISTS majors
(
    code       VARCHAR(20) NOT NULL COMMENT '专业代码',
    grade      VARCHAR(4)  NOT NULL COMMENT '年级',
    class_no   VARCHAR(2)  NOT NULL COMMENT '班级号',
    name       VARCHAR(50) NOT NULL COMMENT '专业名称',
    college_id BIGINT      NOT NULL COMMENT '所属学院ID',
    head_teacher_id BIGINT COMMENT '班主任ID',
    status     TINYINT     NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-停招，2-撤销',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by  VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    update_by  VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (code, grade, class_no),
    FOREIGN KEY (college_id) REFERENCES colleges(id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='专业表';

-- 创建教师信息表
CREATE TABLE IF NOT EXISTS teachers
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id        BIGINT      NOT NULL COMMENT '用户ID',
    teacher_number VARCHAR(20) NOT NULL UNIQUE COMMENT '教师工号',
    title          VARCHAR(50) COMMENT '职称',
    department     VARCHAR(50) COMMENT '所属院系',
    hire_date      DATE COMMENT '入职日期',
    status         TINYINT     NOT NULL DEFAULT 0 COMMENT '状态：0-在职，1-离职，2-退休',
    create_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by      VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    update_by      VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    is_deleted     TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='教师信息表';

-- 创建学生信息表
CREATE TABLE IF NOT EXISTS students
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id        BIGINT      NOT NULL COMMENT '用户ID',
    student_no     VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    birth_date     DATE        NOT NULL COMMENT '出生日期',
    admission_date DATE        NOT NULL COMMENT '入学日期',
    address        VARCHAR(200) COMMENT '家庭住址',
    major_code     VARCHAR(20) NOT NULL COMMENT '专业代码',
    grade          VARCHAR(4)  NOT NULL COMMENT '年级',
    class_no       VARCHAR(2)  NOT NULL COMMENT '班级号',
    status         TINYINT     NOT NULL DEFAULT 0 COMMENT '状态：0-在读，1-休学，2-退学，3-毕业',
    create_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by      VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    update_by      VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    is_deleted     TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (major_code, grade, class_no) REFERENCES majors (code, grade, class_no)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='学生信息表';

-- 创建课程表
CREATE TABLE IF NOT EXISTS courses
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(20)  NOT NULL UNIQUE COMMENT '课程代码',
    teacher_id  BIGINT COMMENT '授课教师ID',
    credits     INT          NOT NULL COMMENT '学分',
    description TEXT COMMENT '课程描述',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by   VARCHAR(50)  DEFAULT NULL COMMENT '创建人',
    update_by   VARCHAR(50)  DEFAULT NULL COMMENT '更新人',
    is_deleted  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    FOREIGN KEY (teacher_id) REFERENCES teachers (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='课程表';

-- 创建选课表
CREATE TABLE IF NOT EXISTS course_selections
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_id     BIGINT   NOT NULL COMMENT '学生ID',
    course_id      BIGINT   NOT NULL COMMENT '课程ID',
    selection_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    status         ENUM ('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '状态：待审核、已通过、已拒绝',
    create_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by      VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    update_by      VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    is_deleted     TINYINT  NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    FOREIGN KEY (student_id) REFERENCES students (id),
    FOREIGN KEY (course_id) REFERENCES courses (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='选课表';

-- 首次登录管理员：默认密码 xiaoer，登录后必须立即修改。
-- 口令为 PBKDF2-HMAC-SM3 哈希；联系方式留空，应用启动时会按 SM4-GCM 规则加密。
INSERT IGNORE INTO users
    (username, password, role, real_name, gender, phone, email, status, must_change_password, create_by, update_by)
VALUES
    ('admin', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA',
     'admin', '系统管理员', 1, '', '', 0, 1, 'bootstrap', 'bootstrap');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
