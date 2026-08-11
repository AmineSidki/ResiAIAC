package org.aminesidki.resiaiac.service.impl;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ServiceDto;
import org.aminesidki.resiaiac.mapper.ServiceMapper;
import org.aminesidki.resiaiac.repository.ServiceRepository;
import org.aminesidki.resiaiac.service.ServiceService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ServiceServiceImpl implements ServiceService {

  private final ServiceRepository serviceRepository;
  private final ServiceMapper serviceMapper;

  @Override
  public ServiceDto save(ServiceDto dto) {
    return null;
  }

  @Override
  public ServiceDto getById(Long id) {
    return null;
  }

  @Override
  public ServiceDto update(Long id, ServiceDto dto) {
    return null;
  }

  @Override
  public void delete(Long id) {}
}
