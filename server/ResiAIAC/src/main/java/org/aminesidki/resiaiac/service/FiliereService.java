package org.aminesidki.resiaiac.service;

import java.util.List;
import org.aminesidki.resiaiac.dto.FiliereDto;
import org.aminesidki.resiaiac.entity.Filiere;

public interface FiliereService {
  Filiere getEntity(Long id);

  List<FiliereDto> getAll();

  FiliereDto save(FiliereDto dto);

  FiliereDto getById(Long id);

  FiliereDto update(Long id, FiliereDto dto);

  void delete(Long id);
}
