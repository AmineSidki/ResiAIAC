package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.aminesidki.resiaiac.dto.ServiceDto;

public record ServiceUpdateRequest(@NotNull Long id, @NotNull @Valid ServiceDto dto) {}
