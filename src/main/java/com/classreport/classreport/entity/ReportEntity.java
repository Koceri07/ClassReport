package com.classreport.classreport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reports")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherEntity teacher;

    private LocalDate reportDate;

    private String content;

    private boolean isActive;

}
