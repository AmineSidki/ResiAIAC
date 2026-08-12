package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.ChambreDto;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.mapper.ChambreMapper;
import org.aminesidki.resiaiac.repository.ChambreRepository;
import org.aminesidki.resiaiac.service.ChambreService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ChambreServiceImpl implements ChambreService {

  private final ChambreRepository chambreRepository;
  private final ChambreMapper chambreMapper;

  @Override
  public ChambreDto save(ChambreDto dto) {
    Chambre entity = chambreMapper.toEntity(dto);
    entity = chambreRepository.save(entity);
    return chambreMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public ChambreDto getById(UUID id) {
    Chambre entity = ResourceFetcher.fetchResource(id, chambreRepository, "Chambre");
    return chambreMapper.toDto(entity);
  }

  @Override
  public ChambreDto update(UUID id, ChambreDto dto) {
    Chambre entity = ResourceFetcher.fetchResource(id, chambreRepository, "Chambre");
    chambreMapper.updateEntityFromDto(dto, entity);
    entity = chambreRepository.save(entity);
    return chambreMapper.toDto(entity);
  }

  @Override
  public void delete(UUID id) {
    chambreRepository.delete(ResourceFetcher.fetchResource(id, chambreRepository, "Chambre"));
  }
}
