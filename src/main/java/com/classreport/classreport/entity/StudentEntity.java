package com.classreport.classreport.entity;

import com.classreport.classreport.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
//@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@PrimaryKeyJoinColumn(name = "id")
public class StudentEntity{

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String surname;

    private String email;

    private String phoneNumber;

    private String parentInvadeCode;

    private boolean isActive;

    private boolean isTransfer;

    private Role role = Role.STUDENT;


    @ManyToMany
    private List<ParentEntity> parents;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "attendance_id")
    public List<AttendanceEntity> attendanceEntity;

    @ManyToMany
    @JoinTable(name = "student_group",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private List<GroupEntity> groups = new ArrayList<>();


}
