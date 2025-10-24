package com.classreport.classreport.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {

    private Long id;
    @NotBlank
    @Size(max = 16)
    private String mailFrom;
    @NotNull
    private List<String> mailTo;
    @NotBlank
    @Size(max = 64)
    private String subject;
    @NotBlank
    @Size(max = 524)
    private String text;

}
