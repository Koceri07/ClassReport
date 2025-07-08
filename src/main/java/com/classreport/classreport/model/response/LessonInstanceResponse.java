package com.classreport.classreport.model.response;

import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.entity.TeacherEntity;
import com.classreport.classreport.model.request.LessonScheduleRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonInstanceResponse {

    private Long id;

    private LocalDateTime date;
    private Boolean isAbsent;
    private String lateTime;
    private String note;

    private StudentResponse student;
    private TeacherResponse teacher;
    private LessonScheduleRequest lessonInstance;

}
