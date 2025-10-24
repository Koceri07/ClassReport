package com.classreport.classreport.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AttendanceUpdateRequest {

    private Long studentId;
    @NotNull
    private String date;
    private Boolean present;
    @Size(max = 16)
    private String lateTime;
    @NotBlank
    @Size(max = 32)
    private String note;
    private Long groupId;
}

