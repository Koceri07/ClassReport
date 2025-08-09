package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.ParentEntity;
import com.classreport.classreport.model.request.ParentRequest;
import com.classreport.classreport.model.response.ParentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ParentMapper {

    ParentMapper INSTANCE = Mappers.getMapper(ParentMapper.class);

    ParentEntity ResponseToEntity(ParentResponse parentResponse);

    ParentResponse EntityToResponse(ParentEntity parentEntity);

    ParentEntity requestToEntity(ParentRequest request);



}
