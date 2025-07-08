package com.classreport.classreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan("com.classreport.classreport.entity")
@EnableScheduling
@SpringBootApplication
public class ClassReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassReportApplication.class, args);
    }

}
