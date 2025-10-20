package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.ExamEntity;
import com.classreport.classreport.model.request.ExamRequest;
import com.classreport.classreport.model.response.ExamResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExamMapper {

    ExamMapper INSTANCE = Mappers.getMapper(ExamMapper.class);

    ExamEntity requestToEntity(ExamRequest request);

    ExamResponse entityToResponse(ExamEntity examEntity);


}
