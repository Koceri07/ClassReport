package com.classreport.classreport.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonPreviewResponse {

    private String startTime;
    private String endTime;
    private LocalDate date;
}
