package com.classreport.classreport.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(max = 1024)
    private String content;

}
