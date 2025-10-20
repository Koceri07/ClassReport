package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.ReportRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;


    @PostMapping("/add")
    public ApiResponse create(@RequestBody ReportRequest reportRequest){
        return reportService.createReport(reportRequest);
    }

    @GetMapping("/id/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return reportService.getReportById(id);
    }

    @GetMapping("/student/{id}")
    public ApiResponse getByStudentId(@PathVariable Long id){
        return reportService.getByStudentId(id);
    }

    @GetMapping("/get-all")
    public ApiResponse getAll(){
        return reportService.getAllReports();
    }


    @GetMapping("/get/student/{studentId}/teacher/{teacherId}")
    public ApiResponse getByTeacherIdAndStudentId(@PathVariable Long studentId, @PathVariable Long teacherId){
        return reportService.getByTeacherIdAndStudentId(studentId,teacherId);
    }

    @PatchMapping("/soft-delete/{id}")
    public ApiResponse softDelete(@PathVariable Long id){
        return reportService.softDelete(id);
    }

    @DeleteMapping("/hard-delete/{id}")
    public ApiResponse hardDelete(@PathVariable Long id){
        return reportService.hardDelete(id);
    }


}
