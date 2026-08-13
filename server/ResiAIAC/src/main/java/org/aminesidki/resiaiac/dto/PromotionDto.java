package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.validator.ValidPromotionYears;

/** Dto for {@link org.aminesidki.resiaiac.entity.Promotion } */
@ValidPromotionYears
public record PromotionDto(
    UUID id,
    @NotNull Long anneeDeDepart,
    @NotNull Long anneeDeFin,
    @NotNull @Min(1) Integer niveau,
    @NotNull Long filiere,
    List<UtilisateurPromotionChambreId> combinaisonsUpc)
    implements Serializable {}
