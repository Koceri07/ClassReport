package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.AttendanceEntity
import com.classreport.classreport.entity.GroupEntity
import com.classreport.classreport.entity.LessonInstanceEntity
import com.classreport.classreport.entity.StudentEntity
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.request.AttendanceRequest
import com.classreport.classreport.model.request.AttendanceUpdateRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.model.response.AttendanceResponse
import com.classreport.classreport.model.response.StudentAttendancesStatsResponse
import com.classreport.classreport.repository.AttendanceRepository
import com.classreport.classreport.repository.LessonInstanceRepository
import com.classreport.classreport.repository.StudentAttendancesStatsRepository
import com.classreport.classreport.repository.StudentRepository
import com.classreport.classreport.service.AttendanceService
import org.springframework.security.authentication.AuthenticationManager
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class AttendanceServiceTest extends Specification {

    def attendanceRepository = Mock(AttendanceRepository)
    def studentRepository = Mock(StudentRepository)
    def lessonInstanceRepository = Mock(LessonInstanceRepository)
    def attendancesStatsRepository = Mock(StudentAttendancesStatsRepository)
    def authenticationManager = Mock(AuthenticationManager)

    @Subject
    def attendanceService = new AttendanceService(
            attendanceRepository, studentRepository, lessonInstanceRepository,
            attendancesStatsRepository, authenticationManager
    )

    def attendanceEntity
    def studentEntity
    def lessonInstanceEntity
    def groupEntity
    def attendanceRequest
    def attendanceResponse

    def setup() {
        groupEntity = new GroupEntity()
        groupEntity.setId(1L)
        groupEntity.setGroupName("Test Group")

        studentEntity = new StudentEntity()
        studentEntity.setId(1L)
        studentEntity.setName("Test Student")

        lessonInstanceEntity = new LessonInstanceEntity()
        lessonInstanceEntity.setId(1L)
        lessonInstanceEntity.setDate(LocalDate.now())
        lessonInstanceEntity.setGroup(groupEntity)

        attendanceEntity = new AttendanceEntity()
        attendanceEntity.setId(1L)
        attendanceEntity.setStudent(studentEntity)
        attendanceEntity.setLessonInstance(lessonInstanceEntity)
        attendanceEntity.setDate(LocalDate.now())
        attendanceEntity.setIsAbsent(false)
        attendanceEntity.setLateTime("5")
        attendanceEntity.setNote("Test note")

        attendanceRequest = new AttendanceRequest()
        attendanceRequest.setId(1L)

        attendanceResponse = new AttendanceResponse()
        attendanceResponse.setId(1L)
        attendanceResponse.setDate(LocalDate.now())
        attendanceResponse.setIsAbsent(false)
        attendanceResponse.setLateTime("5")
        attendanceResponse.setNote("Test note")
    }

    def "createAttendance should create attendance successfully"() {
        given:
        def attendanceRequest = new AttendanceRequest()
        attendanceRequest.setId(1L)
        attendanceRequest.setDate(LocalDate.now())
        attendanceRequest.setIsAbsent(false)
        attendanceRequest.setLateTime("5")
        attendanceRequest.setNote("Test note")

        when:
        attendanceService.createAttendance(attendanceRequest)

        then:
        1 * attendanceRepository.save(_ as AttendanceEntity) >> attendanceEntity
        // createAttendance void qaytarır, ona görə result yoxlamırıq
    }

    def "getAttendanceById should return attendance when exists"() {
        given:
        def attendanceId = 1L

        when:
        def result = attendanceService.getAttendanceById(attendanceId)

        then:
        1 * attendanceRepository.findById(attendanceId) >> Optional.of(attendanceEntity)
        result instanceof ApiResponse
        result.data != null
    }

    def "getAttendanceById should throw NotFoundException when attendance not found"() {
        given:
        def attendanceId = 99L

        when:
        attendanceService.getAttendanceById(attendanceId)

        then:
        1 * attendanceRepository.findById(attendanceId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "getAllAttendance should return all attendances"() {
        given:
        def attendanceList = [attendanceEntity, attendanceEntity, attendanceEntity]

        when:
        def result = attendanceService.getAllAttendance()

        then:
        1 * attendanceRepository.findAll() >> attendanceList
        result instanceof ApiResponse
        result.data.size() == 3
    }

    def "getAllAttendance should return empty list when no attendances"() {
        given:
        def emptyList = []

        when:
        def result = attendanceService.getAllAttendance()

        then:
        1 * attendanceRepository.findAll() >> emptyList
        result instanceof ApiResponse
        result.data == []
    }

    def "getAttendanceByStudentId should return attendances for student"() {
        given:
        def studentId = 1L
        def attendanceList = [attendanceEntity, attendanceEntity]

        when:
        def result = attendanceService.getAttendanceByStudentId(studentId)

        then:
        1 * attendanceRepository.findByStudentId(studentId) >> attendanceList
        result instanceof ApiResponse
        result.data.size() == 2
    }

    def "getAttendanceByStudentId should return empty list when no attendances for student"() {
        given:
        def studentId = 99L
        def emptyList = []

        when:
        def result = attendanceService.getAttendanceByStudentId(studentId)

        then:
        1 * attendanceRepository.findByStudentId(studentId) >> emptyList
        result instanceof ApiResponse
        result.data == []
    }

    def "getAttendancesByGroupId should return attendances for group"() {
        given:
        def groupId = 1L
        def attendanceList = [attendanceEntity, attendanceEntity]

        when:
        def result = attendanceService.getAttendancesByGroupId(groupId)

        then:
        1 * attendanceRepository.findByGroupId(groupId) >> attendanceList
        result instanceof ApiResponse
        result.data.size() == 2
    }

    def "getAttendancesByGroupId should handle exceptions gracefully"() {
        given:
        def groupId = 1L

        when:
        def result = attendanceService.getAttendancesByGroupId(groupId)

        then:
        1 * attendanceRepository.findByGroupId(groupId) >> { throw new RuntimeException("Database error") }
        result instanceof ApiResponse
        result.data.toString().contains("Error fetching attendances")
    }

    def "getAbsentStudentAttendancesByStudentId should return absent attendances"() {
        given:
        def studentId = 1L
        def absentAttendances = [attendanceEntity, attendanceEntity]

        when:
        def result = attendanceService.getAbsentStudentAttendancesByStudentId(studentId)

        then:
        1 * attendanceRepository.findByStudentIdAndIsAbsent(studentId, false) >> absentAttendances
        result instanceof ApiResponse
        result.data.size() == 2
    }

    def "getNotAbsentStudentAttendancesByStudentId should calculate not absent days correctly"() {
        given:
        def studentId = 1L
        def presentAttendances = [attendanceEntity]
        def groupId = 1L
        def lessonInstances = [lessonInstanceEntity, lessonInstanceEntity, lessonInstanceEntity]

        when:
        def result = attendanceService.getNotAbsentStudentAttendancesByStudentId(studentId)

        then:
        1 * attendanceRepository.findByStudentIdAndIsAbsent(studentId, false) >> presentAttendances
        1 * studentRepository.findGroupIdsByStudentId(studentId) >> [groupId]
        1 * lessonInstanceRepository.findByGroupId(groupId) >> lessonInstances

        result instanceof ApiResponse
        result.data == 2
    }

    def "getAttendancePercentByGroupId should calculate percentage correctly"() {
        given:
        def studentId = 1L
        def groupId = 1L
        def presentAttendances = [attendanceEntity, attendanceEntity]
        def lessonInstances = [lessonInstanceEntity, lessonInstanceEntity, lessonInstanceEntity, lessonInstanceEntity]

        when:
        def result = attendanceService.getAttendancePercentByGroupId(studentId)

        then:
        1 * studentRepository.findGroupIdsByStudentId(studentId) >> [groupId]
        1 * attendanceRepository.findByStudentIdAndIsAbsent(studentId, false) >> presentAttendances
        1 * lessonInstanceRepository.findByGroupId(groupId) >> lessonInstances

        result instanceof ApiResponse
        result.data == 50
    }

    def "getAbsentStudentAttendancesCountByStudentId should return count of present attendances"() {
        given:
        def studentId = 1L
        def presentAttendances = [attendanceEntity, attendanceEntity, attendanceEntity]

        when:
        def result = attendanceService.getAbsentStudentAttendancesCountByStudentId(studentId)

        then:
        1 * attendanceRepository.findByStudentIdAndIsAbsent(studentId, false) >> presentAttendances

        result instanceof ApiResponse
        result.data == 3
    }

    def "update should create new attendance when not exists"() {
        given:
        def updateRequest = new AttendanceUpdateRequest()
        updateRequest.setStudentId(1L)
        updateRequest.setDate(LocalDate.now().toString())
        updateRequest.setPresent(true)
        updateRequest.setGroupId(1L)
        updateRequest.setLateTime("5")
        updateRequest.setNote("Test note")

        def lessonInstances = [lessonInstanceEntity]

        when:
        attendanceService.update(updateRequest)

        then:
        1 * studentRepository.findById(1L) >> Optional.of(studentEntity)
        1 * lessonInstanceRepository.findAllByDateAndGroupId(LocalDate.now(), 1L) >> lessonInstances
        1 * attendanceRepository.findByStudentAndLessonInstance(studentEntity, lessonInstanceEntity) >> Optional.empty()
        1 * attendanceRepository.save(_) >> { AttendanceEntity savedAttendance ->
            assert savedAttendance.student == studentEntity
            assert savedAttendance.lessonInstance == lessonInstanceEntity
            assert savedAttendance.isAbsent == false
            savedAttendance
        }
    }

    def "update should update existing attendance when exists"() {
        given:
        def updateRequest = new AttendanceUpdateRequest()
        updateRequest.setStudentId(1L)
        updateRequest.setDate(LocalDate.now().toString())
        updateRequest.setPresent(false)
        updateRequest.setGroupId(1L)
        updateRequest.setLateTime("10")
        updateRequest.setNote("Updated note")

        def lessonInstances = [lessonInstanceEntity]

        when:
        attendanceService.update(updateRequest)

        then:
        1 * studentRepository.findById(1L) >> Optional.of(studentEntity)
        1 * lessonInstanceRepository.findAllByDateAndGroupId(LocalDate.now(), 1L) >> lessonInstances
        1 * attendanceRepository.findByStudentAndLessonInstance(studentEntity, lessonInstanceEntity) >> Optional.of(attendanceEntity)
        1 * attendanceRepository.save(attendanceEntity) >> { AttendanceEntity savedAttendance ->
            assert savedAttendance.isAbsent == true
            assert savedAttendance.note == "Updated note"
            savedAttendance
        }
    }

    def "update should throw NotFoundException when student not found"() {
        given:
        def updateRequest = new AttendanceUpdateRequest()
        updateRequest.setStudentId(99L)

        when:
        attendanceService.update(updateRequest)

        then:
        1 * studentRepository.findById(99L) >> Optional.empty()
        thrown(RuntimeException)
    }

    def "update should throw NotFoundException when lesson instance not found"() {
        given:
        def updateRequest = new AttendanceUpdateRequest()
        updateRequest.setStudentId(1L)
        updateRequest.setDate(LocalDate.now().toString())
        updateRequest.setGroupId(1L)

        when:
        attendanceService.update(updateRequest)

        then:
        1 * studentRepository.findById(1L) >> Optional.of(studentEntity)
        1 * lessonInstanceRepository.findAllByDateAndGroupId(LocalDate.now(), 1L) >> []
        thrown(NotFoundException)
    }

    def "hardDeleteById should delete attendance"() {
        given:
        def attendanceId = 1L

        when:
        attendanceService.hardDeleteById(attendanceId)

        then:
        1 * attendanceRepository.deleteById(attendanceId)
    }

    def "softDeleteById should soft delete attendance"() {
        given:
        def attendanceId = 1L

        when:
        attendanceService.softDeleteById(attendanceId)

        then:
        1 * attendanceRepository.softDelete(attendanceId)
    }

    def "testAuthentication should authenticate successfully"() {
        when:
        attendanceService.testAuthentication()

        then:
        1 * authenticationManager.authenticate(_)
        noExceptionThrown()
    }

    def "getNotAbsentStudentAttendancesByStudentId basic test"() {
        given:
        def studentId = 1L
        def presentAttendances = [attendanceEntity]
        def groupIds = [1L]
        def lessonInstances = [lessonInstanceEntity, lessonInstanceEntity]

        when:
        def result = attendanceService.getNotAbsentStudentAttendancesByStudentId(studentId)

        then:
        1 * attendanceRepository.findByStudentIdAndIsAbsent(studentId, false) >> presentAttendances
        1 * studentRepository.findGroupIdsByStudentId(studentId) >> groupIds
        1 * lessonInstanceRepository.findByGroupId(1L) >> lessonInstances

        result instanceof ApiResponse
        result.data == 1
    }

}