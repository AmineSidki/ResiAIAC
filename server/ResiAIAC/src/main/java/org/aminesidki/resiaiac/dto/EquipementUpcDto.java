package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;

public record EquipementUpcDto(
    EquipementUpcId id, Long quantite, Equipement equipement, UtilisateurPromotionChambre upc)
    implements Serializable {}
