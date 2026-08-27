package org.aminesidki.resiaiac.service.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.EquipementReclamation;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.mapper.EquipementReclamationMapper;
import org.aminesidki.resiaiac.repository.EquipementReclamationRepository;
import org.aminesidki.resiaiac.service.EquipementReclamationService;
import org.aminesidki.resiaiac.service.EquipementService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class EquipementReclamationServiceImpl implements EquipementReclamationService {

  private final EquipementService equipementService;
  private final EquipementReclamationRepository equipementReclamationRepository;
  private final EquipementReclamationMapper equipementReclamationMapper;

  @Transactional(readOnly = true)
  @Override
  public Page<EquipementReclamationDto> getAllByEquipementId(Long id, Pageable pageable) {
    Equipement equipement = equipementService.getEntityById(id);
    return equipementReclamationRepository
        .findAllByEquipement(equipement, pageable)
        .map(equipementReclamationMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public List<EquipementReclamationDto> getAllByReclamationId(UUID id) {
    // This is different from the rest of the getBy_ methods for the sole reason that having
    // ReclamationService
    // imported created a cyclical dependency, to break that, I opted for having this service fetch
    // following
    // the id alone
    return equipementReclamationRepository.findAllByReclamation_Id(id).stream()
        .map(equipementReclamationMapper::toDto)
        .toList();
  }

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
