package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import org.aminesidki.resiaiac.enumeration.EtatDocument;

/** Dto for {@link org.aminesidki.resiaiac.entity.Document } */
public record DocumentDto(
    UUID id,
    String nomFichier,
    String nomSceau,
    EtatDocument etat,
    String noteSurValidite,
    UUID proprietaire,
    Timestamp createdAt)
    implements Serializable {}
