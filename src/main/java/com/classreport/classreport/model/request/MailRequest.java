package com.classreport.classreport.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {

    private Long id;
    private String mailFrom;
    private List<String> mailTo;
    private String subject;
    private String text;

}
