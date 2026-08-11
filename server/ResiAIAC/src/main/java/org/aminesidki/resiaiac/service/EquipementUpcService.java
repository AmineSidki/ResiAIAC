package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;

public interface EquipementUpcService {
  EquipementUpcDto save(EquipementUpcDto dto);

  EquipementUpcDto getById(Long equipementId, UUID utilisateurId, UUID promotionId, UUID chambreId);

  EquipementUpcDto update(EquipementUpcId id, EquipementUpcDto dto);

  void delete(Long equipementId, UUID utilisateurId, UUID promotionId, UUID chambreId);
}
