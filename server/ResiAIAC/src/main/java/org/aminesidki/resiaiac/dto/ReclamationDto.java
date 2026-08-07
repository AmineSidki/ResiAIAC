package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;

/** Dto for {@link org.aminesidki.resiaiac.entity.Reclamation } */
public record ReclamationDto(
    UUID id,
    String message,
    EtatReclamation etat,
    UUID utilisateur,
    UUID chambre,
    Long service,
    List<EquipementReclamationId> equipements,
    Timestamp createdAt)
    implements Serializable {}