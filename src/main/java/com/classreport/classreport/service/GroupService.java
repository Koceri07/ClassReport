package com.classreport.classreport.service;

import com.classreport.classreport.entity.GroupEntity;
import com.classreport.classreport.entity.LessonScheduleEntity;
import com.classreport.classreport.mapper.GroupMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.GroupRequest;
import com.classreport.classreport.model.request.StudentRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.GroupRepository;
import com.classreport.classreport.repository.LessonScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final LessonScheduleRepository lessonScheduleRepository;

    @Transactional
    public void createGroup(GroupRequest groupRequest){
        System.out.println("DEBUG LESSON SCHEDULE: " + groupRequest.getLessonSchedule());

        log.info("Action.createGroup.start for id {}", groupRequest.getId());

        // GroupEntity sadə şəkildə convert olunur (lessonSchedule hələ yoxdu)
        GroupEntity groupEntity = GroupMapper.INSTANCE.requestToEntity(groupRequest);
        groupEntity.setActive(true);

        // LessonSchedule əl ilə yaradılır və əlaqələr qurulur
        LessonScheduleEntity lessonSchedule = new LessonScheduleEntity();
        lessonSchedule.setStartTime(groupRequest.getLessonSchedule().getStartTime());
        lessonSchedule.setEndTime(groupRequest.getLessonSchedule().getEndTime());
        lessonSchedule.setDaysOfWeek(groupRequest.getLessonSchedule().getDaysOfWeek());

        // ƏLAQƏLƏRİN İKİ TƏRƏFLİ QURULMASI — BU VACİBDİR!
        lessonSchedule.setGroup(groupEntity);     // LessonSchedule-də group göstər
        groupEntity.setLessonSchedule(lessonSchedule); // Group-da lessonSchedule göstər

        // Təkcə Group save edilir — Cascade.ALL avtomatik lessonSchedule-u da save edəcək
        groupRepository.save(groupEntity);

        log.info("Action.createGroup.end for id {}", groupRequest.getId());
        System.out.println("DEBUG MAPPED GROUP: " + groupEntity);
        System.out.println("DEBUG LESSON SCHEDULE IN GROUP: " + groupEntity.getLessonSchedule());

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

    public ApiResponse getAllGroupsByTeacherId(Long teacherId){
        log.info("Action.getAllGroupsByTeacherId.start for teacher id {}", teacherId);
        var groups = groupRepository.findByTeacher_Id(teacherId).stream()
                        .map(GroupMapper.INSTANCE::entityToRequest)
                                .toList();
        ApiResponse apiResponse = new ApiResponse(groups);
        log.info("Action.getAllGroupsByTeacherId.end for teacher id {}", teacherId);
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

//    public void linkStudent(StudentRequest request){
//        log.info("Action.linkStudent.start for id {}", request.getId());
//
//        var group = groupRepository.findById(request.getGroupId())
//                .orElseThrow(() -> new NotFoundException("Id Not Found"));
//
//        group.getStudents().add(request);
//        groupRepository.save(group);
//    }



    @Transactional
    public void testGroupLessonSchedule(Long groupId) {
        GroupEntity group = groupRepository.findById(groupId).orElseThrow();
        LessonScheduleEntity ls = group.getLessonSchedule();
        System.out.println("LessonSchedule: " + ls);
        System.out.println("LessonSchedule's group: " + ls.getGroup());
    }

}
