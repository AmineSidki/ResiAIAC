package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;


public interface EquipementReclamationService {
    EquipementReclamationDto save(EquipementReclamationDto dto);
    EquipementReclamationDto getById(EquipementReclamationId id);
    EquipementReclamationDto update(EquipementReclamationId id,EquipementReclamationDto dto);
    void delete(EquipementReclamationId id);

}
