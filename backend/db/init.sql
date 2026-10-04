-- Canonical schema and seed data for a fresh, pre-release deployment.
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

-- 插入测试数据
-- 1. 插入管理员用户
INSERT INTO users (username, password, role, real_name, gender, phone, email, status, create_by, update_by) VALUES
('admin', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'ADMIN', '系统管理员', 1, '13800000000', 'admin@example.com', 0, 'system', 'system');

-- 2. 插入学院数据
INSERT INTO colleges (name, code, description, status, create_time, update_time, create_by, update_by) VALUES
('数学与计算机科学学院', 'CS', '数学与计算机科学学院描述', 0, NOW(), NOW(), 'system', 'system'),
('物理与电子信息学院', 'PH', '物理与电子信息学院描述', 0, NOW(), NOW(), 'system', 'system'),
('化学与材料科学学院', 'CH', '化学与材料科学学院描述', 0, NOW(), NOW(), 'system', 'system'),
('生命科学学院', 'BS', '生命科学学院描述', 0, NOW(), NOW(), 'system', 'system'),
('经济管理学院', 'BM', '经济管理学院描述', 0, NOW(), NOW(), 'system', 'system'),
('外国语学院', 'EN', '外国语学院描述', 0, NOW(), NOW(), 'system', 'system'),
('文学院', 'CL', '文学院描述', 0, NOW(), NOW(), 'system', 'system'),
('法学院', 'LW', '法学院描述', 0, NOW(), NOW(), 'system', 'system'),
('教育学院', 'ED', '教育学院描述', 0, NOW(), NOW(), 'system', 'system'),
('艺术学院', 'MU', '艺术学院描述', 0, NOW(), NOW(), 'system', 'system'),
('体育学院', 'PE', '体育学院描述', 0, NOW(), NOW(), 'system', 'system'),
('医学院', 'CM', '医学院描述', 0, NOW(), NOW(), 'system', 'system'),
('药学院', 'PM', '药学院描述', 0, NOW(), NOW(), 'system', 'system'),
('护理学院', 'NS', '护理学院描述', 0, NOW(), NOW(), 'system', 'system'),
('公共卫生学院', 'VM', '公共卫生学院描述', 0, NOW(), NOW(), 'system', 'system');

-- 3. 插入教师用户和信息（每个学院3个教师，共45个）
INSERT INTO users (username, password, role, real_name, gender, phone, email, status, create_by, update_by) VALUES
-- 数学与计算机科学学院教师
('T2024001', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '张教授', 1, '13811111001', 'teacher01@example.com', 0, 'system', 'system'),
('T2024002', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '李教授', 0, '13811111002', 'teacher02@example.com', 0, 'system', 'system'),
('T2024003', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '王教授', 1, '13811111003', 'teacher03@example.com', 0, 'system', 'system'),
-- 物理与电子信息学院教师
('T2024004', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '赵教授', 0, '13811111004', 'teacher04@example.com', 0, 'system', 'system'),
('T2024005', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '钱教授', 1, '13811111005', 'teacher05@example.com', 0, 'system', 'system'),
('T2024006', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '孙教授', 0, 'system', 'system'),
-- 化学与材料科学学院教师
('T2024007', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '周教授', 1, '13811111007', 'teacher07@example.com', 0, 'system', 'system'),
('T2024008', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '吴教授', 0, '13811111008', 'teacher08@example.com', 0, 'system', 'system'),
('T2024009', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '郑教授', 1, '13811111009', 'teacher09@example.com', 0, 'system', 'system'),
('T2024010', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '王教授', 0, '13811111010', 'teacher10@example.com', 0, 'system', 'system');

