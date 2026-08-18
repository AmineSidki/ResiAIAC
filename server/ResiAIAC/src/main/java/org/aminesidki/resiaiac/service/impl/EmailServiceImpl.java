package org.aminesidki.resiaiac.service.impl;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void envoyerEmail(String destinatire, String sujet, String message) {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(destinatire);
            email.setSubject(sujet);
            email.setText(message);

            javaMailSender.send(email);
    }
}
