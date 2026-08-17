package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/** Dto for {@link org.aminesidki.resiaiac.entity.Batiment } */
public record BatimentDto(
    UUID id, @NotBlank(message = "Nom ne peut pas etre vide !") String nom, List<UUID> etages)
    implements Serializable {}
