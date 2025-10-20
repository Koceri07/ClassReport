package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.model.request.LessonInstanceRequest;
import com.classreport.classreport.model.response.LessonInstanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LessonInstanceMapper {
    LessonInstanceMapper INSTANCE = Mappers.getMapper(LessonInstanceMapper.class);

    LessonInstanceEntity requestToEntity(LessonInstanceRequest lessonInstanceRequest);

    LessonInstanceRequest entityToRequest(LessonInstanceEntity entity);

    LessonInstanceEntity responseToEntity(LessonInstanceResponse response);

    LessonInstanceResponse entityToResponse(LessonInstanceEntity entity);
}
