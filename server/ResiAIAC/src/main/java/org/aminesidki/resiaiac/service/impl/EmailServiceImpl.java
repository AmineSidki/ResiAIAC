package org.aminesidki.resiaiac.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aminesidki.resiaiac.dto.response.EmailResponse;
import org.aminesidki.resiaiac.service.EmailService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
}
