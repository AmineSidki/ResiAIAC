package org.aminesidki.resiaiac.service;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EquipementUpcService {
  Page<EquipementUpcDto> getAllByEquipement(Long equipementId, Pageable pageable);

  List<EquipementUpcDto> getAllByUpc(UUID utilisateurId, UUID promotionId, UUID chambreId);

  EquipementUpcDto save(EquipementUpcDto dto);

  EquipementUpcDto getById(Long equipementId, UUID utilisateurId, UUID promotionId, UUID chambreId);

  EquipementUpcDto update(EquipementUpcId id, EquipementUpcDto dto);

  void delete(Long equipementId, UUID utilisateurId, UUID promotionId, UUID chambreId);
}
