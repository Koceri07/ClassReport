package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.GroupRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public void create(GroupRequest groupRequest){
        groupService.createGroup(groupRequest);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return groupService.getGroupById(id);
    }

    @GetMapping
    public ApiResponse getAll(){
        return groupService.getAllGroups();
    }

    @DeleteMapping("/{id}")
    public void hardDelete(@PathVariable Long id){
        groupService.hardDeleteById(id);
    }

    @PutMapping("/{id}")
    public void softDelete(@PathVariable Long id){
        groupService.softDeleteById(id);
    }
}
