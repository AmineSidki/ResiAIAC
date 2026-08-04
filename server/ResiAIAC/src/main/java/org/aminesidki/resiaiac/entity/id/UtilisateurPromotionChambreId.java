package org.aminesidki.resiaiac.entity.id;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class UtilisateurPromotionChambreId {
    private UUID utilisateur_id;
    private UUID promotion_id;
    private UUID chambre_id;
}
