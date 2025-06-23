package com.classreport.classreport.model.request;

import com.classreport.classreport.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private Long id;

    private String name;
    private String password;
    private Role role;
    private boolean isActive;
}
