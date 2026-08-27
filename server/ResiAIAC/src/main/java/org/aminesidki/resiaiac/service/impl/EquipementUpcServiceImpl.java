package org.aminesidki.resiaiac.service.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.EquipementUpc;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.mapper.EquipementUpcMapper;
import org.aminesidki.resiaiac.repository.EquipementUpcRepository;
import org.aminesidki.resiaiac.service.EquipementService;
import org.aminesidki.resiaiac.service.EquipementUpcService;
import org.aminesidki.resiaiac.service.UtilisateurPromotionChambreService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class EquipementUpcServiceImpl implements EquipementUpcService {

  private final UtilisateurPromotionChambreService utilisateurPromotionChambreService;
  private final EquipementService equipementService;
  private final EquipementUpcRepository equipementUpcRepository;
  private final EquipementUpcMapper equipementUpcMapper;

  @Transactional(readOnly = true)
  @Override
  public Page<EquipementUpcDto> getAllByEquipement(Long equipementId, Pageable pageable) {
    Equipement equipement = equipementService.getEntityById(equipementId);
    return equipementUpcRepository
        .findAllByEquipement(equipement, pageable)
        .map(equipementUpcMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public List<EquipementUpcDto> getAllByUpc(UUID utilisateurId, UUID promotionId, UUID chambreId) {
    UtilisateurPromotionChambreId id =
        new UtilisateurPromotionChambreId(utilisateurId, promotionId, chambreId);
    UtilisateurPromotionChambre entity = utilisateurPromotionChambreService.getEntityById(id);
    return equipementUpcRepository.findAllByUpc(entity).stream()
        .map(equipementUpcMapper::toDto)
        .toList();
  }

  @Override
  public EquipementUpcDto save(EquipementUpcDto dto) {
    EquipementUpc entity = equipementUpcMapper.toEntity(dto);
    entity = equipementUpcRepository.save(entity);
    return equipementUpcMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public EquipementUpcDto getById(
      Long equipementId, UUID utilisateurId, UUID promotionId, UUID chambreId) {
    UtilisateurPromotionChambreId upcId =
        new UtilisateurPromotionChambreId(utilisateurId, promotionId, chambreId);
    EquipementUpcId id = new EquipementUpcId(equipementId, upcId);
    EquipementUpc entity =
        ResourceFetcher.fetchResource(id, equipementUpcRepository, "EquipementUpc");
    return equipementUpcMapper.toDto(entity);
  }

  @Override
  public EquipementUpcDto update(EquipementUpcId id, EquipementUpcDto dto) {
    EquipementUpc entity =
        ResourceFetcher.fetchResource(id, equipementUpcRepository, "EquipementUpc");
    equipementUpcMapper.updateEntityFromDto(dto, entity);
    entity = equipementUpcRepository.save(entity);
    return equipementUpcMapper.toDto(entity);
  }

  @Override
  public void delete(Long equipementId, UUID utilisateurId, UUID promotionId, UUID chambreId) {
    UtilisateurPromotionChambreId upcId =
        new UtilisateurPromotionChambreId(utilisateurId, promotionId, chambreId);
    EquipementUpcId id = new EquipementUpcId(equipementId, upcId);
    equipementUpcRepository.delete(
        ResourceFetcher.fetchResource(id, equipementUpcRepository, "EquipementUpc"));
  }
}
