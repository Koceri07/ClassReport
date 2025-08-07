package com.classreport.classreport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TemporaryGroupTransferEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private StudentEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    private GroupEntity fromGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_group_id")
    private GroupEntity toGroup;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isActive;
}
