package com.classreport.classreport.model.response;

import com.classreport.classreport.entity.GroupEntity;
import com.classreport.classreport.entity.TeacherEntity;
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
public class LessonScheduleResponse {

    private Long id;
    private Set<DayOfWeek> daysOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<LessonInstanceResponse> lessons;
//    private GroupResponse group;  // GroupEntity yox, GroupResponse
    private TeacherResponse teacher;  // TeacherEntity yox, TeacherResponse
    private Set<LocalDate> exceptionDates;

}
