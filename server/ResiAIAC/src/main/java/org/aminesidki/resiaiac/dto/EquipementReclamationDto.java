package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;

public record EquipementReclamationDto(
    EquipementReclamationId id, Long quantite, Equipement equipement, Reclamation reclamation)
    implements Serializable {}
