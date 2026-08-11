package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EtageDto;
import org.aminesidki.resiaiac.mapper.EtageMapper;
import org.aminesidki.resiaiac.repository.EtageRepository;
import org.aminesidki.resiaiac.service.EtageService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EtageServiceImpl implements EtageService {
  private final EtageRepository etageRepository;
  private final EtageMapper etageMapper;

  @Override
  public EtageDto save(EtageDto dto) {
    return null;
  }

  @Override
  public EtageDto getById(UUID id) {
    return null;
  }

  @Override
  public EtageDto update(UUID id, EtageDto dto) {
    return null;
  }

  @Override
  public void delete(UUID id) {}
}
