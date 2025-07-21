package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.TeacherEntity;
import com.classreport.classreport.model.request.TeacherRequest;
import com.classreport.classreport.model.response.TeacherResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {StudentMapper.class})
public interface TeacherMapper {

    TeacherMapper INSTANCE = Mappers.getMapper(TeacherMapper.class);

    TeacherEntity requestToEntity(TeacherRequest request);

    TeacherRequest entityToRequest(TeacherEntity entity);

    TeacherResponse entityToResponse(TeacherEntity entity);
}
