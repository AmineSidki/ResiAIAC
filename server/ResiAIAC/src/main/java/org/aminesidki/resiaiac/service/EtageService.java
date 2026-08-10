package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.EtageDto;

public interface EtageService {
  EtageDto save(EtageDto dto);

  EtageDto getById(UUID id);

  EtageDto update(UUID id, EtageDto dto);

  void delete(UUID id);
}
