package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.ReportEntity;
import com.classreport.classreport.entity.TeacherEntity;
import com.classreport.classreport.model.request.ReportRequest;
import com.classreport.classreport.model.request.TeacherRequest;
import com.classreport.classreport.model.response.ReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    @Autowired
    private StudentMapper studentMapper;

    public ReportResponse entityToResponse(ReportEntity entity) {
        if (entity == null) {
            return null;
        }

        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setId(entity.getId());
        reportResponse.setStudent(studentMapper.entityToResponse(entity.getStudent()));
        reportResponse.setContent(entity.getContent());
        reportResponse.setReportDate(entity.getReportDate());

        return reportResponse;
    }

    public ReportEntity requestToEntity(ReportRequest reportRequest) {
        if (reportRequest == null) {
            return null;
        }

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setId(reportRequest.getId());
        reportEntity.setStudent(studentMapper.requestToEntity(reportRequest.getStudent()));
        reportEntity.setTeacher(teacherRequestToTeacherEntity(reportRequest.getTeacher()));
        reportEntity.setContent(reportRequest.getContent());

        return reportEntity;
    }

    protected TeacherEntity teacherRequestToTeacherEntity(TeacherRequest teacherRequest) {
        if (teacherRequest == null) {
            return null;
        }

        TeacherEntity teacherEntity = new TeacherEntity();
        teacherEntity.setId(teacherRequest.getId());
        teacherEntity.setName(teacherRequest.getName());
        teacherEntity.setSurname(teacherRequest.getSurname());
        teacherEntity.setPassword(teacherRequest.getPassword());
        teacherEntity.setRole(teacherRequest.getRole());
        teacherEntity.setEmail(teacherRequest.getEmail());
        teacherEntity.setActive(teacherRequest.isActive());

        return teacherEntity;
    }
}
