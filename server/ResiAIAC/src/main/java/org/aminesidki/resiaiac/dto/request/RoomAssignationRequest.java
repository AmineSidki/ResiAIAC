package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RoomAssignationRequest(
    @NotNull UUID utilisateurId, @NotNull UUID promotionId, UUID reservationId) {}
