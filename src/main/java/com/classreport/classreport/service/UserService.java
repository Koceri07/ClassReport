package com.classreport.classreport.service;

import com.classreport.classreport.mapper.UserMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(UserRequest userRequest){
        log.info("Action.createUser.start for id {}", userRequest.getId());
        var entity = UserMapper.INSTANCE.requestToEntity(userRequest);
        userRepository.save(entity);
        log.info("Action.createUser.end for id {}", userRequest.getId());
    }

    public ApiResponse getUserById(Long id){
        log.info("Action.getUserById.start for id {}", id);
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id Not Found"));
        var userResponse = UserMapper.INSTANCE.entityToResponse(user);
        ApiResponse apiResponse = new ApiResponse(user);
        log.info("Action.getUserById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getAllUsers(){
        log.info("Action.getAllUsers.start");
        var users = userRepository.findAll().stream()
                .map(UserMapper.INSTANCE::entityToRequest)
                .toList();
        ApiResponse apiResponse = new ApiResponse(users);
        log.info("Action.getAllUsers.end");
        return apiResponse;
    }

    public void hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
        userRepository.deleteById(id);
        log.info("Action.hardDeleteById.end for id {}", id);
    }

    public void softDeleteById(Long id){
        log.info("Action.softDeleteById.start for id {}", id);
        userRepository.softDelete(id);
        log.info("Action.softDeleteById.end for id {}", id);
    }




}
