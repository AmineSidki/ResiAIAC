package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.FiliereDto;

public interface FiliereService {
  FiliereDto save(FiliereDto dto);

  FiliereDto getById(Long id);

  FiliereDto update(Long id, FiliereDto dto);

  void delete(Long id);
}
