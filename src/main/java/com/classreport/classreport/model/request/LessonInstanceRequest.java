package com.classreport.classreport.model.request;

import com.classreport.classreport.entity.AttendanceEntity;
import com.classreport.classreport.entity.LessonScheduleEntity;
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

    private LocalDate date;

    private LessonScheduleEntity lessonSchedule;

    private List<AttendanceEntity> attendances;

}
