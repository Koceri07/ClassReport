package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.LessonScheduleEntity;
import com.classreport.classreport.model.request.LessonScheduleRequest;
import com.classreport.classreport.model.response.LessonScheduleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LessonScheduleMapper {

    LessonScheduleMapper INSTANCE = Mappers.getMapper(LessonScheduleMapper.class);

    LessonScheduleEntity requestToEntity(LessonScheduleRequest lessonScheduleRequest);

    LessonScheduleRequest entityToRequest(LessonScheduleEntity entity);

    LessonScheduleResponse entityToResponse(LessonScheduleEntity entity);
}
