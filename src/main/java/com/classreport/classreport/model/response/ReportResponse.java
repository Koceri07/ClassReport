package com.classreport.classreport.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {

    private Long id;

    private StudentResponse student;

    private String content;

    private LocalDate reportDate;

}
