package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.TeacherEntity
import com.classreport.classreport.entity.UserEntity
import com.classreport.classreport.mapper.TeacherMapper
import com.classreport.classreport.model.enums.Role
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.request.TeacherRequest
import com.classreport.classreport.model.request.UserRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.model.response.TeacherResponse
import com.classreport.classreport.repository.TeacherRepository
import com.classreport.classreport.service.TeacherService
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification
import spock.lang.Subject

class TeacherServiceTest extends Specification {

    def teacherRepository = Mock(TeacherRepository)
    def teacherMapper = Mock(TeacherMapper)

    @Subject
    def teacherService = new TeacherService(teacherRepository, teacherMapper)

    def teacherEntity
    def teacherRequest
    def userRequest
    def teacherResponse

    def setup() {
        teacherEntity = new TeacherEntity()
        teacherEntity.setId(15L)
        teacherEntity.setName("Əli")
        teacherEntity.setSurname("Hüseynov")
        teacherEntity.setEmail("eli.huseynov@edu.az")
        teacherEntity.setPassword("sifre2024")
        teacherEntity.setRole(Role.TEACHER)
        teacherEntity.setActive(true)

        teacherRequest = new TeacherRequest()
        teacherRequest.setId(15L)
        teacherRequest.setName("Əli")
        teacherRequest.setSurname("Hüseynov")
        teacherRequest.setEmail("eli.huseynov@edu.az")

        userRequest = new UserRequest()
        userRequest.setId(15L)
        userRequest.setName("Əli")
        userRequest.setSurname("Hüseynov")
        userRequest.setEmail("eli.huseynov@edu.az")
        userRequest.setPassword("sifre2024")

        teacherResponse = new TeacherResponse()
        teacherResponse.setId(15L)
        teacherResponse.setName("Əli")
        teacherResponse.setSurname("Hüseynov")
        teacherResponse.setEmail("eli.huseynov@edu.az")
    }

    def "createTeacher should save teacher entity"() {
        given:
        def mappedEntity = teacherEntity

        when:
        teacherService.createTeacher(teacherRequest)

        then:
        1 * teacherMapper.requestToEntity(teacherRequest) >> mappedEntity
        1 * teacherRepository.save(mappedEntity)
    }

    def "createTeacherByUserRequest should save teacher with TEACHER role"() {
        when:
        def result = teacherService.createTeacherByUserRequest(userRequest, 15L)

        then:
        1 * teacherRepository.save(_ as TeacherEntity) >> { TeacherEntity entity ->
            assert entity.id == userRequest.id
            assert entity.name == userRequest.name
            assert entity.surname == userRequest.surname
            assert entity.email == userRequest.email
            assert entity.password == userRequest.password
            assert entity.role == Role.TEACHER
            assert entity.active == true
            entity
        }
        result instanceof ApiResponse
        result.data == "success"
    }

    def "getTeacherById should return teacher when exists"() {
        given:
        def teacherId = 15L

        when:
        def result = teacherService.getTeacherById(teacherId)

        then:
        1 * teacherRepository.findById(teacherId) >> Optional.of(teacherEntity)
        1 * teacherMapper.entityToResponse(teacherEntity) >> teacherResponse
        result instanceof ApiResponse
        result.data == teacherResponse
    }

    def "getTeacherById should throw NotFoundException when teacher not found"() {
        given:
        def teacherId = 99L

        when:
        teacherService.getTeacherById(teacherId)

        then:
        1 * teacherRepository.findById(teacherId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "getAllTeachers should return list of teachers"() {
        given:
        def teacherList = [teacherEntity, teacherEntity]

        when:
        def result = teacherService.getAllTeachers()

        then:
        1 * teacherRepository.findAll() >> teacherList
        2 * teacherMapper.entityToRequest(_) >> teacherRequest
        result instanceof ApiResponse
        result.data.size() == 2
    }

    def "getAllTeachers should return empty list when no teachers exist"() {
        given:
        def emptyList = []

        when:
        def result = teacherService.getAllTeachers()

        then:
        1 * teacherRepository.findAll() >> emptyList
        result instanceof ApiResponse
        result.data == []
    }

    def "hardDeleteById should delete teacher"() {
        given:
        def teacherId = 15L

        when:
        teacherService.hardDeleteById(teacherId)

        then:
        1 * teacherRepository.deleteById(teacherId)
    }

    def "softDeleteById should call repository softDelete method"() {
        given:
        def teacherId = 15L

        when:
        teacherService.softDeleteById(teacherId)

        then:
        1 * teacherRepository.softDelete(teacherId)
    }

    def "getTeacherIdFromToken should return id when userDetails is UserEntity"() {
        given:
        def userEntity = new UserEntity()
        userEntity.setId(15L)
        userEntity.setEmail("eli.huseynov@edu.az")

        when:
        def result = teacherService.getTeacherIdFromToken(userEntity)

        then:
        result == 15L
    }

    def "getTeacherIdFromToken should return null when userDetails is not UserEntity"() {
        given:
        def userDetails = Mock(UserDetails)

        when:
        def result = teacherService.getTeacherIdFromToken(userDetails)

        then:
        result == null
    }

    def "getTeacherIdFromTokenApi should return ApiResponse with id"() {
        given:
        def userEntity = new UserEntity()
        userEntity.setId(15L)
        userEntity.setEmail("eli.huseynov@edu.az")

        when:
        def result = teacherService.getTeacherIdFromTokenApi(userEntity)

        then:
        result instanceof ApiResponse
        result.data == 15L
    }

    def "getTeacherIdFromTokenApi should return ApiResponse with null"() {
        given:
        def userDetails = Mock(UserDetails)

        when:
        def result = teacherService.getTeacherIdFromTokenApi(userDetails)

        then:
        result instanceof ApiResponse
        result.data == null
    }
}