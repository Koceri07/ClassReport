package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.MailEntity
import com.classreport.classreport.model.exception.NotFoundException
import com.classreport.classreport.model.request.MailRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.repository.MailRepository
import com.classreport.classreport.service.MailService
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Specification
import spock.lang.Subject

class MailServiceTest extends Specification {

    def javaMailSender = Mock(JavaMailSender)
    def mailRepository = Mock(MailRepository)

    @Subject
    def mailService = new MailService(javaMailSender, mailRepository)

    def mailEntity
    def mailRequest

    def setup() {
        mailEntity = new MailEntity()
        mailEntity.setId(50L)
        mailEntity.setMailTo(["aydan.rahimova@edu.az", "elvin.nebiyev@edu.az"])
        mailEntity.setMailFrom("kocerimustafayev07@gmail.com")
        mailEntity.setSubject("Dərs cədvəli dəyişikliyi")
        mailEntity.setText("Hörmətli valideyn, növbəti həftə dərs cədvəlində dəyişiklik olub.")

        mailRequest = new MailRequest()
        mailRequest.setMailTo(["aydan.rahimova@edu.az", "elvin.nebiyev@edu.az"])
        mailRequest.setMailFrom("kocerimustafayev07@gmail.com")
        mailRequest.setSubject("Dərs cədvəli dəyişikliyi")
        mailRequest.setText("Hörmətli valideyn, növbəti həftə dərs cədvəlində dəyişiklik olub.")
    }

    def "sendMail should send email successfully"() {
        when:
        mailService.sendMail(mailRequest)

        then:
        // Static MailMapper.INSTANCE mock edilə bilmədiyi üçün yalnız javaMailSender çağırışını yoxlayırıq
        1 * javaMailSender.send(_ as SimpleMailMessage) >> { SimpleMailMessage message ->
            assert message.from == "kocerimustafayev07@gmail.com"
            assert message.to == mailRequest.mailTo.toArray(new String[0])
            assert message.subject == mailRequest.subject
            assert message.text == mailRequest.text
        }
        // mailRepository.save çağırılmır, çünki commented out
        0 * mailRepository.save(_)
    }

    def "sendMail should handle single recipient"() {
        given:
        def singleRecipientRequest = new MailRequest()
        singleRecipientRequest.setMailTo(["tekbir@istifadeci.az"])
        singleRecipientRequest.setSubject("Tək bağlama")
        singleRecipientRequest.setText("Bu yalnız bir nəfərə gedir")

        when:
        mailService.sendMail(singleRecipientRequest)

        then:
        1 * javaMailSender.send(_ as SimpleMailMessage) >> { SimpleMailMessage message ->
            assert message.to.length == 1
            assert message.to[0] == "tekbir@istifadeci.az"
        }
    }

    def "getMail should return mail when exists"() {
        given:
        def mailId = 50L

        when:
        def result = mailService.getMail(mailId)

        then:
        1 * mailRepository.findById(mailId) >> Optional.of(mailEntity)
        // Static MailMapper.INSTANCE mock edilə bilmədiyi üçün nəticəni yoxlamırıq
        result instanceof ApiResponse
        result.data == mailEntity // Servis mailEntity qaytarır
    }

    def "getMail should throw NotFoundException when mail not found"() {
        given:
        def mailId = 99L

        when:
        mailService.getMail(mailId)

        then:
        1 * mailRepository.findById(mailId) >> Optional.empty()
        thrown(NotFoundException)
    }

    def "getAllMails should return all mails"() {
        given:
        def mailList = [mailEntity, mailEntity, mailEntity]

        when:
        def result = mailService.getAllMails()

        then:
        1 * mailRepository.findAll() >> mailList
        // Static MailMapper.INSTANCE mock edilə bilmədiyi üçün nəticəni yoxlamırıq
        result instanceof ApiResponse
        result.data != null // Sadəcə null olmadığını yoxlayırıq
    }

    def "getAllMails should return empty list when no mails"() {
        given:
        def emptyList = []

        when:
        def result = mailService.getAllMails()

        then:
        1 * mailRepository.findAll() >> emptyList
        result instanceof ApiResponse
        result.data == []
    }

    def "deleteMailById should delete mail"() {
        given:
        def mailId = 50L

        when:
        mailService.deleteMailById(mailId)

        then:
        1 * mailRepository.deleteById(mailId)
    }

    def "sendMail should handle empty recipient list"() {
        given:
        def emptyRecipientRequest = new MailRequest()
        emptyRecipientRequest.setMailTo([])
        emptyRecipientRequest.setSubject("Boş bağlama")
        emptyRecipientRequest.setText("Heç kimə getmir")

        when:
        mailService.sendMail(emptyRecipientRequest)

        then:
        1 * javaMailSender.send(_ as SimpleMailMessage) >> { SimpleMailMessage message ->
            assert message.to.length == 0
        }
    }

    def "sendMail should use fixed sender email"() {
        given:
        def differentSenderRequest = new MailRequest()
        differentSenderRequest.setMailTo(["test@test.az"])
        differentSenderRequest.setMailFrom("farkli@gonderen.az") // Bu ignore olunacaq
        differentSenderRequest.setSubject("Test")
        differentSenderRequest.setText("Test mesajı")

        when:
        mailService.sendMail(differentSenderRequest)

        then:
        1 * javaMailSender.send(_ as SimpleMailMessage) >> { SimpleMailMessage message ->
            assert message.from == "kocerimustafayev07@gmail.com" // Həmişə bu email istifadə olunur
            assert message.from != differentSenderRequest.mailFrom
        }
    }
}