package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.ReportEntity
import com.classreport.classreport.entity.StudentEntity
import com.classreport.classreport.entity.TeacherEntity
import com.classreport.classreport.mapper.ReportMapper
import com.classreport.classreport.model.exception.AlreadyExistsException
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.request.ReportRequest
import com.classreport.classreport.model.request.StudentRequest
import com.classreport.classreport.model.request.TeacherRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.model.response.ReportResponse
import com.classreport.classreport.repository.ReportRepository
import com.classreport.classreport.repository.StudentRepository
import com.classreport.classreport.repository.TeacherRepository
import com.classreport.classreport.service.ReportService
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class ReportServiceTest extends Specification {

    def reportRepository = Mock(ReportRepository)
    def studentRepository = Mock(StudentRepository)
    def teacherRepository = Mock(TeacherRepository)
    def reportMapper = Mock(ReportMapper)

    @Subject
    def reportService = new ReportService(reportRepository, studentRepository, teacherRepository, reportMapper)

    def reportEntity
    def reportRequest
    def reportResponse
    def studentEntity
    def teacherEntity
    def studentRequest
    def teacherRequest

    def setup() {
        studentEntity = new StudentEntity()
        studentEntity.setId(15L)
        studentEntity.setName("Elvin")
        studentEntity.setSurname("Nəbiyev")

        teacherEntity = new TeacherEntity()
        teacherEntity.setId(8L)
        teacherEntity.setName("Gülnarə")
        teacherEntity.setSurname("Əliyeva")

        // Request obyektləri
        studentRequest = new StudentRequest()
        studentRequest.setId(15L)

        teacherRequest = new TeacherRequest()
        teacherRequest.setId(8L)

        reportEntity = new ReportEntity()
        reportEntity.setId(30L)
        reportEntity.setStudent(studentEntity)
        reportEntity.setTeacher(teacherEntity)
        reportEntity.setReportDate(LocalDate.now())
        reportEntity.setContent("Şagirdin dərs fəaliyyəti yaxşıdır")
        reportEntity.setActive(true)

        reportRequest = new ReportRequest()
        reportRequest.setStudent(studentRequest)
        reportRequest.setTeacher(teacherRequest)
        reportRequest.setContent("Şagirdin dərs fəaliyyəti yaxşıdır")

        // ReportResponse'u sadəcə əsas field-larla yaradırıq
        reportResponse = new ReportResponse()
        reportResponse.setId(30L)
        reportResponse.setContent("Şagirdin dərs fəaliyyəti yaxşıdır")
        reportResponse.setReportDate(LocalDate.now())
        // Yalnız mövcud field-ları set edirik
    }

    def "createReport should create report successfully"() {
        given:
        def mappedReport = reportEntity

        when:
        def result = reportService.createReport(reportRequest)

        then:
        1 * studentRepository.findById(reportRequest.student.id) >> Optional.of(studentEntity)
        1 * teacherRepository.findById(reportRequest.teacher.id) >> Optional.of(teacherEntity)
        1 * reportRepository.findByStudentAndReportDate(studentEntity, LocalDate.now()) >> Optional.empty()
        1 * reportMapper.requestToEntity(reportRequest) >> mappedReport
        1 * reportRepository.save(_) >> { ReportEntity report ->
            assert report.active == true
            assert report.student == studentEntity
            assert report.teacher == teacherEntity
            assert report.reportDate == LocalDate.now()
            report
        }
        result instanceof ApiResponse
        result.data == "success"
    }

    def "createReport should throw NotFoundException when student not found"() {
        when:
        reportService.createReport(reportRequest)

        then:
        1 * studentRepository.findById(reportRequest.student.id) >> Optional.empty()
        0 * teacherRepository.findById(_)
        thrown(NotFoundException)
    }

    def "createReport should throw NotFoundException when teacher not found"() {
        when:
        reportService.createReport(reportRequest)

        then:
        1 * studentRepository.findById(reportRequest.student.id) >> Optional.of(studentEntity)
        1 * teacherRepository.findById(reportRequest.teacher.id) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "createReport should throw AlreadyExistsException when report exists for today"() {
        when:
        reportService.createReport(reportRequest)

        then:
        1 * studentRepository.findById(reportRequest.student.id) >> Optional.of(studentEntity)
        1 * teacherRepository.findById(reportRequest.teacher.id) >> Optional.of(teacherEntity)
        1 * reportRepository.findByStudentAndReportDate(studentEntity, LocalDate.now()) >> Optional.of(reportEntity)
        thrown(AlreadyExistsException)
    }

    def "getReportById should return report when exists"() {
        given:
        def reportId = 30L

        when:
        def result = reportService.getReportById(reportId)

        then:
        1 * reportRepository.findById(reportId) >> Optional.of(reportEntity)
        1 * reportMapper.entityToResponse(reportEntity) >> reportResponse
        result instanceof ApiResponse
        result.data == reportResponse
    }

    def "getReportById should throw NotFoundException when report not found"() {
        given:
        def reportId = 99L

        when:
        reportService.getReportById(reportId)

        then:
        1 * reportRepository.findById(reportId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "getByStudentId should return reports for student"() {
        given:
        def studentId = 15L
        def reportList = [reportEntity, reportEntity]

        when:
        def result = reportService.getByStudentId(studentId)

        then:
        1 * reportRepository.findByStudent_Id(studentId) >> reportList
        2 * reportMapper.entityToResponse(_) >> reportResponse
        result instanceof ApiResponse
        result.data.size() == 2
    }

    def "getByStudentId should return empty list when no reports"() {
        given:
        def studentId = 15L
        def emptyList = []

        when:
        def result = reportService.getByStudentId(studentId)

        then:
        1 * reportRepository.findByStudent_Id(studentId) >> emptyList
        0 * reportMapper.entityToResponse(_)
        result instanceof ApiResponse
        result.data == []
    }

    def "getByTeacherId should return reports for teacher"() {
        given:
        def teacherId = 8L
        def reportList = [reportEntity, reportEntity]

        when:
        def result = reportService.getByTeacherId(teacherId)

        then:
        1 * reportRepository.findByTeacher_Id(teacherId) >> reportList
        2 * reportMapper.entityToResponse(_) >> reportResponse
        result instanceof ApiResponse
        result.data.size() == 2
    }

    def "getByTeacherIdAndStudentId should return reports for teacher and student"() {
        given:
        def studentId = 15L
        def teacherId = 8L
        def reportList = [reportEntity]

        when:
        def result = reportService.getByTeacherIdAndStudentId(studentId, teacherId)

        then:
        1 * reportRepository.findByStudentIdAndTeacherId(studentId, teacherId) >> reportList
        1 * reportMapper.entityToResponse(_) >> reportResponse
        result instanceof ApiResponse
        result.data.size() == 1
    }

    def "getAllReports should return all reports"() {
        given:
        def reportList = [reportEntity, reportEntity, reportEntity]

        when:
        def result = reportService.getAllReports()

        then:
        1 * reportRepository.findAll() >> reportList
        3 * reportMapper.entityToResponse(_) >> reportResponse
        result instanceof ApiResponse
        result.data.size() == 3
    }

    def "softDelete should deactivate report"() {
        given:
        def reportId = 30L

        when:
        def result = reportService.softDelete(reportId)

        then:
        1 * reportRepository.findById(reportId) >> Optional.of(reportEntity)
        0 * reportRepository.deleteById(_)
        result instanceof ApiResponse
        result.data == "success"
        !reportEntity.active
    }

    def "softDelete should throw NotFoundException when report not found"() {
        given:
        def reportId = 99L

        when:
        reportService.softDelete(reportId)

        then:
        1 * reportRepository.findById(reportId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "hardDelete should delete report"() {
        given:
        def reportId = 30L

        when:
        def result = reportService.hardDelete(reportId)

        then:
        1 * reportRepository.deleteById(reportId)
        result instanceof ApiResponse
        result.data == "success"
    }
}