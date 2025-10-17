package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.ParentEntity
import com.classreport.classreport.entity.StudentEntity
import com.classreport.classreport.mapper.ParentMapper
import com.classreport.classreport.model.enums.Role
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.request.ParentRequest
import com.classreport.classreport.model.request.UserRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.model.response.ParentResponse
import com.classreport.classreport.repository.ParentRepository
import com.classreport.classreport.repository.StudentRepository
import com.classreport.classreport.service.ParentService
import spock.lang.Specification
import spock.lang.Subject

class ParentServiceTest extends Specification {

    def parentRepository = Mock(ParentRepository)
    def studentRepository = Mock(StudentRepository)
    def parentMapper = Mock(ParentMapper)

    @Subject
    def parentService = new ParentService(parentRepository, studentRepository, parentMapper)

    def parentEntity
    def parentRequest
    def parentResponse
    def userRequest
    def studentEntity

    def setup() {
        parentEntity = new ParentEntity()
        parentEntity.setId(12L)
        parentEntity.setName("Nərmin")
        parentEntity.setSurname("Qasımova")
        parentEntity.setEmail("nermin.qasimova@mail.az")
        parentEntity.setPassword("valideyn789")
        parentEntity.setRole(Role.PARENT)
        parentEntity.setActive(true)
        parentEntity.setStudents([])

        parentRequest = new ParentRequest()
        parentRequest.setId(12L)
        parentRequest.setName("Nərmin")
        parentRequest.setSurname("Qasımova")
        parentRequest.setEmail("nermin.qasimova@mail.az")
        parentRequest.setPassword("valideyn789")

        userRequest = new UserRequest()
        userRequest.setId(12L)
        userRequest.setName("Nərmin")
        userRequest.setSurname("Qasımova")
        userRequest.setEmail("nermin.qasimova@mail.az")
        userRequest.setPassword("valideyn789")

        parentResponse = new ParentResponse()
        parentResponse.setId(12L)
        parentResponse.setName("Nərmin")
        parentResponse.setSurname("Qasımova")
        parentResponse.setEmail("nermin.qasimova@mail.az")

        studentEntity = new StudentEntity()
        studentEntity.setId(25L)
        studentEntity.setName("Aydan")
        studentEntity.setSurname("Rəhimova")
        studentEntity.setParentInvadeCode("ABC12345")
        studentEntity.setActive(true)
    }

    def "createParent should create parent successfully"() {
        given:
        def mappedParent = parentEntity

        when:
        def result = parentService.createParent(parentRequest)

        then:
        1 * parentMapper.requestToEntity(parentRequest) >> mappedParent
        1 * parentRepository.save(_) >> { ParentEntity parent ->
            assert parent.role == Role.PARENT
            assert parent.active == true
            parent
        }
        result instanceof ApiResponse
        result.data == "success"
    }

    def "createByUserRequest should create parent from user request"() {
        when:
        def result = parentService.createByUserRequest(userRequest, 12L)

        then:
        1 * parentRepository.save(_) >> { ParentEntity parent ->
            assert parent.id == userRequest.id
            assert parent.name == userRequest.name
            assert parent.surname == userRequest.surname
            assert parent.email == userRequest.email
            assert parent.password == userRequest.password
            assert parent.role == Role.PARENT
            assert parent.active == true
            parent
        }
        result instanceof ApiResponse
        result.data == "success"
    }

    def "linkStudent should link student to parent"() {
        given:
        def studentCode = "ABC12345"
        def parentId = 12L
        parentEntity.setStudents([])

        when:
        def result = parentService.linkStudent(studentCode, parentId)

        then:
        1 * studentRepository.findByParentInvadeCodeAndActiveTrue(studentCode) >> studentEntity
        1 * parentRepository.findById(parentId) >> Optional.of(parentEntity)
        1 * parentRepository.save(_) >> { ParentEntity parent ->
            assert parent.students.contains(studentEntity)
            parent
        }
        result instanceof ApiResponse
        result.data == "success"
    }

