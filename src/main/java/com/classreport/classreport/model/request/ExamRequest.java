package com.classreport.classreport.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamRequest {

    private Long id;

    @NotNull
    private Long point;

    @NotBlank
    @Size(max = 16)
    private String examName;

    private StudentRequest student;

}
