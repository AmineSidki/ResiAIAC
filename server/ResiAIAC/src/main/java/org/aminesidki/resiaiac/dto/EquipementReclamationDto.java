package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;

/** Dto for {@link org.aminesidki.resiaiac.entity.EquipementReclamation } */
public record EquipementReclamationDto(
    EquipementReclamationId id, Long quantite, Long equipement, UUID reclamation)
    implements Serializable {}