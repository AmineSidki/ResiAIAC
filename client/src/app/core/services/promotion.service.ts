import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { buildPageableParams, PageableParams } from '../api/pageable';
import { Page, PromotionDto, PromotionUpdateRequest } from '../models/dtos';

/**
 * MANAGER-gated reads (paginated), RESPONSABLE-gated writes. Doesn't extend
 * BaseCrudService: it's paginated (unlike the flat reference tables) and has
 * the extra by-filiere route.
 */
@Injectable({ providedIn: 'root' })
export class PromotionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/promotion`;

  getAll(pageable?: PageableParams): Observable<Page<PromotionDto>> {
    return this.http.get<Page<PromotionDto>>(`${this.baseUrl}/`, {
      params: buildPageableParams(pageable),
    });
  }

  getAllByFiliere(filiereId: number, pageable?: PageableParams): Observable<Page<PromotionDto>> {
    return this.http.get<Page<PromotionDto>>(`${this.baseUrl}/by-filiere/${filiereId}`, {
      params: buildPageableParams(pageable),
    });
  }

  getById(id: string): Observable<PromotionDto> {
    return this.http.get<PromotionDto>(`${this.baseUrl}/${id}`);
  }

  create(dto: PromotionDto): Observable<PromotionDto> {
    return this.http.post<PromotionDto>(`${this.baseUrl}/`, dto);
  }

  update(request: PromotionUpdateRequest): Observable<PromotionDto> {
    return this.http.put<PromotionDto>(`${this.baseUrl}/`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
