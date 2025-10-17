package com.classreport.classreport.service;

import com.classreport.classreport.mapper.MailMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.MailRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.model.response.MailResponse;
import com.classreport.classreport.repository.MailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MailRepository mailRepository;

    public void sendMail(MailRequest request){
        log.info("Action.senMail.start for mail to:{}, from:{}", request.getMailTo(), request.getMailFrom());
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setFrom(mailDto.getMailFrom());
        simpleMailMessage.setFrom("kocerimustafayev07@gmail.com");
//        simpleMailMessage.setTo(mailDto.getMailTo());
        simpleMailMessage.setTo(request.getMailTo().toArray(new String[0]));
        simpleMailMessage.setSubject(request.getSubject());
        simpleMailMessage.setText(request.getText());

        var mailEntity = MailMapper.INSTANCE.toEntity(request);

//        mailRepository.save(mailEntity);
        javaMailSender.send(simpleMailMessage);
        log.info("Action.send.success");
    }

    public ApiResponse getMail(Long id){
        log.info("Action.getMail.start for id {}", id);
        var mail = mailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id Not Found"));
        var mailDto = MailMapper.INSTANCE.toDto(mail);

        ApiResponse apiResponse = new ApiResponse(mail);
        log.info("Action.getMail.end for id {}",id);
        return apiResponse;
    }

    public ApiResponse getAllMails(){
        log.info("Action.getAllMails.start");
        List<MailRequest> mails = mailRepository.findAll()
                .stream().map(MailMapper.INSTANCE::toDto)
                .toList();
        ApiResponse apiResponse = new ApiResponse(mails);
        log.info("Action.getAllMails.end");
        return apiResponse;
    }


    public void deleteMailById(Long id){
        log.info("Action.deleteMailById.start for id {}",id);
        mailRepository.deleteById(id);
        log.info("Action.deleteMailById.end for id {}",id);
    }
}

