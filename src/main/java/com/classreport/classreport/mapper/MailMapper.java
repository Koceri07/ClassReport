package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.MailEntity;
import com.classreport.classreport.model.request.MailRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MailMapper {

    MailMapper INSTANCE = Mappers.getMapper(MailMapper.class);

    //    @Mapping(target = "id",ignore = true)
    MailRequest toDto(MailEntity mailEntity);

    //    @Mapping(target = "id",ignore = true)
    MailEntity toEntity(MailRequest mailDto);
}

