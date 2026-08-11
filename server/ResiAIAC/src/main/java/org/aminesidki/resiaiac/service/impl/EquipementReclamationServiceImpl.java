package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.mapper.EquipementReclamationMapper;
import org.aminesidki.resiaiac.repository.EquipementReclamationRepository;
import org.aminesidki.resiaiac.service.EquipementReclamationService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EquipementReclamationServiceImpl implements EquipementReclamationService {

  private final EquipementReclamationRepository equipementReclamationRepository;
  private final EquipementReclamationMapper equipementReclamationMapper;

  @Override
  public EquipementReclamationDto save(EquipementReclamationDto dto) {
    return null;
  }

  @Override
  public EquipementReclamationDto getById(Long equipementId, UUID reclamationId) {
    return null;
  }

  @Override
  public EquipementReclamationDto update(EquipementReclamationId id, EquipementReclamationDto dto) {
    return null;
  }

  @Override
  public void delete(Long equipementId, UUID reclamationId) {}
}
