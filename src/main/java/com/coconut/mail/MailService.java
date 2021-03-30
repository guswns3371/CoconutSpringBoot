package com.coconut.mail;

import com.coconut.client.dto.req.MailDto;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


// https://victorydntmd.tistory.com/342
@Service
@AllArgsConstructor
public class MailService {

    private JavaMailSender mailSender;
    private SpringTemplateEngine templateEngine;

    public void sendEmail(MailDto mailDto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);


        helper.setSubject(mailDto.getTitle());
        helper.setTo(mailDto.getAddress());

        Context context = new Context();
        context.setVariable("token_string", mailDto.getToken());
        helper.setText(templateEngine.process("mail-template", context),true);

        mailSender.send(message);
    }

}
