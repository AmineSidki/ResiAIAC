package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.aminesidki.resiaiac.dto.response.EmailResponse;
import org.aminesidki.resiaiac.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Unit tests for {@link EmailService}, exercised through its {@link EmailServiceImpl}
 * implementation.
 *
 * <p>{@code @Async} is only honored through a Spring-managed proxy, so calling the method directly
 * here (no application context involved) runs it synchronously — that's fine, since these tests
 * only lock in message construction and failure handling, not the executor wiring itself.
 *
 * <p>{@code JavaMailSender} is mocked entirely — no real SMTP server involved.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  @Mock private JavaMailSender javaMailSender;

  private EmailService emailService;

  @BeforeEach
  void setUp() {
    emailService = new EmailServiceImpl(javaMailSender);
  }

  // ---------- envoyerEmail ----------

  @Test
  void envoyerEmail_shouldBuildAndSendMessageMatchingResponse() {
    EmailResponse response =
        new EmailResponse(
            "etudiant@example.com", "Confirmation", "Votre réservation est confirmée.");

    emailService.envoyerEmail(response);

    ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(javaMailSender, times(1)).send(captor.capture());

    SimpleMailMessage sent = captor.getValue();
    assertThat(sent.getTo()).containsExactly(response.destinataire());
    assertThat(sent.getSubject()).isEqualTo(response.sujet());
    assertThat(sent.getText()).isEqualTo(response.corps());
  }

  @Test
  void envoyerEmail_shouldSwallowMailExceptionRatherThanPropagate() {
    EmailResponse response =
        new EmailResponse(
            "etudiant@example.com", "Confirmation", "Votre réservation est confirmée.");
    doThrow(new MailSendException("smtp unavailable"))
        .when(javaMailSender)
        .send(any(SimpleMailMessage.class));

    assertThatCode(() -> emailService.envoyerEmail(response)).doesNotThrowAnyException();

    verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
  }
}
