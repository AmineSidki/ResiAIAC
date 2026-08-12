package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.BatimentDto;

public interface BatimentService {
  BatimentDto save(BatimentDto dto);

  BatimentDto getById(UUID id);

  BatimentDto update(UUID id, BatimentDto dto);

  void delete(UUID id);
}
