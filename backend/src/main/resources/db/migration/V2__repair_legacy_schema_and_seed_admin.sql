-- Repair the two known pre-Flyway schema gaps while remaining safe on new installs.
-- MySQL 8.4 does not support ADD COLUMN IF NOT EXISTS, so use prepared SQL.
SET @has_course_type = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'courses'
      AND column_name = 'course_type'
);
SET @course_type_ddl = IF(
    @has_course_type = 0,
    'ALTER TABLE courses ADD COLUMN course_type VARCHAR(30) NOT NULL DEFAULT ''必修课'' COMMENT ''课程类型'' AFTER credits',
    'SELECT 1'
);
PREPARE course_type_statement FROM @course_type_ddl;
EXECUTE course_type_statement;
DEALLOCATE PREPARE course_type_statement;

-- Some pre-Flyway development databases predate the scores table.
CREATE TABLE IF NOT EXISTS scores
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

-- Keep an existing administrator unchanged; create the bootstrap account only when absent.
INSERT IGNORE INTO users
    (username, password, role, real_name, gender, phone, email, status, must_change_password, create_by, update_by)
VALUES
    ('admin', '{pbkdf2-sm3}310000$dB_rALR6a4f-5G1EPGF0Lg$y-Kl4LGA_kb6m2RqXrefOwwS6ECibtRJ2kGngmPISFA',
     'admin', '系统管理员', 1, '', '', 0, 1, 'bootstrap', 'bootstrap');
