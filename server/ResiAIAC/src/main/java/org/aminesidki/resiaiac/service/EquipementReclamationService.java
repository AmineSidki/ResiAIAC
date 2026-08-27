package org.aminesidki.resiaiac.service;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EquipementReclamationService {
  Page<EquipementReclamationDto> getAllByEquipementId(Long id, Pageable pageable);

  List<EquipementReclamationDto> getAllByReclamationId(UUID id);

  EquipementReclamationDto save(EquipementReclamationDto dto);

  EquipementReclamationDto getById(Long equipementId, UUID reclamationId);

  EquipementReclamationDto update(EquipementReclamationId id, EquipementReclamationDto dto);

  void delete(Long equipementId, UUID reclamationId);
}
