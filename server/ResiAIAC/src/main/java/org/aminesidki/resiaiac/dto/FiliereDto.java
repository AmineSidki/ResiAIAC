package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/** Dto for {@link org.aminesidki.resiaiac.entity.Filiere } */
public record FiliereDto(Long id, String nom, Integer niveauMaximal, List<UUID> promotions)
    implements Serializable {}
