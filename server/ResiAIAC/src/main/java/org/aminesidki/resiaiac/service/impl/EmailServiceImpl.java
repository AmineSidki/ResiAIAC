package org.aminesidki.resiaiac.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aminesidki.resiaiac.dto.response.EmailResponse;
import org.aminesidki.resiaiac.service.EmailService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender javaMailSender;

  @Override
  @Async("emailExecutor")
  public void envoyerEmail(EmailResponse response) {
    try {
      SimpleMailMessage email = new SimpleMailMessage();
      email.setTo(response.destinataire());
      email.setSubject(response.sujet());
      email.setText(response.corps());

      javaMailSender.send(email);
      log.info("Email envoyé à {} avec succès", response.destinataire());

    } catch (MailException e) {
      log.error("Echec de l'envoi de l'email à {} :{}", response.destinataire(), e.getMessage());
    }
  }

  @Override
  @Async("emailExecutor")
  public void envoyerEmailHtml(String destinataire, String sujet, String corpsHtml) {
    try {
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      // multipart=false, encoding=UTF-8: required so accented French text (é, à, ç...)
      // renders correctly instead of being mangled.
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      helper.setTo(destinataire);
      helper.setSubject(sujet);
      helper.setText(corpsHtml, true);

      javaMailSender.send(mimeMessage);
      log.info("Email HTML envoyé à {} avec succès", destinataire);

    } catch (MailException | jakarta.mail.MessagingException e) {
      log.error("Echec de l'envoi de l'email HTML à {} : {}", destinataire, e.getMessage());
    }
  }
}
