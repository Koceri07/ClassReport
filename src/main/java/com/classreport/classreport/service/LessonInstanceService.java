package com.classreport.classreport.service;

import com.classreport.classreport.mapper.LessonInstanceMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.LessonInstanceRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.LessonInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonInstanceService {

    private final LessonInstanceRepository lessonInstanceRepository;

    public void createInstance(LessonInstanceRequest request){
        log.info("Action.createInstance.start for id {}", request.getId());
        var entity = LessonInstanceMapper.INSTANCE.requestToEntity(request);
        lessonInstanceRepository.save(entity);
        log.info("Action.createInstance.end for id {}", request.getId());
    }

    public ApiResponse getInstanceById(Long id){
        log.info("Action.getInstanceById.start for id {}", id);
        var entity = lessonInstanceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id Not Found"));
        var response = LessonInstanceMapper.INSTANCE.entityToResponse(entity);
        ApiResponse apiResponse = new ApiResponse(response);
        log.info("Action.getInstanceById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getAllInstance(){
        log.info("Action.getAllInstance.start");
        var instances = lessonInstanceRepository.findAll().stream()
                        .map(LessonInstanceMapper.INSTANCE::entityToResponse)
                                .toList();
        ApiResponse apiResponse = new ApiResponse(instances);
        log.info("Action.getAllInstance.end");
        return apiResponse;
    }

    public void softDeleteById(Long id){
        log.info("Action.softDeleteById.start for id {}", id);
        lessonInstanceRepository.softDelete(id);
        log.info("Action.softDeleteById.end for id {}", id);
    }

    public void hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
        lessonInstanceRepository.deleteById(id);
        log.info("Action.hardDeleteById.end for id {}", id);
    }

}
