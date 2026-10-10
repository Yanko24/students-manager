-- Consolidates the pre-release schema additions into one forward migration.
-- Kept separate from V1 so databases already baselined at version 1 can upgrade without data loss.

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
