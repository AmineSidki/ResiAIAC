package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.enumeration.EtatChambre;

/** Dto for {@link org.aminesidki.resiaiac.entity.Chambre } */
public record ChambreDto(
    UUID id,
    String matricule,
    Long capacite,
    EtatChambre etat,
    List<UUID> reservations,
    List<UUID> reclamations,
    List<UtilisateurPromotionChambreId> combinaisonsUpc,
    UUID etage)
    implements Serializable {}