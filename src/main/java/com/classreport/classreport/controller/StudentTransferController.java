package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.StudentTransferRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.StudentTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transfers")
@RequiredArgsConstructor
public class StudentTransferController {

    private final StudentTransferService studentTransferService;

    @PostMapping("/transfer")
    public ApiResponse transfer(@RequestBody StudentTransferRequest request){
        return studentTransferService.transferStudent(request.getStudentId(), request.getTargetGroupId());
    }
}
