package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.aminesidki.resiaiac.dto.FiliereDto;

public record FiliereUpdateRequest(@NotNull Long id, @NotNull @Valid FiliereDto dto) {}
