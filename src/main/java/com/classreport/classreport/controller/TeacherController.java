package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.TeacherRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    public void create(@RequestBody TeacherRequest teacherRequest){
        teacherService.createTeacher(teacherRequest);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return teacherService.getTeacherById(id);
    }

    @GetMapping
    public ApiResponse getAll(){
        return teacherService.getAllTeachers();
    }

    @DeleteMapping("/{id}")
    public void hardDelete(@PathVariable Long id){
        teacherService.hardDeleteById(id);
    }

    @PutMapping("/{id}")
    public void softDelete(@PathVariable Long id){
        teacherService.softDeleteById(id);
    }

}
