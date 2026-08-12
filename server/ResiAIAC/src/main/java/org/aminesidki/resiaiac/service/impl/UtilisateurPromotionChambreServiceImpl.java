package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.mapper.UtilisateurPromotionChambreMapper;
import org.aminesidki.resiaiac.repository.UtilisateurPromotionChambreRepository;
import org.aminesidki.resiaiac.service.UtilisateurPromotionChambreService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UtilisateurPromotionChambreServiceImpl implements UtilisateurPromotionChambreService {

  private final UtilisateurPromotionChambreRepository utilisateurPromotionChambreRepository;
  private final UtilisateurPromotionChambreMapper utilisateurPromotionChambreMapper;

  @Override
  public UtilisateurPromotionChambreDto save(UtilisateurPromotionChambreDto dto) {
    UtilisateurPromotionChambre entity = utilisateurPromotionChambreMapper.toEntity(dto);
    utilisateurPromotionChambreRepository.save(entity);
    return utilisateurPromotionChambreMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public UtilisateurPromotionChambreDto getById(
      UUID utilisateurId, UUID promotionId, UUID chambreId) {
    UtilisateurPromotionChambreId id =
        new UtilisateurPromotionChambreId(utilisateurId, promotionId, chambreId);
    UtilisateurPromotionChambre entity =
        ResourceFetcher.fetchResource(
            id, utilisateurPromotionChambreRepository, "UtilisateurPromotionChambre");
    return utilisateurPromotionChambreMapper.toDto(entity);
  }

  @Override
  public UtilisateurPromotionChambreDto update(
      UtilisateurPromotionChambreId id, UtilisateurPromotionChambreDto dto) {
    UtilisateurPromotionChambre entity =
        ResourceFetcher.fetchResource(
            id, utilisateurPromotionChambreRepository, "UtilisateurPromotionChambre");
    utilisateurPromotionChambreMapper.updateEntityFromDto(dto, entity);
    entity = utilisateurPromotionChambreRepository.save(entity);
    return utilisateurPromotionChambreMapper.toDto(entity);
  }

  @Override
  public void delete(UUID utilisateurId, UUID promotionId, UUID chambreId) {
    UtilisateurPromotionChambreId id =
        new UtilisateurPromotionChambreId(utilisateurId, promotionId, chambreId);
    utilisateurPromotionChambreRepository.delete(
        ResourceFetcher.fetchResource(
            id, utilisateurPromotionChambreRepository, "UtilisateurPromotionChambre"));
  }
}
