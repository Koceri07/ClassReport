package com.classreport.classreport.model.response;

import com.classreport.classreport.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;

    private String name;
    private String surname;
    private String password;
    private Role role;
    private boolean isActive;
}
