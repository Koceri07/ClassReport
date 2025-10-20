package com.classreport.classreport.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamRequest {

    private Long id;

    private Long point;

    private String examName;

    private StudentRequest student;

}
