package com.classreport.classreport.service;

import com.classreport.classreport.mapper.GroupMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.GroupRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {

    private GroupRepository groupRepository;

    public void createGroup(GroupRequest groupRequest){
        log.info("Action.createGroup.start for id {}", groupRequest.getId());
        var groupEntity = GroupMapper.INSTANCE.requestToEntity(groupRequest);
        groupRepository.save(groupEntity);
        log.info("Action.createGroup.end for id {}", groupRequest.getId());
    }

    public ApiResponse getGroupById(Long id){
        log.info("Action.getGroupById.start for id {}", id);
        var groupEntity = groupRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Id Not Found"));
        var group = GroupMapper.INSTANCE.entityToRequest(groupEntity);
        ApiResponse apiResponse = new ApiResponse(group);
        log.info("Action.getGroupById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getAllGroups(){
        log.info("Action.getAllGroups.start");
        var groups = groupRepository.findAll().stream()
                        .map(GroupMapper.INSTANCE::entityToRequest)
                                .toList();
        ApiResponse apiResponse = new ApiResponse(groups);
        log.info("Action.getAllGroups.end");
        return apiResponse;
    }

    public void  hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
        groupRepository.deleteById(id);
        log.info("Action.hardDeleteById.end for id {}", id);
    }

    public void softDeleteById(Long id){
        log.info("Action.softDeleteById.start for id {}", id);
        groupRepository.softDelete(id);
        log.info("Action.softDeleteById.end for id {}", id);
    }
}
