package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.Utilisateur;

public record DocumentDto(
    UUID id,
    String nomFichier,
    String nomSceau,
    Boolean valide,
    String noteSurValidite,
    UUID proprietaireId,
    Timestamp createdAt)
    implements Serializable {}
