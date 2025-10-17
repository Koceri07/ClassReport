package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.UserEntity;
import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity requestToEntity(UserRequest userRequest);

    UserRequest entityToRequest(UserEntity entity);

    UserResponse entityToResponse(UserEntity entity);


}
