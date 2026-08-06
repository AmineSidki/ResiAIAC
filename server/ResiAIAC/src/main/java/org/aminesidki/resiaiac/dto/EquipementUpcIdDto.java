package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

public record EquipementUpcIdDto(
    Long equipement_id, UtilisateurPromotionChambreId utilisateurPromotionChambre_id)
    implements Serializable {}
