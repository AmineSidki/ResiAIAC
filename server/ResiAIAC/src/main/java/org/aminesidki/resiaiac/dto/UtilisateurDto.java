package org.aminesidki.resiaiac.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record UtilisateurDto(
    UUID id,
    String nom,
    String prenom,
    String cin,
    String adresse,
    String telephone,
    Timestamp createdAt,
    Timestamp updatedAt) {}
