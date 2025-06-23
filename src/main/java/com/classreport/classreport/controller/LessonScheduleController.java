package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.LessonScheduleRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.LessonScheduleService;
import jakarta.servlet.http.PushBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/schedules")
@RequiredArgsConstructor
public class LessonScheduleController {

    private final LessonScheduleService lessonScheduleService;

    @PostMapping
    public void create(@RequestBody LessonScheduleRequest request){
        lessonScheduleService.createSchedule(request);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return lessonScheduleService.getScheduleById(id);
    }

    @GetMapping
    public ApiResponse getAll(){
        return lessonScheduleService.getAllSchedules();
    }

    @DeleteMapping("/{id}")
    public void hardDelete(@PathVariable Long id){
        lessonScheduleService.hardDeleteById(id);
    }

    @PutMapping("/{id}")
    public void softDelete(@PathVariable Long id){
        lessonScheduleService.softDeleteById(id);
    }
}

