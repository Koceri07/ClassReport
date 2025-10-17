package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.GroupEntity
import com.classreport.classreport.entity.LessonInstanceEntity
import com.classreport.classreport.entity.LessonScheduleEntity
import com.classreport.classreport.mapper.LessonInstanceMapper
import com.classreport.classreport.model.exception.AlreadyExistsException
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.exception.TodayHaventLessonException
import com.classreport.classreport.model.request.DateRequest
import com.classreport.classreport.model.request.LessonAddRequest
import com.classreport.classreport.model.request.LessonInstanceRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.model.response.LessonInstanceResponse
import com.classreport.classreport.model.response.LessonPreviewResponse
import com.classreport.classreport.repository.GroupRepository
import com.classreport.classreport.repository.LessonInstanceRepository
import com.classreport.classreport.repository.LessonScheduleRepository
import com.classreport.classreport.service.LessonInstanceService
import spock.lang.Specification
import spock.lang.Subject

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class LessonInstanceServiceTest extends Specification {

    def lessonInstanceRepository = Mock(LessonInstanceRepository)
    def lessonScheduleRepository = Mock(LessonScheduleRepository)
    def groupRepository = Mock(GroupRepository)

    @Subject
    def lessonInstanceService = new LessonInstanceService(
            lessonInstanceRepository,
            lessonScheduleRepository,
            groupRepository
    )

    def groupEntity
    def lessonScheduleEntity
    def lessonInstanceEntity
    def lessonInstanceRequest
    def lessonInstanceResponse

    def setup() {
        groupEntity = new GroupEntity()
        groupEntity.setId(1L)
        groupEntity.setGroupName("Test Group")

        lessonScheduleEntity = new LessonScheduleEntity()
        lessonScheduleEntity.setId(1L)
        lessonScheduleEntity.setDaysOfWeek([DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY] as Set)
        lessonScheduleEntity.setStartTime(LocalTime.of(9, 0))
        lessonScheduleEntity.setEndTime(LocalTime.of(10, 30))
        lessonScheduleEntity.setExceptionDates([] as Set)
        lessonScheduleEntity.setGroup(groupEntity)

        lessonInstanceEntity = new LessonInstanceEntity()
        lessonInstanceEntity.setId(1L)
        lessonInstanceEntity.setDate(LocalDate.now())
        lessonInstanceEntity.setLessonSchedule(lessonScheduleEntity)
        lessonInstanceEntity.setGroup(groupEntity)
        lessonInstanceEntity.setExtra(false)

        lessonInstanceRequest = new LessonInstanceRequest()
        lessonInstanceRequest.setId(1L)
        lessonInstanceRequest.setDate(LocalDate.now())

        lessonInstanceResponse = new LessonInstanceResponse()
        lessonInstanceResponse.setId(1L)
        lessonInstanceResponse.setDate(LocalDate.now())
    }

    def "createInstance should create lesson instance successfully"() {
        when:
        lessonInstanceService.createInstance(lessonInstanceRequest)

        then:
        1 * lessonInstanceRepository.save(_)
    }

    def "addExtraLesson should create extra lesson when not exists"() {
        given:
        def lessonAddRequest = Mock(LessonAddRequest)
        lessonAddRequest.getGroupId() >> 1L
        lessonAddRequest.getDate() >> LocalDate.now().plusDays(1)

        when:
        lessonInstanceService.addExtraLesson(lessonAddRequest)

        then:
        1 * groupRepository.findById(1L) >> Optional.of(groupEntity)
        1 * lessonInstanceRepository.existsByGroupAndDateAndIsExtraTrue(groupEntity, lessonAddRequest.getDate()) >> false
        1 * lessonScheduleRepository.findByGroupId(1L) >> lessonScheduleEntity
        1 * lessonInstanceRepository.save(_) >> { LessonInstanceEntity entity ->
            assert entity.group == groupEntity
            assert entity.date == lessonAddRequest.getDate()
            assert entity.extra == true
            assert entity.lessonSchedule == lessonScheduleEntity
        }
    }

    def "addExtraLesson should throw AlreadyExistsException when extra lesson already exists"() {
        given:
        def lessonAddRequest = Mock(LessonAddRequest)
        lessonAddRequest.getGroupId() >> 1L
        lessonAddRequest.getDate() >> LocalDate.now().plusDays(1)

        when:
        lessonInstanceService.addExtraLesson(lessonAddRequest)

        then:
        1 * groupRepository.findById(1L) >> Optional.of(groupEntity)
        1 * lessonInstanceRepository.existsByGroupAndDateAndIsExtraTrue(groupEntity, lessonAddRequest.getDate()) >> true
        thrown(AlreadyExistsException)
    }

    def "addExtraLesson should throw NotFoundException when group not found"() {
        given:
        def lessonAddRequest = Mock(LessonAddRequest)
        lessonAddRequest.getGroupId() >> 99L

        when:
        lessonInstanceService.addExtraLesson(lessonAddRequest)

        then:
        1 * groupRepository.findById(99L) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "addExtraLesson should handle when lessonSchedule is provided"() {
        given:
        def lessonAddRequest = Mock(LessonAddRequest)
        lessonAddRequest.getGroupId() >> 1L
        lessonAddRequest.getDate() >> LocalDate.now().plusDays(1)
        lessonAddRequest.getLessonSchedule() >> null

        when:
        lessonInstanceService.addExtraLesson(lessonAddRequest)

        then:
        1 * groupRepository.findById(1L) >> Optional.of(groupEntity)
        1 * lessonInstanceRepository.existsByGroupAndDateAndIsExtraTrue(groupEntity, lessonAddRequest.getDate()) >> false
        1 * lessonScheduleRepository.findByGroupId(1L) >> lessonScheduleEntity
        1 * lessonInstanceRepository.save(_)
    }

    def "getLessonsByGroupId should return lessons for group"() {
        given:
        def groupId = 1L
        def lessonEntities = [lessonInstanceEntity, lessonInstanceEntity]

        when:
        def result = lessonInstanceService.getLessonsByGroupId(groupId)

        then:
        1 * lessonInstanceRepository.findByGroupId(groupId) >> lessonEntities
        // Mapper-i yoxlamırıq, sadəcə service metodunun işlədiyini yoxlayırıq
        result instanceof ApiResponse
        result.data != null
    }

    def "getLessonsByGroupId should return empty list when no lessons"() {
        given:
        def groupId = 1L
        def emptyList = []

        when:
        def result = lessonInstanceService.getLessonsByGroupId(groupId)

        then:
        1 * lessonInstanceRepository.findByGroupId(groupId) >> emptyList
        result instanceof ApiResponse
        result.data == []
    }

    def "getInstanceById should return instance when exists"() {
        given:
        def instanceId = 1L

        when:
        def result = lessonInstanceService.getInstanceById(instanceId)

        then:
        1 * lessonInstanceRepository.findById(instanceId) >> Optional.of(lessonInstanceEntity)
        result instanceof ApiResponse
        result.data != null
    }

    def "getInstanceById should throw NotFoundException when instance not found"() {
        given:
        def instanceId = 99L

        when:
        lessonInstanceService.getInstanceById(instanceId)

        then:
        1 * lessonInstanceRepository.findById(instanceId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "getAllInstance should return all instances"() {
        given:
        def instanceList = [lessonInstanceEntity, lessonInstanceEntity, lessonInstanceEntity]

        when:
        def result = lessonInstanceService.getAllInstance()

        then:
        1 * lessonInstanceRepository.findAll() >> instanceList
        result instanceof ApiResponse
        result.data.size() == 3
    }

    def "getAllInstance should return empty list when no instances"() {
        given:
        def emptyList = []

        when:
        def result = lessonInstanceService.getAllInstance()

        then:
        1 * lessonInstanceRepository.findAll() >> emptyList
        result instanceof ApiResponse
        result.data == []
    }

    def "softDeleteById should call repository softDelete method"() {
        given:
        def instanceId = 1L

        when:
        lessonInstanceService.softDeleteById(instanceId)

        then:
        1 * lessonInstanceRepository.softDelete(instanceId)
    }

    def "hardDeleteById should call repository deleteById method"() {
        given:
        def instanceId = 1L

        when:
        lessonInstanceService.hardDeleteById(instanceId)

        then:
        1 * lessonInstanceRepository.deleteById(instanceId)
    }

    def "getAllDates should return all distinct sorted dates"() {
        given:
        def date1 = LocalDate.now()
        def date2 = LocalDate.now().plusDays(1)
        def date3 = LocalDate.now().plusDays(2)

        def instances = [
                createInstanceWithDate(date2),
                createInstanceWithDate(date1),
                createInstanceWithDate(date3),
                createInstanceWithDate(date1) // duplicate
        ]

        when:
        def result = lessonInstanceService.getAllDates()

        then:
        1 * lessonInstanceRepository.findAll() >> instances
        result instanceof ApiResponse
        result.data == [date1, date2, date3]
    }

    def "generateLessonInstancesForWeek should generate instances for schedule"() {
        given:
        def dateRequest = new DateRequest()
        dateRequest.setStartDate(LocalDate.now())
        dateRequest.setEndDate(LocalDate.now().plusDays(7))
        dateRequest.setGroupId(1L)

        def existingInstances = [lessonInstanceEntity]

        when:
        lessonInstanceService.generateLessonInstancesForWeek(dateRequest)

        then:
        1 * lessonInstanceRepository.findByGroupId(1L) >> existingInstances
        _ * lessonInstanceRepository.existsByLessonScheduleAndDate(_, _) >> false
        _ * lessonInstanceRepository.save(_)
    }

    def "generateLessonInstancesForWeek should not generate instances when already exists"() {
        given:
        def dateRequest = new DateRequest()
        dateRequest.setStartDate(LocalDate.now())
        dateRequest.setEndDate(LocalDate.now().plusDays(7))
        dateRequest.setGroupId(1L)

        def existingInstances = [lessonInstanceEntity]

        when:
        lessonInstanceService.generateLessonInstancesForWeek(dateRequest)

        then:
        1 * lessonInstanceRepository.findByGroupId(1L) >> existingInstances
        _ * lessonInstanceRepository.existsByLessonScheduleAndDate(_, _) >> true
        0 * lessonInstanceRepository.save(_)
    }

    def "previewTodayLesson should return lesson preview"() {
        given:
        def groupId = 1L
        def today = LocalDate.now()

        when:
        def result = lessonInstanceService.previewTodayLesson(groupId)

        then:
        1 * lessonScheduleRepository.findByGroupId(groupId) >> lessonScheduleEntity
        result instanceof ApiResponse
        result.data instanceof LessonPreviewResponse
        result.data.date == today
    }

    def "confirmLesson should create lesson instance for today"() {
        given:
        def groupId = 1L
        def today = LocalDate.now()

        def todaySchedule = new LessonScheduleEntity()
        todaySchedule.setDaysOfWeek([today.getDayOfWeek()] as Set)
        todaySchedule.setGroup(groupEntity)

        def savedInstance = new LessonInstanceEntity()
        savedInstance.setId(1L) // ID təyin et
        savedInstance.setGroup(groupEntity)
        savedInstance.setDate(today)
        savedInstance.setLessonSchedule(todaySchedule)
        savedInstance.setExtra(false)

        when:
        def result = lessonInstanceService.confirmLesson(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.of(groupEntity)
        1 * lessonScheduleRepository.findByGroupId(groupId) >> todaySchedule
        1 * lessonInstanceRepository.save(_) >> { LessonInstanceEntity entity ->
            assert entity.group == groupEntity
            assert entity.date == today
            assert entity.lessonSchedule == todaySchedule
            assert entity.extra == false
            savedInstance // save metodu entity qaytarmalıdır
        }
        1 * lessonScheduleRepository.flush()
        result instanceof ApiResponse
        result.data == "success"
    }

    def "confirmLesson should throw TodayHaventLessonException when no lesson today"() {
        given:
        def groupId = 1L
        def tomorrow = LocalDate.now().plusDays(1)
        def scheduleWithoutToday = new LessonScheduleEntity()
        scheduleWithoutToday.setDaysOfWeek([tomorrow.getDayOfWeek()] as Set)

        when:
        lessonInstanceService.confirmLesson(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.of(groupEntity)
        1 * lessonScheduleRepository.findByGroupId(groupId) >> scheduleWithoutToday
        thrown(TodayHaventLessonException)
    }

    def "confirmLesson should throw NotFoundException when group not found"() {
        given:
        def groupId = 99L

        when:
        lessonInstanceService.confirmLesson(groupId)

        then:
        1 * groupRepository.findById(groupId) >> Optional.empty()
        thrown(NotFoundException)
    }

    // Helper method
    private LessonInstanceEntity createInstanceWithDate(LocalDate date) {
        def instance = new LessonInstanceEntity()
        instance.setDate(date)
        return instance
    }
}