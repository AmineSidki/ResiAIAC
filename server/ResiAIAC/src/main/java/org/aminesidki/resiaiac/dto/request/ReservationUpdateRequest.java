package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReservationDto;

public record ReservationUpdateRequest(@NotNull UUID id, @NotNull @Valid ReservationDto dto) {}
