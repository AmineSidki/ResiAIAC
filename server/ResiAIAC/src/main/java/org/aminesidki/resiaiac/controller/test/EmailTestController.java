package org.aminesidki.resiaiac.controller.test;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.response.EmailResponse;
import org.aminesidki.resiaiac.service.EmailService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email-test")
public class EmailTestController {

    private final EmailService emailService;

    @PostMapping("/")
    public ResponseEntity<?> testEndpoint(@RequestBody EmailResponse request) {
        emailService.envoyerEmail(request);
        return ResponseEntity.ok("Email envoyé (ou tentative loggée) vers " + request.destinataire());
    }
}