package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

/** Dto for {@link org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre } */
public record UtilisateurPromotionChambreDto(
    UtilisateurPromotionChambreId id,
    Boolean retard,
    String note,
    UUID utilisateur,
    UUID promotion,
    UUID chambre,
    List<EquipementUpcId> equipementsEndommages)
    implements Serializable {}