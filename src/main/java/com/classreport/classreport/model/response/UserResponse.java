package com.classreport.classreport.model.response;

import com.classreport.classreport.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;

    private String name;
    private String surname;

    private Role role;

    private String accessToken;
    private String refreshToke;

    private boolean isActive;

    private String email;

    private Long specificEntityId;

}
