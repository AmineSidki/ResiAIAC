import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { buildCompositeParams } from '../api/composite-params.util';
import { buildPageableParams, PageableParams } from '../api/pageable';
import { EquipementUpcDto, EquipementUpcUpdateRequest, Page } from '../models/dtos';
import { EquipementUpcId } from '../models/ids';

/**
 * Whole controller is class-level @PreAuthorize(MANAGER) — every method
 * here, including reads, requires MANAGER or above.
 */
@Injectable({ providedIn: 'root' })
export class EquipementUpcService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/equipement-upc`;

  getAllByUpc(upcId: {
    utilisateur_id: string;
    promotion_id: string;
    chambre_id: string;
  }): Observable<EquipementUpcDto[]> {
    return this.http.get<EquipementUpcDto[]>(`${this.baseUrl}/by-upc/`, {
      params: buildCompositeParams({
        utilisateurId: upcId.utilisateur_id,
        promotionId: upcId.promotion_id,
        chambreId: upcId.chambre_id,
      }),
    });
  }

  getAllByEquipement(equipementId: number, pageable?: PageableParams): Observable<Page<EquipementUpcDto>> {
    const params = buildPageableParams(pageable).set('equipementId', equipementId);
    return this.http.get<Page<EquipementUpcDto>>(`${this.baseUrl}/by-equipement/`, { params });
  }

  getById(id: EquipementUpcId): Observable<EquipementUpcDto> {
    return this.http.get<EquipementUpcDto>(`${this.baseUrl}/`, {
      params: buildCompositeParams({
        equipementId: id.equipement_id,
        utilisateurId: id.utilisateurPromotionChambre_id.utilisateur_id,
        promotionId: id.utilisateurPromotionChambre_id.promotion_id,
        chambreId: id.utilisateurPromotionChambre_id.chambre_id,
      }),
    });
  }

  create(dto: EquipementUpcDto): Observable<EquipementUpcDto> {
    return this.http.post<EquipementUpcDto>(`${this.baseUrl}/`, dto);
  }

  update(request: EquipementUpcUpdateRequest): Observable<EquipementUpcDto> {
    return this.http.put<EquipementUpcDto>(`${this.baseUrl}/`, request);
  }

  delete(id: EquipementUpcId): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/`, {
      params: buildCompositeParams({
        equipementId: id.equipement_id,
        utilisateurId: id.utilisateurPromotionChambre_id.utilisateur_id,
        promotionId: id.utilisateurPromotionChambre_id.promotion_id,
        chambreId: id.utilisateurPromotionChambre_id.chambre_id,
      }),
    });
  }
}
