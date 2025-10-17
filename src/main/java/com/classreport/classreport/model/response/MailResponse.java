package com.classreport.classreport.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailResponse {

    private Long id;
    private String mailFrom;
    private List<String> mailTo;
    private String subject;
    private String text;

}
