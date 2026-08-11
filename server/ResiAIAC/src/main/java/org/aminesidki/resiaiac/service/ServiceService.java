package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.ServiceDto;

public interface ServiceService {
  ServiceDto save(ServiceDto dto);

  ServiceDto getById(Long id);

  ServiceDto update(Long id, ServiceDto dto);

  void delete(Long id);
}
