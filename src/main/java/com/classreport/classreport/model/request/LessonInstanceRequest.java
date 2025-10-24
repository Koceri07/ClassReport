package com.classreport.classreport.model.request;

import com.classreport.classreport.entity.AttendanceEntity;
import com.classreport.classreport.entity.LessonScheduleEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonInstanceRequest {
    private Long id;

    @NotNull
    private LocalDate date;

    private LessonScheduleRequest lessonSchedule;

    private List<AttendanceRequest> attendances;

}
