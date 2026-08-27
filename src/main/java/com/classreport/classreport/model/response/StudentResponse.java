package com.classreport.classreport.model.response;

import com.classreport.classreport.entity.GroupEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse{

    private Long id;

    private String name;

    private String surname;

    private String email;

    private String phoneNumber;

    private boolean isActive;

    private Long groupId;
}
