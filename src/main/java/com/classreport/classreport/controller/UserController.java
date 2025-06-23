package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public void create(@RequestBody UserRequest userRequest){
        userService.createUser(userRequest);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @GetMapping
    public ApiResponse getALl(){
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void hardDelete(@PathVariable Long id){
        userService.hardDeleteById(id);
    }

    @PutMapping("/{id}")
    public void softDelete(@PathVariable Long id){
        userService.softDeleteById(id);
    }

}
