package org.aminesidki.resiaiac.service;

import java.util.List;
import org.aminesidki.resiaiac.dto.FiliereDto;

public interface FiliereService {
  List<FiliereDto> getAll();

  FiliereDto save(FiliereDto dto);

  FiliereDto getById(Long id);

  FiliereDto update(Long id, FiliereDto dto);

  void delete(Long id);
}
