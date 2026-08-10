package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.EquipementDto;

public interface EquipementService {
  EquipementDto save(EquipementDto dto);

  EquipementDto getById(Long id);

  EquipementDto update(Long id, EquipementDto dto);

  void delete(Long id);
}
