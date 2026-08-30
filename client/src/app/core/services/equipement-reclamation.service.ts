import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { buildCompositeParams } from '../api/composite-params.util';
import { buildPageableParams, PageableParams } from '../api/pageable';
import { EquipementReclamationDto, EquipementReclamationRequest, Page } from '../models/dtos';
import { EquipementReclamationId } from '../models/ids';

/**
 * Whole controller is class-level @PreAuthorize(MANAGER) — every method
 * here, including reads, requires MANAGER or above.
 *
 * NOTE the `by-Equipement` segment is capitalized in the actual backend
 * route (unlike `by-equipement` on EquipementUpcService) — keep this exact
 * casing, it is not a typo to "fix".
 */
@Injectable({ providedIn: 'root' })
export class EquipementReclamationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/equipement-reclamation`;

  getAllByEquipementId(
    equipementId: number,
    pageable?: PageableParams,
  ): Observable<Page<EquipementReclamationDto>> {
    return this.http.get<Page<EquipementReclamationDto>>(`${this.baseUrl}/by-Equipement/${equipementId}`, {
      params: buildPageableParams(pageable),
    });
  }

  getAllByReclamationId(reclamationId: string): Observable<EquipementReclamationDto[]> {
    return this.http.get<EquipementReclamationDto[]>(`${this.baseUrl}/by-reclamation/${reclamationId}`);
  }

  getById(id: EquipementReclamationId): Observable<EquipementReclamationDto> {
    return this.http.get<EquipementReclamationDto>(`${this.baseUrl}/`, {
      params: buildCompositeParams({
        equipementId: id.equipement_id,
        reclamationId: id.reclamation_id,
      }),
    });
  }

  create(dto: EquipementReclamationDto): Observable<EquipementReclamationDto> {
    return this.http.post<EquipementReclamationDto>(`${this.baseUrl}/`, dto);
  }

  update(request: EquipementReclamationRequest): Observable<EquipementReclamationDto> {
    return this.http.put<EquipementReclamationDto>(`${this.baseUrl}/`, request);
  }

  delete(id: EquipementReclamationId): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/`, {
      params: buildCompositeParams({
        equipementId: id.equipement_id,
        reclamationId: id.reclamation_id,
      }),
    });
  }
}
