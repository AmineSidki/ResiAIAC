package org.aminesidki.resiaiac.entity.id;

import jakarta.persistence.Embeddable;

@Embeddable
public class EquipementUpcId {
    private Long equipementId;
    private UtilisateurPromotionChambreId utilisateurPromotionChambreId;
}
