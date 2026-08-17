package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;

/** Dto for {@link org.aminesidki.resiaiac.entity.Equipement } */
public record EquipementDto(
    Long id,
    @NotNull @NotEmpty(message = "Nom ne peut pas etre vide !") String nom,
    List<EquipementReclamationId> reclamations,
    List<EquipementUpcId> upcs)
    implements Serializable {}
