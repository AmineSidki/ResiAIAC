package org.aminesidki.resiaiac.service;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.BatimentDto;

public interface BatimentService {
  BatimentDto save(BatimentDto dto);

  BatimentDto getById(UUID id);

  BatimentDto update(UUID id, BatimentDto dto);

  /*1. I m not sure if we have to do exception here in all delete methods ?
  if (!batimentRepository.existsById(id)) {
      throw new ResourceNotFoundException("Bâtiment introuvable avec l'id : " + id);
  }
  */
  void delete(UUID id);
}
