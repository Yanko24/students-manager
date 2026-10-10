package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.model.entity.ScheduledTaskRun;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface ScheduledTaskRunMapper extends BaseMapper<ScheduledTaskRun> {
    @Delete("DELETE FROM scheduled_task_runs WHERE started_at < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 90 DAY)")
    int deleteExpiredRuns();

    @Select("SELECT * FROM scheduled_task_runs ORDER BY started_at DESC, id DESC")
    Page<ScheduledTaskRun> selectLatest(Page<ScheduledTaskRun> page);
}
