package org.aminesidki.resiaiac.service.impl;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.FiliereDto;
import org.aminesidki.resiaiac.mapper.FiliereMapper;
import org.aminesidki.resiaiac.repository.FiliereRepository;
import org.aminesidki.resiaiac.service.FiliereService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FiliereServiceImpl implements FiliereService {

  private final FiliereRepository filiereRepository;
  private final FiliereMapper filiereMapper;

  @Override
  public FiliereDto save(FiliereDto dto) {
    return null;
  }

  @Override
  public FiliereDto getById(Long id) {
    return null;
  }

  @Override
  public FiliereDto update(Long id, FiliereDto dto) {
    return null;
  }

  @Override
  public void delete(Long id) {}
}
