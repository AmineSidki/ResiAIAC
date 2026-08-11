package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

public interface UtilisateurPromotionChambreService {

  UtilisateurPromotionChambreDto save(UtilisateurPromotionChambreDto dto);

  UtilisateurPromotionChambreDto getById(UUID utilisateurId, UUID promotionId, UUID chambreId);

  UtilisateurPromotionChambreDto update(
      UtilisateurPromotionChambreId id, UtilisateurPromotionChambreDto dto);

  void delete(UUID utilisateurId, UUID promotionId, UUID chambreId);
}
