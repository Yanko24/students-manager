-- Consolidated pre-release baseline: complete schema and bootstrap administrator.
-- Database and application account are created outside Flyway.

CREATE TABLE users
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

CREATE TABLE colleges
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

CREATE TABLE majors
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

CREATE TABLE teachers
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

CREATE TABLE students
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

CREATE TABLE course_catalog
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    course_code VARCHAR(20)  NOT NULL COMMENT '课程代码',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    credits     DECIMAL(4,1) NOT NULL COMMENT '学分',
    course_type VARCHAR(30)  NOT NULL DEFAULT '必修课' COMMENT '课程类型',
    hours       INT          NOT NULL DEFAULT 48 COMMENT '学时',
    description TEXT COMMENT '课程简介',
    objectives  TEXT COMMENT '教学目标',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_course_catalog_code (course_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='课程目录';

CREATE TABLE courses
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    catalog_id  BIGINT NULL COMMENT '课程目录ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(20)  NOT NULL COMMENT '课程代码',
    section_code VARCHAR(10) NOT NULL DEFAULT '01' COMMENT '教学班编号',
    teacher_id  BIGINT COMMENT '授课教师ID',
    credits     DECIMAL(4,1) NOT NULL COMMENT '学分',
    course_type VARCHAR(30)  NOT NULL DEFAULT '必修课' COMMENT '课程类型',
    semester    VARCHAR(20)  NOT NULL DEFAULT '2026-2027-1' COMMENT '开课学期',
    hours       INT          NOT NULL DEFAULT 48 COMMENT '学时',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-未开课，1-已开课，2-已结课',
    selection_open TINYINT NOT NULL DEFAULT 1 COMMENT '是否开放选课：0-关闭，1-开放',
    max_students INT NOT NULL DEFAULT 60 COMMENT '最大选课人数',
    selection_scope VARCHAR(16) NOT NULL DEFAULT 'ALL' COMMENT '选课范围：ALL全校、COLLEGE学院、MAJOR专业',
    selection_college_id BIGINT NULL COMMENT '限定学院ID',
    selection_major_code VARCHAR(20) NULL COMMENT '限定专业代码',
    selection_grade VARCHAR(4) NULL COMMENT '限定入学年级，空表示不限',
    description TEXT COMMENT '课程描述',
    objectives  TEXT COMMENT '教学目标',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by   VARCHAR(50)  DEFAULT NULL COMMENT '创建人',
    update_by   VARCHAR(50)  DEFAULT NULL COMMENT '更新人',
    is_deleted  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    KEY idx_courses_selection_scope (selection_scope, selection_college_id, selection_major_code, selection_grade),
    UNIQUE KEY uk_courses_offering (course_code, semester, section_code),
    KEY idx_courses_catalog_id (catalog_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers (id),
    FOREIGN KEY (selection_college_id) REFERENCES colleges (id),
    FOREIGN KEY (catalog_id) REFERENCES course_catalog (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='课程表';

CREATE TABLE course_schedules
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id    BIGINT       NOT NULL,
    day_of_week  TINYINT      NOT NULL COMMENT '星期：1-7',
    start_period TINYINT      NOT NULL COMMENT '开始节次：1-12',
    end_period   TINYINT      NOT NULL COMMENT '结束节次：1-12',
    week_start   TINYINT      NOT NULL COMMENT '开始周：1-30',
    week_end     TINYINT      NOT NULL COMMENT '结束周：1-30',
    week_parity  VARCHAR(8)   NOT NULL DEFAULT 'ALL' COMMENT '周次：ALL、ODD、EVEN',
    classroom    VARCHAR(100) NOT NULL,
    KEY idx_course_schedules_course (course_id, day_of_week, start_period),
    KEY idx_course_schedules_classroom (classroom, day_of_week, start_period),
    CONSTRAINT fk_course_schedules_course FOREIGN KEY (course_id) REFERENCES courses (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='课程排课时段';

CREATE TABLE course_selections
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_id     BIGINT   NOT NULL COMMENT '学生ID',
    course_id      BIGINT   NOT NULL COMMENT '课程ID',
    selection_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    status         ENUM ('pending', 'waitlisted', 'approved', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '状态：待审核、候补中、已通过、已拒绝',
    create_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by      VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    update_by      VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    is_deleted     TINYINT  NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    KEY idx_course_selections_course_active (course_id, is_deleted, status),
    KEY idx_course_selections_student_course_active (student_id, course_id, is_deleted, status),
    KEY idx_course_selections_waitlist_queue (course_id, status, is_deleted, selection_date, id),
    FOREIGN KEY (student_id) REFERENCES students (id),
    FOREIGN KEY (course_id) REFERENCES courses (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='选课表';

CREATE TABLE curriculum_plans
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_name        VARCHAR(100) NOT NULL,
    major_code       VARCHAR(20)  NOT NULL,
    grade            VARCHAR(4)   NOT NULL,
    total_credits    DECIMAL(5,1) NOT NULL,
    required_credits DECIMAL(5,1) NOT NULL,
    elective_credits DECIMAL(5,1) NOT NULL,
    create_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by        VARCHAR(50) DEFAULT NULL,
    update_by        VARCHAR(50) DEFAULT NULL,
    is_deleted       TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_curriculum_plan_major_grade (major_code, grade),
    KEY idx_curriculum_plan_grade (grade)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='专业年级培养方案';

CREATE TABLE scores
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_id  BIGINT        NOT NULL COMMENT '学生ID',
    course_id   BIGINT        NOT NULL COMMENT '课程ID',
    score       DECIMAL(5,2)  NOT NULL COMMENT '百分制成绩',
    grade       VARCHAR(5)    NOT NULL COMMENT '等级',
    grade_point DECIMAL(4,2)  NOT NULL COMMENT '绩点',
    semester    VARCHAR(20)   NOT NULL COMMENT '学期',
    attempt_type ENUM ('REGULAR', 'MAKEUP', 'RETAKE') NOT NULL DEFAULT 'REGULAR' COMMENT '考试类型',
    attempt_no  INT NOT NULL DEFAULT 1 COMMENT '考试次数',
    publish_status ENUM ('DRAFT', 'PUBLISHED') NOT NULL DEFAULT 'PUBLISHED' COMMENT '发布状态',
    exam_time   DATETIME      NOT NULL COMMENT '考试时间',
    remarks     TEXT COMMENT '评语',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by   VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    update_by   VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    is_deleted  TINYINT       NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    UNIQUE KEY uk_score_student_course_attempt (student_id, course_id, semester, attempt_no),
    KEY idx_scores_exam_time (exam_time),
    CONSTRAINT fk_scores_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_scores_course FOREIGN KEY (course_id) REFERENCES courses (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='学生成绩表';

CREATE TABLE score_change_logs
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    score_id            BIGINT NOT NULL,
    action              VARCHAR(20) NOT NULL COMMENT 'CREATE、UPDATE、DELETE、PUBLISH',
    old_score           DECIMAL(5,2) NULL,
    new_score           DECIMAL(5,2) NULL,
    old_attempt_type    VARCHAR(16) NULL,
    new_attempt_type    VARCHAR(16) NULL,
    old_publish_status  VARCHAR(16) NULL,
    new_publish_status  VARCHAR(16) NULL,
    reason              VARCHAR(500) NOT NULL,
    operator_name       VARCHAR(50) NOT NULL,
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_score_change_logs_score (score_id, create_time),
    KEY idx_score_change_logs_operator (operator_name, create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='成绩变更审计记录';

CREATE TABLE student_status_change_requests
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id          BIGINT NOT NULL,
    change_type         ENUM ('SUSPENSION', 'RETURN', 'MAJOR_TRANSFER', 'WITHDRAWAL') NOT NULL,
    current_status      TINYINT NOT NULL,
    target_status       TINYINT NOT NULL,
    target_major_code   VARCHAR(20) NULL,
    target_class_no     VARCHAR(2) NULL,
    effective_date      DATE NOT NULL,
    reason              VARCHAR(1000) NOT NULL,
    status              ENUM ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    review_comment      VARCHAR(1000) NULL,
    reviewed_by         VARCHAR(50) NULL,
    reviewed_at         DATETIME NULL,
    applied_at          DATETIME NULL,
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_status_change_student (student_id, status, create_time),
    KEY idx_status_change_review (status, create_time),
    CONSTRAINT fk_status_change_student FOREIGN KEY (student_id) REFERENCES students (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='学生学籍异动申请';

CREATE TABLE operation_audits
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    actor           VARCHAR(50) NOT NULL,
    action          VARCHAR(50) NOT NULL,
    entity_type     VARCHAR(50) NOT NULL,
    entity_id       VARCHAR(100) NOT NULL,
    summary         VARCHAR(1000) NOT NULL,
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_operation_audits_actor (actor, create_time),
    KEY idx_operation_audits_entity (entity_type, entity_id, create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='关键操作审计记录';

CREATE TABLE system_notifications
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    notification_type VARCHAR(40) NOT NULL,
    title           VARCHAR(200) NOT NULL,
    message         VARCHAR(1000) NOT NULL,
    read_at         DATETIME NULL,
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_notifications_user_unread (user_id, read_at, create_time),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='站内通知';

CREATE TABLE attendance_records
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_id      BIGINT      NOT NULL COMMENT '学生ID',
    course_id       BIGINT      NOT NULL COMMENT '课程ID',
    attendance_date DATE        NOT NULL COMMENT '考勤日期',
    class_period    VARCHAR(30) NOT NULL DEFAULT '未指定' COMMENT '上课节次',
    status          VARCHAR(20) NOT NULL COMMENT '正常、迟到、早退、缺勤、请假',
    remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    update_by       VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    is_deleted      TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    KEY idx_attendance_student_date (student_id, attendance_date),
    KEY idx_attendance_course_date (course_id, attendance_date),
    KEY idx_attendance_date_status (attendance_date, status),
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_attendance_course FOREIGN KEY (course_id) REFERENCES courses (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='学生考勤记录表';

-- Bootstrap administrator for an empty database. Development demo data is in backend/db/seed-dev.sql.
INSERT INTO users
    (username, password, role, real_name, gender, phone, email, status, must_change_password, create_by, update_by)
VALUES
    ('admin', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA',
     'admin', '系统管理员', 1, '', '', 0, 1, 'bootstrap', 'bootstrap');

-- Course selection, curriculum, academic term, and scheduled task structures.
ALTER TABLE courses
    ADD COLUMN selection_start_at DATETIME NULL COMMENT '选课开始时间',
    ADD COLUMN selection_end_at DATETIME NULL COMMENT '选课截止时间',
    ADD COLUMN drop_deadline_at DATETIME NULL COMMENT '退选截止时间';

CREATE TABLE course_prerequisites
(
    course_catalog_id BIGINT NOT NULL COMMENT '目标课程目录ID',
    prerequisite_catalog_id BIGINT NOT NULL COMMENT '先修课程目录ID',
    PRIMARY KEY (course_catalog_id, prerequisite_catalog_id),
    KEY idx_course_prerequisites_prerequisite (prerequisite_catalog_id),
    CONSTRAINT chk_course_prerequisite_not_self CHECK (course_catalog_id <> prerequisite_catalog_id),
    CONSTRAINT fk_course_prerequisites_course FOREIGN KEY (course_catalog_id) REFERENCES course_catalog (id),
    CONSTRAINT fk_course_prerequisites_required FOREIGN KEY (prerequisite_catalog_id) REFERENCES course_catalog (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程先修关系';

CREATE TABLE curriculum_plan_courses
(
    plan_id BIGINT NOT NULL COMMENT '培养方案ID',
    course_catalog_id BIGINT NOT NULL COMMENT '必修课程目录ID',
    PRIMARY KEY (plan_id, course_catalog_id),
    KEY idx_curriculum_plan_courses_catalog (course_catalog_id),
    CONSTRAINT fk_curriculum_plan_courses_plan FOREIGN KEY (plan_id) REFERENCES curriculum_plans (id),
    CONSTRAINT fk_curriculum_plan_courses_catalog FOREIGN KEY (course_catalog_id) REFERENCES course_catalog (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '培养方案必修课程';

CREATE TABLE academic_terms
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    term_code VARCHAR(20) NOT NULL COMMENT '学期编码，例如2026-2027-1',
    academic_year VARCHAR(9) NOT NULL COMMENT '学年，例如2026-2027',
    term_no TINYINT NOT NULL COMMENT '学期序号：1或2',
    start_date DATE NULL COMMENT '学期开始日期',
    end_date DATE NULL COMMENT '学期结束日期',
    is_current TINYINT NOT NULL DEFAULT 0 COMMENT '是否当前学期',
    is_active TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(50) DEFAULT NULL,
    update_by VARCHAR(50) DEFAULT NULL,
    UNIQUE KEY uk_academic_terms_code (term_code),
    KEY idx_academic_terms_current (is_current, is_active)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学期管理';

INSERT INTO academic_terms (term_code, academic_year, term_no)
SELECT terms.term_code,
       SUBSTRING_INDEX(terms.term_code, '-', 2),
       CAST(SUBSTRING_INDEX(terms.term_code, '-', -1) AS UNSIGNED)
FROM (
    SELECT semester AS term_code FROM courses WHERE semester REGEXP '^[0-9]{4}-[0-9]{4}-[12]$'
    UNION
    SELECT semester AS term_code FROM scores WHERE semester REGEXP '^[0-9]{4}-[0-9]{4}-[12]$'
) terms;

UPDATE academic_terms
SET is_current = 1
WHERE term_code = (SELECT term_code FROM (SELECT MAX(term_code) AS term_code FROM academic_terms) latest);

CREATE TABLE scheduled_task_runs
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_name VARCHAR(100) NOT NULL,
    status VARCHAR(16) NOT NULL COMMENT 'RUNNING、SUCCESS、FAILED',
    started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at DATETIME NULL,
    processed_count INT NOT NULL DEFAULT 0,
    error_message VARCHAR(1000) NULL,
    KEY idx_scheduled_task_runs_started (started_at),
    KEY idx_scheduled_task_runs_name_status (task_name, status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务运行记录';

-- Key relationships and single-current-term enforcement.
ALTER TABLE students
    ADD CONSTRAINT uk_students_user_id UNIQUE (user_id);

ALTER TABLE teachers
    ADD CONSTRAINT uk_teachers_user_id UNIQUE (user_id);

ALTER TABLE colleges
    ADD CONSTRAINT uk_colleges_code UNIQUE (code);

ALTER TABLE academic_terms
    ADD COLUMN current_term_guard TINYINT
        GENERATED ALWAYS AS (CASE WHEN is_current = 1 THEN 1 ELSE NULL END) STORED,
    ADD CONSTRAINT uk_academic_terms_single_current UNIQUE (current_term_guard);

ALTER TABLE courses
    ADD KEY idx_courses_semester (semester),
    ADD CONSTRAINT fk_courses_academic_term
        FOREIGN KEY (semester) REFERENCES academic_terms (term_code)
        ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE scores
    ADD KEY idx_scores_semester (semester),
    ADD CONSTRAINT fk_scores_academic_term
        FOREIGN KEY (semester) REFERENCES academic_terms (term_code)
        ON UPDATE RESTRICT ON DELETE RESTRICT;
