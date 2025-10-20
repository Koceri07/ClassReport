package com.classreport.classreport.model.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponse {

    private Long id;

    private LocalDate date;
    private Boolean isAbsent;
    private String lateTime;
    private String note;

    private Long studentId;
    private String studentName;


}
