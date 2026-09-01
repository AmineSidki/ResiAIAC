import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EtageDto, EtageUpdateRequest } from '../models/dtos';

/**
 * Temporary diagnostic twin of EtageService, written by hand with no
 * shared base class — same endpoints, same DTOs, everything inlined.
 * If swapping this in for EtageService makes the bug disappear, that
 * points squarely at BaseCrudService; if the bug persists, BaseCrudService
 * is cleared and the problem is elsewhere (backend, DTO shape, the
 * component consuming it, etc).
 *
 * Delete this file once the theory has been tested either way.
 */
@Injectable({ providedIn: 'root' })
export class EtageManualService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/etage`;

  getAll(): Observable<EtageDto[]> {
    return this.http.get<EtageDto[]>(`${this.baseUrl}/`);
  }

  getById(id: string): Observable<EtageDto> {
    return this.http.get<EtageDto>(`${this.baseUrl}/${id}`);
  }

  create(dto: EtageDto): Observable<EtageDto> {
    return this.http.post<EtageDto>(`${this.baseUrl}/`, dto);
  }

  update(request: EtageUpdateRequest): Observable<EtageDto> {
    return this.http.put<EtageDto>(`${this.baseUrl}/`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
