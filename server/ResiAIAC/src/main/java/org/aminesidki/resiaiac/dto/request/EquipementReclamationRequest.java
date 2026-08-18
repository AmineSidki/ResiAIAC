package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;

public record EquipementReclamationRequest(
    @NotNull EquipementReclamationId id, @NotNull @Valid EquipementReclamationDto dto) {}
