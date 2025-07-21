package com.classreport.classreport.model.request;

import lombok.Data;

@Data
public class AttendanceUpdateRequest {
    private Long studentId;
    private String date;
    private Boolean present;
    private String lateTime;
    private String note;
}

