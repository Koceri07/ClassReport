package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.MailRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.MailService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/mail-sender")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@OpenAPIDefinition(tags = {
        @Tag(name = "Mail", description = "Mails operations")
})
public class MailController {
    private final MailService mailService;

    @PostMapping
    public void sendMail(@RequestBody MailRequest mailDto){
        mailService.sendMail(mailDto);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return mailService.getMail(id);
    }

    @GetMapping
    public ApiResponse getAll(){
        return mailService.getAllMails();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        mailService.deleteMailById(id);
    }
}
