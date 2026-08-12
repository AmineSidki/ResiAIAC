package org.aminesidki.resiaiac.service.impl;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ServiceDto;
import org.aminesidki.resiaiac.entity.Service;
import org.aminesidki.resiaiac.mapper.ServiceMapper;
import org.aminesidki.resiaiac.repository.ServiceRepository;
import org.aminesidki.resiaiac.service.ServiceService;
import org.aminesidki.resiaiac.util.ResourceFetcher;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

  private final ServiceRepository serviceRepository;
  private final ServiceMapper serviceMapper;

  @Override
  public ServiceDto save(ServiceDto dto) {
    Service entity = serviceMapper.toEntity(dto);
    entity = serviceRepository.save(entity);
    return serviceMapper.toDto(entity);
  }

  @Override
  public ServiceDto getById(Long id) {
    Service entity = ResourceFetcher.fetchResource(id, serviceRepository, "Service");
    return serviceMapper.toDto(entity);
  }

  @Override
  public ServiceDto update(Long id, ServiceDto dto) {
    Service entity = ResourceFetcher.fetchResource(id, serviceRepository, "Service");
    serviceMapper.updateEntityFromDto(dto, entity);
    entity = serviceRepository.save(entity);
    return serviceMapper.toDto(entity);
  }

  @Override
  public void delete(Long id) {
    serviceRepository.delete(ResourceFetcher.fetchResource(id, serviceRepository, "Service"));
  }
}
