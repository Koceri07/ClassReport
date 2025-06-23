package com.classreport.classreport.service;

import com.classreport.classreport.mapper.AttendanceMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.AttendanceRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

    private AttendanceRepository attendanceRepository;

    public void createAttendance(AttendanceRequest request){
        log.info("Action.createAttendance.start for id {}", request.getId());
        var entity = AttendanceMapper.INSTANCE.requestToEntity(request);
        attendanceRepository.save(entity);
        log.info("Action.createAttendance.end for id {}", request.getId());
    }

    public ApiResponse getAttendanceById(Long id){
        log.info("Action.getAttendanceById.start for id {}", id);
        var entity = attendanceRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Id Not Found"));
        var request = AttendanceMapper.INSTANCE.entityToRequest(entity);
        ApiResponse apiResponse = new ApiResponse(request);
        log.info("Action.getAttendanceById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getAllAttendance(){
        log.info("Action.getAllAttendance.start");
        var attendances = attendanceRepository.findAll().stream()
                        .map(AttendanceMapper.INSTANCE::entityToRequest)
                                .toList();
        ApiResponse apiResponse = new ApiResponse(attendances);
        log.info("Action.getAllAttendance.end");
        return apiResponse;
    }

    public void hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
        attendanceRepository.deleteById(id);
        log.info("Action.hardDeleteById.end for id {}", id);
    }

    public void softDeleteById(Long id){
        log.info("Action.softDeleteById.start for id {}", id);
        attendanceRepository.softDelete(id);
        log.info("Action.softDeleteById.end for id {}", id);
    }
}
