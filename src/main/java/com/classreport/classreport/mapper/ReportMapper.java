package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.ReportEntity;
import com.classreport.classreport.model.request.ReportRequest;
import com.classreport.classreport.model.response.ReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportMapper {
    ReportMapper INStANCE = Mappers.getMapper(ReportMapper.class);

//    @Mapping(target = "teacher", ignore = true)
    ReportResponse entityToResponse(ReportEntity entity);

    @Mapping(target = "teacher", ignore = true)
    ReportEntity requestToEntity(ReportRequest reportRequest);
}
