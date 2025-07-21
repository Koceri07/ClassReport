package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.GroupRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody GroupRequest groupRequest){
        groupService.createGroup(groupRequest);
        ApiResponse apiResponse = new ApiResponse("groud Added");
        return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return groupService.getGroupById(id);
    }

    @GetMapping("/get-all")
    public ApiResponse getAll(){
        return groupService.getAllGroups();
    }

    @GetMapping("/get-teacher-id/{teacherId}")
    public ApiResponse getByTeacherId(@PathVariable Long teacherId){
        return groupService.getAllGroupsByTeacherId(teacherId);
    }

    @DeleteMapping("/{id}")
    public void hardDelete(@PathVariable Long id){
        groupService.hardDeleteById(id);
    }

    @PutMapping("/{id}")
    public void softDelete(@PathVariable Long id){
        groupService.softDeleteById(id);
    }

    @GetMapping("/test/{groupId}")
    public void test(@PathVariable Long groupId){
        groupService.testGroupLessonSchedule(groupId);
    }
}
