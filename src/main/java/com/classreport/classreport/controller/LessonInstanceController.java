package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.DateRequest;
import com.classreport.classreport.model.request.LessonAddRequest;
import com.classreport.classreport.model.request.LessonInstanceRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.LessonInstanceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/lesson_instances")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class LessonInstanceController {

    private final LessonInstanceService lessonInstanceService;

    @PostMapping("/add")
    private void create(@RequestBody LessonInstanceRequest request){
        lessonInstanceService.createInstance(request);
    }

    @PostMapping("/extra/add")
    public void addExtra(@RequestBody LessonAddRequest request){
        lessonInstanceService.addExtraLesson(request);
    }

    @PostMapping("/confirm-today/{id}")
    public ApiResponse confirmToday(@PathVariable Long id){
        return lessonInstanceService.confirmLesson(id);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return lessonInstanceService.getInstanceById(id);
    }

    @GetMapping("/get-all")
    public ApiResponse getAll(){
        return lessonInstanceService.getAllInstance();
    }

    @GetMapping("/get-dates")
    public ApiResponse getAllDates(){
        return lessonInstanceService.getAllDates();
    }

    @GetMapping("/by-group-id/{groupId}")
    public ApiResponse getByGroupId(@PathVariable Long groupId){
        return lessonInstanceService.getLessonsByGroupId(groupId);
    }

    @PutMapping("/{id}")
    public void softDelete(@PathVariable Long id){
        lessonInstanceService.softDeleteById(id);
    }

    @DeleteMapping("/{id}")
    public void hardDelete(@PathVariable Long id) {
        lessonInstanceService.hardDeleteById(id);
    }


    @PostMapping("/generate")
    public void generateInstancesForWeek(@RequestBody DateRequest startDate){
        lessonInstanceService.generateLessonInstancesForWeek(startDate);
    }

    @GetMapping("/preview-today/{id}")
    public ApiResponse getToday(@PathVariable Long id){
        return lessonInstanceService.previewTodayLesson(id);
    }
}
