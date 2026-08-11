package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.mapper.UtilisateurPromotionChambreMapper;
import org.aminesidki.resiaiac.repository.UtilisateurPromotionChambreRepository;
import org.aminesidki.resiaiac.service.UtilisateurPromotionChambreService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UtilisateurPromotionChambreServiceImpl implements UtilisateurPromotionChambreService {

  private final UtilisateurPromotionChambreRepository utilisateurPromotionChambreRepository;
  private final UtilisateurPromotionChambreMapper utilisateurPromotionChambreMapper;

  @Override
  public UtilisateurPromotionChambreDto save(UtilisateurPromotionChambreDto dto) {
    return null;
  }

  @Override
  public UtilisateurPromotionChambreDto getById(
      UUID utilisateurId, UUID promotionId, UUID chambreId) {
    return null;
  }

  @Override
  public UtilisateurPromotionChambreDto update(
      UtilisateurPromotionChambreId id, UtilisateurPromotionChambreDto dto) {
    return null;
  }

  @Override
  public void delete(UUID utilisateurId, UUID promotionId, UUID chambreId) {}
}
