/**
 * Mirrors org.aminesidki.resiaiac.enumeration.* exactly.
 * Values are the raw enum names as they appear in JSON (Jackson default enum serialization).
 */

export type EtatDocument = 'AUCUN' | 'EN_ATTENTE' | 'VALIDE' | 'INVALIDE';

export const ETAT_DOCUMENT_VALUES: EtatDocument[] = [
  'AUCUN',
  'EN_ATTENTE',
  'VALIDE',
  'INVALIDE',
];

export type EtatReclamation =
  | 'EN_ATTENTE'
  | 'EN_TRAITEMENT'
  | 'FERME_TRAITE'
  | 'FERME_SANS_TRAITEMENT';

export const ETAT_RECLAMATION_VALUES: EtatReclamation[] = [
  'EN_ATTENTE',
  'EN_TRAITEMENT',
  'FERME_TRAITE',
  'FERME_SANS_TRAITEMENT',
];

export type EtatReservation = 'ACTIVE' | 'TERMINEE' | 'FERMEE';

export const ETAT_RESERVATION_VALUES: EtatReservation[] = ['ACTIVE', 'TERMINEE', 'FERMEE'];

export type EtatChambre = 'LIBRE' | 'PARTIELLEMENT_LIBRE' | 'MAINTENANCE' | 'OCCUPEE';

export const ETAT_CHAMBRE_VALUES: EtatChambre[] = [
  'LIBRE',
  'PARTIELLEMENT_LIBRE',
  'MAINTENANCE',
  'OCCUPEE',
];

/** FileType.bucketName is server-internal (bucket routing) and never appears in DTO JSON. */
export type FileType = 'IMAGE' | 'CIN' | 'DIPLOMA';

/**
 * Realm roles, in strict hierarchy order (highest to lowest).
 * Spring RoleHierarchyImpl: ADMINISTRATEUR > RESPONSABLE > MANAGER > ETUDIANT.
 * Keycloak realm_access.roles carries these WITHOUT the "ROLE_" prefix that
 * Spring Security adds server-side when building GrantedAuthority instances.
 */
export type AppRole = 'ADMINISTRATEUR' | 'RESPONSABLE' | 'MANAGER' | 'ETUDIANT';

/** Index = rank, 0 is highest. Used to resolve "at least this role" checks. */
export const ROLE_HIERARCHY: AppRole[] = ['ADMINISTRATEUR', 'RESPONSABLE', 'MANAGER', 'ETUDIANT'];
