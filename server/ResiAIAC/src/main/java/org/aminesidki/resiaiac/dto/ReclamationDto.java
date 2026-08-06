package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Service;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;

public record ReclamationDto(
    UUID id,
    String message,
    EtatReclamation etat,
    UUID utilisateurId,
    UUID chambreId,
    Long serviceId,
    Timestamp createdAt)
    implements Serializable {}
