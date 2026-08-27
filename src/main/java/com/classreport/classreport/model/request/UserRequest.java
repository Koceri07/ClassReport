package com.classreport.classreport.model.request;

import com.classreport.classreport.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(max = 16)
    private String name;
    @NotBlank
    @Size(max = 16)
    private String surname;
    @NotBlank
    @Size(max = 32)
    private String password;

//    @NotNull
    private boolean isActive;

    @NotNull
    private Role role;

    @NotNull
    private String email;

}
