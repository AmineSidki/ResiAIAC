import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

/**
 * Base for the simple, non-paginated, flat-list entities (Batiment, Etage,
 * Filiere, Service, Equipement) — small reference/lookup tables where
 * pagination was deliberately skipped server-side.
 *
 * Collection routes always end in a trailing slash (`GET /x/`), item routes
 * never do (`/x/{id}`) — baked in here once so no per-call code has to
 * remember it.
 *
 * `TId` is `string` (UUID) or `number` (Long) depending on the entity.
 */
export abstract class BaseCrudService<TDto, TUpdateRequest, TId extends string | number> {
  protected readonly http = inject(HttpClient);

  /** e.g. `${environment.apiBaseUrl}/batiment` — no leading/trailing slash beyond the segment. */
  protected abstract readonly resourcePath: string;

  private get baseUrl(): string {
    return `${environment.apiBaseUrl}/${this.resourcePath}`;
  }

  getAll(): Observable<TDto[]> {
    return this.http.get<TDto[]>(`${this.baseUrl}/`);
  }

  getById(id: TId): Observable<TDto> {
    return this.http.get<TDto>(`${this.baseUrl}/${id}`);
  }

  create(dto: TDto): Observable<TDto> {
    return this.http.post<TDto>(`${this.baseUrl}/`, dto);
  }

  update(request: TUpdateRequest): Observable<TDto> {
    return this.http.put<TDto>(`${this.baseUrl}/`, request);
  }

  delete(id: TId): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
