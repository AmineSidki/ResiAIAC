package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

public record MyReservationRequest(@NotNull UUID chambre) implements Serializable {}
