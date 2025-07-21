package com.classreport.classreport.model.response;

import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.entity.TeacherEntity;
import com.classreport.classreport.model.request.LessonScheduleRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonInstanceResponse {

    private Long id;

    private LocalDate date;
//    private String note;

    private boolean isExtra;

    private StudentResponse student;
    private TeacherResponse teacher;
    private GroupResponse group;

//    @JsonIgnore
    private LessonScheduleResponse lessonInstance;

}
