package com.classreport.classreport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lesson_instances")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonInstanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne
    private LessonScheduleEntity lessonSchedule;


    @OneToMany(mappedBy = "lessonInstance")
    private List<AttendanceEntity> attendances;

}
