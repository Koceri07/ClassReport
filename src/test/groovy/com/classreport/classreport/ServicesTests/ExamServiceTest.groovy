package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.ExamEntity
import com.classreport.classreport.entity.StudentEntity
import com.classreport.classreport.mapper.ExamMapper
import com.classreport.classreport.mapper.StudentMapper
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.request.ExamRequest
import com.classreport.classreport.model.request.StudentRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.model.response.ExamResponse
import com.classreport.classreport.repository.ExamRepository
import com.classreport.classreport.service.ExamService
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class ExamServiceTest extends Specification {

    def examRepository = Mock(ExamRepository)

    @Subject
    def examService = new ExamService(examRepository)

    def examEntity
    def examRequest
    def studentRequest
    def examResponse

    def setup() {
        studentRequest = new StudentRequest()
        studentRequest.setId(1L)
        studentRequest.setName("Test Student")

        examRequest = new ExamRequest()
        examRequest.setId(1L)
        examRequest.setStudent(studentRequest)

        def studentEntity = new StudentEntity()
        studentEntity.setId(1L)
        studentEntity.setName("Test Student")

        examEntity = new ExamEntity()
        examEntity.setId(1L)
        examEntity.setStudent(studentEntity)
        examEntity.setExamDate(LocalDate.now())

        examResponse = new ExamResponse()
        examResponse.setId(1L)
        examResponse.setExamDate(LocalDate.now())
    }

    def "createExam should create exam successfully"() {
        when:
        def result = examService.createExam(examRequest)

        then:
        1 * examRepository.save(_) >> { ExamEntity savedExam ->
            assert savedExam.student != null
            assert savedExam.student.id == 1L
            assert savedExam.examDate == LocalDate.now()
            savedExam
        }
        result instanceof ApiResponse
        result.data == "success"
    }

    def "createExam should handle when student is null"() {
        given:
        def examRequestWithoutStudent = new ExamRequest()
        examRequestWithoutStudent.setId(1L)
        examRequestWithoutStudent.setStudent(null)

        when:
        def result = examService.createExam(examRequestWithoutStudent)

        then:
        1 * examRepository.save(_) >> { ExamEntity savedExam ->
            assert savedExam.student == null
            assert savedExam.examDate == LocalDate.now()
            savedExam
        }
        result instanceof ApiResponse
        result.data == "success"
    }

    def "getExamById should return exam when exists"() {
        given:
        def examId = 1L

        when:
        def result = examService.getExamById(examId)

        then:
        1 * examRepository.findById(examId) >> Optional.of(examEntity)
        result instanceof ApiResponse
        result.data != null
    }

    def "getExamById should throw NotFoundException when exam not found"() {
        given:
        def examId = 99L

        when:
        examService.getExamById(examId)

        then:
        1 * examRepository.findById(examId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "getExamByStudentId should return exams for student"() {
        given:
        def studentId = 1L
        def examList = [examEntity, examEntity, examEntity]

        when:
        def result = examService.getExamByStudentId(studentId)

        then:
        1 * examRepository.findByStudentId(studentId) >> examList
        result instanceof ApiResponse
        result.data.size() == 3
    }

    def "getExamByStudentId should return empty list when no exams for student"() {
        given:
        def studentId = 99L
        def emptyList = []

        when:
        def result = examService.getExamByStudentId(studentId)

        then:
        1 * examRepository.findByStudentId(studentId) >> emptyList
        result instanceof ApiResponse
        result.data == []
    }

    def "hardDeleteById should delete exam and return success"() {
        given:
        def examId = 1L

        when:
        def result = examService.hardDeleteById(examId)

        then:
        1 * examRepository.deleteById(examId)
        result instanceof ApiResponse
        result.data == "success"
    }

    def "createExam should set current date as exam date"() {
        given:
        def today = LocalDate.now()

        when:
        examService.createExam(examRequest)

        then:
        1 * examRepository.save(_) >> { ExamEntity savedExam ->
            assert savedExam.examDate == today
            savedExam
        }
    }

    def "getExamByStudentId should handle when repository returns null by throwing NullPointerException"() {
        given:
        def studentId = 1L

        when:
        examService.getExamByStudentId(studentId)

        then:
        1 * examRepository.findByStudentId(studentId) >> null
        thrown(NullPointerException) // Service null-i handle etmir, exception atır
    }

    def "createExam should handle mapping exceptions gracefully"() {
        given:
        def invalidExamRequest = new ExamRequest()
        // Invalid data that might cause mapping issues

        when:
        def result = examService.createExam(invalidExamRequest)

        then:
        notThrown(Exception)
        result instanceof ApiResponse
    }

    def "createExam should work with minimal exam request"() {
        given:
        def minimalExamRequest = new ExamRequest()
        minimalExamRequest.setId(1L)

        when:
        def result = examService.createExam(minimalExamRequest)

        then:
        1 * examRepository.save(_) >> { ExamEntity savedExam ->
            assert savedExam.examDate == LocalDate.now()
            savedExam
        }
        result instanceof ApiResponse
        result.data == "success"
    }
}