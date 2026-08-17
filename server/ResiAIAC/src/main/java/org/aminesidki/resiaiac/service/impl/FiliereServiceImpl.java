package org.aminesidki.resiaiac.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.FiliereDto;
import org.aminesidki.resiaiac.entity.Filiere;
import org.aminesidki.resiaiac.mapper.FiliereMapper;
import org.aminesidki.resiaiac.repository.FiliereRepository;
import org.aminesidki.resiaiac.service.FiliereService;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class FiliereServiceImpl implements FiliereService {

  private final FiliereRepository filiereRepository;
  private final FiliereMapper filiereMapper;

  @Override
  public List<FiliereDto> getAll() {
    return filiereRepository.findAll().stream().map(filiereMapper::toDto).toList();
  }

  @Override
  public FiliereDto save(FiliereDto dto) {
    Filiere entity = filiereMapper.toEntity(dto);
    entity = filiereRepository.save(entity);
    return filiereMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public FiliereDto getById(Long id) {
    Filiere entity = ResourceFetcher.fetchResource(id, filiereRepository, "Filiere");
    return filiereMapper.toDto(entity);
  }

  @Override
  public FiliereDto update(Long id, FiliereDto dto) {
    Filiere entity = ResourceFetcher.fetchResource(id, filiereRepository, "Filiere");
    filiereMapper.updateEntityFromDto(dto, entity);
    entity = filiereRepository.save(entity);
    return filiereMapper.toDto(entity);
  }

  @Override
  public void delete(Long id) {
    filiereRepository.delete(ResourceFetcher.fetchResource(id, filiereRepository, "Filiere"));
  }
}
