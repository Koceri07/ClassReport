package com.classreport.classreport.model.request;

import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.entity.TeacherEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRequest {

    private Long id;

    private LocalDateTime date;
    private Boolean isAbsent;
    private String lateTime;
    private String note;

    private StudentEntity student;
    private TeacherEntity teacher;
    private LessonInstanceEntity lessonInstance;
}
