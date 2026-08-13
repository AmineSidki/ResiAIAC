package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;

/** Dto for {@link org.aminesidki.resiaiac.entity.EquipementReclamation } */
public record EquipementReclamationDto(
    EquipementReclamationId id,
    @NotNull @Min(1) Long quantite,
    @NotNull Long equipement,
    @NotNull UUID reclamation)
    implements Serializable {}
