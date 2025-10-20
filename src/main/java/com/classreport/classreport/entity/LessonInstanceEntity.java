package com.classreport.classreport.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private LocalDate date;

    private boolean isExtra;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "group_id",nullable = false)
    private GroupEntity group;


    @ManyToOne
    private LessonScheduleEntity lessonSchedule;


    @OneToMany(mappedBy = "lessonInstance")
    private List<AttendanceEntity> attendances;

}
