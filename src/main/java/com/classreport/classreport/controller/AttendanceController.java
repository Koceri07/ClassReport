package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.AttendanceRequest;
import com.classreport.classreport.model.request.AttendanceUpdateRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/add")
    public void create(@RequestBody AttendanceRequest request){
        attendanceService.createAttendance(request);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return attendanceService.getAttendanceById(id);
    }

    @GetMapping("/get-all")
    public ApiResponse getAll(){
        return attendanceService.getAllAttendance();
    }

    @GetMapping("/by-group/{groupId}")
    public ApiResponse getByGroup(@PathVariable Long groupId) {
        return attendanceService.getAttendancesByGroupId(groupId);
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse getByStudentId(@PathVariable Long studentId){
        return attendanceService.getAttendanceByStudentId(studentId);
    }

    @GetMapping("/get-student/{studentId}/absent-false")
    public ApiResponse getByStudentIdAndAbsentFalse(@PathVariable Long studentId){
        return attendanceService.getAbsentStudentAttendancesByStudentId(studentId);
    }

    @GetMapping("/get-student/{studentId}/absent-true")
    public ApiResponse getByStudentIdAndAbsentTrue(@PathVariable Long studentId) {
        return attendanceService.getNotAbsentStudentAttendancesByStudentId(studentId);
    }

    @GetMapping("/get-student/{studentId}/stats")
    public ApiResponse getCountByStudentIdAndIsAbsentFalse(@PathVariable Long studentId){
        return attendanceService.getAbsentStudentAttendancesCountByStudentId(studentId);
    }

    @GetMapping("/get-stats/student/{studentId}")
    public ApiResponse getStats(@PathVariable Long studentId) {
        return attendanceService.getAttendanceStats(studentId);
    }

    @GetMapping("/get-percent/student/{studentId}")
    public ApiResponse getPercent(@PathVariable Long studentId) {
        return attendanceService.getAttendancePercentByGroupId(studentId);
    }

    @PostMapping("/update")
    public ApiResponse update(@RequestBody AttendanceUpdateRequest request) throws Throwable {
        attendanceService.update(request);
        return new ApiResponse("updated");
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
