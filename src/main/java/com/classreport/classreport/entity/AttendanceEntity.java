package com.classreport.classreport.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Boolean isAbsent;
    private String lateTime;
    private String note;
    private Boolean isTemporaryTransfer;
    private Long groupId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;


    @ManyToOne
    @JoinColumn(name = "lesson_instance_id")
    private LessonInstanceEntity lessonInstance;

}
