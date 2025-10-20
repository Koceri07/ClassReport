package com.classreport.classreport.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {

    private Long id;

    private StudentRequest student;

    private TeacherRequest teacher;

//    private Long studentId;
//
//    private Long teacherId;

    private String content;

}
