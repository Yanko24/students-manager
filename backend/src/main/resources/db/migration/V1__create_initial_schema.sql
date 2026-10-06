-- Flyway V1 baseline schema for a new database. Existing databases are baselined separately.
-- Table definitions are aligned with the pre-Flyway canonical Docker schema.

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

-- 创建学院表
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

-- 创建专业表（包含班级信息）
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

-- 创建教师信息表
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

-- 创建学生信息表
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

-- 创建课程表
CREATE TABLE courses
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(20)  NOT NULL UNIQUE COMMENT '课程代码',
    teacher_id  BIGINT COMMENT '授课教师ID',
    credits     DECIMAL(4,1) NOT NULL COMMENT '学分',
    course_type VARCHAR(30)  NOT NULL DEFAULT '必修课' COMMENT '课程类型',
    semester    VARCHAR(20)  NOT NULL DEFAULT '2026-2027-1' COMMENT '开课学期',
    hours       INT          NOT NULL DEFAULT 48 COMMENT '学时',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-未开课，1-已开课，2-已结课',
    description TEXT COMMENT '课程描述',
    objectives  TEXT COMMENT '教学目标',
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
CREATE TABLE course_selections
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

-- 创建成绩表
CREATE TABLE scores
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_id  BIGINT        NOT NULL COMMENT '学生ID',
    course_id   BIGINT        NOT NULL COMMENT '课程ID',
    score       DECIMAL(5,2)  NOT NULL COMMENT '百分制成绩',
    grade       VARCHAR(5)    NOT NULL COMMENT '等级',
    grade_point DECIMAL(4,2)  NOT NULL COMMENT '绩点',
    semester    VARCHAR(20)   NOT NULL COMMENT '学期',
    exam_time   DATETIME      NOT NULL COMMENT '考试时间',
    remarks     TEXT COMMENT '评语',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by   VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    update_by   VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    is_deleted  TINYINT       NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    UNIQUE KEY uk_score_student_course_semester (student_id, course_id, semester),
    KEY idx_scores_exam_time (exam_time),
    CONSTRAINT fk_scores_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_scores_course FOREIGN KEY (course_id) REFERENCES courses (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='学生成绩表';

-- 创建考勤记录表
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
