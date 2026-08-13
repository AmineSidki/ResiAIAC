package org.aminesidki.resiaiac.dto.request;

import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;

public record EquipementUpcUpdateRequest(EquipementUpcId id, EquipementUpcDto dto) {}
