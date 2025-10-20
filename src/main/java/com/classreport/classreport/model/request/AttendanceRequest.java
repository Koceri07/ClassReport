package com.classreport.classreport.model.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRequest {

    private Long id;

    private LocalDate date;
    private Boolean isAbsent;
    private String lateTime;
    private String note;

    private StudentRequest student;
    private TeacherRequest teacher;
    private LessonInstanceRequest lessonInstance;
}
