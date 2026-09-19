package com.classreport.classreport.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token boş olamaz")
    private String refreshToken;
}
