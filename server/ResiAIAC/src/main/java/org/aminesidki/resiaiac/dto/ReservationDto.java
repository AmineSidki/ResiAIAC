package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import org.aminesidki.resiaiac.enumeration.EtatReservation;

/** Dto for {@link org.aminesidki.resiaiac.entity.Reservation } */
public record ReservationDto(
    UUID id, EtatReservation etat, UUID utilisateur, UUID chambre, Timestamp createdAt)
    implements Serializable {}
