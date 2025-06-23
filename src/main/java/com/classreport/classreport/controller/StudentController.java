package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.StudentRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public void create(@RequestBody StudentRequest studentRequest){
        studentService.createStudent(studentRequest);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @GetMapping
    public ApiResponse getAll(){
        return studentService.getAllStudents();
    }

    @DeleteMapping("/{id}")
    public void hardDelete(@PathVariable Long id){
        studentService.hardDeleteById(id);
    }

    @PutMapping("/{id}")
    public void softDelete(@PathVariable Long id){
        studentService.softDeleteById(id);
    }
}
