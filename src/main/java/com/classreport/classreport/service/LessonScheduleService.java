package com.classreport.classreport.service;

import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.LessonScheduleEntity;
import com.classreport.classreport.mapper.LessonScheduleMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.LessonScheduleRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.LessonScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonScheduleService {

    private final LessonScheduleRepository lessonScheduleRepository;

    public void createSchedule(LessonScheduleRequest request){
        log.info("Action.createSchedule.start for id {}", request.getId());
        var entity = LessonScheduleMapper.INSTANCE.requestToEntity(request);
        lessonScheduleRepository.save(entity);
        log.info("Action.createSchedule.end for id {}", request.getId());
    }

    public ApiResponse getScheduleById(Long id){
        log.info("Action.getScheduleById.start for id {}", id);
        var scheduleEntity = lessonScheduleRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Id Not Found"));
        var response = LessonScheduleMapper.INSTANCE.entityToResponse(scheduleEntity);
        ApiResponse apiResponse = new ApiResponse(response);
        log.info("Action.getScheduleById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getAllSchedules(){
        log.info("Action.getAllSchedules.start");
        var schedules = lessonScheduleRepository.findAll().stream()
                        .map(LessonScheduleMapper.INSTANCE::entityToRequest)
                                .toList();
        ApiResponse apiResponse = new ApiResponse(schedules);
        log.info("Action.getAllSchedules.end");
        return apiResponse;
    }

//    public ApiResponse getAllDates(){
//        log.info("Action.getAllDates.start");
//        var dates = lessonScheduleRepository.findAll().stream()
//                .map(LessonScheduleEntity::getDate)
//                .distinct()
//                .sorted()
//                .toList();
//        ApiResponse apiResponse = new ApiResponse(dates);
//        log.info("Action.getAllDates.end");
//        return apiResponse;
//    }

    public void softDeleteById(Long id){
        log.info("Action.softDeleteById.start for id {}", id);
        lessonScheduleRepository.softDelete(id);
        log.info("Action.softDeleteById.end for id {}", id);
    }

    public void hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
        lessonScheduleRepository.deleteById(id);
        log.info("Action.hardDeleteById.end for id {}", id);
    }

}
