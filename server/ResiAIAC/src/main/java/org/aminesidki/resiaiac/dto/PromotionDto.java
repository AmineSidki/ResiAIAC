package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

/** Dto for {@link org.aminesidki.resiaiac.entity.Promotion } */
public record PromotionDto(
    UUID id,
    Long anneeDeDepart,
    Long anneeDeFin,
    Long filiere,
    Integer niveau,
    List<UtilisateurPromotionChambreId> combinaisonsUpc)
    implements Serializable {}
