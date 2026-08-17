package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

/** Dto for {@link org.aminesidki.resiaiac.entity.EquipementUpc } */
public record EquipementUpcDto(
    EquipementUpcId id,
    @NotNull @Min(1) Long quantite,
    @NotNull Long equipement,
    @NotNull UtilisateurPromotionChambreId upc)
    implements Serializable {}
