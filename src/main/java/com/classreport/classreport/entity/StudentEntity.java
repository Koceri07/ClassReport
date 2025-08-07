package com.classreport.classreport.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class StudentEntity extends UserEntity{

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "attendance_id")
    public List<AttendanceEntity> attendanceEntity;

    @ManyToMany
    @JoinTable(name = "student_group",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private List<GroupEntity> groups = new ArrayList<>();

    private boolean isTransfer;

}
