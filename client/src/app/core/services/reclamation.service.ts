import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { buildPageableParams, PageableParams } from '../api/pageable';
import { MyReclamationRequest, Page, ReclamationDto, ReclamationUpdateRequest } from '../models/dtos';
import { EtatReclamation } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class ReclamationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/reclamation`;

  // --- self-service (/me) — open to any authenticated user ---

  getAllMy(pageable?: PageableParams): Observable<Page<ReclamationDto>> {
    return this.http.get<Page<ReclamationDto>>(`${this.baseUrl}/me`, {
      params: buildPageableParams(pageable),
    });
  }

  getAllMyByStatus(etat: EtatReclamation, pageable?: PageableParams): Observable<Page<ReclamationDto>> {
    return this.http.get<Page<ReclamationDto>>(`${this.baseUrl}/me/by-etat/${etat}`, {
      params: buildPageableParams(pageable),
    });
  }

  /** Resolves the student's own room via UPC server-side — no chambre id in the request body. */
  createMy(request: MyReclamationRequest): Observable<ReclamationDto> {
    return this.http.post<ReclamationDto>(`${this.baseUrl}/me`, request);
  }

  getMyById(id: string): Observable<ReclamationDto> {
    return this.http.get<ReclamationDto>(`${this.baseUrl}/me/${id}`);
  }

  // --- MANAGER-and-above ---

  getAll(pageable?: PageableParams): Observable<Page<ReclamationDto>> {
    return this.http.get<Page<ReclamationDto>>(`${this.baseUrl}/`, {
      params: buildPageableParams(pageable),
    });
  }

  getAllByStatus(etat: EtatReclamation, pageable?: PageableParams): Observable<Page<ReclamationDto>> {
    return this.http.get<Page<ReclamationDto>>(`${this.baseUrl}/by-etat/${etat}`, {
      params: buildPageableParams(pageable),
    });
  }

  getById(id: string): Observable<ReclamationDto> {
    return this.http.get<ReclamationDto>(`${this.baseUrl}/${id}`);
  }

  /** Admin-side create — a manager filing a Reclamation on a student's behalf. */
  create(dto: ReclamationDto): Observable<ReclamationDto> {
    return this.http.post<ReclamationDto>(`${this.baseUrl}/`, dto);
  }

  update(request: ReclamationUpdateRequest): Observable<ReclamationDto> {
    return this.http.put<ReclamationDto>(`${this.baseUrl}/`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
