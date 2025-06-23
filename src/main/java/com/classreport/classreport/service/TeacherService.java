package com.classreport.classreport.service;

import com.classreport.classreport.mapper.TeacherMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.TeacherRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public void createTeacher(TeacherRequest teacherRequest){
        log.info("Action.createTeacher.start for id {}", teacherRequest.getId());
        var teacherEntity = TeacherMapper.INSTANCE.requestToEntity(teacherRequest);
        teacherRepository.save(teacherEntity);
        log.info("Action.createTeacher.end for id {}", teacherRequest.getId());
    }

    public ApiResponse getTeacherById(Long id){
        log.info("Action.getTeacherById.start for id {}", id);
        var teacherEntity = teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id Not Found"));
        var teacher = TeacherMapper.INSTANCE.entityToResponse(teacherEntity);
        ApiResponse apiResponse = new ApiResponse(teacher);
        log.info("Action.getTeacherById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getAllTeachers(){
        log.info("Action.getAllTeachers.start");
        var teachers = teacherRepository.findAll().stream()
                .map(TeacherMapper.INSTANCE::entityToRequest)
                .toList();
        ApiResponse apiResponse = new ApiResponse(teachers);
        log.info("Action.getAllTeachers.end");
        return apiResponse;
    }

    public void hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
        teacherRepository.deleteById(id);
        log.info("Action.hardDeleteById.end for id {}", id);
    }

    public void softDeleteById(Long id){
        log.info("Action.softDeleteById.start for id {}", id);
        teacherRepository.softDelete(id);
        log.info("Action.softDeleteById.end for id {}", id);
    }
}
