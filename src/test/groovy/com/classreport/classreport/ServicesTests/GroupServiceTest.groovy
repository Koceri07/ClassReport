package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.GroupEntity
import com.classreport.classreport.entity.LessonScheduleEntity
import com.classreport.classreport.entity.TeacherEntity
import com.classreport.classreport.mapper.GroupMapper
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.request.GroupRequest
import com.classreport.classreport.model.request.LessonScheduleRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.repository.GroupRepository
import com.classreport.classreport.repository.LessonScheduleRepository
import com.classreport.classreport.repository.TeacherRepository
import com.classreport.classreport.service.GroupService
import com.classreport.classreport.service.TeacherService
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification
import spock.lang.Subject

import java.time.DayOfWeek
import java.time.LocalTime

class GroupServiceTest extends Specification {

    def groupRepository = Mock(GroupRepository)
    def lessonScheduleRepository = Mock(LessonScheduleRepository)
    def teacherService = Mock(TeacherService)
    def teacherRepository = Mock(TeacherRepository)

    @Subject
    def groupService = new GroupService(groupRepository, lessonScheduleRepository, teacherService, teacherRepository)

    def teacherEntity
    def groupEntity
    def lessonScheduleEntity
    def groupRequest
    def userDetails

    def setup() {
        teacherEntity = new TeacherEntity()
        teacherEntity.setId(1L)
        teacherEntity.setName("Test Teacher")

        lessonScheduleEntity = new LessonScheduleEntity()
        lessonScheduleEntity.setId(1L)
        lessonScheduleEntity.setStartTime(LocalTime.of(9, 0))
        lessonScheduleEntity.setEndTime(LocalTime.of(10, 30))
        lessonScheduleEntity.setDaysOfWeek([DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY] as Set)

        groupEntity = new GroupEntity()
        groupEntity.setId(1L)
        groupEntity.setGroupName("Test Group")
        groupEntity.setActive(true)
        groupEntity.setTeacher(teacherEntity)
        groupEntity.setLessonSchedule(lessonScheduleEntity)

        def lessonScheduleRequest = new LessonScheduleRequest()
        lessonScheduleRequest.setStartTime(LocalTime.of(9, 0))
        lessonScheduleRequest.setEndTime(LocalTime.of(10, 30))
        lessonScheduleRequest.setDaysOfWeek([DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY] as Set)

        groupRequest = new GroupRequest()
        groupRequest.setId(1L)
        groupRequest.setGroupName("Test Group")
        groupRequest.setLessonSchedule(lessonScheduleRequest)

        userDetails = Mock(UserDetails)
        userDetails.getUsername() >> "teacher@example.com"
    }

    def "createGroup should create group with lesson schedule successfully"() {
        when:
        groupService.createGroup(groupRequest, userDetails)

        then:
        1 * teacherService.getTeacherIdFromToken(userDetails) >> 1L
        1 * teacherRepository.findTeacherEntityById(1L) >> teacherEntity
        1 * groupRepository.save(_) >> { GroupEntity savedGroup ->
            assert savedGroup.groupName == "Test Group"
            assert savedGroup.active == true
            assert savedGroup.teacher == teacherEntity
            assert savedGroup.lessonSchedule != null
            assert savedGroup.lessonSchedule.startTime == LocalTime.of(9, 0)
            assert savedGroup.lessonSchedule.endTime == LocalTime.of(10, 30)
            assert savedGroup.lessonSchedule.daysOfWeek == [DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY] as Set
            assert savedGroup.lessonSchedule.group == savedGroup // two-way relationship
            savedGroup
        }
    }

    def "createGroup should throw NullPointerException when lesson schedule is null"() {
        given:
        def groupRequestWithoutSchedule = new GroupRequest()
        groupRequestWithoutSchedule.setId(1L)
        groupRequestWithoutSchedule.setGroupName("Test Group")
        groupRequestWithoutSchedule.setLessonSchedule(null)

        when:
        groupService.createGroup(groupRequestWithoutSchedule, userDetails)

        then:
        thrown(NullPointerException)
    }

    def "getGroupById should return group when exists"() {
        given:
        def groupId = 1L

        when:
        def result = groupService.getGroupById(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.of(groupEntity)
        result instanceof ApiResponse
        result.data != null
    }

    def "getGroupById should throw NotFoundException when group not found"() {
        given:
        def groupId = 99L

        when:
        groupService.getGroupById(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "getAllGroups should return all groups"() {
        given:
        def groupList = [groupEntity, groupEntity, groupEntity]

        when:
        def result = groupService.getAllGroups()

        then:
        1 * groupRepository.findAll() >> groupList
        result instanceof ApiResponse
        result.data.size() == 3
    }

    def "getAllGroups should return empty list when no groups"() {
        given:
        def emptyList = []

        when:
        def result = groupService.getAllGroups()

        then:
        1 * groupRepository.findAll() >> emptyList
        result instanceof ApiResponse
        result.data == []
    }

    def "getAllGroupsByTeacherId should return groups for teacher"() {
        given:
        def teacherId = 1L
        def groupList = [groupEntity, groupEntity]

        when:
        def result = groupService.getAllGroupsByTeacherId(teacherId)

        then:
        1 * groupRepository.findByTeacher_Id(teacherId) >> groupList
        result instanceof ApiResponse
        result.data.size() == 2
    }

    def "getAllGroupsByTeacherId should return empty list when no groups for teacher"() {
        given:
        def teacherId = 99L
        def emptyList = []

        when:
        def result = groupService.getAllGroupsByTeacherId(teacherId)

        then:
        1 * groupRepository.findByTeacher_Id(teacherId) >> emptyList
        result instanceof ApiResponse
        result.data == []
    }

    def "hardDeleteById should call repository deleteById method"() {
        given:
        def groupId = 1L

        when:
        groupService.hardDeleteById(groupId)

        then:
        1 * groupRepository.deleteById(groupId)
    }

    def "softDeleteById should call repository softDelete method"() {
        given:
        def groupId = 1L

        when:
        groupService.softDeleteById(groupId)

        then:
        1 * groupRepository.softDelete(groupId)
    }

    def "testGroupLessonSchedule should retrieve group with lesson schedule"() {
        given:
        def groupId = 1L

        when:
        groupService.testGroupLessonSchedule(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.of(groupEntity)
        noExceptionThrown()
    }

    def "testGroupLessonSchedule should handle when group not found"() {
        given:
        def groupId = 99L

        when:
        groupService.testGroupLessonSchedule(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.empty()
        thrown(NoSuchElementException)
    }

    def "createGroup should handle teacher not found scenario gracefully"() {
        given:
        def teacherId = 99L

        when:
        groupService.createGroup(groupRequest, userDetails)

        then:
        1 * teacherService.getTeacherIdFromToken(userDetails) >> teacherId
        1 * teacherRepository.findTeacherEntityById(teacherId) >> null
        // Service handles null teacher gracefully, no exception expected
        noExceptionThrown()
    }

    def "createGroup should handle when teacher service returns null"() {
        when:
        groupService.createGroup(groupRequest, userDetails)

        then:
        1 * teacherService.getTeacherIdFromToken(userDetails) >> null
        // Service handles null teacherId gracefully, no exception expected
        noExceptionThrown()
    }
}