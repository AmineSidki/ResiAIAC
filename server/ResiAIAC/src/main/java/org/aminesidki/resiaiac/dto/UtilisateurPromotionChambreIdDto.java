package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.UUID;

public record UtilisateurPromotionChambreIdDto(
    UUID utilisateur_id, UUID promotion_id, UUID chambre_id) implements Serializable {}
