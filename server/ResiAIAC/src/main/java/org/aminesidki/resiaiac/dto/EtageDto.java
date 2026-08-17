package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/** Dto for {@link org.aminesidki.resiaiac.entity.Etage } */
public record EtageDto(
    UUID id,
    @NotBlank(message = "Numero d'etage ne peut pas etre vide !") String numero,
    @NotNull UUID batiment,
    List<UUID> chambres)
    implements Serializable {}
