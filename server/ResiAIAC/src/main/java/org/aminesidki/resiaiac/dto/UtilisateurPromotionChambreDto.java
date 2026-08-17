package org.aminesidki.resiaiac.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.validator.OptionalNotBlank;

/** Dto for {@link org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre } */
public record UtilisateurPromotionChambreDto(
    UtilisateurPromotionChambreId id,
    Boolean retard,
    @OptionalNotBlank String note,
    @NotNull UUID utilisateur,
    @NotNull UUID promotion,
    @NotNull UUID chambre,
    List<EquipementUpcId> equipementsEndommages)
    implements Serializable {}
