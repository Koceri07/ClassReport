package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.TeacherRequest;
import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.TeacherService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/teachers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    public void create(@RequestBody TeacherRequest teacherRequest){
        teacherService.createTeacher(teacherRequest);
    }

    @PostMapping("/create-by-user/id/{id}")
    public ApiResponse createByUserRequest(@RequestBody UserRequest userRequest, @PathVariable Long id){
        return teacherService.createTeacherByUserRequest(userRequest, id);
    }

    @GetMapping("/get-id/token")
    public ApiResponse getIdFromToken(UserDetails userDetails) {
        return teacherService.getTeacherIdFromTokenApi(userDetails);
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
