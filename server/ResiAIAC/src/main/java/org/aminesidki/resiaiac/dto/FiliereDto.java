package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/** Dto for {@link org.aminesidki.resiaiac.entity.Filiere } */
public record FiliereDto(
    Long id,
    @NotBlank(message = "Nom ne peut pas etre vide !") String nom,
    @NotNull @Min(1) Integer niveauMaximal)
    implements Serializable {}
