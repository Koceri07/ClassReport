package com.classreport.classreport.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "lesson_days", joinColumns = @JoinColumn(name = "lesson_schedule_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "days_of_week")
    private Set<DayOfWeek> daysOfWeek;

    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;



    @OneToOne(mappedBy = "lessonSchedule",fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @ManyToOne
    private TeacherEntity teacher;


    @ElementCollection
    @CollectionTable(name = "lesson_exceptions", joinColumns = @JoinColumn(name = "lesson_schedule_id"))
    private Set<LocalDate> exceptionDates;

}
