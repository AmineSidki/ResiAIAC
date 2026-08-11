package org.aminesidki.resiaiac.service.impl;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementDto;
import org.aminesidki.resiaiac.mapper.EquipementMapper;
import org.aminesidki.resiaiac.repository.EquipementRepository;
import org.aminesidki.resiaiac.service.EquipementService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EquipementServiceImpl implements EquipementService {

  private final EquipementRepository equipementRepository;
  private final EquipementMapper equipementMapper;

  @Override
  public EquipementDto save(EquipementDto dto) {
    return null;
  }

  @Override
  public EquipementDto getById(Long id) {
    return null;
  }

  @Override
  public EquipementDto update(Long id, EquipementDto dto) {
    return null;
  }

  @Override
  public void delete(Long id) {}
}
