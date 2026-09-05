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

  /**
   * buildPageableParams defaults `sort` to "createdAt,desc" (see
   * core/api/pageable.ts) — a sane default for entities that actually have
   * a createdAt column (most of them). Promotion doesn't: no @CreatedDate,
   * no createdAt field on PromotionDto or the JPA entity. Left unoverridden,
   * every request here asked Spring Data to sort by a property that doesn't
   * exist on Promotion, which Spring rejects — that's the "ghost field"
   * that broke every promotions fetch. anneeDeDepart is the closest
   * equivalent (newest cohort first) and actually exists on the entity.
   */
  private withPromotionSort(pageable?: PageableParams): PageableParams {
    return { sort: 'anneeDeDepart,desc', ...pageable };
  }

  getAll(pageable?: PageableParams): Observable<Page<PromotionDto>> {
    return this.http.get<Page<PromotionDto>>(`${this.baseUrl}/`, {
      params: buildPageableParams(this.withPromotionSort(pageable)),
    });
  }

  getAllByFiliere(filiereId: number, pageable?: PageableParams): Observable<Page<PromotionDto>> {
    return this.http.get<Page<PromotionDto>>(`${this.baseUrl}/by-filiere/${filiereId}`, {
      params: buildPageableParams(this.withPromotionSort(pageable)),
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
