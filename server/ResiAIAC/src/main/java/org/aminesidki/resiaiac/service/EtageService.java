package org.aminesidki.resiaiac.service;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.EtageDto;

public interface EtageService {
  List<EtageDto> getAll();

  EtageDto save(EtageDto dto);

  EtageDto getById(UUID id);

  EtageDto update(UUID id, EtageDto dto);

  void delete(UUID id);
}
