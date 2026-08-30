/**
 * Mirrors org.aminesidki.resiaiac.entity.id.* @Embeddable composite key classes.
 * Field names kept 1:1 with the JSON (snake_case as declared server-side), not
 * renamed to camelCase.
 */

export interface UtilisateurPromotionChambreId {
  utilisateur_id: string; // UUID
  promotion_id: string; // UUID
  chambre_id: string; // UUID
}

export interface EquipementUpcId {
  equipement_id: number; // Long
  utilisateurPromotionChambre_id: UtilisateurPromotionChambreId;
}

export interface EquipementReclamationId {
  equipement_id: number; // Long
  reclamation_id: string; // UUID
}
