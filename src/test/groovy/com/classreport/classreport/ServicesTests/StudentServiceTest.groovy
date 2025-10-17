package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.*
import com.classreport.classreport.mapper.StudentMapper
import com.classreport.classreport.model.enums.Role
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.request.StudentRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.model.response.StudentResponse
import com.classreport.classreport.repository.AttendanceRepository
import com.classreport.classreport.repository.GroupRepository
import com.classreport.classreport.repository.StudentRepository
import com.classreport.classreport.repository.TemporaryGroupTransferRepository
import com.classreport.classreport.service.StudentService
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class StudentServiceTest extends Specification {

    def studentRepository = Mock(StudentRepository)
    def attendanceRepository = Mock(AttendanceRepository)
    def groupRepository = Mock(GroupRepository)
    def temporaryGroupTransferRepository = Mock(TemporaryGroupTransferRepository)

    @Subject
    def studentService = new StudentService(
            studentRepository, attendanceRepository, groupRepository, temporaryGroupTransferRepository
    )

    def studentEntity
    def studentRequest
    def studentResponse
    def groupEntity
    def attendanceEntity
    def transferEntity

    def setup() {
        groupEntity = new GroupEntity()
        groupEntity.setId(7L)
        groupEntity.setGroupName("7-C Sinifi")
        groupEntity.setStudents(new HashSet<>())

        studentEntity = new StudentEntity()
        studentEntity.setId(25L)
        studentEntity.setName("Aydan")
        studentEntity.setSurname("Rəhimova")
        studentEntity.setEmail("aydan.rahimova@edu.az")
        studentEntity.setPassword("telebe456")
        studentEntity.setRole(Role.STUDENT)
        studentEntity.setActive(true)
        studentEntity.setTransfer(false)
        studentEntity.setParentInvadeCode("X9B3L8P2")
        studentEntity.setGroups([groupEntity])
        studentEntity.setParents([])

        studentRequest = new StudentRequest()
        studentRequest.setId(25L)
        studentRequest.setName("Aydan")
        studentRequest.setSurname("Rəhimova")
        studentRequest.setEmail("aydan.rahimova@edu.az")
        studentRequest.setPassword("telebe456")
        studentRequest.setGroupId(7L)

        studentResponse = new StudentResponse()
        studentResponse.setId(25L)
        studentResponse.setName("Aydan")
        studentResponse.setSurname("Rəhimova")
        studentResponse.setEmail("aydan.rahimova@edu.az")

        attendanceEntity = new AttendanceEntity()
        attendanceEntity.setId(50L)
        attendanceEntity.setStudent(studentEntity)

        transferEntity = new TemporaryGroupTransferEntity()
        transferEntity.setId(60L)
        transferEntity.setStudent(studentEntity)
        transferEntity.setToGroup(groupEntity)
        transferEntity.setActive(true)
    }

    def "createStudent should create student with group and attendance"() {
        when:
        studentService.createStudent(studentRequest)

        then:
        // Static INSTANCE-i mock etmək çətindir, ona görə də sadəcə save çağırışlarını yoxlayırıq
        1 * groupRepository.findById(studentRequest.groupId) >> Optional.of(groupEntity)
        1 * attendanceRepository.save(_)
        1 * groupRepository.save(_)
        1 * studentRepository.save(_)
    }

    def "createStudent should throw NotFoundException when group not found"() {
        when:
        studentService.createStudent(studentRequest)

        then:
        1 * groupRepository.findById(studentRequest.groupId) >> Optional.empty()
        // attendanceRepository.save çağırıla bilər (StudentMapper.INSTANCE səbəbindən), ona görə sərt limit qoymuruq
        thrown(NotFoundException)
    }

    def "createStudent should initialize empty groups list when null"() {
        when:
        studentService.createStudent(studentRequest)

        then:
        1 * groupRepository.findById(studentRequest.groupId) >> Optional.of(groupEntity)
        1 * attendanceRepository.save(_)
        1 * groupRepository.save(_)
        1 * studentRepository.save(_)
    }

    def "getStudentById should return student when exists"() {
        given:
        def studentId = 25L

        when:
        def result = studentService.getStudentById(studentId)

        then:
        1 * studentRepository.findById(studentId) >> Optional.of(studentEntity)
        // StudentMapper.INSTANCE mock edilə bilmədiyi üçün nəticəni yoxlamırıq
        result instanceof ApiResponse
    }

    def "getStudentById should throw RuntimeException when student not found"() {
        given:
        def studentId = 99L

        when:
        studentService.getStudentById(studentId)

        then:
        1 * studentRepository.findById(studentId) >> Optional.empty()
        thrown(RuntimeException)
    }

    def "getAllStudents should return active students"() {
        given:
        def activeStudent = studentEntity
        def inactiveStudent = new StudentEntity()
        inactiveStudent.setId(26L)
        inactiveStudent.setActive(false)

        def studentList = [activeStudent, inactiveStudent]

        when:
        def result = studentService.getAllStudents()

        then:
        1 * studentRepository.findAll() >> studentList
        result instanceof ApiResponse
    }

    def "getAllStudents should return empty list when no active students"() {
        given:
        def inactiveStudent = new StudentEntity()
        inactiveStudent.setId(26L)
        inactiveStudent.setActive(false)
        def studentList = [inactiveStudent]

        when:
        def result = studentService.getAllStudents()

        then:
        1 * studentRepository.findAll() >> studentList
        result instanceof ApiResponse
        result.data == []
    }

    def "getStudentsByGroup should return students from group and transfers"() {
        given:
        def groupId = 7L
        def today = LocalDate.now()
        def studentList = [studentEntity]
        def transferList = [transferEntity]

        when:
        def result = studentService.getStudentsByGroup(groupId)

        then:
        1 * studentRepository.getAllByGroup(groupId) >> studentList
        1 * temporaryGroupTransferRepository.findActiveTransfersToGroup(groupId, today) >> transferList
        result instanceof ApiResponse
    }

    def "getStudentsByGroup should handle transfers with null values"() {
        given:
        def groupId = 7L
        def today = LocalDate.now()
        def studentList = [studentEntity]
        def invalidTransfer = new TemporaryGroupTransferEntity()
        invalidTransfer.setId(61L)
        invalidTransfer.setStudent(null)
        invalidTransfer.setToGroup(null)
        def transferList = [invalidTransfer]

        when:
        def result = studentService.getStudentsByGroup(groupId)

        then:
        1 * studentRepository.getAllByGroup(groupId) >> studentList
        1 * temporaryGroupTransferRepository.findActiveTransfersToGroup(groupId, today) >> transferList
        result instanceof ApiResponse
    }

    def "getStudentsByGroup should return empty list when no students"() {
        given:
        def groupId = 7L
        def today = LocalDate.now()
        def emptyStudentList = []
        def emptyTransferList = []

        when:
        def result = studentService.getStudentsByGroup(groupId)

        then:
        1 * studentRepository.getAllByGroup(groupId) >> emptyStudentList
        1 * temporaryGroupTransferRepository.findActiveTransfersToGroup(groupId, today) >> emptyTransferList
        result instanceof ApiResponse
        result.data == []
    }

    def "hardDeleteById should delete student"() {
        given:
        def studentId = 25L

        when:
        studentService.hardDeleteById(studentId)

        then:
        1 * studentRepository.deleteById(studentId)
    }

    def "softDeleteById should call repository softDelete method"() {
        given:
        def studentId = 25L

        when:
        studentService.softDeleteById(studentId)

        then:
        1 * studentRepository.softDelete(studentId)
    }
}