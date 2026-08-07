package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

/** Dto for {@link org.aminesidki.resiaiac.entity.Utilisateur } */
public record UtilisateurDto(
    UUID id,
    String nom,
    String prenom,
    String cin,
    String adresse,
    String telephone,
    List<UUID> reservations,
    List<UUID> reclamations,
    List<UUID> documents,
    List<UtilisateurPromotionChambreId> combinaisonsUpc,
    Timestamp createdAt,
    Timestamp updatedAt)
    implements Serializable {}