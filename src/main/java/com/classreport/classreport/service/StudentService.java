package com.classreport.classreport.service;

import com.classreport.classreport.entity.*;
import com.classreport.classreport.mapper.StudentMapper;
import com.classreport.classreport.model.enums.Role;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.StudentRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.model.response.StudentResponse;
import com.classreport.classreport.repository.AttendanceRepository;
import com.classreport.classreport.repository.GroupRepository;
import com.classreport.classreport.repository.StudentRepository;
import com.classreport.classreport.repository.TemporaryGroupTransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final GroupRepository groupRepository;
    private final TemporaryGroupTransferRepository temporaryGroupTransferRepository;

    @Transactional
    public void createStudent(StudentRequest studentRequest){
        log.info("Action.createStudent.start for id {}", studentRequest.getId());


        var student = StudentMapper.INSTANCE.requestToEntity(studentRequest);

        if (student.getGroups() == null){
            student.setGroups(new ArrayList<>());
        }

        AttendanceEntity attendanceEntity = new AttendanceEntity();
        attendanceEntity.setStudent(student);
        attendanceRepository.save(attendanceEntity);
        student.setActive(true);
        student.setTransfer(false);
        student.setRole(Role.STUDENT);
        student.setParentInvadeCode(UUID.randomUUID().toString().substring(0, 8));


        var group = groupRepository.findById(studentRequest.getGroupId())
                        .orElseThrow(() -> new NotFoundException("Group Id Not Found"));

        student.getGroups().add(group);
        group.getStudents().add(student);
        groupRepository.save(group);
        studentRepository.save(student);
        log.info("Student Group id {}", studentRequest.getGroupId());
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
                .map(StudentMapper.INSTANCE::entityToResponse)
                .toList();
        ApiResponse apiResponse = new ApiResponse(students);
        log.info("Action.getAllStudents.end");
        return apiResponse;
    }

    public ApiResponse getStudentsByGroup(Long groupId) {
        log.info("Action.getStudentsByGroup.start for id {}", groupId);
        log.info("Current date: {}", LocalDate.now());  // Tarixi yoxlamaq üçün

        Set<Long> seenIds = new HashSet<>();
        List<StudentResponse> allStudents = new ArrayList<>();

        // Əsas qrupdakı şagirdlər
        studentRepository.getAllByGroup(groupId).forEach(student -> {
            StudentResponse s = StudentMapper.INSTANCE.entityToResponse(student);
            if (seenIds.add(s.getId())) {
                allStudents.add(s);
            }
        });

        // Müvəqqəti transferdəki aktiv şagirdlər
        List<TemporaryGroupTransferEntity> transferList =
                temporaryGroupTransferRepository.findActiveTransfersToGroup(groupId, LocalDate.now());

        log.info("Found {} active transfers", transferList.size());  // Neçə transfer tapıldı?

        for (TemporaryGroupTransferEntity transfer : transferList) {
            if (transfer.getToGroup() == null) {
                log.error("TRANSFER ERROR: transferId={} has NULL toGroup", transfer.getId());
                continue;
            }

            if (transfer.getStudent() == null) {
                log.error("TRANSFER ERROR: transferId={} has NULL student", transfer.getId());
                continue;
            }

            StudentResponse s = StudentMapper.INSTANCE.entityToResponse(transfer.getStudent());
            if (seenIds.add(s.getId())) {
                allStudents.add(s);
            }
        }

        log.info("Action.getStudentsByGroup.end for id {}", groupId);
        return new ApiResponse(allStudents);
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
