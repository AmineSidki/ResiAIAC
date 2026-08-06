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
    Utilisateur utilisateur,
    Chambre chambre,
    Service service,
    Timestamp createdAt)
    implements Serializable {}
