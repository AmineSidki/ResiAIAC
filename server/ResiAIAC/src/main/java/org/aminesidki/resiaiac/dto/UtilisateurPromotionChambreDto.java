package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.UUID;

import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Promotion;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

public record UtilisateurPromotionChambreDto(
    UtilisateurPromotionChambreId id,
    Boolean retard,
    String note,
    UUID utilisateurId,
    UUID promotionId,
    UUID chambreId)
    implements Serializable {}