    def "linkStudent should throw NotFoundException when parent not found"() {
        given:
        def studentCode = "ABC12345"
        def parentId = 99L

        when:
        parentService.linkStudent(studentCode, parentId)

        then:
        1 * studentRepository.findByParentInvadeCodeAndActiveTrue(studentCode) >> studentEntity
        1 * parentRepository.findById(parentId) >> Optional.empty()
        0 * parentRepository.save(_)
        thrown(NotFoundException)
    }

    def "linkStudent should handle null student code"() {
        given:
        def studentCode = null
        def parentId = 12L

        when:
        def result = parentService.linkStudent(studentCode, parentId) // ✅ result dəyişənini təyin et

        then:
        1 * studentRepository.findByParentInvadeCodeAndActiveTrue(studentCode) >> null
        1 * parentRepository.findById(parentId) >> Optional.of(parentEntity)
        1 * parentRepository.save(_)
        result instanceof ApiResponse
        result.data == "success" // ✅ data-nı da yoxla
    }

    def "getAllParents should return all parents"() {
        given:
        def parentList = [parentEntity, parentEntity]

        when:
        def result = parentService.getAllParents()

        then:
        1 * parentRepository.findAll() >> parentList
        2 * parentMapper.entityToResponse(_) >> parentResponse
        result instanceof ApiResponse
        result.data.size() == 2
    }

    def "getAllParents should return empty list when no parents"() {
        given:
        def emptyList = []

        when:
        def result = parentService.getAllParents()

        then:
        1 * parentRepository.findAll() >> emptyList
        0 * parentMapper.entityToResponse(_)
        result instanceof ApiResponse
        result.data == []
    }

    def "getParentById should return parent when exists"() {
        given:
        def parentId = 12L

        when:
        def result = parentService.getParentById(parentId)

        then:
        1 * parentRepository.findById(parentId) >> Optional.of(parentEntity)
        1 * parentMapper.entityToResponse(parentEntity) >> parentResponse
        result instanceof ApiResponse
        result.data == parentResponse
    }

    def "getParentById should throw NotFoundException when parent not found"() {
        given:
        def parentId = 99L

        when:
        parentService.getParentById(parentId)

        then:
        1 * parentRepository.findById(parentId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "getStudentsByParentId should return students for parent"() {
        given:
        def parentId = 12L
        parentEntity.setStudents([studentEntity])

        when:
        def result = parentService.getStudentsByParentId(parentId)

        then:
        1 * parentRepository.findById(parentId) >> Optional.of(parentEntity)
        result instanceof ApiResponse
        result.data == [studentEntity]
    }

    def "getStudentsByParentId should throw NotFoundException when parent not found"() {
        given:
        def parentId = 99L

        when:
        parentService.getStudentsByParentId(parentId)

        then:
        1 * parentRepository.findById(parentId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "getStudentsByParentId should return empty list when no students"() {
        given:
        def parentId = 12L
        parentEntity.setStudents([])

        when:
        def result = parentService.getStudentsByParentId(parentId)

        then:
        1 * parentRepository.findById(parentId) >> Optional.of(parentEntity)
        result instanceof ApiResponse
        result.data == []
    }

    def "hardDeleteById should delete parent"() {
        given:
        def parentId = 12L

        when:
        def result = parentService.hardDeleteById(parentId)

        then:
        1 * parentRepository.deleteById(parentId)
        result instanceof ApiResponse
        result.data == "success"
    }

    def "softDeleteById should deactivate parent"() {
        given:
        def parentId = 12L

        when:
        def result = parentService.softDeleteById(parentId)

        then:
        1 * parentRepository.findById(parentId) >> Optional.of(parentEntity)
        0 * parentRepository.deleteById(_)
        result instanceof ApiResponse
        result.data == "success"
        !parentEntity.active
    }

    def "softDeleteById should throw NotFoundException when parent not found"() {
        given:
        def parentId = 99L

        when:
        parentService.softDeleteById(parentId)

        then:
        1 * parentRepository.findById(parentId) >> Optional.empty()
        thrown(NotFoundException)
    }
}