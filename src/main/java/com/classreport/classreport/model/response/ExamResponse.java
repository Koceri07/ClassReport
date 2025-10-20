package com.classreport.classreport.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponse {

    private Long id;

    private Long point;

    private String examName;

    private LocalDate examDate;

    private StudentResponse student;

}
