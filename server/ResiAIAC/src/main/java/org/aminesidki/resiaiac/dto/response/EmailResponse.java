package org.aminesidki.resiaiac.dto.response;

public record EmailResponse(
        String destinataire,
        String sujet,
        String corps
) {
}
