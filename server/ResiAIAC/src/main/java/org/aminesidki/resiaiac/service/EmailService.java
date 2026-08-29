package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.response.EmailResponse;

public interface EmailService {
  void envoyerEmail(EmailResponse response);

  /**
   * Sends a rendered HTML email (e.g. from a Thymeleaf template).
   *
   * @param destinataire recipient address
   * @param sujet email subject
   * @param corpsHtml full HTML body, already rendered
   */
  void envoyerEmailHtml(String destinataire, String sujet, String corpsHtml);
}
