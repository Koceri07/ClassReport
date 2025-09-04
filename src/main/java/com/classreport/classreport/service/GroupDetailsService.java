package com.classreport.classreport.service;

import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.mapper.LessonInstanceMapper;
import com.classreport.classreport.mapper.LessonScheduleMapperManual;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.model.response.GroupDetailsResponse;
import com.classreport.classreport.model.response.LessonInstanceResponse;
import com.classreport.classreport.model.response.LessonScheduleResponse;
import com.classreport.classreport.repository.GroupRepository;
import com.classreport.classreport.repository.LessonInstanceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupDetailsService {

    private final GroupRepository groupRepository;
    private final LessonInstanceRepository lessonInstanceRepository;
    private final LessonScheduleMapperManual lessonScheduleMapperManual;

    public ApiResponse getGroupDetails(Long groupId){
        log.info("Action.getGroupDetails.start for id {}", groupId);
        var group = groupRepository.findById(groupId)
                        .orElseThrow(() -> new NotFoundException("Id Not Found"));

        var lessonInstance = lessonInstanceRepository.findByGroupId(groupId);
        List<LessonInstanceResponse> lessonInstances = lessonInstance.stream()
                .map(LessonInstanceMapper.INSTANCE::entityToResponse)
                .toList();

        var lessonSchedule = lessonInstanceRepository.findByGroupId(groupId).stream()
                .map(LessonInstanceEntity::getLessonSchedule)
                .toList();
        List<LessonScheduleResponse> lessonSchedules = lessonSchedule.stream()
                .map(lessonScheduleMapperManual::entityToResponse)
                .toList();

        GroupDetailsResponse groupDetails = new GroupDetailsResponse();
        groupDetails.setId(groupId);
        groupDetails.setGroupName(group.getGroupName());
        groupDetails.setLessons(lessonInstances);
        groupDetails.setLessonTime(lessonSchedules);

        ApiResponse apiResponse = new ApiResponse(groupDetails);
        log.info("Action.getGroupDetails.end for id {}", groupId);
        return apiResponse;
    }
}
