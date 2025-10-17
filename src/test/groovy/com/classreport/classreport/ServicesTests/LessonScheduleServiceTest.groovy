package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.LessonInstanceEntity
import com.classreport.classreport.entity.LessonScheduleEntity
import com.classreport.classreport.mapper.LessonScheduleMapperManual
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.request.LessonScheduleRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.model.response.LessonScheduleResponse
import com.classreport.classreport.repository.LessonScheduleRepository
import com.classreport.classreport.service.LessonScheduleService
import spock.lang.Specification
import spock.lang.Subject

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.LocalDate

class LessonScheduleServiceTest extends Specification {

    def lessonScheduleRepository = Mock(LessonScheduleRepository)
    def lessonScheduleMapper = Mock(LessonScheduleMapperManual)

    @Subject
    def lessonScheduleService = new LessonScheduleService(lessonScheduleRepository, lessonScheduleMapper)

    def lessonScheduleEntity
    def lessonScheduleRequest
    def lessonScheduleResponse

    def setup() {
        lessonScheduleEntity = new LessonScheduleEntity()
        lessonScheduleEntity.setId(35L)
        lessonScheduleEntity.setDaysOfWeek([DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY] as Set)
        lessonScheduleEntity.setStartTime(LocalTime.of(9, 0))
        lessonScheduleEntity.setEndTime(LocalTime.of(10, 30))
        lessonScheduleEntity.setExceptionDates([] as Set)
        lessonScheduleEntity.setGroup(null)
        lessonScheduleEntity.setTeacher(null)

        lessonScheduleRequest = new LessonScheduleRequest()
        lessonScheduleRequest.setId(35L)
        lessonScheduleRequest.setDaysOfWeek([DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY] as Set)
        lessonScheduleRequest.setStartTime(LocalTime.of(9, 0))
        lessonScheduleRequest.setEndTime(LocalTime.of(10, 30))

        lessonScheduleResponse = new LessonScheduleResponse()
        lessonScheduleResponse.setId(35L)
        lessonScheduleResponse.setDaysOfWeek([DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY] as Set)
        lessonScheduleResponse.setStartTime(LocalTime.of(9, 0))
        lessonScheduleResponse.setEndTime(LocalTime.of(10, 30))
    }

    def "createSchedule should create lesson schedule successfully"() {
        given:
        def mappedEntity = lessonScheduleEntity

        when:
        lessonScheduleService.createSchedule(lessonScheduleRequest)

        then:
        1 * lessonScheduleMapper.requestToEntity(lessonScheduleRequest) >> mappedEntity
        1 * lessonScheduleRepository.save(mappedEntity)
    }

    def "getScheduleById should return schedule when exists"() {
        given:
        def scheduleId = 35L

        when:
        def result = lessonScheduleService.getScheduleById(scheduleId)

        then:
        1 * lessonScheduleRepository.findById(scheduleId) >> Optional.of(lessonScheduleEntity)
        1 * lessonScheduleMapper.entityToResponse(lessonScheduleEntity) >> lessonScheduleResponse
        result instanceof ApiResponse
        result.data == lessonScheduleResponse
    }

