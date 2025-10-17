package com.classreport.classreport.controller;

import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.GroupDetailsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/group-details")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GroupDetailsController {
    private final GroupDetailsService groupDetailsService;

    @GetMapping("/{groupId}")
    public ApiResponse get(@PathVariable Long groupId){
        return groupDetailsService.getGroupDetails(groupId);
    }
}
