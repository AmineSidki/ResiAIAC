package org.aminesidki.resiaiac.service;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.ChambreDto;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.enumeration.EtatChambre;

public interface ChambreService {
  void updateEtatChambre(UUID id, EtatChambre etatChambre);

  Chambre getEntityById(@NotNull UUID id);

  List<ChambreDto> getAll();

  ChambreDto save(ChambreDto dto);

  ChambreDto getById(UUID id);

  ChambreDto update(UUID id, ChambreDto dto);

  void delete(UUID id);

  Chambre getRandom();
}
