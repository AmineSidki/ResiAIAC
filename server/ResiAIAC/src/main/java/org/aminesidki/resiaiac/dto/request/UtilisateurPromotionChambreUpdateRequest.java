package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

public record UtilisateurPromotionChambreUpdateRequest(
    @NotNull UtilisateurPromotionChambreId id,
    @NotNull @Valid UtilisateurPromotionChambreDto dto) {}
