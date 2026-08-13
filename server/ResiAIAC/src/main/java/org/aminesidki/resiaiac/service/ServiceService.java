package org.aminesidki.resiaiac.service;

import java.util.List;
import org.aminesidki.resiaiac.dto.ServiceDto;

public interface ServiceService {
  List<ServiceDto> getAll();

  ServiceDto save(ServiceDto dto);

  ServiceDto getById(Long id);

  ServiceDto update(Long id, ServiceDto dto);

  void delete(Long id);
}
