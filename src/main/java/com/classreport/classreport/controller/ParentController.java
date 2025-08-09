package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.ParentRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody ParentRequest request){
        return parentService.createParent(request);
    }

    @GetMapping("/id/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return parentService.getParentById(id);
    }

    @GetMapping("/all")
    public ApiResponse getAll(){
        return parentService.getAllParents();
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
