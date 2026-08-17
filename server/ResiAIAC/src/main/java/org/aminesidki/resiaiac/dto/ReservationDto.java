package org.aminesidki.resiaiac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import org.aminesidki.resiaiac.enumeration.EtatReservation;

/** Dto for {@link org.aminesidki.resiaiac.entity.Reservation } */
public record ReservationDto(
    UUID id,
    EtatReservation etat,
    @NotNull UUID utilisateur,
    @NotNull UUID chambre,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) Timestamp createdAt,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) Timestamp updatedAt)
    implements Serializable {}
