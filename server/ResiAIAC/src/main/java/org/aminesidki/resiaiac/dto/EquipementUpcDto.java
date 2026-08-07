package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

/** Dto for {@link org.aminesidki.resiaiac.entity.EquipementUpc } */
public record EquipementUpcDto(
    EquipementUpcId id, Long quantite, Long equipement, UtilisateurPromotionChambreId upc)
    implements Serializable {}