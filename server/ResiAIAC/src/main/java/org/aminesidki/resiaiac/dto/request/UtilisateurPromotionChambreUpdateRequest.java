package org.aminesidki.resiaiac.dto.request;

import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

public record UtilisateurPromotionChambreUpdateRequest(
    UtilisateurPromotionChambreId id, UtilisateurPromotionChambreDto dto) {}
