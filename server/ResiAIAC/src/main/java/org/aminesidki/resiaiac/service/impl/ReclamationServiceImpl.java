package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.mapper.ReclamationMapper;
import org.aminesidki.resiaiac.repository.ReclamationRepository;
import org.aminesidki.resiaiac.service.ReclamationService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReclamationServiceImpl implements ReclamationService {

  private final ReclamationRepository reclamationRepository;
  private final ReclamationMapper reclamationMapper;

  @Override
  public ReclamationDto save(ReclamationDto dto) {
    return null;
  }

  @Override
  public ReclamationDto getById(UUID id) {
    return null;
  }

  @Override
  public ReclamationDto update(UUID id, ReclamationDto dto) {
    return null;
  }

  @Override
  public void delete(UUID id) {}
}
