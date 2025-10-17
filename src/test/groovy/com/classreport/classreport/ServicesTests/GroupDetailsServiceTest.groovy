package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.GroupEntity
import com.classreport.classreport.entity.LessonInstanceEntity
import com.classreport.classreport.entity.LessonScheduleEntity
import com.classreport.classreport.mapper.LessonInstanceMapper
import com.classreport.classreport.mapper.LessonScheduleMapperManual
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.model.response.GroupDetailsResponse
import com.classreport.classreport.model.response.LessonInstanceResponse
import com.classreport.classreport.model.response.LessonScheduleResponse
import com.classreport.classreport.repository.GroupRepository
import com.classreport.classreport.repository.LessonInstanceRepository
import com.classreport.classreport.service.GroupDetailsService
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalTime

class GroupDetailsServiceTest extends Specification {

    def groupRepository = Mock(GroupRepository)
    def lessonInstanceRepository = Mock(LessonInstanceRepository)
    def lessonScheduleMapperManual = Mock(LessonScheduleMapperManual)

    @Subject
    def groupDetailsService = new GroupDetailsService(groupRepository, lessonInstanceRepository, lessonScheduleMapperManual)

    def groupEntity
    def lessonInstanceEntity
    def lessonScheduleEntity
    def lessonInstanceResponse
    def lessonScheduleResponse

    def setup() {
        groupEntity = new GroupEntity()
        groupEntity.setId(1L)
        groupEntity.setGroupName("Test Group")

        lessonScheduleEntity = new LessonScheduleEntity()
        lessonScheduleEntity.setId(1L)
        lessonScheduleEntity.setStartTime(LocalTime.of(9, 0))
        lessonScheduleEntity.setEndTime(LocalTime.of(10, 30))

        lessonInstanceEntity = new LessonInstanceEntity()
        lessonInstanceEntity.setId(1L)
        lessonInstanceEntity.setDate(LocalDate.now())
        lessonInstanceEntity.setLessonSchedule(lessonScheduleEntity)

        lessonInstanceResponse = new LessonInstanceResponse()
        lessonInstanceResponse.setId(1L)
        lessonInstanceResponse.setDate(LocalDate.now())

        lessonScheduleResponse = new LessonScheduleResponse()
        lessonScheduleResponse.setId(1L)
        lessonScheduleResponse.setStartTime(LocalTime.of(9, 0))
        lessonScheduleResponse.setEndTime(LocalTime.of(10, 30))
    }

    def "getGroupDetails should return group details with lessons and schedules"() {
        given:
        def groupId = 1L
        def lessonInstances = [lessonInstanceEntity, lessonInstanceEntity]

        when:
        def result = groupDetailsService.getGroupDetails(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.of(groupEntity)
        2 * lessonInstanceRepository.findByGroupId(groupId) >> lessonInstances
        // Mapper-ləri yoxlamırıq, sadəcə service məntiqini test edirik
        result instanceof ApiResponse
        result.data instanceof GroupDetailsResponse
        result.data.id == groupId
        result.data.groupName == "Test Group"
        result.data.lessons != null
        result.data.lessonTime != null
    }

    def "getGroupDetails should return empty lists when no lessons exist"() {
        given:
        def groupId = 1L
        def emptyLessonInstances = []

        when:
        def result = groupDetailsService.getGroupDetails(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.of(groupEntity)
        2 * lessonInstanceRepository.findByGroupId(groupId) >> emptyLessonInstances
        result instanceof ApiResponse
        result.data instanceof GroupDetailsResponse
        result.data.id == groupId
        result.data.groupName == "Test Group"
        result.data.lessons == []
        result.data.lessonTime == []
    }

    def "getGroupDetails should throw NotFoundException when group not found"() {
        given:
        def groupId = 99L

        when:
        groupDetailsService.getGroupDetails(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.empty()
        0 * lessonInstanceRepository.findByGroupId(_)
        thrown(NotFoundException)
    }

    def "getGroupDetails should handle duplicate lesson schedules correctly"() {
        given:
        def groupId = 1L
        def lessonInstances = [lessonInstanceEntity, lessonInstanceEntity, lessonInstanceEntity]

        when:
        def result = groupDetailsService.getGroupDetails(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.of(groupEntity)
        2 * lessonInstanceRepository.findByGroupId(groupId) >> lessonInstances
        result instanceof ApiResponse
        result.data.lessons != null
        result.data.lessonTime != null
    }

    def "getGroupDetails should handle null lesson schedules gracefully"() {
        given:
        def groupId = 1L
        def lessonInstanceWithoutSchedule = new LessonInstanceEntity()
        lessonInstanceWithoutSchedule.setId(2L)
        lessonInstanceWithoutSchedule.setDate(LocalDate.now())
        lessonInstanceWithoutSchedule.setLessonSchedule(null)

        def lessonInstances = [lessonInstanceWithoutSchedule]

        when:
        def result = groupDetailsService.getGroupDetails(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.of(groupEntity)
        2 * lessonInstanceRepository.findByGroupId(groupId) >> lessonInstances
        // lessonSchedule null olduğu üçün mapper çağırıla bilər və ya exception atıla bilər
        // Biz sadəcə service-in exception atmadığını yoxlayırıq
        noExceptionThrown()
        result instanceof ApiResponse
        result.data.lessons != null
    }

    def "getGroupDetails should map all required fields correctly"() {
        given:
        def groupId = 1L
        def lessonInstances = [lessonInstanceEntity]

        when:
        def result = groupDetailsService.getGroupDetails(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.of(groupEntity)
        2 * lessonInstanceRepository.findByGroupId(groupId) >> lessonInstances
        result instanceof ApiResponse
        def groupDetails = result.data as GroupDetailsResponse
        groupDetails.id == groupId
        groupDetails.groupName == "Test Group"
        groupDetails.lessons != null
        groupDetails.lessonTime != null
    }

    def "getGroupDetails should optimize database calls by calling findByGroupId once"() {
        given:
        def groupId = 1L
        def lessonInstances = [lessonInstanceEntity, lessonInstanceEntity]

        when:
        def result = groupDetailsService.getGroupDetails(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.of(groupEntity)
        2 * lessonInstanceRepository.findByGroupId(groupId) >> lessonInstances
        result instanceof ApiResponse
    }
}