-- 插入教师详细信息
INSERT INTO teachers (user_id, teacher_number, title, department, hire_date, status, create_time, update_time, create_by, update_by)
SELECT id,
       username,
       CASE MOD(id, 4)
           WHEN 0 THEN '教授'
           WHEN 1 THEN '副教授'
           WHEN 2 THEN '讲师'
           WHEN 3 THEN '助教'
           END,
       CASE 
           WHEN id BETWEEN 1 AND 3 THEN '数学与计算机科学学院'
           WHEN id BETWEEN 4 AND 6 THEN '物理与电子信息学院'
           WHEN id BETWEEN 7 AND 10 THEN '化学与材料科学学院'
           END,
       DATE_SUB(CURRENT_DATE, INTERVAL FLOOR(RAND() * 3650) DAY),
       0, -- 状态：0-在职
       NOW(),
       NOW(),
       'system',
       'system'
FROM users
WHERE role = 'teacher'
ORDER BY id;

-- 插入专业数据
INSERT INTO majors (name, code, college_id, grade, class_no, head_teacher_id, status, create_time, update_time, create_by, update_by) VALUES
-- 数学与计算机科学学院
('计算机科学与技术', 'CS', 1, '2023', '01', 1, 0, NOW(), NOW(), 'system', 'system'),
('计算机科学与技术', 'CS', 1, '2023', '02', 2, 0, NOW(), NOW(), 'system', 'system'),
('软件工程', 'SE', 1, '2023', '01', 3, 0, NOW(), NOW(), 'system', 'system'),
('软件工程', 'SE', 1, '2023', '02', 1, 0, NOW(), NOW(), 'system', 'system'),
-- 物理与电子信息学院
('物理学', 'PH', 2, '2023', '01', 4, 0, NOW(), NOW(), 'system', 'system'),
('物理学', 'PH', 2, '2023', '02', 5, 0, NOW(), NOW(), 'system', 'system'),
('电子信息工程', 'EE', 2, '2023', '01', 6, 0, NOW(), NOW(), 'system', 'system'),
('电子信息工程', 'EE', 2, '2023', '02', 4, 0, NOW(), NOW(), 'system', 'system'),
-- 化学与材料科学学院
('化学', 'CH', 3, '2023', '01', 7, 0, NOW(), NOW(), 'system', 'system'),
('化学', 'CH', 3, '2023', '02', 8, 0, NOW(), NOW(), 'system', 'system'),
('材料科学与工程', 'MS', 3, '2023', '01', 9, 0, NOW(), NOW(), 'system', 'system'),
('材料科学与工程', 'MS', 3, '2023', '02', 7, 0, NOW(), NOW(), 'system', 'system'),
-- 生命科学学院
('生物科学', 'BS', 4, '2023', '01', 10, 0, NOW(), NOW(), 'system', 'system'),
('生物科学', 'BS', 4, '2023', '02', 11, 0, NOW(), NOW(), 'system', 'system'),
('生物工程', 'BE', 4, '2023', '01', 12, 0, NOW(), NOW(), 'system', 'system'),
('生物工程', 'BE', 4, '2023', '02', 10, 0, NOW(), NOW(), 'system', 'system'),
-- 经济管理学院
('工商管理', 'BM', 5, '2023', '01', 13, 0, NOW(), NOW(), 'system', 'system'),
('工商管理', 'BM', 5, '2023', '02', 14, 0, NOW(), NOW(), 'system', 'system'),
('市场营销', 'MK', 5, '2023', '01', 15, 0, NOW(), NOW(), 'system', 'system'),
('市场营销', 'MK', 5, '2023', '02', 13, 0, NOW(), NOW(), 'system', 'system'),
-- 外国语学院
('英语', 'EN', 6, '2023', '01', 16, 0, NOW(), NOW(), 'system', 'system'),
('英语', 'EN', 6, '2023', '02', 17, 0, NOW(), NOW(), 'system', 'system'),
('日语', 'JP', 6, '2023', '01', 18, 0, NOW(), NOW(), 'system', 'system'),
('日语', 'JP', 6, '2023', '02', 16, 0, NOW(), NOW(), 'system', 'system'),
-- 文学院
('汉语言文学', 'CL', 7, '2023', '01', 19, 0, NOW(), NOW(), 'system', 'system'),
('汉语言文学', 'CL', 7, '2023', '02', 20, 0, NOW(), NOW(), 'system', 'system'),
('新闻学', 'JN', 7, '2023', '01', 21, 0, NOW(), NOW(), 'system', 'system'),
('新闻学', 'JN', 7, '2023', '02', 19, 0, NOW(), NOW(), 'system', 'system'),
-- 法学院
('法学', 'LW', 8, '2023', '01', 22, 0, NOW(), NOW(), 'system', 'system'),
('法学', 'LW', 8, '2023', '02', 23, 0, NOW(), NOW(), 'system', 'system'),
('知识产权', 'IP', 8, '2023', '01', 24, 0, NOW(), NOW(), 'system', 'system'),
('知识产权', 'IP', 8, '2023', '02', 22, 0, NOW(), NOW(), 'system', 'system'),
-- 教育学院
('教育学', 'ED', 9, '2023', '01', 25, 0, NOW(), NOW(), 'system', 'system'),
('教育学', 'ED', 9, '2023', '02', 26, 0, NOW(), NOW(), 'system', 'system'),
('心理学', 'PS', 9, '2023', '01', 27, 0, NOW(), NOW(), 'system', 'system'),
('心理学', 'PS', 9, '2023', '02', 25, 0, NOW(), NOW(), 'system', 'system'),
-- 艺术学院
('音乐学', 'MU', 10, '2023', '01', 28, 0, NOW(), NOW(), 'system', 'system'),
('音乐学', 'MU', 10, '2023', '02', 29, 0, NOW(), NOW(), 'system', 'system'),
('美术学', 'AR', 10, '2023', '01', 30, 0, NOW(), NOW(), 'system', 'system'),
('美术学', 'AR', 10, '2023', '02', 28, 0, NOW(), NOW(), 'system', 'system'),
-- 体育学院
('体育教育', 'PE', 11, '2023', '01', 31, 0, NOW(), NOW(), 'system', 'system'),
('体育教育', 'PE', 11, '2023', '02', 32, 0, NOW(), NOW(), 'system', 'system'),
('运动训练', 'ST', 11, '2023', '01', 33, 0, NOW(), NOW(), 'system', 'system'),
('运动训练', 'ST', 11, '2023', '02', 31, 0, NOW(), NOW(), 'system', 'system'),
-- 医学院
('临床医学', 'CM', 12, '2023', '01', 34, 0, NOW(), NOW(), 'system', 'system'),
('临床医学', 'CM', 12, '2023', '02', 35, 0, NOW(), NOW(), 'system', 'system'),
('口腔医学', 'DM', 12, '2023', '01', 36, 0, NOW(), NOW(), 'system', 'system'),
('口腔医学', 'DM', 12, '2023', '02', 34, 0, NOW(), NOW(), 'system', 'system'),
-- 药学院
('药学', 'PM', 13, '2023', '01', 37, 0, NOW(), NOW(), 'system', 'system'),
('药学', 'PM', 13, '2023', '02', 38, 0, NOW(), NOW(), 'system', 'system'),
('中药学', 'TM', 13, '2023', '01', 39, 0, NOW(), NOW(), 'system', 'system'),
('中药学', 'TM', 13, '2023', '02', 37, 0, NOW(), NOW(), 'system', 'system'),
-- 护理学院
('护理学', 'NS', 14, '2023', '01', 40, 0, NOW(), NOW(), 'system', 'system'),
('护理学', 'NS', 14, '2023', '02', 41, 0, NOW(), NOW(), 'system', 'system'),
('助产学', 'MW', 14, '2023', '01', 42, 0, NOW(), NOW(), 'system', 'system'),
('助产学', 'MW', 14, '2023', '02', 40, 0, NOW(), NOW(), 'system', 'system'),
-- 公共卫生学院
('预防医学', 'VM', 15, '2023', '01', 43, 0, NOW(), NOW(), 'system', 'system'),
('预防医学', 'VM', 15, '2023', '02', 44, 0, NOW(), NOW(), 'system', 'system'),
('卫生检验与检疫', 'HI', 15, '2023', '01', 45, 0, NOW(), NOW(), 'system', 'system'),
('卫生检验与检疫', 'HI', 15, '2023', '02', 43, 0, NOW(), NOW(), 'system', 'system');

