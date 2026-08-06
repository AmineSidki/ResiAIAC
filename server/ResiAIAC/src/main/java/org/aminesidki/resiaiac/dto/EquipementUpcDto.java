package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

public record EquipementUpcDto(
    EquipementUpcId id, Long quantite, Long equipementId, UtilisateurPromotionChambreId upcId)
    implements Serializable {}
