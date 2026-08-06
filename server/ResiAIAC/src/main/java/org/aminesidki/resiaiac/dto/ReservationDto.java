package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatReservation;

public record ReservationDto(
    UUID id, EtatReservation etat, UUID utilisateurId, UUID chambreId, Timestamp createdAt)
    implements Serializable {}
