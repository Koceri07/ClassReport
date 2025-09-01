package com.classreport.classreport.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAttendancesStatsResponse {

    private Long studentId;
    private String fullName;
    private Long totalLessons;
    private Long attendedLessons;
    private Long absentLessons;


}
