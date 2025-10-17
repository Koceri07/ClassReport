package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.AttendanceEntity
import com.classreport.classreport.entity.LessonInstanceEntity
import com.classreport.classreport.entity.StudentEntity
import com.classreport.classreport.entity.GroupEntity
import com.classreport.classreport.entity.TemporaryGroupTransferEntity
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.repository.*
import com.classreport.classreport.service.StudentTransferService
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class StudentTransferServiceTest extends Specification {

    def studentRepository = Mock(StudentRepository)
    def groupRepository = Mock(GroupRepository)
    def lessonInstanceRepository = Mock(LessonInstanceRepository)
    def attendanceRepository = Mock(AttendanceRepository)
    def temporaryGroupTransferRepository = Mock(TemporaryGroupTransferRepository)

    @Subject
    def studentTransferService = new StudentTransferService(
            studentRepository, groupRepository, lessonInstanceRepository,
            attendanceRepository, temporaryGroupTransferRepository
    )

    def studentEntity
    def targetGroup
    def sourceGroup
    def lessonInstance
    def transferEntity

    def setup() {
        sourceGroup = new GroupEntity()
        sourceGroup.setId(10L)
        sourceGroup.setGroupName("Köhnə Qrup")

        targetGroup = new GroupEntity()
        targetGroup.setId(20L)
        targetGroup.setGroupName("Yeni Qrup")

        studentEntity = new StudentEntity()
        studentEntity.setId(5L)
        studentEntity.setName("Nərmin")
        studentEntity.setSurname("Quliyeva")
        studentEntity.setTransfer(false)
        studentEntity.setGroups([sourceGroup])

        lessonInstance = new LessonInstanceEntity()
        lessonInstance.setId(30L)
        lessonInstance.setDate(LocalDate.now())

        transferEntity = new TemporaryGroupTransferEntity()
        transferEntity.setId(40L)
        transferEntity.setStudent(studentEntity)
        transferEntity.setFromGroup(sourceGroup)
        transferEntity.setToGroup(targetGroup)
        transferEntity.setStartDate(LocalDate.now())
        transferEntity.setEndDate(LocalDate.now().plusDays(1))
        transferEntity.setActive(true)
    }

    def "transferStudent should successfully transfer student to target group"() {
        given:
        def studentId = 5L
        def targetGroupId = 20L
        def lessonInstances = [lessonInstance]

        when:
        def result = studentTransferService.transferStudent(studentId, targetGroupId)

        then:
        1 * studentRepository.findById(studentId) >> Optional.of(studentEntity)
        1 * groupRepository.findById(targetGroupId) >> Optional.of(targetGroup)
        1 * lessonInstanceRepository.findByGroupId(targetGroupId) >> lessonInstances
        1 * studentRepository.save(studentEntity) >> { StudentEntity savedStudent ->
            assert savedStudent.transfer == true
            assert savedStudent.groups.contains(targetGroup)
            savedStudent
        }
        1 * attendanceRepository.save(_ as AttendanceEntity) >> { AttendanceEntity attendance ->
            assert attendance.student == studentEntity
            assert attendance.lessonInstance == lessonInstance
            assert attendance.date == lessonInstance.date
            assert attendance.isAbsent == false
            attendance
        }
        1 * temporaryGroupTransferRepository.save(_ as TemporaryGroupTransferEntity) >> { TemporaryGroupTransferEntity transfer ->
            assert transfer.student == studentEntity
            assert transfer.fromGroup == sourceGroup
            assert transfer.toGroup == targetGroup
            assert transfer.startDate == LocalDate.now()
            assert transfer.endDate == LocalDate.now().plusDays(1)
            assert transfer.active == true
            transfer
        }

        result instanceof ApiResponse
        result.data == "success"
    }

    def "transferStudent should throw NotFoundException when student not found"() {
        given:
        def studentId = 99L
        def targetGroupId = 20L

        when:
        studentTransferService.transferStudent(studentId, targetGroupId)

        then:
        1 * studentRepository.findById(studentId) >> Optional.empty()
        0 * groupRepository.findById(_)
        thrown(NotFoundException)
    }

    def "transferStudent should throw NotFoundException when group not found"() {
        given:
        def studentId = 5L
        def targetGroupId = 99L

        when:
        studentTransferService.transferStudent(studentId, targetGroupId)

        then:
        1 * studentRepository.findById(studentId) >> Optional.of(studentEntity)
        1 * groupRepository.findById(targetGroupId) >> Optional.empty()
        0 * lessonInstanceRepository.findByGroupId(_)
        thrown(NotFoundException)
    }

    def "transferStudent should not add duplicate group to student"() {
        given:
        def studentId = 5L
        def targetGroupId = 20L
        studentEntity.getGroups().add(targetGroup) // Artıq qrup var

        when:
        def result = studentTransferService.transferStudent(studentId, targetGroupId)

        then:
        1 * studentRepository.findById(studentId) >> Optional.of(studentEntity)
        1 * groupRepository.findById(targetGroupId) >> Optional.of(targetGroup)
        1 * lessonInstanceRepository.findByGroupId(targetGroupId) >> [lessonInstance]
        0 * studentRepository.save(_) // Yenidən save etməməli
        1 * attendanceRepository.save(_)
        1 * temporaryGroupTransferRepository.save(_)

        result instanceof ApiResponse
    }

    def "checkTransferDays should deactivate expired transfers"() {
        given:
        def expiredTransfers = [transferEntity]
        def today = LocalDate.now()

        when:
        studentTransferService.checkTransferDays()

        then:
        1 * temporaryGroupTransferRepository.findAllByEndDate(today) >> expiredTransfers
        1 * temporaryGroupTransferRepository.save(_) >> { TemporaryGroupTransferEntity transfer ->
            assert transfer.active == false
            transfer
        }
    }

    def "checkTransferDays should handle empty transfer list"() {
        given:
        def emptyTransfers = []
        def today = LocalDate.now()

        when:
        studentTransferService.checkTransferDays()

        then:
        1 * temporaryGroupTransferRepository.findAllByEndDate(today) >> emptyTransfers
        0 * temporaryGroupTransferRepository.save(_)
    }

    def "transferStudent should create attendance for each lesson instance"() {
        given:
        def studentId = 5L
        def targetGroupId = 20L
        def multipleLessons = [lessonInstance, lessonInstance, lessonInstance]

        when:
        studentTransferService.transferStudent(studentId, targetGroupId)

        then:
        1 * studentRepository.findById(studentId) >> Optional.of(studentEntity)
        1 * groupRepository.findById(targetGroupId) >> Optional.of(targetGroup)
        1 * lessonInstanceRepository.findByGroupId(targetGroupId) >> multipleLessons
        1 * studentRepository.save(studentEntity)
        3 * attendanceRepository.save(_ as AttendanceEntity)
        1 * temporaryGroupTransferRepository.save(_ as TemporaryGroupTransferEntity)
    }
}