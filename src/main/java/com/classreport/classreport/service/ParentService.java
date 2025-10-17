package com.classreport.classreport.service;

import com.classreport.classreport.entity.ParentEntity;
import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.mapper.ParentMapper;
import com.classreport.classreport.mapper.StudentMapper;
import com.classreport.classreport.model.enums.Role;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.ParentRequest;
import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.ParentRepository;
import com.classreport.classreport.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final ParentMapper parentMapper;


    public ApiResponse createParent(ParentRequest request){
        log.info("Action.createParent.start for id {}", request.getId());
        var parent = parentMapper.requestToEntity(request);
        parent.setRole(Role.PARENT);
        parent.setActive(true);

        parentRepository.save(parent);

        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.createParent.end for id {}", request.getId());
        return apiResponse;
    }

    public ApiResponse createByUserRequest(UserRequest userRequest, Long id){
        log.info("Action.createByUserRequest.start for id {}", userRequest.getId());
        var parent = new ParentEntity();

        parent.setName(userRequest.getName());
        parent.setSurname(userRequest.getSurname());
        parent.setId(userRequest.getId());
        parent.setEmail(userRequest.getEmail());
        parent.setActive(true);
        parent.setPassword(userRequest.getPassword());
        parent.setRole(Role.PARENT);

        parentRepository.save(parent);

        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.createByUserRequest.end for id {}", userRequest.getId());
        return apiResponse;
    }

    public ApiResponse linkStudent(String studentCode, Long parentId){
        log.info("Action.linkStudent.start");
        StudentEntity studentEntity = studentRepository.findByParentInvadeCodeAndActiveTrue(studentCode);

//        var parent = parentRepository.findById(parentId)
        var parent = parentRepository.findById(parentId)

                .orElseThrow(() -> new NotFoundException("Parent Id Not Found"));

        parent.getStudents().add(studentEntity);

        parentRepository.save(parent);
        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.linkStudent.end");
        return apiResponse;
    }

    public ApiResponse getAllParents(){
        log.info("Action.getAllParents.start");
        var parents = parentRepository.findAll().stream()
                        .map(parentMapper::entityToResponse)
                                .toList();
        ApiResponse apiResponse = new ApiResponse(parents);
        log.info("Action.getAllParents.end");
        return apiResponse;
    }

    public ApiResponse getParentById(Long id){
        log.info("Action.getParentById.strat for id {}", id);
        var parentEntity = parentRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Id Not Found"));

        var parent = parentMapper.entityToResponse(parentEntity);
        ApiResponse apiResponse = new ApiResponse(parent);
        log.info("Action.getParentById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getStudentsByParentId(Long parentId){
        log.info("Action.getStudentsByParentId.start for id {}", parentId);

        var parent = parentRepository.findById(parentId)
                        .orElseThrow(() -> new NotFoundException("Parent Id Not Found"));

        var students = parent.getStudents();
        ApiResponse apiResponse = new ApiResponse(students);
        log.info("Action.getStudentsByParentId.end for id {}", parentId);
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
