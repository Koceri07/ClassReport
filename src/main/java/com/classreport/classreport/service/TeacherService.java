package com.classreport.classreport.service;

import com.classreport.classreport.entity.TeacherEntity;
import com.classreport.classreport.entity.UserEntity;
import com.classreport.classreport.mapper.TeacherMapper;
import com.classreport.classreport.model.enums.Role;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.TeacherRequest;
import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    public void createTeacher(TeacherRequest teacherRequest){
        log.info("Action.createTeacher.start for id {}", teacherRequest.getId());
        var teacherEntity = teacherMapper.requestToEntity(teacherRequest);
        teacherRepository.save(teacherEntity);
        log.info("Action.createTeacher.end for id {}", teacherRequest.getId());
    }

    public ApiResponse createTeacherByUserRequest(UserRequest userRequest, Long id){
        log.info("Action.createTeacherByUserRequest.start for id {}", userRequest.getId());
        TeacherEntity teacherEntity = new TeacherEntity();
        teacherEntity.setId(userRequest.getId());
        teacherEntity.setActive(true);
        teacherEntity.setEmail(userRequest.getEmail());
        teacherEntity.setName(userRequest.getName());
        teacherEntity.setSurname(userRequest.getSurname());
        teacherEntity.setPassword(userRequest.getPassword());
        teacherEntity.setRole(Role.TEACHER);

        teacherRepository.save(teacherEntity);

        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.createTeacherByUserRequest.end for id {}", userRequest.getId());
        return apiResponse;
    }

//    public ApiResponse getTeacherByToken(String token){
//        log.info("Action.getTeacherByToken.start");
//        var teacherEntity = teacherRepository.find
//        log.info("Action.getTeacherByToken.end");
//    }

    public ApiResponse getTeacherById(Long id){
        log.info("Action.getTeacherById.start for id {}", id);
        var teacherEntity = teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id Not Found"));
        var teacher = teacherMapper.entityToResponse(teacherEntity);
        ApiResponse apiResponse = new ApiResponse(teacher);
        log.info("Action.getTeacherById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getAllTeachers(){
        log.info("Action.getAllTeachers.start");
        var teachers = teacherRepository.findAll().stream()
                .map(teacherMapper::entityToRequest)
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

    public Long getTeacherIdFromToken(UserDetails userDetails) {
        if (userDetails instanceof UserEntity) {
            return ((UserEntity) userDetails).getId();
        }
        return null;
    }

    public ApiResponse getTeacherIdFromTokenApi(UserDetails userDetails){
        log.info("Action.getTeacherIdFromTokenApi.strat");
        if (userDetails instanceof UserEntity) {
            Long id = ((UserEntity) userDetails).getId();
            return new ApiResponse(id);
        }
        log.info("Action.getTeacherIdFromTokenApi.end");
        return new ApiResponse(null);
    }
}
