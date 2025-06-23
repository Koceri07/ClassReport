package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.AttendanceEntity;
import com.classreport.classreport.model.request.AttendanceRequest;
import com.classreport.classreport.model.response.AttendanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttendanceMapper {

    AttendanceMapper INSTANCE = Mappers.getMapper(AttendanceMapper.class);

    AttendanceEntity requestToEntity(AttendanceRequest requsert);

    AttendanceRequest entityToRequest(AttendanceEntity entity);

    AttendanceResponse entityToResponse(AttendanceEntity entity);

}
