package com.classreport.classreport.model.request;

import com.classreport.classreport.entity.GroupEntity;
import com.classreport.classreport.entity.TeacherEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonScheduleRequest {

    private Long id;
    private Set<DayOfWeek> daysOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private GroupEntity group;
    private TeacherEntity teacher;
    private Set<LocalDate> exceptionDates;

}
