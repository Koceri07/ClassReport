package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.UserEntity
import com.classreport.classreport.mapper.UserMapper
import com.classreport.classreport.model.request.UserRequest
import com.classreport.classreport.model.response.UserResponse
import com.classreport.classreport.model.enums.Role
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class UserMapperTest extends Specification {

    @Subject
    def userMapper = UserMapper.INSTANCE

    def "requestToEntity should map UserRequest to UserEntity correctly"() {
        given:
        def userRequest = new UserRequest()
        userRequest.setId(1L)
        userRequest.setName("testuser")
        userRequest.setEmail("test@example.com")
        userRequest.setPassword("password123")
        userRequest.setSurname("Doe")
        userRequest.setRole(Role.ROLE_USER) // String əvəzinə Role enum
        userRequest.setActive(true)

        when:
        def result = userMapper.requestToEntity(userRequest)

        then:
        result != null
        result.getId() == userRequest.getId()
        result.getName() == userRequest.getName()
        result.getEmail() == userRequest.getEmail()
        result.getPassword() == userRequest.getPassword()
        result.getSurname() == userRequest.getSurname()
        result.getRole() == userRequest.getRole()
        result.isActive() == userRequest.getActive()
    }

    def "requestToEntity should handle null request"() {
        when:
        def result = userMapper.requestToEntity(null)

        then:
        result == null
    }

    def "entityToRequest should map UserEntity to UserRequest correctly"() {
        given:
        def userEntity = new UserEntity()
        userEntity.setId(1L)
        userEntity.setName("testuser")
        userEntity.setEmail("test@example.com")
        userEntity.setPassword("password123")
        userEntity.setSurname("Doe")
        userEntity.setRole(Role.ROLE_USER) // String əvəzinə Role enum
        userEntity.setActive(true)
        userEntity.setCreatedAt(LocalDateTime.now())
        userEntity.setUpdatedAt(LocalDateTime.now())

        when:
        def result = userMapper.entityToRequest(userEntity)

        then:
        result != null
        result.getId() == userEntity.getId()
        result.getName() == userEntity.getName()
        result.getEmail() == userEntity.getEmail()
        result.getPassword() == userEntity.getPassword()
        result.getSurname() == userEntity.getSurname()
        result.getRole() == userEntity.getRole()
        result.getActive() == userEntity.isActive()
    }

    def "entityToRequest should handle null entity"() {
        when:
        def result = userMapper.entityToRequest(null)

        then:
        result == null
    }

    def "entityToResponse should map UserEntity to UserResponse correctly"() {
        given:
        def userEntity = new UserEntity()
        userEntity.setId(1L)
        userEntity.setName("testuser")
        userEntity.setEmail("test@example.com")
        userEntity.setSurname("Doe")
        userEntity.setRole(Role.ROLE_USER) // String əvəzinə Role enum
        userEntity.setActive(true)
        userEntity.setCreatedAt(LocalDateTime.of(2023, 1, 1, 10, 0))
        userEntity.setUpdatedAt(LocalDateTime.of(2023, 1, 2, 10, 0))

        when:
        def result = userMapper.entityToResponse(userEntity)

        then:
        result != null
        result.getId() == userEntity.getId()
        result.getName() == userEntity.getName()
        result.getEmail() == userEntity.getEmail()
        result.getSurname() == userEntity.getSurname()
        result.getRole() == userEntity.getRole()
        result.isActive() == userEntity.isActive() // getIsActive() əvəzinə isActive()
        result.getCreatedAt() == userEntity.getCreatedAt()
        result.getUpdatedAt() == userEntity.getUpdatedAt()
    }

    def "entityToResponse should handle null entity"() {
        when:
        def result = userMapper.entityToResponse(null)

        then:
        result == null
    }

    def "entityToResponse should handle partial data"() {
        given:
        def userEntity = new UserEntity()
        userEntity.setId(1L)
        userEntity.setName("testuser")
        userEntity.setEmail("test@example.com")
        // Digər field'lar null ola bilər

        when:
        def result = userMapper.entityToResponse(userEntity)

        then:
        result != null
        result.getId() == 1L
        result.getName() == "testuser"
        result.getEmail() == "test@example.com"
        result.getSurname() == null
        result.getRole() == null
        result.isActive() == false // default dəyər
        result.getCreatedAt() == null
        result.getUpdatedAt() == null
    }

    def "requestToEntity should handle partial data"() {
        given:
        def userRequest = new UserRequest()
        userRequest.setId(1L)
        userRequest.setName("testuser")
        // Digər field'lar null ola bilər

        when:
        def result = userMapper.requestToEntity(userRequest)

        then:
        result != null
        result.getId() == 1L
        result.getName() == "testuser"
        result.getEmail() == null
        result.getPassword() == null
        result.getSurname() == null
        result.getRole() == null
        result.isActive() == false // default dəyər
    }

    def "circular mapping should work correctly"() {
        given:
        def userRequest = new UserRequest()
        userRequest.setId(1L)
        userRequest.setName("testuser")
        userRequest.setEmail("test@example.com")
        userRequest.setPassword("password123")
        userRequest.setSurname("Doe")
        userRequest.setRole(Role.ROLE_USER) // String əvəzinə Role enum
        userRequest.setActive(true)

        when:
        def entity = userMapper.requestToEntity(userRequest)
        def backToRequest = userMapper.entityToRequest(entity)
        def response = userMapper.entityToResponse(entity)

        then:
        entity != null
        backToRequest != null
        response != null

        // Entity-dən Request-ə mapping düzgün olmalıdır
        backToRequest.getId() == userRequest.getId()
        backToRequest.getName() == userRequest.getName()
        backToRequest.getEmail() == userRequest.getEmail()
        backToRequest.getPassword() == userRequest.getPassword()
        backToRequest.getSurname() == userRequest.getSurname()
        backToRequest.getRole() == userRequest.getRole()
        backToRequest.getActive() == userRequest.getActive()

        // Entity-dən Response-a mapping düzgün olmalıdır
        response.getId() == userRequest.getId()
        response.getName() == userRequest.getName()
        response.getEmail() == userRequest.getEmail()
        response.getSurname() == userRequest.getSurname()
        response.getRole() == userRequest.getRole()
        response.isActive() == userRequest.getActive()
    }

    def "should handle boolean values correctly"() {
        given:
        def userRequest = new UserRequest()
        userRequest.setActive(false)

        when:
        def entity = userMapper.requestToEntity(userRequest)

        then:
        entity != null
        entity.isActive() == false
    }

    def "should handle empty strings"() {
        given:
        def userRequest = new UserRequest()
        userRequest.setName("")
        userRequest.setEmail("")
        userRequest.setSurname("")

        when:
        def entity = userMapper.requestToEntity(userRequest)

        then:
        entity != null
        entity.getName() == ""
        entity.getEmail() == ""
        entity.getSurname() == ""
    }

    def "should handle missing fields in mapping"() {
        given:
        def userEntity = new UserEntity()
        userEntity.setId(1L)
        userEntity.setName("testuser")
        userEntity.setEmail("test@example.com")

        when:
        def response = userMapper.entityToResponse(userEntity)

        then:
        response != null
        // accessToken, refreshToken, specificEntityId kimi field'lar MapStruct tərəfindən handle edilməlidir
        noExceptionThrown()
    }

    // Yeni test: Boolean field'ların düzgün mapping edilməsi
    def "should map boolean fields correctly between different naming conventions"() {
        given:
        def userRequest = new UserRequest()
        userRequest.setActive(true)

        def userEntity = new UserEntity()
        userEntity.setActive(false)

        when:
        def entityFromRequest = userMapper.requestToEntity(userRequest)
        def requestFromEntity = userMapper.entityToRequest(userEntity)
        def responseFromEntity = userMapper.entityToResponse(userEntity)

        then:
        entityFromRequest.isActive() == true // UserEntity-də isActive()
        requestFromEntity.getActive() == false // UserRequest-də getActive()
        responseFromEntity.isActive() == false // UserResponse-də isActive()
    }

    // Role enum testləri
    def "should handle different role values"() {
        given:
        def userRequest = new UserRequest()
        userRequest.setRole(Role.ROLE_ADMIN)

        when:
        def entity = userMapper.requestToEntity(userRequest)
        def response = userMapper.entityToResponse(entity)

        then:
        entity.getRole() == Role.ROLE_ADMIN
        response.getRole() == Role.ROLE_ADMIN
    }

    def "should handle null role"() {
        given:
        def userRequest = new UserRequest()
        userRequest.setRole(null)

        when:
        def entity = userMapper.requestToEntity(userRequest)

        then:
        entity != null
        entity.getRole() == null
    }
}