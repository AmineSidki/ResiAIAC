package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.mapper.EquipementUpcMapper;
import org.aminesidki.resiaiac.repository.EquipementUpcRepository;
import org.aminesidki.resiaiac.service.EquipementUpcService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EquipementUpcServiceImpl implements EquipementUpcService {

  private final EquipementUpcRepository equipementUpcRepository;
  private final EquipementUpcMapper equipementUpcMapper;

  @Override
  public EquipementUpcDto save(EquipementUpcDto dto) {
    return null;
  }

  @Override
  public EquipementUpcDto getById(
      Long equipementId, UUID utilisateurId, UUID promotionId, UUID chambreId) {
    return null;
  }

  @Override
  public EquipementUpcDto update(EquipementUpcId id, EquipementUpcDto dto) {
    return null;
  }

  @Override
  public void delete(Long equipementId, UUID utilisateurId, UUID promotionId, UUID chambreId) {}
}
