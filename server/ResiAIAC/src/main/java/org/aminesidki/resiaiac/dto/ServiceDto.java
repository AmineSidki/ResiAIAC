package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/** Dto for {@link org.aminesidki.resiaiac.entity.Service } */
public record ServiceDto(
    Long id, @NotBlank(message = "Nom ne peut pas etre vide !") String nom, List<UUID> reclamations)
    implements Serializable {}
