-- Local development demo records only.
-- Run once after Flyway has created the schema and bootstrap administrator.
-- This script does not create databases or tables.
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 插入学院演示数据
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

-- 插入教师用户和信息（每个学院3个教师，共45个）
INSERT INTO users (username, password, role, real_name, gender, phone, email, status, create_by, update_by) VALUES
-- 数学与计算机科学学院教师
('T2024001', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '张教授', 1, '13811111001', 'teacher01@example.com', 0, 'system', 'system'),
('T2024002', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '李教授', 0, '13811111002', 'teacher02@example.com', 0, 'system', 'system'),
('T2024003', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '王教授', 1, '13811111003', 'teacher03@example.com', 0, 'system', 'system'),
-- 物理与电子信息学院教师
('T2024004', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '赵教授', 0, '13811111004', 'teacher04@example.com', 0, 'system', 'system'),
('T2024005', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '钱教授', 1, '13811111005', 'teacher05@example.com', 0, 'system', 'system'),
('T2024006', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '孙教授', 0, '13811111006', 'teacher06@example.com', 0, 'system', 'system'),
-- 化学与材料科学学院教师
('T2024007', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '周教授', 1, '13811111007', 'teacher07@example.com', 0, 'system', 'system'),
('T2024008', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '吴教授', 0, '13811111008', 'teacher08@example.com', 0, 'system', 'system'),
('T2024009', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '郑教授', 1, '13811111009', 'teacher09@example.com', 0, 'system', 'system'),
('T2024010', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA', 'teacher', '王教授', 0, '13811111010', 'teacher10@example.com', 0, 'system', 'system');

-- 补齐其余学院的教师账号，确保下面 45 个班主任编号都有对应教师记录。
INSERT INTO users (username, password, role, real_name, gender, phone, email, status, create_by, update_by)
SELECT CONCAT('T2024', LPAD(numbers.teacher_no, 3, '0')),
       '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA',
       'teacher', CONCAT('教师', LPAD(numbers.teacher_no, 2, '0')),
       MOD(numbers.teacher_no, 2),
       CONCAT('138', LPAD(11111000 + numbers.teacher_no, 8, '0')),
       CONCAT('teacher', LPAD(numbers.teacher_no, 2, '0'), '@example.com'),
       0, 'system', 'system'
FROM (
    SELECT tens.digit * 10 + ones.digit AS teacher_no
    FROM (SELECT 1 AS digit UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) tens
    CROSS JOIN (SELECT 0 AS digit UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) ones
    WHERE tens.digit * 10 + ones.digit BETWEEN 11 AND 45
) numbers;

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
           WHEN username BETWEEN 'T2024001' AND 'T2024003' THEN '数学与计算机科学学院'
           WHEN username BETWEEN 'T2024004' AND 'T2024006' THEN '物理与电子信息学院'
           WHEN username BETWEEN 'T2024007' AND 'T2024009' THEN '化学与材料科学学院'
           WHEN username BETWEEN 'T2024010' AND 'T2024012' THEN '生命科学学院'
           WHEN username BETWEEN 'T2024013' AND 'T2024015' THEN '经济管理学院'
           WHEN username BETWEEN 'T2024016' AND 'T2024018' THEN '外国语学院'
           WHEN username BETWEEN 'T2024019' AND 'T2024021' THEN '文学院'
           WHEN username BETWEEN 'T2024022' AND 'T2024024' THEN '法学院'
           WHEN username BETWEEN 'T2024025' AND 'T2024027' THEN '教育学院'
           WHEN username BETWEEN 'T2024028' AND 'T2024030' THEN '艺术学院'
           WHEN username BETWEEN 'T2024031' AND 'T2024033' THEN '体育学院'
           WHEN username BETWEEN 'T2024034' AND 'T2024036' THEN '医学院'
           WHEN username BETWEEN 'T2024037' AND 'T2024039' THEN '药学院'
           WHEN username BETWEEN 'T2024040' AND 'T2024042' THEN '护理学院'
           WHEN username BETWEEN 'T2024043' AND 'T2024045' THEN '公共卫生学院'
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

-- 插入课程数据
INSERT INTO courses (course_name, course_code, teacher_id, credits, description, create_time, update_time, create_by, update_by)
VALUES ('Java程序设计', 'CS101', 1, 4, 'Java语言基础与面向对象程序设计', NOW(), NOW(), 'system', 'system'),
       ('数据结构', 'CS102', 2, 4, '数据结构与算法基础', NOW(), NOW(), 'system', 'system'),
       ('数据库原理', 'CS103', 3, 4, '数据库系统原理与应用', NOW(), NOW(), 'system', 'system'),
       ('操作系统', 'CS104', 4, 4, '操作系统原理与实践', NOW(), NOW(), 'system', 'system'),
       ('计算机网络', 'CS105', 5, 4, '计算机网络基础与应用', NOW(), NOW(), 'system', 'system'),
       ('软件工程', 'SE101', 6, 3, '软件开发方法与项目管理', NOW(), NOW(), 'system', 'system'),
       ('人工智能导论', 'AI101', 7, 3, '人工智能基础理论与应用', NOW(), NOW(), 'system', 'system'),
       ('机器学习', 'AI102', 8, 4, '机器学习算法与实践', NOW(), NOW(), 'system', 'system');

INSERT INTO courses (course_name, course_code, teacher_id, credits, course_type, semester, hours, status,
                     selection_open, max_students, selection_scope, selection_college_id, selection_major_code,
                     selection_grade, description, create_time, update_time, create_by, update_by)
VALUES ('大学心理健康', 'GE201', 1, 2, '公共课', '2026-2027-1', 32, 1, 1, 200, 'ALL', NULL, NULL, '2023',
        '面向全校2023级学生开放的心理健康通识选修课。', NOW(), NOW(), 'eligibility-demo', 'eligibility-demo'),
       ('中国传统文化概论', 'GE202', 2, 2, '选修课', '2026-2027-1', 32, 1, 1, 150, 'ALL', NULL, NULL, '2023',
        '面向全校2023级学生开放的传统文化选修课。', NOW(), NOW(), 'eligibility-demo', 'eligibility-demo'),
       ('数据可视化', 'CS206', 3, 3, '选修课', '2026-2027-1', 48, 1, 1, 60, 'COLLEGE', 1, NULL, '2023',
        '面向数学与计算机科学学院2023级学生开放。', NOW(), NOW(), 'eligibility-demo', 'eligibility-demo'),
       ('机器学习实践', 'CS207', 4, 3, '选修课', '2026-2027-1', 48, 1, 1, 50, 'MAJOR', 1, 'CS', '2023',
        '面向计算机科学与技术专业2023级学生开放。', NOW(), NOW(), 'eligibility-demo', 'eligibility-demo');

INSERT INTO course_catalog (course_code, course_name, credits, course_type, hours, description, objectives)
SELECT course_code, MAX(course_name), MAX(credits), MAX(course_type), MAX(hours), MAX(description), MAX(objectives)
FROM courses WHERE is_deleted = 0 GROUP BY course_code;

UPDATE courses c JOIN course_catalog catalog ON catalog.course_code = c.course_code
SET c.catalog_id = catalog.id WHERE c.catalog_id IS NULL;

-- 新学期集中维护；演示课程与成绩共用统一学期记录。
INSERT IGNORE INTO academic_terms (term_code, academic_year, term_no, is_active, is_current)
SELECT DISTINCT c.semester, SUBSTRING_INDEX(c.semester, '-', 2),
       CAST(SUBSTRING_INDEX(c.semester, '-', -1) AS UNSIGNED), 1, 0
FROM courses c WHERE c.semester REGEXP '^[0-9]{4}-[0-9]{4}-[12]$';
UPDATE academic_terms SET is_current = 0;
UPDATE academic_terms SET is_current = 1, is_active = 1 WHERE term_code = '2026-2027-1';

-- 选课规则演示：数据结构要求已通过 Java 程序设计；机器学习要求已通过数据结构和人工智能导论。
INSERT INTO course_prerequisites (course_catalog_id, prerequisite_catalog_id)
SELECT target.id, required.id
FROM course_catalog target
JOIN course_catalog required ON (target.course_code = 'CS102' AND required.course_code = 'CS101')
    OR (target.course_code = 'AI102' AND required.course_code IN ('CS102', 'AI101'));

-- 演示选课/退选时间窗；从脚本执行时刻起开放 30 天，退选多开放 5 天。
UPDATE courses
SET selection_start_at = DATE_SUB(NOW(), INTERVAL 1 DAY),
    selection_end_at = DATE_ADD(NOW(), INTERVAL 30 DAY),
    drop_deadline_at = DATE_ADD(NOW(), INTERVAL 35 DAY)
WHERE course_code IN ('CS206', 'CS207');

-- 课程排课演示时段：同一学期内不冲突，便于本地查看课表并验证冲突校验。
INSERT INTO course_schedules (course_id, day_of_week, start_period, end_period, week_start, week_end, week_parity, classroom)
SELECT c.id, seed.day_of_week, seed.start_period, seed.end_period, 1, 16, 'ALL', seed.classroom
FROM (
    SELECT 'CS101' AS course_code, 1 AS day_of_week, 1 AS start_period, 2 AS end_period, 'A101' AS classroom UNION ALL
    SELECT 'CS102', 2, 3, 4, 'A102' UNION ALL
    SELECT 'CS103', 3, 5, 6, 'A103' UNION ALL
    SELECT 'CS104', 4, 1, 2, 'A104' UNION ALL
    SELECT 'CS105', 5, 3, 4, 'A105' UNION ALL
    SELECT 'GE201', 2, 5, 6, 'B201' UNION ALL
    SELECT 'GE202', 4, 3, 4, 'B202' UNION ALL
    SELECT 'CS206', 5, 1, 2, 'C206' UNION ALL
    SELECT 'CS207', 3, 1, 2, 'C207'
) seed
JOIN courses c ON c.course_code = seed.course_code AND c.is_deleted = 0
WHERE NOT EXISTS (
    SELECT 1 FROM course_schedules existing
    WHERE existing.course_id = c.id AND existing.day_of_week = seed.day_of_week
      AND existing.start_period = seed.start_period AND existing.classroom = seed.classroom
);

-- 初始化少量成绩样例：前10名学生的3门示例课程，共最多30条；重复执行不会重复插入。
INSERT IGNORE INTO scores (student_id, course_id, score, grade, grade_point, semester, exam_time, remarks, create_time, update_time, create_by, update_by)
SELECT seed.student_id,
       seed.course_id,
       seed.score,
       CASE WHEN seed.score >= 90 THEN 'A+'
            WHEN seed.score >= 85 THEN 'A'
            WHEN seed.score >= 80 THEN 'B+'
            WHEN seed.score >= 75 THEN 'B'
            WHEN seed.score >= 70 THEN 'C+'
            WHEN seed.score >= 60 THEN 'C'
            WHEN seed.score >= 50 THEN 'D' ELSE 'F' END,
       CASE WHEN seed.score >= 90 THEN 4.00
            WHEN seed.score >= 85 THEN 3.70
            WHEN seed.score >= 82 THEN 3.30
            WHEN seed.score >= 78 THEN 3.00
            WHEN seed.score >= 75 THEN 2.70
            WHEN seed.score >= 72 THEN 2.30
            WHEN seed.score >= 68 THEN 2.00
            WHEN seed.score >= 64 THEN 1.50
            WHEN seed.score >= 60 THEN 1.00 ELSE 0.00 END,
       '2026-2027-1', DATE_SUB(NOW(), INTERVAL 1 DAY), '本地开发初始化演示成绩', NOW(), NOW(), 'system', 'system'
FROM (
    SELECT s.id AS student_id, c.id AS course_id,
           60 + MOD(s.id + c.id * 3, 41) AS score
    FROM (SELECT id FROM students WHERE is_deleted = 0 ORDER BY id LIMIT 10) s
    CROSS JOIN (SELECT id FROM courses WHERE course_code IN ('CS101', 'CS102', 'CS103') AND is_deleted = 0) c
) seed;

-- 插入最近14天考勤演示记录：前20名学生、3门课程，状态按固定规则分布，重复执行不会重复插入。
INSERT INTO attendance_records
    (student_id, course_id, attendance_date, class_period, status, remark, create_by, update_by)
SELECT s.id,
       c.id,
       DATE_SUB(CURRENT_DATE, INTERVAL day_offset DAY),
       CASE MOD(c.id, 3) WHEN 0 THEN '第1-2节' WHEN 1 THEN '第3-4节' ELSE '第5-6节' END,
       CASE MOD(s.id + c.id + day_offset, 20)
           WHEN 0 THEN '缺勤'
           WHEN 1 THEN '迟到'
           WHEN 2 THEN '早退'
           WHEN 3 THEN '请假'
           ELSE '正常'
       END,
       CASE MOD(s.id + c.id + day_offset, 20)
           WHEN 0 THEN '演示缺勤记录'
           WHEN 1 THEN '演示迟到记录'
           WHEN 2 THEN '演示早退记录'
           WHEN 3 THEN '演示请假记录'
           ELSE NULL
       END,
       'system', 'system'
FROM (SELECT id FROM students WHERE is_deleted = 0 ORDER BY id LIMIT 20) s
CROSS JOIN (SELECT id FROM courses WHERE course_code IN ('CS101', 'CS102', 'CS103') AND is_deleted = 0) c
CROSS JOIN (
    SELECT 0 AS day_offset UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3
    UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7
    UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11
    UNION ALL SELECT 12 UNION ALL SELECT 13
) days
WHERE NOT EXISTS (
    SELECT 1 FROM attendance_records existing
    WHERE existing.student_id = s.id AND existing.course_id = c.id
      AND existing.attendance_date = DATE_SUB(CURRENT_DATE, INTERVAL day_offset DAY)
      AND existing.class_period = CASE MOD(c.id, 3) WHEN 0 THEN '第1-2节' WHEN 1 THEN '第3-4节' ELSE '第5-6节' END
      AND existing.is_deleted = 0
);

-- 插入少量、确定性的选课演示数据，避免初始化后超过默认选课容量


-- 新增业务模块演示数据
-- 为每个有在校生的专业年级生成培养方案
INSERT INTO curriculum_plans
    (plan_name, major_code, grade, total_credits, required_credits, elective_credits, create_by, update_by)
SELECT CONCAT(MIN(m.name), m.grade, '级培养方案'), m.code, m.grade, 160.0, 128.0, 32.0, 'seed-dev', 'seed-dev'
FROM majors m
JOIN students s ON s.major_code = m.code AND s.grade = m.grade AND s.class_no = m.class_no
WHERE m.status = 0 AND m.is_deleted = 0 AND s.is_deleted = 0
GROUP BY m.code, m.grade;

-- 为计算机类培养方案配置逐门必修课程，便于演示毕业资格预检查。
INSERT INTO curriculum_plan_courses (plan_id, course_catalog_id)
SELECT plans.id, catalog.id
FROM curriculum_plans plans
JOIN course_catalog catalog ON catalog.course_code IN ('CS101', 'CS102', 'CS103')
WHERE plans.major_code = 'CS';

-- 给已有正常考试成绩增加补考与待发布重修记录
INSERT INTO scores
    (student_id, course_id, score, grade, grade_point, semester, attempt_type, attempt_no,
     publish_status, exam_time, remarks, create_by, update_by)
SELECT student_id, course_id, 82.00, 'B', 3.00, semester, 'MAKEUP', 2, 'PUBLISHED',
       DATE_ADD(exam_time, INTERVAL 7 DAY), '演示补考成绩', 'seed-dev', 'seed-dev'
FROM scores WHERE id = 1;

INSERT INTO scores
    (student_id, course_id, score, grade, grade_point, semester, attempt_type, attempt_no,
     publish_status, exam_time, remarks, create_by, update_by)
SELECT student_id, course_id, 88.00, 'A', 4.00, semester, 'RETAKE', 3, 'DRAFT',
       DATE_ADD(exam_time, INTERVAL 14 DAY), '演示重修成绩，待管理员发布', 'seed-dev', 'seed-dev'
FROM scores WHERE id = 1;

-- 成绩变更历史关联到实际成绩记录
INSERT INTO score_change_logs
    (score_id, action, old_score, new_score, old_attempt_type, new_attempt_type,
     old_publish_status, new_publish_status, reason, operator_name, create_time)
SELECT id, 'UPDATE', 68.00, score, attempt_type, attempt_type, publish_status, publish_status,
       '演示：复核后更正成绩', 'admin', DATE_SUB(NOW(), INTERVAL 3 DAY)
FROM scores WHERE id = 1;

INSERT INTO score_change_logs
    (score_id, action, old_score, new_score, old_attempt_type, new_attempt_type,
     old_publish_status, new_publish_status, reason, operator_name, create_time)
SELECT id, 'PUBLISH', score, score, attempt_type, attempt_type, 'DRAFT', 'PUBLISHED',
       '演示：审核并发布成绩', 'admin', DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM scores WHERE id = 2;

INSERT INTO score_change_logs
    (score_id, action, old_score, new_score, old_attempt_type, new_attempt_type,
     old_publish_status, new_publish_status, reason, operator_name, create_time)
SELECT id, 'CREATE', NULL, score, NULL, attempt_type, NULL, publish_status,
       '演示：录入正常考试成绩', 'admin', DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM scores WHERE id = 3;

INSERT INTO score_change_logs
    (score_id, action, old_score, new_score, old_attempt_type, new_attempt_type,
     old_publish_status, new_publish_status, reason, operator_name, create_time)
SELECT id, 'CREATE', NULL, score, NULL, attempt_type, NULL, publish_status,
       '演示：录入补考成绩', 'admin', DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM scores WHERE attempt_type = 'MAKEUP' AND attempt_no = 2;

INSERT INTO score_change_logs
    (score_id, action, old_score, new_score, old_attempt_type, new_attempt_type,
     old_publish_status, new_publish_status, reason, operator_name, create_time)
SELECT id, 'CREATE', NULL, score, NULL, attempt_type, NULL, publish_status,
       '演示：录入重修成绩，等待发布', 'admin', NOW()
FROM scores WHERE attempt_type = 'RETAKE' AND attempt_no = 3;

-- 学籍异动演示：待审核、已批准（未来生效）和已驳回
INSERT INTO student_status_change_requests
    (student_id, change_type, current_status, target_status, effective_date, reason, status, create_time, update_time)
SELECT id, 'SUSPENSION', 0, 1, '2027-09-01', '演示：因个人原因申请休学', 'PENDING', NOW(), NOW()
FROM students WHERE student_no = 'CS20230108';

INSERT INTO student_status_change_requests
    (student_id, change_type, current_status, target_status, target_major_code, target_class_no,
     effective_date, reason, status, review_comment, reviewed_by, reviewed_at, create_time, update_time)
SELECT id, 'MAJOR_TRANSFER', 0, 0, 'SE', '01', '2027-09-01', '演示：申请转入软件工程专业',
       'APPROVED', '材料齐全，同意于生效日办理', 'admin', DATE_SUB(NOW(), INTERVAL 1 DAY),
       DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()
FROM students WHERE student_no = 'CS20230112';

-- 临界值演示：已批准、今天生效但尚未应用。后端每分钟扫描一次，到期后应将学生状态更新为休学。
INSERT INTO student_status_change_requests
    (student_id, change_type, current_status, target_status, effective_date, reason, status,
     review_comment, reviewed_by, reviewed_at, applied_at, create_time, update_time)
SELECT s.id, 'SUSPENSION', 0, 1, CURRENT_DATE, '演示：已批准、今日生效的休学申请（生效边界测试）', 'APPROVED',
       '演示：验证生效日期等于今天时自动更新学籍', 'admin', NOW(), NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()
FROM students s
WHERE s.student_no = 'CS20230104'
  AND s.status = 0
  AND NOT EXISTS (
      SELECT 1 FROM student_status_change_requests existing
      WHERE existing.student_id = s.id
        AND existing.reason = '演示：已批准、今日生效的休学申请（生效边界测试）'
  );

INSERT INTO student_status_change_requests
    (student_id, change_type, current_status, target_status, effective_date, reason, status,
     review_comment, reviewed_by, reviewed_at, create_time, update_time)
SELECT id, 'RETURN', 1, 0, '2027-09-01', '演示：申请复学', 'REJECTED',
       '请补充校医院复核材料后重新提交', 'admin', DATE_SUB(NOW(), INTERVAL 2 DAY),
       DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM students WHERE student_no = 'CS20230105';

INSERT INTO student_status_change_requests
    (student_id, change_type, current_status, target_status, effective_date, reason, status, create_time, update_time)
SELECT id, 'WITHDRAWAL', 0, 2, '2027-09-01', '演示：提交退学申请，等待审核', 'PENDING', NOW(), NOW()
FROM students WHERE student_no = 'CS20230116';

-- 操作审计演示
INSERT INTO operation_audits (actor, action, entity_type, entity_id, summary, create_time) VALUES
('admin', 'SCORE_UPDATE', 'SCORE', '1', '演示：复核并更正学生成绩', DATE_SUB(NOW(), INTERVAL 3 DAY)),
('admin', 'SCORE_PUBLISH', 'SCORE', '2', '演示：发布课程成绩', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('admin', 'COURSE_SELECTION_APPROVE', 'COURSE_SELECTION', '1', '演示：审核通过选课申请', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('admin', 'STUDENT_STATUS_REVIEW', 'STUDENT_STATUS_CHANGE', 'CS20230112', '演示：批准转专业申请', DATE_SUB(NOW(), INTERVAL 1 DAY)),
('admin', 'STUDENT_STATUS_REVIEW', 'STUDENT_STATUS_CHANGE', 'CS20230105', '演示：驳回复学申请', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 站内通知演示：管理员和学生均有通知，并包含已读、未读状态
INSERT INTO system_notifications (user_id, notification_type, title, message, read_at, create_time)
SELECT id, 'STUDENT_STATUS_PENDING', '有新的学籍异动申请', '学生 CS20230108 提交了休学申请，请及时审核。', NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR)
FROM users WHERE username = 'admin'
UNION ALL
SELECT id, 'STUDENT_STATUS_PENDING', '有新的学籍异动申请', '学生 CS20230116 提交了退学申请，请及时审核。', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM users WHERE username = 'admin';

INSERT INTO system_notifications (user_id, notification_type, title, message, read_at, create_time)
SELECT u.id, 'STATUS_CHANGE_REVIEWED', '学籍异动申请已通过', '你的转专业申请已通过，将于 2027-09-01 生效。', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM users u JOIN students s ON s.user_id = u.id WHERE s.student_no = 'CS20230112'
UNION ALL
SELECT u.id, 'STATUS_CHANGE_REVIEWED', '学籍异动申请未通过', '你的复学申请未通过，请查看审核意见并补充材料。', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM users u JOIN students s ON s.user_id = u.id WHERE s.student_no = 'CS20230105'
UNION ALL
SELECT u.id, 'STATUS_CHANGE_REVIEWED', '休学申请已通过，今日生效', '演示：该申请的生效日期为今天，后端定时任务应用后学籍应变更为休学。', NULL, NOW()
FROM users u JOIN students s ON s.user_id = u.id WHERE s.student_no = 'CS20230104'
UNION ALL
SELECT u.id, 'SCORE_PUBLISHED', '课程成绩已发布', '《数据库原理》成绩已发布，可在成绩查询中查看。', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM users u JOIN students s ON s.user_id = u.id WHERE s.student_no = 'VM20230201';

INSERT INTO course_selections (student_id, course_id, status, create_time, update_time, create_by, update_by)
SELECT s.id,
       c.id,
       CASE MOD(s.id + c.id, 3)
           WHEN 0 THEN 'pending'
           WHEN 1 THEN 'approved'
           ELSE 'rejected'
       END,
       NOW(),
       NOW(),
       'system',
       'system'
FROM (SELECT id FROM students WHERE is_deleted = 0 AND status = 0 ORDER BY id LIMIT 30) s
CROSS JOIN courses c
WHERE c.is_deleted = 0 AND MOD(s.id + c.id, 2) = 0;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
