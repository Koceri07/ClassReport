package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.ParentRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.ParentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/parents")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ParentController {

    private final ParentService parentService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody ParentRequest request){
        return parentService.createParent(request);
    }

    @PatchMapping("/link-student/id/{parentId}")
    public ApiResponse linkStudent(@RequestParam String invadeCode, @PathVariable Long parentId){
        return parentService.linkStudent(invadeCode,parentId);
    }

    @GetMapping("/id/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return parentService.getParentById(id);
    }

    @GetMapping("/all")
    public ApiResponse getAll(){
        return parentService.getAllParents();
    }

    @GetMapping("/get-student/parentId/{parentId}")
    public ApiResponse getStudentById(@PathVariable Long parentId){
        return parentService.getStudentsByParentId(parentId);
    }

    @PatchMapping("/soft-delete/{id}")
    public ApiResponse softDelete(@PathVariable Long id){
        return parentService.softDeleteById(id);
    }

    @DeleteMapping("/hard-delete/{id}")
    public ApiResponse hardDelete(@PathVariable Long id){
        return parentService.hardDeleteById(id);
    }


}
