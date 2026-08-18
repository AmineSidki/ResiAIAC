package org.aminesidki.resiaiac.service;

public interface EmailService {
    void envoyerEmail(String destinataire, String sujet, String message);
}
