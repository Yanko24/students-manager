package com.example.studentsmanager.service;

import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.MajorMapper;
import com.example.studentsmanager.mapper.UserMapper;
import com.example.studentsmanager.model.dto.student.StudentUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentImportService {
    private static final long MAX_FILE_BYTES = 5L * 1024 * 1024;
    private static final int MAX_ROWS = 5000;
    private static final List<String> HEADERS = List.of(
            "学号", "姓名", "性别", "手机号", "邮箱", "专业代码", "年级", "班级号",
            "出生日期", "入学日期", "家庭住址", "状态");

    private final StudentService studentService;
    private final UserMapper userMapper;
    private final MajorMapper majorMapper;
    private final Validator validator;

    @Transactional(rollbackFor = Exception.class)
    public int importCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择 CSV 文件");
        }
        if (file.getSize() > MAX_FILE_BYTES) {
            throw new BusinessException("CSV 文件不能超过 5 MB");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new BusinessException("仅支持 CSV 文件");
        }

        final List<List<String>> rows;
        try {
            rows = parseCsv(decode(file.getBytes()));
        } catch (IOException e) {
            throw new BusinessException("读取 CSV 文件失败");
        }
        if (rows.size() < 2) {
            throw new BusinessException("文件没有可导入的数据行");
        }
        if (!HEADERS.equals(rows.get(0))) {
            throw new BusinessException("表头不匹配，请下载并使用学生导入模板");
        }
        if (rows.size() - 1 > MAX_ROWS) {
            throw new BusinessException("单次最多导入 5000 名学生");
        }

        List<StudentUpdateDTO> students = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<String> seenStudentNos = new HashSet<>();
        for (int index = 1; index < rows.size(); index++) {
            List<String> row = rows.get(index);
            if (row.size() == 1 && row.get(0).trim().isEmpty()) continue;
            int rowNumber = index + 1;
            if (row.size() != HEADERS.size()) {
                errors.add("第 " + rowNumber + " 行：应有 " + HEADERS.size() + " 列，实际 " + row.size() + " 列");
                continue;
            }
            try {
                StudentUpdateDTO student = toStudent(row);
                Set<ConstraintViolation<StudentUpdateDTO>> violations = validator.validate(student);
                if (!violations.isEmpty()) {
                    errors.add("第 " + rowNumber + " 行：" + violations.stream()
                            .map(ConstraintViolation::getMessage).distinct().collect(Collectors.joining("、")));
                    continue;
                }
                String studentNo = student.getStudentNo();
                if (!seenStudentNos.add(studentNo)) {
                    errors.add("第 " + rowNumber + " 行：文件内学号重复 " + studentNo);
                    continue;
                }
                if (userMapper.countByUsernameIncludingDeleted(studentNo) > 0) {
                    errors.add("第 " + rowNumber + " 行：账号或学号已存在 " + studentNo);
                    continue;
                }
                if (!majorMapper.existsActiveClass(student.getMajorCode(), student.getGrade(), student.getClassNo())) {
                    errors.add("第 " + rowNumber + " 行：专业、年级或班级不存在/已停用");
                    continue;
                }
                students.add(student);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                errors.add("第 " + rowNumber + " 行：" + e.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            throw new BusinessException("导入未执行，请修正后重试：" + String.join("；", errors.subList(0, Math.min(errors.size(), 20))));
        }
        if (students.isEmpty()) {
            throw new BusinessException("文件没有可导入的数据行");
        }

        for (StudentUpdateDTO student : students) {
            studentService.addStudent(student);
        }
        return students.size();
    }

    private StudentUpdateDTO toStudent(List<String> row) {
        StudentUpdateDTO student = new StudentUpdateDTO();
        student.setStudentNo(value(row, 0));
        student.setRealName(value(row, 1));
        student.setGender(parseGender(value(row, 2)));
        student.setPhone(value(row, 3));
        student.setEmail(value(row, 4));
        student.setMajorCode(value(row, 5));
        student.setGrade(value(row, 6));
        student.setClassNo(value(row, 7));
        student.setBirthDate(parseDate(value(row, 8), "出生日期"));
        student.setAdmissionDate(parseDate(value(row, 9), "入学日期"));
        student.setAddress(value(row, 10));
        student.setStatus(parseStatus(value(row, 11)));
        return student;
    }

    private String value(List<String> row, int column) {
        return row.get(column).trim();
    }

    private Integer parseGender(String value) {
        if ("男".equals(value) || "1".equals(value)) return 1;
        if ("女".equals(value) || "0".equals(value)) return 0;
        throw new IllegalArgumentException("性别请填写男/女");
    }

    private Integer parseStatus(String value) {
        switch (value) {
            case "在读": case "0": return 0;
            case "休学": case "1": return 1;
            case "退学": case "2": return 2;
            case "毕业": case "3": return 3;
            default: throw new IllegalArgumentException("状态请填写在读、休学、退学或毕业");
        }
    }

    private LocalDate parseDate(String value, String field) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(field + "格式应为 YYYY-MM-DD");
        }
    }

    private String decode(byte[] bytes) throws CharacterCodingException {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes)).toString().replaceFirst("^\\uFEFF", "");
        } catch (CharacterCodingException utf8Error) {
            return Charset.forName("GB18030").newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes)).toString();
        }
    }

    private List<List<String>> parseCsv(String content) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < content.length(); i++) {
            char current = content.charAt(i);
            if (quoted) {
                if (current == '"' && i + 1 < content.length() && content.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else if (current == '"') {
                    quoted = false;
                } else {
                    field.append(current);
                }
            } else if (current == '"' && field.length() == 0) {
                quoted = true;
            } else if (current == ',') {
                row.add(field.toString());
                field.setLength(0);
            } else if (current == '\n' || current == '\r') {
                if (current == '\r' && i + 1 < content.length() && content.charAt(i + 1) == '\n') i++;
                row.add(field.toString());
                field.setLength(0);
                if (!(row.size() == 1 && row.get(0).isEmpty())) rows.add(row);
                row = new ArrayList<>();
            } else {
                field.append(current);
            }
        }
        if (quoted) throw new BusinessException("CSV 文件引号未闭合");
        if (field.length() > 0 || !row.isEmpty()) {
            row.add(field.toString());
            rows.add(row);
        }
        return rows;
    }
}
