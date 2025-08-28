package com.classreport.classreport.service;

import com.classreport.classreport.entity.TeacherEntity;
import com.classreport.classreport.mapper.ReportMapper;
import com.classreport.classreport.model.exception.AlreadyExistsException;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.ReportRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.ReportRepository;
import com.classreport.classreport.repository.StudentRepository;
import com.classreport.classreport.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;


    @Transactional
    public ApiResponse createReport(ReportRequest reportRequest) {
        log.info("Action.createReport.start for studentId={}",
                reportRequest.getStudent().getId());

        var student = studentRepository.findById(reportRequest.getStudent().getId())
                .orElseThrow(() -> new NotFoundException("Student Id Not Found"));

        var teacher = teacherRepository.findById(2L)
                .orElseThrow(() -> new NotFoundException("Teacher Id Not Found"));

        var reports = reportRepository.findByStudentAndReportDate(student, LocalDate.now());

        if (reports.isPresent()){
            throw new AlreadyExistsException("This Month Report Already Exist");
        }

        var reportEntity = ReportMapper.INStANCE.requestToEntity(reportRequest);

        reportEntity.setActive(true);
        reportEntity.setStudent(student);
        reportEntity.setReportDate(LocalDate.now());
        reportEntity.setTeacher(teacher);

        reportRepository.save(reportEntity);

        log.info("Action.createReport.end for studentId={}",
                reportRequest.getStudent().getId());

        return new ApiResponse("success");
    }


    public ApiResponse getReportById(Long id){
        log.info("Action.getReportById.start for id {}",id);
        var report = reportRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Report Id Not Found"));

        var reportResponse = ReportMapper.INStANCE.entityToResponse(report);
        ApiResponse apiResponse = new ApiResponse(reportResponse);
        log.info("Action.getReportById.end for id {}",id);
        return apiResponse;
    }

    public ApiResponse getByStudentId(Long id){
        log.info("Action.getByStudentId.start for student id {}", id);

        var reports = reportRepository.findByStudent_Id(id).stream()
                .map(ReportMapper.INStANCE::entityToResponse)
                .toList();
        ApiResponse apiResponse = new ApiResponse(reports);

        log.info("Action.getByStudentId.end for student id {}", id);
        return apiResponse;
    }

    public ApiResponse getByTeacherId(Long teacherId){
        log.info("Action.getByTeacherId.start for teacher id {}", teacherId);

        var reports = reportRepository.findByTeacher_Id(teacherId).stream()
                .map(ReportMapper.INStANCE::entityToResponse)
                .toList();
        ApiResponse apiResponse = new ApiResponse(reports);

        log.info("Action.getByTeacherId.end for teacher id {}", teacherId);
        return apiResponse;
    }

    public ApiResponse getByTeacherIdAndStudentId(Long studentId, Long teacherId){
        log.info("Action.getByTeacherIdAndStudentId.start for student id {} teacher id {}",studentId,teacherId);

        var reports = reportRepository.findByStudentIdAndTeacherId(studentId,2L).stream()
                .map(ReportMapper.INStANCE::entityToResponse)
                .toList();
        ApiResponse apiResponse = new ApiResponse(reports);

        log.info("Action.getByTeacherIdAndStudentId.end for student id {} teacher id {}",studentId,teacherId);
        return apiResponse;
    }

    public ApiResponse getAllReports(){
        log.info("Action.getAllReports.start");
        var reports = reportRepository.findAll().stream()
                        .map(ReportMapper.INStANCE::entityToResponse)
                                .toList();

        ApiResponse apiResponse = new ApiResponse(reports);
        log.info("Action.getAllReports.end");
        return apiResponse;
    }

    public ApiResponse softDelete(Long id){
        log.info("Action.softDelete.start for id {}", id);
        var report = reportRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Report Id Not Found"));

        report.setActive(false);
        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.softDelete.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse hardDelete(Long id){
        log.info("Action.hardDelete.start for id {}", id);
        reportRepository.deleteById(id);
        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.hardDelete.end for id {}", id);
        return apiResponse;
    }
}
