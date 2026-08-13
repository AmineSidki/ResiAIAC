package org.aminesidki.resiaiac.service;

import java.util.List;
import org.aminesidki.resiaiac.dto.EquipementDto;

public interface EquipementService {
  List<EquipementDto> getAll();

  EquipementDto save(EquipementDto dto);

  EquipementDto getById(Long id);

  EquipementDto update(Long id, EquipementDto dto);

  void delete(Long id);
}
