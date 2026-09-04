package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;
import org.aminesidki.resiaiac.enumeration.EtatChambre;

/** Dto for {@link org.aminesidki.resiaiac.entity.Chambre } */
public record ChambreDto(
    UUID id,
    @NotNull @NotBlank(message = "Matricule chambre ne peut pas etre vide !") String matricule,
    @NotNull @Min(1) Long capacite,
    EtatChambre etat,
    @NotNull UUID etage)
    implements Serializable {}
