package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.ExamRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody ExamRequest request) {
        return examService.createExam(request);
    }

    @GetMapping("/get/student/{studentId}")
    public ApiResponse getByStudentId(@PathVariable Long studentId) {
        return examService.getExamByStudentId(studentId);
    }

    @GetMapping("/get/id/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        return examService.getExamById(id);
    }

    @DeleteMapping("/hard-delete/id/{id}")
    public ApiResponse deleteById(@PathVariable Long id) {
        return examService.hardDeleteById(id);
    }
}
