package com.classreport.classreport.model.request;

import com.classreport.classreport.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private Long id;

    private String name;
    private String surname;
    private String password;

    private boolean isActive;

    private Role role;
    private String email;

}
