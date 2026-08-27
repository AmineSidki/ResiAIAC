package org.aminesidki.resiaiac.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ServiceDto;
import org.aminesidki.resiaiac.entity.Service;
import org.aminesidki.resiaiac.mapper.ServiceMapper;
import org.aminesidki.resiaiac.repository.ServiceRepository;
import org.aminesidki.resiaiac.service.ServiceService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@org.springframework.stereotype.Service
@CacheConfig(cacheNames = "services")
public class ServiceServiceImpl implements ServiceService {

  private final ServiceRepository serviceRepository;
  private final ServiceMapper serviceMapper;

  @Override
  @Cacheable(key = "'all'")
  public List<ServiceDto> getAll() {
    return serviceRepository.findAll().stream().map(serviceMapper::toDto).toList();
  }

  @Override
  @Caching(evict = {@CacheEvict(key = "'all'")})
  public ServiceDto save(ServiceDto dto) {
    Service entity = serviceMapper.toEntity(dto);
    entity = serviceRepository.save(entity);
    return serviceMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(key = "#id")
  public ServiceDto getById(Long id) {
    Service entity = ResourceFetcher.fetchResource(id, serviceRepository, "Service");
    return serviceMapper.toDto(entity);
  }

  @Override
  @Caching(evict = {@CacheEvict(key = "#id"), @CacheEvict(key = "'all'")})
  public ServiceDto update(Long id, ServiceDto dto) {
    Service entity = ResourceFetcher.fetchResource(id, serviceRepository, "Service");
    serviceMapper.updateEntityFromDto(dto, entity);
    entity = serviceRepository.save(entity);
    return serviceMapper.toDto(entity);
  }

  @Override
  @Caching(evict = {@CacheEvict(key = "#id"), @CacheEvict(key = "'all'")})
  public void delete(Long id) {
    serviceRepository.delete(ResourceFetcher.fetchResource(id, serviceRepository, "Service"));
  }
}
