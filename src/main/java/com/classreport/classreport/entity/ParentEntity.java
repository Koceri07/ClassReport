package com.classreport.classreport.entity;

import com.classreport.classreport.model.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@Table
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ParentEntity extends UserEntity{

    @ManyToMany
    private List<StudentEntity> students;


}

