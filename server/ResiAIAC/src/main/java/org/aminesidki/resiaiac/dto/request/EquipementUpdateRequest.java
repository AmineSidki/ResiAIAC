package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.aminesidki.resiaiac.dto.EquipementDto;

public record EquipementUpdateRequest(@NotNull Long id, @NotNull @Valid EquipementDto dto) {}
