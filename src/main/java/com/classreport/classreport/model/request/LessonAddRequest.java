package com.classreport.classreport.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LessonAddRequest {

    private LessonScheduleRequest lessonSchedule;
    @NotNull
    private Long groupId;
    @NotNull
    private LocalDate date;
}
