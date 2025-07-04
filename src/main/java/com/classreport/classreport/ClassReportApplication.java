package com.classreport.classreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.classreport.classreport.entity")
@SpringBootApplication
public class ClassReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassReportApplication.class, args);
    }

}
