package org.aminesidki.resiaiac.service;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.BatimentDto;

public interface BatimentService {
  List<BatimentDto> getAll();

  BatimentDto save(BatimentDto dto);

  BatimentDto getById(UUID id);

  BatimentDto update(UUID id, BatimentDto dto);

  void delete(UUID id);
}
