package com.coconut.common.utils.mail;

import com.coconut.crawl.presentation.dto.MailDto;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

// https://victorydntmd.tistory.com/342
@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;

  public void sendEmail(MailDto mailDto) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setSubject(mailDto.getTitle());
    helper.setTo(mailDto.getAddress());

    Context context = new Context();
    context.setVariable("token_string", mailDto.getToken());
    helper.setText(templateEngine.process("mail-template", context), true);

    mailSender.send(message);
  }

}
