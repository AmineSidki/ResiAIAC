package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.BatimentDto;
import org.aminesidki.resiaiac.mapper.BatimentMapper;
import org.aminesidki.resiaiac.repository.BatimentRepository;
import org.aminesidki.resiaiac.service.BatimentService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BatimentServiceImpl implements BatimentService {

  private final BatimentRepository batimentRepository;
  private final BatimentMapper batimentMapper;

  @Override
  public BatimentDto save(BatimentDto dto) {
    return null;
  }

  @Override
  public BatimentDto getById(UUID id) {
    return null;
  }

  @Override
  public BatimentDto update(UUID id, BatimentDto dto) {
    return null;
  }

  @Override
  public void delete(UUID id) {}
}
