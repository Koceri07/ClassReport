package com.classreport.classreport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "lesson_schedules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ElementCollection()
    @CollectionTable(name = "lesson_days", joinColumns = @JoinColumn(name = "lesson_schedule_id"))
    private Set<DayOfWeek> daysOfWeek;

    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @ManyToOne
    private TeacherEntity teacher;


    @ElementCollection
    @CollectionTable(name = "lesson_exceptions", joinColumns = @JoinColumn(name = "lesson_schedule_id"))
    private Set<LocalDate> exceptionDates;


}
