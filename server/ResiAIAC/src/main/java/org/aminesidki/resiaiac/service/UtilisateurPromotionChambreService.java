package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

public interface UtilisateurPromotionChambreService {
  UtilisateurPromotionChambreDto save(UtilisateurPromotionChambreDto dto);

  UtilisateurPromotionChambreDto getById(UtilisateurPromotionChambreId id);

  UtilisateurPromotionChambreDto update(
      UtilisateurPromotionChambreId id, UtilisateurPromotionChambreDto dto);

  void delete(UtilisateurPromotionChambreId id);
}
