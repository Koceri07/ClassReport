package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.model.request.StudentRequest;
import com.classreport.classreport.model.response.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {GroupMapper.class})
public interface StudentMapper {

    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    StudentEntity requestToEntity(StudentRequest request);

    StudentRequest entityToRequest(StudentEntity studentEntity);

    StudentResponse entityToResponse(StudentEntity entity);
}
