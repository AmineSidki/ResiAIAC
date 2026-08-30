import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { buildCompositeParams } from '../api/composite-params.util';
import { UtilisateurPromotionChambreDto, UtilisateurPromotionChambreUpdateRequest } from '../models/dtos';
import { UtilisateurPromotionChambreId } from '../models/ids';

/**
 * Alias "Upc" — the backend calls this entity UtilisateurPromotionChambre and
 * routes it under /api/v1/upc.
 *
 * Note the route shape here breaks the usual trailing-slash-means-collection
 * convention: getById/save/update/delete are all single-item operations but
 * still hit `/upc/` (trailing slash, composite key in query params) — this
 * is the actual server route, not a generic pattern to replicate elsewhere.
 * MANAGER-gated except delete, which is RESPONSABLE-gated.
 */
@Injectable({ providedIn: 'root' })
export class UtilisateurPromotionChambreService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/upc`;

  getAllByUtilisateurId(utilisateurId: string): Observable<UtilisateurPromotionChambreDto[]> {
    return this.http.get<UtilisateurPromotionChambreDto[]>(`${this.baseUrl}/by-utilisateur/${utilisateurId}`);
  }

  getAllByChambreId(chambreId: string): Observable<UtilisateurPromotionChambreDto[]> {
    return this.http.get<UtilisateurPromotionChambreDto[]>(`${this.baseUrl}/by-chambre/${chambreId}`);
  }

  getById(id: UtilisateurPromotionChambreId): Observable<UtilisateurPromotionChambreDto> {
    return this.http.get<UtilisateurPromotionChambreDto>(`${this.baseUrl}/`, {
      params: buildCompositeParams({
        utilisateurId: id.utilisateur_id,
        promotionId: id.promotion_id,
        chambreId: id.chambre_id,
      }),
    });
  }

  create(dto: UtilisateurPromotionChambreDto): Observable<UtilisateurPromotionChambreDto> {
    return this.http.post<UtilisateurPromotionChambreDto>(`${this.baseUrl}/`, dto);
  }

  update(request: UtilisateurPromotionChambreUpdateRequest): Observable<UtilisateurPromotionChambreDto> {
    return this.http.put<UtilisateurPromotionChambreDto>(`${this.baseUrl}/`, request);
  }

  /** RESPONSABLE-gated (the only write in this controller not gated at MANAGER). */
  delete(id: UtilisateurPromotionChambreId): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/`, {
      params: buildCompositeParams({
        utilisateurId: id.utilisateur_id,
        promotionId: id.promotion_id,
        chambreId: id.chambre_id,
      }),
    });
  }
}
