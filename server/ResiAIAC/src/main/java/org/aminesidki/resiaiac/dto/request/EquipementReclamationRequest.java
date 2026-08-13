package org.aminesidki.resiaiac.dto.request;

import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;

public record EquipementReclamationRequest(
    EquipementReclamationId id, EquipementReclamationDto dto) {}
