package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/** Dto for {@link org.aminesidki.resiaiac.entity.Document } */
public record DocumentDto(
    UUID id,
    String nomFichier,
    String nomSceau,
    Boolean valide,
    String noteSurValidite,
    UUID proprietaire,
    Timestamp createdAt)
    implements Serializable {}
