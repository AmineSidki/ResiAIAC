package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatReservation;

public record ReservationDto(
    UUID id, EtatReservation etat, Utilisateur utilisateur, Chambre chambre, Timestamp createdAt)
    implements Serializable {}
