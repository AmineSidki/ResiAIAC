package org.aminesidki.resiaiac.dto.request;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReservationDto;

public record ReservationUpdateRequest(UUID id, ReservationDto dto) {}
