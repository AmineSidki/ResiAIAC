package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.Filiere;

public record PromotionDto(UUID id, Long anneeDeDepart, Long anneeDeFin, Filiere filiere)
    implements Serializable {}
