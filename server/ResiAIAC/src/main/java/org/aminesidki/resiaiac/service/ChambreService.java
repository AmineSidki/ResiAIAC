package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ChambreDto;

public interface ChambreService {
  ChambreDto save(ChambreDto dto);

  ChambreDto getById(UUID id);

  ChambreDto update(UUID id, ChambreDto dto);

  void delete(UUID id);
}
