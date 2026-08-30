/**
 * Mirrors org.aminesidki.resiaiac.dto.* records exactly — field names, order,
 * and nullability kept 1:1 with the Java source. Do NOT rename fields for
 * camelCase aesthetics; several are already camelCase server-side and some
 * (composite ID fields) are intentionally snake_case. See core/models/ids.ts
 * for composite key shapes.
 *
 * Timestamps arrive as ISO-8601 strings over the wire (java.sql.Timestamp
 * serialized by Jackson) — typed as `string` here, not `Date`.
 */

import {
  EquipementReclamationId,
  EquipementUpcId,
  UtilisateurPromotionChambreId,
} from './ids';
import { EtatChambre, EtatDocument, EtatReclamation, EtatReservation } from './enums';

export interface BatimentDto {
  id: string | null; // UUID
  nom: string;
  etages: string[]; // UUID[]
}

export interface ChambreDto {
  id: string | null; // UUID
  matricule: string;
  capacite: number; // Long
  etat: EtatChambre | null;
  reservations: string[]; // UUID[]
  reclamations: string[]; // UUID[]
  combinaisonsUpc: UtilisateurPromotionChambreId[];
  etage: string; // UUID
}

export interface DocumentDto {
  id: string | null; // UUID
  nomFichier: string;
  nomSceau: string | null;
  etat: EtatDocument | null;
  noteSurValidite: string | null;
  proprietaire: string; // UUID
  readonly createdAt: string | null; // read-only
}

export interface EquipementDto {
  id: number | null; // Long
  nom: string;
  reclamations: EquipementReclamationId[];
  upcs: EquipementUpcId[];
}

export interface EquipementReclamationDto {
  id: EquipementReclamationId | null;
  quantite: number; // Long, @Min(1)
  equipement: number; // Long
  reclamation: string; // UUID
}

export interface EquipementUpcDto {
  id: EquipementUpcId | null;
  quantite: number; // Long, @Min(1)
  equipement: number; // Long
  upc: UtilisateurPromotionChambreId;
}

export interface EtageDto {
  id: string | null; // UUID
  numero: string;
  batiment: string; // UUID
  chambres: string[]; // UUID[]
}

export interface FiliereDto {
  id: number | null; // Long
  nom: string;
  niveauMaximal: number; // Integer, @Min(1)
  promotions: string[]; // UUID[]
}

export interface PromotionDto {
  id: string | null; // UUID
  anneeDeDepart: number; // Long
  anneeDeFin: number; // Long
  niveau: number; // Integer, @Min(1)
  filiere: number; // Long
  combinaisonsUpc: UtilisateurPromotionChambreId[];
}

export interface ReclamationDto {
  id: string | null; // UUID
  message: string | null;
  etat: EtatReclamation | null;
  utilisateur: string; // UUID
  chambre: string; // UUID
  service: number; // Long
  equipements: EquipementReclamationId[];
  readonly createdAt: string | null; // read-only
  readonly updatedAt: string | null; // read-only
}

export interface ReservationDto {
  id: string | null; // UUID
  etat: EtatReservation | null;
  utilisateur: string; // UUID
  chambre: string; // UUID
  readonly createdAt: string | null; // read-only
  readonly updatedAt: string | null; // read-only
}

export interface ServiceDto {
  id: number | null; // Long
  nom: string;
  reclamations: string[]; // UUID[]
}

export interface UtilisateurDto {
  id: string | null; // UUID
  email: string;
  nom: string;
  prenom: string;
  cin: string;
  adresse: string | null; // optional-not-blank
  telephone: string; // ^\+?[0-9]{8,15}$
  reservations: string[]; // UUID[]
  reclamations: string[]; // UUID[]
  documents: string[]; // UUID[]
  combinaisonsUpc: UtilisateurPromotionChambreId[];
  readonly createdAt: string | null; // read-only
  readonly updatedAt: string | null; // read-only
}

export interface UtilisateurPromotionChambreDto {
  id: UtilisateurPromotionChambreId | null;
  retard: boolean | null;
  note: string | null; // optional-not-blank
  utilisateur: string; // UUID
  promotion: string; // UUID
  chambre: string; // UUID
  equipementsEndommages: EquipementUpcId[];
}

// --- dto/entry ---

/** org.aminesidki.resiaiac.dto.entry.EquipementEntry — lightweight, NOT EquipementReclamationDto. */
export interface EquipementEntry {
  id: number; // Long, equipement id
  quantite: number; // Long, @Min(1)
}

// --- dto/request (update envelopes: {id, dto}) ---

export interface BatimentUpdateRequest {
  id: string; // UUID
  dto: BatimentDto;
}

export interface ChambreUpdateRequest {
  id: string; // UUID
  dto: ChambreDto;
}

export interface DocumentUpdateRequest {
  id: string; // UUID
  dto: DocumentDto;
}

export interface EquipementReclamationRequest {
  id: EquipementReclamationId;
  dto: EquipementReclamationDto;
}

export interface EquipementUpcUpdateRequest {
  id: EquipementUpcId;
  dto: EquipementUpcDto;
}

export interface EquipementUpdateRequest {
  id: number; // Long
  dto: EquipementDto;
}

export interface EtageUpdateRequest {
  id: string; // UUID
  dto: EtageDto;
}

export interface FiliereUpdateRequest {
  id: number; // Long
  dto: FiliereDto;
}

/** POST /api/v1/reclamation/me body. */
export interface MyReclamationRequest {
  message: string | null;
  service: number; // Long
  equipements: EquipementEntry[];
}

/** POST /api/v1/reservation/me body. */
export interface MyReservationRequest {
  chambre: string; // UUID
}

export interface PromotionUpdateRequest {
  id: string; // UUID
  dto: PromotionDto;
}

export interface ReclamationUpdateRequest {
  id: string; // UUID
  dto: ReclamationDto;
}

export interface ReservationUpdateRequest {
  id: string; // UUID
  dto: ReservationDto;
}

export interface ServiceUpdateRequest {
  id: number; // Long
  dto: ServiceDto;
}

/** PUT /api/v1/utilisateur/me body — only these two fields are self-editable. */
export interface UpdateMeRequest {
  adresse: string | null;
  telephone: string;
}

export interface UtilisateurPromotionChambreUpdateRequest {
  id: UtilisateurPromotionChambreId;
  dto: UtilisateurPromotionChambreDto;
}

export interface UtilisateurUpdateRequest {
  id: string; // UUID
  dto: UtilisateurDto;
}

// --- dto/response ---

/** org.aminesidki.resiaiac.dto.response.ErrorResponse — every handled backend error. */
export interface ErrorResponse {
  status: number;
  message: string;
  timestamp: string; // LocalDateTime, ISO string over the wire
}

/** Spring Data Page<T> shape, as returned by every paginated endpoint. */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // current page index, 0-based
  size: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}
