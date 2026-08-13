package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.EquipementReclamation;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.mapper.EquipementReclamationMapper;
import org.aminesidki.resiaiac.repository.EquipementReclamationRepository;
import org.aminesidki.resiaiac.service.EquipementReclamationService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class EquipementReclamationServiceImpl implements EquipementReclamationService {

  private final EquipementReclamationRepository equipementReclamationRepository;
  private final EquipementReclamationMapper equipementReclamationMapper;

  @Override
  public EquipementReclamationDto save(EquipementReclamationDto dto) {
    EquipementReclamation entity = equipementReclamationMapper.toEntity(dto);
    entity = equipementReclamationRepository.save(entity);
    return equipementReclamationMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public EquipementReclamationDto getById(Long equipementId, UUID reclamationId) {
    EquipementReclamationId id = new EquipementReclamationId(equipementId, reclamationId);
    EquipementReclamation entity =
        ResourceFetcher.fetchResource(id, equipementReclamationRepository, "EquipementReclamation");
    return equipementReclamationMapper.toDto(entity);
  }

  @Override
  public EquipementReclamationDto update(EquipementReclamationId id, EquipementReclamationDto dto) {
    EquipementReclamation entity =
        ResourceFetcher.fetchResource(id, equipementReclamationRepository, "EquipementReclamation");
    equipementReclamationMapper.updateEntityFromDto(dto, entity);
    entity = equipementReclamationRepository.save(entity);
    return equipementReclamationMapper.toDto(entity);
  }

  @Override
  public void delete(Long equipementId, UUID reclamationId) {
    EquipementReclamationId id = new EquipementReclamationId(equipementId, reclamationId);
    equipementReclamationRepository.delete(
        ResourceFetcher.fetchResource(
            id, equipementReclamationRepository, "EquipementReclamation"));
  }
}