-- 插入学生用户数据（每个班级20人，包含不同状态）
INSERT INTO users (username, password, role, real_name, gender, phone, email, status, create_by, update_by)
SELECT 
    CONCAT(m.code, m.grade, m.class_no, LPAD(n.n, 2, '0')),
    '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA',
    'student',
    CONCAT('学生', LPAD(n.n, 2, '0')),
    CASE WHEN RAND() > 0.5 THEN 1 ELSE 0 END,
    CONCAT('138', LPAD(FLOOR(RAND() * 100000000), 8, '0')),
    CONCAT('student', LPAD(n.n, 2, '0'), '@example.com'),
    0, -- 状态：0-正常
    'system',
    'system'
FROM majors m
CROSS JOIN (
    SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
    UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
    UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
) n
WHERE m.status = 0; -- 状态：0-正常

-- 插入学生详细信息
INSERT INTO students (user_id, student_no, birth_date, admission_date, address, major_code, grade, class_no, status, create_by, update_by)
SELECT 
    u.id,
    u.username,
    DATE_SUB(CURRENT_DATE, INTERVAL FLOOR(RAND() * 3650) DAY),
    '2023-09-01',
    CONCAT('学生', SUBSTRING(u.username, -2), '家庭住址'),
    SUBSTRING(u.username, 1, 2),
    SUBSTRING(u.username, 3, 4),
    SUBSTRING(u.username, 7, 2),
    CASE 
        WHEN MOD(SUBSTRING(u.username, -2), 4) = 0 THEN 0 -- 在读
        WHEN MOD(SUBSTRING(u.username, -2), 4) = 1 THEN 1 -- 休学
        WHEN MOD(SUBSTRING(u.username, -2), 4) = 2 THEN 2 -- 退学
        WHEN MOD(SUBSTRING(u.username, -2), 4) = 3 THEN 3 -- 毕业
    END,
    'system',
    'system'
