package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.entity.TeacherEntity;
import com.classreport.classreport.mapper.StudentMapper;
import com.classreport.classreport.model.request.StudentRequest;
import com.classreport.classreport.model.request.TeacherRequest;
import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.StudentResponse;
import com.classreport.classreport.model.response.TeacherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor // Əgər StudentMapper istifadə edəcəksinizsə
public class TeacherMapper {

    private final StudentMapper studentMapper; // StudentMapper inject edin

    public TeacherEntity requestToEntity(TeacherRequest request) {
        if (request == null) {
            return null;
        }

        TeacherEntity entity = new TeacherEntity();
        // UserEntity field-ləri
        entity.setId(request.getId());
        entity.setName(request.getName());
        entity.setSurname(request.getSurname());
        entity.setPassword(request.getPassword());
        entity.setRole(request.getRole());
        entity.setEmail(request.getEmail());
        entity.setActive(request.isActive());

        // TeacherEntity xüsusi field-ləri - StudentRequest-ləri StudentEntity-ə çevir
        if (request.getStudents() != null) {
            List<StudentEntity> studentEntities = request.getStudents().stream()
                    .map(studentMapper::requestToEntity)
                    .collect(Collectors.toList());
            entity.setStudents(studentEntities);
        } else {
            entity.setStudents(null);
        }

        return entity;
    }

    public TeacherRequest entityToRequest(TeacherEntity entity) {
        if (entity == null) {
            return null;
        }

        TeacherRequest request = new TeacherRequest();
        // UserRequest field-ləri
        request.setId(entity.getId());
        request.setName(entity.getName());
        request.setSurname(entity.getSurname());
        request.setPassword(entity.getPassword());
        request.setRole(entity.getRole());
        request.setEmail(entity.getEmail());
        request.setActive(entity.isActive());

        // TeacherRequest xüsusi field-ləri - StudentEntity-ləri StudentRequest-ə çevir
        if (entity.getStudents() != null) {
            List<StudentRequest> studentRequests = entity.getStudents().stream()
                    .map(studentMapper::entityToRequest)
                    .collect(Collectors.toList());
            request.setStudents(studentRequests);
        } else {
            request.setStudents(null);
        }

        return request;
    }

    public TeacherResponse entityToResponse(TeacherEntity entity) {
        if (entity == null) {
            return null;
        }

        TeacherResponse response = new TeacherResponse();
        // UserResponse field-ləri
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setSurname(entity.getSurname());
        response.setRole(entity.getRole());
        response.setEmail(entity.getEmail());
        response.setActive(entity.isActive());

        // TeacherResponse xüsusi field-ləri - StudentEntity-ləri StudentResponse-ə çevir
        if (entity.getStudents() != null) {
            List<StudentResponse> studentResponses = entity.getStudents().stream()
                    .map(studentMapper::entityToResponse)
                    .collect(Collectors.toList());
            response.setStudents(studentResponses);
        } else {
            response.setStudents(null);
        }

        return response;
    }

    public TeacherResponse requestToResponse(TeacherRequest request){
        if (request == null){
            return null;
        }

        TeacherResponse response = new TeacherResponse();

        response.setName(request.getName());
        response.setSurname(request.getSurname());
        response.setActive(request.isActive());
        response.setEmail(request.getEmail());
        response.setId(request.getId());
        response.setRole(request.getRole());

        return response;
    }

    public TeacherResponse userRequestToResponse(UserRequest request){
        if (request == null){
            return null;
        }

        TeacherResponse response = new TeacherResponse();

        response.setName(request.getName());
        response.setSurname(request.getSurname());
        response.setActive(request.isActive());
        response.setEmail(request.getEmail());
        response.setId(request.getId());
        response.setRole(request.getRole());

        return response;
    }
}