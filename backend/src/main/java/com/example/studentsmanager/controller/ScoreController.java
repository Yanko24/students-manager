package com.example.studentsmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.score.ScoreQueryDTO;
import com.example.studentsmanager.model.dto.score.ScoreUpdateDTO;
import com.example.studentsmanager.model.entity.Course;
import com.example.studentsmanager.model.entity.Student;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.ScoreDistributionResponse;
import com.example.studentsmanager.service.CourseService;
import com.example.studentsmanager.service.ScoreService;
import com.example.studentsmanager.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
@Api(tags = "成绩管理")
public class ScoreController {
    private final ScoreService scoreService;
    private final StudentService studentService;
    private final CourseService courseService;

    @GetMapping
    @ApiOperation(value = "分页查询成绩", notes = "根据查询条件分页获取成绩列表")
    public Result<Page<ScoreVO>> getScores(ScoreQueryDTO query) { return Result.success(scoreService.getScorePage(query)); }

    @GetMapping("/distribution")
    @ApiOperation(value = "查询成绩分布", notes = "按时间范围统计成绩分布情况")
    public Result<ScoreDistributionResponse> getScoreDistribution(
            @RequestParam(defaultValue = "semester") String period) {
        return Result.success(scoreService.getScoreDistribution(period));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询成绩详情", notes = "根据成绩编号获取成绩信息")
    public Result<ScoreVO> getScore(@PathVariable Long id) { return Result.success(scoreService.getScore(id)); }

    @PostMapping
    @ApiOperation(value = "新增成绩", notes = "创建一条学生成绩记录")
    public Result<ScoreVO> createScore(@RequestBody ScoreUpdateDTO dto) { return Result.success(scoreService.createScore(dto)); }

    @PutMapping("/{id}")
    @ApiOperation(value = "更新成绩", notes = "根据成绩编号更新成绩信息")
    public Result<ScoreVO> updateScore(@PathVariable Long id, @RequestBody ScoreUpdateDTO dto) { return Result.success(scoreService.updateScore(id, dto)); }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除成绩", notes = "根据成绩编号删除成绩记录")
    public Result<Void> deleteScore(@PathVariable Long id) { scoreService.deleteScore(id); return Result.success(); }

    @PostMapping("/import")
    @ApiOperation(value = "导入成绩", notes = "通过CSV文件批量导入成绩记录")
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Integer>> importScores(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) throw new IllegalArgumentException("请选择CSV文件");
        int imported = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) throw new IllegalArgumentException("CSV文件为空");
            List<String> headers = parseCsvLine(headerLine.replace("\uFEFF", ""));
            Map<String, Integer> columns = new LinkedHashMap<>();
            for (int i = 0; i < headers.size(); i++) columns.put(headers.get(i).trim(), i);
            for (String required : new String[]{"studentNo", "courseCode", "score", "semester", "examTime"}) {
                if (!columns.containsKey(required)) throw new IllegalArgumentException("缺少CSV列：" + required);
            }
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                try {
                    List<String> values = parseCsvLine(line);
                    String studentNo = cell(values, columns.get("studentNo"));
                    String courseCode = cell(values, columns.get("courseCode"));
                    Student student = studentService.getOne(new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, studentNo));
                    Course course = courseService.getOne(new LambdaQueryWrapper<Course>().eq(Course::getCourseCode, courseCode));
                    if (student == null || course == null) throw new IllegalArgumentException("学号或课程代码不存在");
                    ScoreUpdateDTO dto = new ScoreUpdateDTO();
                    dto.setStudentId(student.getId()); dto.setCourseId(course.getId());
                    dto.setScore(new java.math.BigDecimal(cell(values, columns.get("score"))));
                    dto.setSemester(cell(values, columns.get("semester")));
                    dto.setExamTime(LocalDateTime.parse(cell(values, columns.get("examTime"))));
                    if (columns.containsKey("comment")) dto.setComment(cell(values, columns.get("comment")));
                    scoreService.createScore(dto);
                    imported++;
                } catch (Exception e) {
                    throw new IllegalArgumentException("第" + lineNumber + "行导入失败：" + e.getMessage(), e);
                }
            }
        }
        return Result.success(java.util.Collections.singletonMap("imported", imported));
    }

    @GetMapping("/export")
    @ApiOperation(value = "导出成绩", notes = "根据查询条件导出成绩CSV文件")
    public ResponseEntity<byte[]> exportScores(ScoreQueryDTO query) {
        query.setPage(1); query.setSize(100000);
        Page<ScoreVO> page = scoreService.getScorePage(query);
        StringBuilder csv = new StringBuilder("\uFEFFstudentNo,studentName,courseCode,courseName,credit,score,gradePoint,semester,examTime,status,comment\r\n");
        for (ScoreVO row : page.getRecords()) {
            csv.append(csv(row.getStudentNo())).append(',').append(csv(row.getStudentName())).append(',')
                    .append(csv(row.getCourseCode())).append(',').append(csv(row.getCourseName())).append(',')
                    .append(row.getCredit()).append(',').append(row.getScore()).append(',').append(row.getGradePoint()).append(',')
                    .append(csv(row.getSemester())).append(',').append(csv(String.valueOf(row.getExamTime()))).append(',')
                    .append(csv(row.getStatus())).append(',').append(csv(row.getComment())).append("\r\n");
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scores.csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8)).body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String cell(List<String> values, int index) {
        return index < values.size() ? values.get(index).trim() : "";
    }

    private static String csv(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"' && quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') { cell.append('"'); i++; }
            else if (c == '"') quoted = !quoted;
            else if (c == ',' && !quoted) { values.add(cell.toString()); cell.setLength(0); }
            else cell.append(c);
        }
        values.add(cell.toString());
        return values;
    }
}
