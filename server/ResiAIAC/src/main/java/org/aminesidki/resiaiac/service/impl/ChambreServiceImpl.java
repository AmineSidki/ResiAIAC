package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ChambreDto;
import org.aminesidki.resiaiac.mapper.ChambreMapper;
import org.aminesidki.resiaiac.repository.ChambreRepository;
import org.aminesidki.resiaiac.service.ChambreService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChambreServiceImpl implements ChambreService {

  private final ChambreRepository chambreRepository;
  private final ChambreMapper chambreMapper;

  @Override
  public ChambreDto save(ChambreDto dto) {
    return null;
  }

  @Override
  public ChambreDto getById(UUID id) {
    return null;
  }

  @Override
  public ChambreDto update(UUID id, ChambreDto dto) {
    return null;
  }

  @Override
  public void delete(UUID id) {}
}
