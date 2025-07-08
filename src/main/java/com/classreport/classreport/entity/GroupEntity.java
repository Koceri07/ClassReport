package com.classreport.classreport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupName;

    private boolean isActive;

    @ManyToMany(mappedBy = "groups")
    private List<StudentEntity> students;

//    @OneToMany(mappedBy = "group")
//    private List<LessonScheduleEntity> lessonSchedules;
}
