package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;

public interface EquipementReclamationService {
  EquipementReclamationDto save(EquipementReclamationDto dto);

  EquipementReclamationDto getById(Long equipementId, UUID reclamationId);

  EquipementReclamationDto update(EquipementReclamationId id, EquipementReclamationDto dto);

  void delete(Long equipementId, UUID reclamationId);
}
