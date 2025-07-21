package com.classreport.classreport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemporaryroupTransfer {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private StudentEntity student;

    @ManyToOne
    private GroupEntity fromGroup;

    @ManyToOne
    private GroupEntity toGroup;

    private LocalDate date;
}
