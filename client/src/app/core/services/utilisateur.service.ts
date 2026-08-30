import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { buildPageableParams, PageableParams } from '../api/pageable';
import { Page, UpdateMeRequest, UtilisateurDto, UtilisateurUpdateRequest } from '../models/dtos';

@Injectable({ providedIn: 'root' })
export class UtilisateurService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/utilisateur`;

  /** Open to any authenticated user — resolves via the JWT, no id needed. */
  getMe(): Observable<UtilisateurDto> {
    return this.http.get<UtilisateurDto>(`${this.baseUrl}/me`);
  }

  /** Only `adresse`/`telephone` are self-editable — see UpdateMeRequest. */
  updateMe(request: UpdateMeRequest): Observable<UtilisateurDto> {
    return this.http.put<UtilisateurDto>(`${this.baseUrl}/me`, request);
  }

  /** RESPONSABLE-gated, paginated. */
  getAll(pageable?: PageableParams): Observable<Page<UtilisateurDto>> {
    return this.http.get<Page<UtilisateurDto>>(`${this.baseUrl}/`, {
      params: buildPageableParams(pageable),
    });
  }

  /** MANAGER-gated. */
  getById(id: string): Observable<UtilisateurDto> {
    return this.http.get<UtilisateurDto>(`${this.baseUrl}/${id}`);
  }

  /** RESPONSABLE-gated — admin-side creation; provisions the Keycloak account server-side too. */
  create(dto: UtilisateurDto): Observable<UtilisateurDto> {
    return this.http.post<UtilisateurDto>(`${this.baseUrl}/`, dto);
  }

  /** RESPONSABLE-gated. Note: does not currently propagate nom/prenom changes to the Keycloak username. */
  update(request: UtilisateurUpdateRequest): Observable<UtilisateurDto> {
    return this.http.put<UtilisateurDto>(`${this.baseUrl}/`, request);
  }

  /** RESPONSABLE-gated — also deprovisions the Keycloak account server-side. */
  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
