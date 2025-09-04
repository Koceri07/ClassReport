package com.classreport.classreport.mapper;

import com.classreport.classreport.entity.ParentEntity;
import com.classreport.classreport.model.request.ParentRequest;
import com.classreport.classreport.model.response.ParentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class ParentMapper {

    public ParentEntity responseToEntity(ParentResponse parentResponse) {
        if (parentResponse == null) {
            return null;
        }

        ParentEntity parentEntity = new ParentEntity();
        parentEntity.setId(parentResponse.getId());
        parentEntity.setName(parentResponse.getName());
        parentEntity.setSurname(parentResponse.getSurname());
        parentEntity.setRole(parentResponse.getRole());
        parentEntity.setEmail(parentResponse.getEmail());
        parentEntity.setActive(parentResponse.isActive());

        return parentEntity;
    }

    public ParentResponse entityToResponse(ParentEntity parentEntity) {
        if (parentEntity == null) {
            return null;
        }

        ParentResponse parentResponse = new ParentResponse();
        parentResponse.setId(parentEntity.getId());
        parentResponse.setName(parentEntity.getName());
        parentResponse.setSurname(parentEntity.getSurname());
        parentResponse.setRole(parentEntity.getRole());
        parentResponse.setEmail(parentEntity.getEmail());
        parentResponse.setActive(parentEntity.isActive());

        return parentResponse;
    }

    public ParentEntity requestToEntity(ParentRequest request) {
        if (request == null) {
            return null;
        }

        ParentEntity parentEntity = new ParentEntity();
        parentEntity.setId(request.getId());
        parentEntity.setName(request.getName());
        parentEntity.setSurname(request.getSurname());
        parentEntity.setPassword(request.getPassword());
        parentEntity.setRole(request.getRole());
        parentEntity.setEmail(request.getEmail());
        parentEntity.setActive(request.isActive());

        return parentEntity;
    }
}

