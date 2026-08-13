package org.aminesidki.resiaiac.service;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.ChambreDto;

public interface ChambreService {
  List<ChambreDto> getAll();

  ChambreDto save(ChambreDto dto);

  ChambreDto getById(UUID id);

  ChambreDto update(UUID id, ChambreDto dto);

  void delete(UUID id);
}
