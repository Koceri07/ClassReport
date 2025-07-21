package com.classreport.classreport.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LessonAddRequest {

    private LessonScheduleRequest lessonSchedule;
    private Long groupId;
    private LocalDate date;
}