    def "getScheduleById should throw NotFoundException when schedule not found"() {
        given:
        def scheduleId = 99L

        when:
        lessonScheduleService.getScheduleById(scheduleId)

        then:
        1 * lessonScheduleRepository.findById(scheduleId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "getAllSchedules should return all schedules"() {
        given:
        def scheduleList = [lessonScheduleEntity, lessonScheduleEntity, lessonScheduleEntity]
        def requestList = [lessonScheduleRequest, lessonScheduleRequest, lessonScheduleRequest]

        when:
        def result = lessonScheduleService.getAllSchedules()

        then:
        1 * lessonScheduleRepository.findAll() >> scheduleList
        3 * lessonScheduleMapper.entityToRequest(_) >> lessonScheduleRequest
        result instanceof ApiResponse
        result.data.size() == 3
        result.data == requestList
    }

    def "getAllSchedules should return empty list when no schedules"() {
        given:
        def emptyList = []

        when:
        def result = lessonScheduleService.getAllSchedules()

        then:
        1 * lessonScheduleRepository.findAll() >> emptyList
        0 * lessonScheduleMapper.entityToRequest(_)
        result instanceof ApiResponse
        result.data == []
    }

    def "softDeleteById should call repository softDelete method"() {
        given:
        def scheduleId = 35L

        when:
        lessonScheduleService.softDeleteById(scheduleId)

        then:
        1 * lessonScheduleRepository.softDelete(scheduleId)
    }

    def "hardDeleteById should delete schedule"() {
        given:
        def scheduleId = 35L

        when:
        lessonScheduleService.hardDeleteById(scheduleId)

        then:
        1 * lessonScheduleRepository.deleteById(scheduleId)
    }

    def "createSchedule should handle schedule with exception dates"() {
        given:
        def exceptionDate = LocalDate.now().plusDays(1)

        // LessonScheduleRequest-də exceptionDates olmaya bilər, ona görə də mapper vasitəsilə test et
        def scheduleWithExceptions = new LessonScheduleRequest()
        scheduleWithExceptions.setId(35L)
        scheduleWithExceptions.setDaysOfWeek([DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY] as Set)
        scheduleWithExceptions.setStartTime(LocalTime.of(9, 0))
        scheduleWithExceptions.setEndTime(LocalTime.of(10, 30))

        def mappedEntityWithExceptions = new LessonScheduleEntity()
        mappedEntityWithExceptions.setId(35L)
        mappedEntityWithExceptions.setDaysOfWeek([DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY] as Set)
        mappedEntityWithExceptions.setStartTime(LocalTime.of(9, 0))
        mappedEntityWithExceptions.setEndTime(LocalTime.of(10, 30))
        mappedEntityWithExceptions.setExceptionDates([exceptionDate] as Set)

        when:
        lessonScheduleService.createSchedule(scheduleWithExceptions)

        then:
        // Mapper-in exceptionDates-i düzgün map etdiyini yoxla
        1 * lessonScheduleMapper.requestToEntity(scheduleWithExceptions) >> mappedEntityWithExceptions
        1 * lessonScheduleRepository.save(mappedEntityWithExceptions)
    }

    def "createSchedule should handle schedule with weekend days"() {
        given:
        def weekendSchedule = new LessonScheduleRequest()
        weekendSchedule.setId(36L)
        weekendSchedule.setDaysOfWeek([DayOfWeek.SATURDAY, DayOfWeek.SUNDAY] as Set)
        weekendSchedule.setStartTime(LocalTime.of(11, 0))
        weekendSchedule.setEndTime(LocalTime.of(12, 30))

        def mappedEntity = new LessonScheduleEntity()
        mappedEntity.setId(36L)
        mappedEntity.setDaysOfWeek([DayOfWeek.SATURDAY, DayOfWeek.SUNDAY] as Set)
        mappedEntity.setStartTime(LocalTime.of(11, 0))
        mappedEntity.setEndTime(LocalTime.of(12, 30))

        when:
        lessonScheduleService.createSchedule(weekendSchedule)

        then:
        1 * lessonScheduleMapper.requestToEntity(weekendSchedule) >> mappedEntity
        1 * lessonScheduleRepository.save(mappedEntity)
    }

    def "getAllSchedules should handle mixed schedule types"() {
        given:
        def morningSchedule = new LessonScheduleEntity()
        morningSchedule.setId(37L)
        morningSchedule.setDaysOfWeek([DayOfWeek.MONDAY] as Set)
        morningSchedule.setStartTime(LocalTime.of(8, 0))
        morningSchedule.setEndTime(LocalTime.of(9, 0))

        def eveningSchedule = new LessonScheduleEntity()
        eveningSchedule.setId(38L)
        eveningSchedule.setDaysOfWeek([DayOfWeek.FRIDAY] as Set)
        eveningSchedule.setStartTime(LocalTime.of(17, 0))
        eveningSchedule.setEndTime(LocalTime.of(18, 0))

        def scheduleList = [morningSchedule, eveningSchedule]
        def requestList = [lessonScheduleRequest, lessonScheduleRequest]

        when:
        def result = lessonScheduleService.getAllSchedules()

        then:
        1 * lessonScheduleRepository.findAll() >> scheduleList
        2 * lessonScheduleMapper.entityToRequest(_) >> lessonScheduleRequest
        result instanceof ApiResponse
        result.data.size() == 2
    }
}