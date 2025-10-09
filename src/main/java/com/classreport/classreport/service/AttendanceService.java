package com.classreport.classreport.service;

import com.classreport.classreport.entity.AttendanceEntity;
import com.classreport.classreport.entity.GroupEntity;
import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.mapper.AttendanceMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.AttendanceRequest;
import com.classreport.classreport.model.request.AttendanceUpdateRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.model.response.AttendanceResponse;
import com.classreport.classreport.model.response.StudentAttendancesStatsResponse;
import com.classreport.classreport.repository.AttendanceRepository;
import com.classreport.classreport.repository.LessonInstanceRepository;
import com.classreport.classreport.repository.StudentAttendancesStatsRepository;
import com.classreport.classreport.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final LessonInstanceRepository lessonInstanceRepository;
    private final StudentAttendancesStatsRepository attendancesStatsRepository;
    private final AuthenticationManager authenticationManager;

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

    public ApiResponse getAttendanceByStudentId(Long studentId){
        log.info("Action.getByStudentId.start for student id {}", studentId);

        var attendanceEntities = attendanceRepository.findByStudentId(studentId);

        var attendances = attendanceEntities.stream()
                        .map(AttendanceMapper.INSTANCE::entityToResponse)
                                .toList();

        ApiResponse apiResponse = new ApiResponse(attendances);

        log.info("Action.getByStudentId.end for student id {}", studentId);
        return apiResponse;
    }

    public ApiResponse getAttendancesByGroupId(Long groupId){
        try {
            List<AttendanceEntity> attendances = attendanceRepository.findByGroupId(groupId);
            List<AttendanceResponse> response = attendances.stream()
                    .map(AttendanceMapper.INSTANCE::entityToResponse)
                    .collect(Collectors.toList());
            return new ApiResponse(response);
        } catch (Exception e) {
            return new ApiResponse("Error fetching attendances: " + e.getMessage());
        }
    }

    public ApiResponse getAbsentStudentAttendancesByStudentId(Long studentId){
        log.info("Action.getAbsentStudentAttendancesByStudentId.start for student id {}", studentId);

        var inLessonDaysEntities = attendanceRepository.findByStudentIdAndIsAbsent(studentId, Boolean.FALSE);

        var inLessonDays = inLessonDaysEntities.stream()
                        .map(AttendanceMapper.INSTANCE::entityToResponse)
                                .toList();

        ApiResponse apiResponse = new ApiResponse(inLessonDays);
        log.info("Action.getAbsentStudentAttendancesByStudentId.end for student id {}", studentId);
        return apiResponse;
    }



    public ApiResponse getNotAbsentStudentAttendancesByStudentId(Long studentId){
        log.info("Action.getAbsentStudentAttendancesByStudentId.start for student id {}", studentId);

        var inLessonDaysEntities = attendanceRepository.findByStudentIdAndIsAbsent(studentId, Boolean.FALSE);

        var groupId = studentRepository.findGroupIdsByStudentId(studentId).get(0);

        var lessonInstances = lessonInstanceRepository.findByGroupId(groupId).size();

        int lessonDaysCount = inLessonDaysEntities.size();

        int notAbsentDays = lessonInstances - inLessonDaysEntities.size();

        ApiResponse apiResponse = new ApiResponse(notAbsentDays);
        log.info("Action.getAbsentStudentAttendancesByStudentId.end for student id {}", studentId);
        return apiResponse;
    }


    public ApiResponse getAttendancePercentByGroupId(Long studentId) {
        log.info("Action.getAttendancePercentByGroupId.start for student id {}", studentId);

        var groupId = studentRepository.findGroupIdsByStudentId(studentId).get(0);

        int allLessons = lessonInstanceRepository.findByGroupId(groupId).size();
        int inLessonDays = attendanceRepository.findByStudentIdAndIsAbsent(studentId, Boolean.FALSE).size();

        int percent = (100 * inLessonDays) / allLessons;

        // Cavabı map şəklində formalaşdırırıq
        Map<String, Object> result = new HashMap<>();
        result.put("percent", percent);
        result.put("presentLessons", inLessonDays);
        result.put("totalLessons", allLessons);

        ApiResponse apiResponse = new ApiResponse(percent);

        log.info("Action.getAttendancePercentByGroupId.end for student id {}", studentId);
        return apiResponse;
    }



    public ApiResponse getAbsentStudentAttendancesCountByStudentId(Long studentId){
        log.info("Action.getAbsentStudentAttendancesCountByStudentId.start for student id {}", studentId);

        var inLessonDaysEntities = attendanceRepository.findByStudentIdAndIsAbsent(studentId, Boolean.FALSE);

        Integer total = inLessonDaysEntities.size();

        ApiResponse apiResponse = new ApiResponse(total);
        log.info("Action.getAbsentStudentAttendancesCountByStudentId.end for student id {}", studentId);
        return apiResponse;
    }


    public ApiResponse getAttendanceStats(Long studentId) {
        log.info("Action.getAttendanceStats.start");
        var projection = attendancesStatsRepository.getStudentAttendanceStats(studentId);

        var stats = new StudentAttendancesStatsResponse();
        stats.setAbsentLessons(projection.getAbsentLessons());
        stats.setAttendedLessons(projection.getAttendedLessons());
        stats.setFullName(projection.getFullName());
        stats.setTotalLessons(projection.getTotalLessons());
        stats.setStudentId(projection.getStudentId());

        ApiResponse apiResponse = new ApiResponse(stats);
        log.info("Action.getAttendanceStats.end");
        return apiResponse;
    }


    public void update(AttendanceUpdateRequest request) throws Throwable {
        log.info("Updating attendance for studentId {}, date {}, present {}", request.getStudentId(), request.getDate(), request.getPresent());

        StudentEntity student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        LocalDate attendanceDate = LocalDate.parse(request.getDate());

        // Frontend-dən gələn groupId-ni istifadə edirik
        Long groupId = request.getGroupId();

        // findAllByDateAndGroupId istifadə edək ki, çoxlu nəticə olsa da problem yaranmasın
        List<LessonInstanceEntity> lessonInstances = lessonInstanceRepository
                .findAllByDateAndGroupId(attendanceDate, groupId);

        if (lessonInstances.isEmpty()) {
            throw new NotFoundException("LessonInstance not found for date " + request.getDate() + " and group " + groupId);
        }

        // Birinci tapılan LessonInstance-i istifadə edirik
        LessonInstanceEntity lessonInstance = lessonInstances.get(0);

        // Əgər birdən çox LessonInstance varsa, log yazaq
        if (lessonInstances.size() > 1) {
            log.warn("Found {} LessonInstances for date {} and group {}. Using the first one.",
                    lessonInstances.size(), attendanceDate, groupId);
        }

        AttendanceEntity attendance = attendanceRepository.findByStudentAndLessonInstance(student, lessonInstance)
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


    // AuthService-ə əlavə edin
    @PostConstruct
    public void testAuthentication() {
        try {
            log.info("Testing authentication manager...");
            // Test üçün mövcud userin məlumatlarını istifadə edin
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            "koceri@mail", // test email
                            "123456"  // test şifrə
                    )
            );
            log.info("Authentication manager test: SUCCESS");
        } catch (Exception e) {
            log.error("Authentication manager test: FAILED - {}", e.getMessage());
        }
    }
}
