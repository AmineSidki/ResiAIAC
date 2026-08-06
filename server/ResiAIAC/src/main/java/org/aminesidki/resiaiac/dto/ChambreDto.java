package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.Etage;
import org.aminesidki.resiaiac.enumeration.EtatChambre;

public record ChambreDto(UUID id, String matricule, long capacite, EtatChambre etat, UUID etageId)
    implements Serializable {}
