package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.EtageDto;

public record EtageUpdateRequest(@NotNull UUID id, @NotNull @Valid EtageDto dto) {}
