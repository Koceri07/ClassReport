package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.AttendanceRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public void create(@RequestBody AttendanceRequest request){
        attendanceService.createAttendance(request);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return attendanceService.getAttendanceById(id);
    }

    @GetMapping
    public ApiResponse getAll(){
        return attendanceService.getAllAttendance();
    }

    @DeleteMapping("/{id}")
    public void hardDelete(@PathVariable Long id){
        attendanceService.hardDeleteById(id);
    }

    @PutMapping("/{id}")
    public void softDelete(@PathVariable Long id){
        attendanceService.softDeleteById(id);
    }

}
