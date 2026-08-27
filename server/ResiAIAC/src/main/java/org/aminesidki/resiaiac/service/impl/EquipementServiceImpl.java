package org.aminesidki.resiaiac.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementDto;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.mapper.EquipementMapper;
import org.aminesidki.resiaiac.repository.EquipementRepository;
import org.aminesidki.resiaiac.service.EquipementService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
@CacheConfig(cacheNames = "equipements")
public class EquipementServiceImpl implements EquipementService {

  private final EquipementRepository equipementRepository;
  private final EquipementMapper equipementMapper;

  @Override
  @Cacheable(key = "'all'")
  public List<EquipementDto> getAll() {
    return equipementRepository.findAll().stream().map(equipementMapper::toDto).toList();
  }

  @Override
  @CacheEvict(allEntries = true)
  public EquipementDto save(EquipementDto dto) {
    Equipement entity = equipementMapper.toEntity(dto);
    entity = equipementRepository.save(entity);
    return equipementMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public Equipement getEntityById(Long id) {
    return ResourceFetcher.fetchResource(id, equipementRepository, "Equipement");
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(key = "#id")
  public EquipementDto getById(Long id) {
    Equipement entity = ResourceFetcher.fetchResource(id, equipementRepository, "Equipement");
    return equipementMapper.toDto(entity);
  }

  @Override
  @CacheEvict(allEntries = true)
  public EquipementDto update(Long id, EquipementDto dto) {
    Equipement entity = ResourceFetcher.fetchResource(id, equipementRepository, "Equipement");
    equipementMapper.updateEntityFromDto(dto, entity);
    entity = equipementRepository.save(entity);
    return equipementMapper.toDto(entity);
  }

  @Override
  @CacheEvict(allEntries = true)
  public void delete(Long id) {
    equipementRepository.delete(
        ResourceFetcher.fetchResource(id, equipementRepository, "Equipement"));
  }
}
