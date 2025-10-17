package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.UserEntity
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.request.UserRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.repository.UserRepository
import com.classreport.classreport.service.UserService
import spock.lang.Specification
import spock.lang.Subject

class UserServiceTest extends Specification {

    def userRepository = Mock(UserRepository)

    @Subject
    def userService = new UserService(userRepository)

    def "createUser should save user entity"() {
        given:
        def userRequest = new UserRequest()
        userRequest.setId(1L)
        userRequest.setName("John")
        userRequest.setEmail("john@test.com")

        when:
        userService.createUser(userRequest)

        then:
        1 * userRepository.save(_)
    }

    def "getUserById should return user when exists"() {
        given:
        def userId = 1L
        def userEntity = new UserEntity()
        userEntity.setId(userId)
        userEntity.setName("John")
        userEntity.setEmail("john@test.com")

        when:
        def result = userService.getUserById(userId)

        then:
        1 * userRepository.findById(userId) >> Optional.of(userEntity)
        result instanceof ApiResponse
    }

    def "getUserById should throw NotFoundException when user not found"() {
        given:
        def userId = 999L

        when:
        userService.getUserById(userId)

        then:
        1 * userRepository.findById(userId) >> Optional.empty()
        thrown(NotFoundException)
    }
}