FROM users u
WHERE u.role = 'student';

-- 5. 插入课程数据
INSERT INTO courses (course_name, course_code, teacher_id, credits, description, create_time, update_time, create_by, update_by)
VALUES ('Java程序设计', 'CS101', 1, 4, 'Java语言基础与面向对象程序设计', NOW(), NOW(), 'system', 'system'),
       ('数据结构', 'CS102', 2, 4, '数据结构与算法基础', NOW(), NOW(), 'system', 'system'),
       ('数据库原理', 'CS103', 3, 4, '数据库系统原理与应用', NOW(), NOW(), 'system', 'system'),
       ('操作系统', 'CS104', 4, 4, '操作系统原理与实践', NOW(), NOW(), 'system', 'system'),
       ('计算机网络', 'CS105', 5, 4, '计算机网络基础与应用', NOW(), NOW(), 'system', 'system'),
       ('软件工程', 'SE101', 6, 3, '软件开发方法与项目管理', NOW(), NOW(), 'system', 'system'),
       ('人工智能导论', 'AI101', 7, 3, '人工智能基础理论与应用', NOW(), NOW(), 'system', 'system'),
       ('机器学习', 'AI102', 8, 4, '机器学习算法与实践', NOW(), NOW(), 'system', 'system');

-- 6. 插入选课数据
INSERT INTO course_selections (student_id, course_id, status, create_time, update_time, create_by, update_by)
SELECT s.id,
       c.id,
       CASE FLOOR(RAND() * 3)
           WHEN 0 THEN 'pending'
           WHEN 1 THEN 'approved'
           WHEN 2 THEN 'rejected'
           END,
       NOW(),
       NOW(),
       'system',
       'system'
FROM students s
         CROSS JOIN courses c
WHERE RAND() < 0.3; -- 30%的概率选课

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
