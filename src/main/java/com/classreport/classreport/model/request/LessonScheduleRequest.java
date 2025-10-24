package com.classreport.classreport.model.request;

import com.classreport.classreport.entity.GroupEntity;
import com.classreport.classreport.entity.TeacherEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonScheduleRequest {

    private Long id;
    @NotNull
    private Set<DayOfWeek> daysOfWeek;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    @NotNull
    private List<LessonInstanceRequest> lessons;
//    private GroupRequest group;
//    private TeacherRequest teacher;
    @NotNull
    private Long teacherId;
//    private Set<LocalDate> exceptionDates;

}
