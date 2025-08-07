package com.classreport.classreport.service;

import com.classreport.classreport.entity.AttendanceEntity;
import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.TemporaryGroupTransferEntity;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentTransferService {

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final LessonInstanceRepository lessonInstanceRepository;
    private final AttendanceRepository attendanceRepository;
    private final TemporaryGroupTransferRepository temporaryGroupTransferRepository;


    @Transactional
    public ApiResponse transferStudent(Long studentId, Long targetGroupId){
        log.info("Action.transferStudent.start for student id {}", studentId);

        var studentEntity = studentRepository.findById(studentId)
                        .orElseThrow(() -> new NotFoundException("Student Id Not Found"));

        studentEntity.setTransfer(true);

        var targetGroup = groupRepository.findById(targetGroupId)
                        .orElseThrow(() -> new NotFoundException("Group Id Not Found"));

        if (!studentEntity.getGroups().contains(targetGroup)){
            studentEntity.getGroups().add(targetGroup);
            studentRepository.save(studentEntity);
        }

        var lessonInstances = lessonInstanceRepository.findByGroupId(targetGroupId);

        for (LessonInstanceEntity instance : lessonInstances){
            AttendanceEntity attendanceEntity = new AttendanceEntity();
            attendanceEntity.setStudent(studentEntity);
            attendanceEntity.setLessonInstance(instance);
            attendanceEntity.setDate(instance.getDate());
            attendanceEntity.setIsAbsent(false);

            attendanceRepository.save(attendanceEntity);
        }

        TemporaryGroupTransferEntity temporaryGroupTransferEntity = new TemporaryGroupTransferEntity();

        temporaryGroupTransferEntity.setStudent(studentEntity);
        temporaryGroupTransferEntity.setFromGroup(studentEntity.getGroups().get(0));
        temporaryGroupTransferEntity.setToGroup(targetGroup);
        temporaryGroupTransferEntity.setStartDate(LocalDate.now());
        temporaryGroupTransferEntity.setEndDate(LocalDate.now().plusDays(1));
        temporaryGroupTransferEntity.setActive(true);

        temporaryGroupTransferRepository.save(temporaryGroupTransferEntity);

        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.transferStudent.end for student id {}", studentId);
        return apiResponse;
    }

    @Scheduled(fixedDelay = 5000)
    public void checkTransferDays(){
        List<TemporaryGroupTransferEntity> transfers = temporaryGroupTransferRepository.findAllByEndDate(LocalDate.now());

        for (TemporaryGroupTransferEntity transferEntity : transfers){
        log.info("Action.checkTransferDays.start for id {}",transferEntity.getId());
        transferEntity.setActive(false);
        temporaryGroupTransferRepository.save(transferEntity);
        log.info("Action.checkTransferDays.end for id{}",transferEntity.getId());
        }
    }
}
