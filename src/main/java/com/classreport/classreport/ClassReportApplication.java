package com.classreport.classreport;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan("com.classreport.classreport.entity")
@EnableScheduling
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Class Report API",
                version = "1.0.0",
                description = "Class Report Management System API",
                contact = @Contact(name = "Support", email = "support@classreport.com")
        )
)
public class ClassReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassReportApplication.class, args);
    }

}
