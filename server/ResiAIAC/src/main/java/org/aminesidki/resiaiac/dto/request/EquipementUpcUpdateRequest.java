package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.aminesidki.resiaiac.dto.EquipementUpcDto;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;

public record EquipementUpcUpdateRequest(
    @NotNull EquipementUpcId id, @NotNull @Valid EquipementUpcDto dto) {}
