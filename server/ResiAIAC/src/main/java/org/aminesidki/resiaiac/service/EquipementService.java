package org.aminesidki.resiaiac.service;

import java.util.List;
import org.aminesidki.resiaiac.dto.EquipementDto;
import org.aminesidki.resiaiac.entity.Equipement;

public interface EquipementService {
  List<EquipementDto> getAll();

  EquipementDto save(EquipementDto dto);

  Equipement getEntityById(Long id);

  EquipementDto getById(Long id);

  EquipementDto update(Long id, EquipementDto dto);

  void delete(Long id);
}
