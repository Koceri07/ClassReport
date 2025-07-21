package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.GroupEntity;
import com.classreport.classreport.model.request.GroupRequest;
import com.classreport.classreport.model.response.GroupResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GroupMapper {

    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

    @Mapping(target = "lessonSchedule", ignore = true)
    GroupEntity requestToEntity(GroupRequest request);

    GroupRequest entityToRequest(GroupEntity entity);

    GroupResponse entityToResponse(GroupEntity entity);
}
