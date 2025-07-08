package com.classreport.classreport.service;

import com.classreport.classreport.entity.AttendanceEntity;
import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.entity.UserEntity;
import com.classreport.classreport.mapper.StudentMapper;
import com.classreport.classreport.model.enums.Role;
import com.classreport.classreport.model.request.StudentRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.AttendanceRepository;
import com.classreport.classreport.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public void createStudent(StudentRequest studentRequest){
        log.info("Action.createStudent.start for id {}", studentRequest.getId());
        var student = StudentMapper.INSTANCE.requestToEntity(studentRequest);
        AttendanceEntity attendanceEntity = new AttendanceEntity();
        attendanceEntity.setStudent(student);
        attendanceRepository.save(attendanceEntity);
        studentRepository.save(student);
        log.info("Action.createStudent.end for id {}", studentRequest.getId());
    }

    public ApiResponse getStudentById(Long id){
        log.info("Action.getStudentById.start fot id {}",id);
        var student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id Not Found"));
        var studentResponse = StudentMapper.INSTANCE.entityToResponse(student);
        ApiResponse apiResponse = new ApiResponse(studentResponse);
        log.info("Action.getStudentById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getAllStudents(){
        log.info("Action.getAllStudents.start");
        var students = studentRepository.findAll().stream()
//                .filter(studentEntity -> studentEntity.getRole().equals(Role.STUDENT))
                .filter(UserEntity::isActive)
                .map(StudentMapper.INSTANCE::entityToRequest)
                .toList();
        ApiResponse apiResponse = new ApiResponse(students);
        log.info("Action.getAllStudents.end");
        return apiResponse;
    }

    public void hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
        studentRepository.deleteById(id);
        log.info("Action.hardDeleteById.end for id {}", id);
    }

    public void softDeleteById(Long id){
        log.info("Action.softDeleteById.start for id {}", id);
        studentRepository.softDelete(id);
        log.info("Action.softDeleteById.end for id {}", id);
    }
}
