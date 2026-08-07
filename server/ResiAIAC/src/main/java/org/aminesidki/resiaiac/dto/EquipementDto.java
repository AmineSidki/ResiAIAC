package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.List;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;

/** Dto for {@link org.aminesidki.resiaiac.entity.Equipement } */
public record EquipementDto(
    Long id, String nom, List<EquipementReclamationId> reclamations, List<EquipementUpcId> upcs)
    implements Serializable {}
