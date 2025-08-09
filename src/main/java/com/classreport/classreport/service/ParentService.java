package com.classreport.classreport.service;

import com.classreport.classreport.mapper.ParentMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.ParentRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.NotFound;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;


    public ApiResponse createParent(ParentRequest request){
        log.info("Action.createParent.start for id {}", request.getId());
        var parent = ParentMapper.INSTANCE.requestToEntity(request);

        parentRepository.save(parent);

        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.createParent.end for id {}", request.getId());
        return apiResponse;
    }

    public ApiResponse getAllParents(){
        log.info("Action.getAllParents.start");
        var parents = parentRepository.findAll().stream()
                        .map(ParentMapper.INSTANCE::EntityToResponse)
                                .toList();
        ApiResponse apiResponse = new ApiResponse(parents);
        log.info("Action.getAllParents.end");
        return apiResponse;
    }

    public ApiResponse getParentById(Long id){
        log.info("Action.getParentById.strat for id {}", id);
        var parentEntity = parentRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Id Not Found"));

        var parent = ParentMapper.INSTANCE.EntityToResponse(parentEntity);
        ApiResponse apiResponse = new ApiResponse(parent);
        log.info("Action.getParentById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
        parentRepository.deleteById(id);
        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.hardDeleteById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse softDeleteById(Long id){
        log.info("Action.softDeleteById.start for id {}", id);
        var parent = parentRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Id Not Found"));

        parent.setActive(false);
        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.softDeleteById.end for id {}", id);
        return apiResponse;
    }


}
