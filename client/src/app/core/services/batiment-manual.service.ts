import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BatimentDto, BatimentUpdateRequest } from '../models/dtos';

/**
 * Temporary diagnostic twin of BatimentService — see etage-manual.service.ts
 * for why this exists. Delete once the theory has been tested either way.
 */
@Injectable({ providedIn: 'root' })
export class BatimentManualService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/batiment`;

  getAll(): Observable<BatimentDto[]> {
    return this.http.get<BatimentDto[]>(`${this.baseUrl}/`);
  }

  getById(id: string): Observable<BatimentDto> {
    return this.http.get<BatimentDto>(`${this.baseUrl}/${id}`);
  }

  create(dto: BatimentDto): Observable<BatimentDto> {
    return this.http.post<BatimentDto>(`${this.baseUrl}/`, dto);
  }

  update(request: BatimentUpdateRequest): Observable<BatimentDto> {
    return this.http.put<BatimentDto>(`${this.baseUrl}/`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
