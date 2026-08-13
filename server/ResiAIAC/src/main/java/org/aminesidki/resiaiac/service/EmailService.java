package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.response.EmailResponse;

public interface EmailService {
  void envoyerEmail(EmailResponse request);
}
