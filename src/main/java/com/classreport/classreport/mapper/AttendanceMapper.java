package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.AttendanceEntity;
import com.classreport.classreport.model.request.AttendanceRequest;
import com.classreport.classreport.model.response.AttendanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttendanceMapper {

    AttendanceMapper INSTANCE = Mappers.getMapper(AttendanceMapper.class);

    AttendanceEntity requestToEntity(AttendanceRequest request);

    AttendanceRequest entityToRequest(AttendanceEntity entity);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    AttendanceResponse entityToResponse(AttendanceEntity entity);

}
