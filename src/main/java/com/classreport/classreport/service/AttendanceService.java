package com.classreport.classreport.service;

import com.classreport.classreport.entity.AttendanceEntity;
import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.mapper.AttendanceMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.AttendanceRequest;
import com.classreport.classreport.model.request.AttendanceUpdateRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.AttendanceRepository;
import com.classreport.classreport.repository.LessonInstanceRepository;
import com.classreport.classreport.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final LessonInstanceRepository lessonInstanceRepository;

    public void createAttendance(AttendanceRequest request){
        log.info("Action.createAttendance.start for id {}", request.getId());
        var entity = AttendanceMapper.INSTANCE.requestToEntity(request);
        attendanceRepository.save(entity);
        log.info("Action.createAttendance.end for id {}", request.getId());
    }

    public ApiResponse getAttendanceById(Long id){
        log.info("Action.getAttendanceById.start for id {}", id);
        var entity = attendanceRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Id Not Found"));
        var request = AttendanceMapper.INSTANCE.entityToRequest(entity);
        ApiResponse apiResponse = new ApiResponse(request);
        log.info("Action.getAttendanceById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getAllAttendance(){
        log.info("Action.getAllAttendance.start");
        var attendances = attendanceRepository.findAll().stream()
                        .map(AttendanceMapper.INSTANCE::entityToResponse)
                                .toList();
        ApiResponse apiResponse = new ApiResponse(attendances);
        log.info("Action.getAllAttendance.end");
        return apiResponse;
    }


//    public void updateAttendances(AttendanceRequest request){
//        log.info("Action.updateAttendances.start for id {}", request.getId());
//        attendanceRepository.updateAttendances(request.getId(),
//                request.getDate(),
//                request.getIsAbsent(),
//                request.getLateTime(),
//                request.getNote(),
//                request.getStudent(),
//                request.getTeacher(),
//                request.getLessonInstance()
//                );
//        log.info("Action.updateAttendances.end for id {}", request.getId());
//    }

    public void update(AttendanceUpdateRequest request) throws Throwable {
        log.info("Updating attendance for studentId {}, date {}, present {}", request.getStudentId(), request.getDate(), request.getPresent());

        StudentEntity student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        LessonInstanceEntity lessonInstance = (LessonInstanceEntity) lessonInstanceRepository.findByDate(LocalDate.parse(request.getDate()))
                .orElseThrow(() -> new NotFoundException("LessonInstance not found"));

        AttendanceEntity attendance = (AttendanceEntity) attendanceRepository.findByStudentAndLessonInstance(student, lessonInstance)
                .orElseGet(() -> {
                    AttendanceEntity newAttendance = new AttendanceEntity();
                    newAttendance.setStudent(student);
                    newAttendance.setLessonInstance(lessonInstance);
                    return newAttendance;
                });

        attendance.setIsAbsent(!request.getPresent());
        attendance.setDate(LocalDate.from(lessonInstance.getDate().atStartOfDay()));
        attendance.setLateTime(request.getLateTime());
        attendance.setNote(request.getNote());

//        lessonInstance.getDate().atStartOfDay()
        attendanceRepository.save(attendance);

    }

    public void hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
        attendanceRepository.deleteById(id);
        log.info("Action.hardDeleteById.end for id {}", id);
    }

    public void softDeleteById(Long id){
        log.info("Action.softDeleteById.start for id {}", id);
        attendanceRepository.softDelete(id);
        log.info("Action.softDeleteById.end for id {}", id);
    }
}
