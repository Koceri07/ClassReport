package com.classreport.classreport.service;

import com.classreport.classreport.mapper.ExamMapper;
import com.classreport.classreport.mapper.StudentMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.ExamRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;

    public ApiResponse createExam(ExamRequest request){
        log.info("Action.createExam.start for id {}", request.getId());
        var entity = ExamMapper.INSTANCE.requestToEntity(request);

        var studentEntity = StudentMapper.INSTANCE.requestToEntity(request.getStudent());

        entity.setStudent(studentEntity);
        entity.setExamDate(LocalDate.now());

        examRepository.save(entity);

        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.createExam.end for id {}", request.getId());
        return apiResponse;
    }


    public ApiResponse getExamById(Long id){
        log.info("Action.getExamById.start for id {}", id);
        var entity = examRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Exam Id Not Found"));

        var response = ExamMapper.INSTANCE.entityToResponse(entity);

        ApiResponse apiResponse = new ApiResponse(response);
        log.info("Action.getExamById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getExamByStudentId(Long studentId){
        log.info("Action.getExamByStudentId.start for student {}", studentId);
        var exams = examRepository.findByStudentId(studentId).stream()
                        .map(ExamMapper.INSTANCE::entityToResponse)
                                .toList();

        ApiResponse apiResponse = new ApiResponse(exams);
        log.info("Action.getExamByStudentId.end for student {}", studentId);
        return apiResponse;
    }

    public ApiResponse hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
         examRepository.deleteById(id);

         ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.hardDeleteById.end for id {}", id);
        return apiResponse;
    }
}
