package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;

public interface EquipementUpcService {
  EquipementUpcDto save(EquipementUpcDto dto);

  EquipementUpcDto getById(EquipementUpcId id);

  EquipementUpcDto update(EquipementUpcId id, EquipementUpcDto dto);

  void delete(EquipementUpcId id);
}
