import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ChambreDto, ChambreUpdateRequest } from '../models/dtos';

/**
 * Temporary diagnostic twin of ChambreService — see etage-manual.service.ts
 * for why this exists. Delete once the theory has been tested either way.
 */
@Injectable({ providedIn: 'root' })
export class ChambreManualService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/chambre`;

  getAll(): Observable<ChambreDto[]> {
    return this.http.get<ChambreDto[]>(`${this.baseUrl}/`);
  }

  getById(id: string): Observable<ChambreDto> {
    return this.http.get<ChambreDto>(`${this.baseUrl}/${id}`);
  }

  create(dto: ChambreDto): Observable<ChambreDto> {
    return this.http.post<ChambreDto>(`${this.baseUrl}/`, dto);
  }

  update(request: ChambreUpdateRequest): Observable<ChambreDto> {
    return this.http.put<ChambreDto>(`${this.baseUrl}/`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
