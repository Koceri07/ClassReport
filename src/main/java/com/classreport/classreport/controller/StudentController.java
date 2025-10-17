package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.StudentRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/students")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;

    @PostMapping(value = "/add", consumes = "application/json")
    public ApiResponse create(@RequestBody StudentRequest studentRequest){
        studentService.createStudent(studentRequest);
        ApiResponse apiResponse = new ApiResponse("Student Add");
        return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @GetMapping("/get-all")
    public ApiResponse getAll(){
        return studentService.getAllStudents();
    }

    @GetMapping("/filter/{id}")
    public ApiResponse getByGroup(@PathVariable Long id){
        return studentService.getStudentsByGroup(id);
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
