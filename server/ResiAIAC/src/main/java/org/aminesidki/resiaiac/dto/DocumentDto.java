package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import org.aminesidki.resiaiac.enumeration.EtatDocument;
import org.aminesidki.resiaiac.validator.OptionalNotBlank;

/** Dto for {@link org.aminesidki.resiaiac.entity.Document } */
public record DocumentDto(
    UUID id,
    @NotBlank(message = "Nom du fichier ne peut pas etre vide !") String nomFichier,
    String nomSceau,
    EtatDocument etat,
    @OptionalNotBlank String noteSurValidite,
    @NotNull UUID proprietaire,
    Timestamp createdAt)
    implements Serializable {}
