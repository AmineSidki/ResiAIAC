import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { buildPageableParams, PageableParams } from '../api/pageable';
import { MyReservationRequest, Page, ReservationDto, ReservationUpdateRequest } from '../models/dtos';

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/reservation`;

  // --- self-service (/me) — open to any authenticated user ---

  getAllMy(pageable?: PageableParams): Observable<Page<ReservationDto>> {
    return this.http.get<Page<ReservationDto>>(`${this.baseUrl}/me`, {
      params: buildPageableParams(pageable),
    });
  }

  getMyById(id: string): Observable<ReservationDto> {
    return this.http.get<ReservationDto>(`${this.baseUrl}/me/${id}`);
  }

  /**
   * A 400 here is very likely RoomFullException (the chosen chambre has no
   * capacity left) — see AppError.presentation and the note in
   * core/api/app-error.ts on how 4xx are treated as expected/user-facing.
   */
  createMy(request: MyReservationRequest): Observable<ReservationDto> {
    return this.http.post<ReservationDto>(`${this.baseUrl}/me`, request);
  }

  // --- MANAGER-and-above ---

  getAll(pageable?: PageableParams): Observable<Page<ReservationDto>> {
    return this.http.get<Page<ReservationDto>>(`${this.baseUrl}/`, {
      params: buildPageableParams(pageable),
    });
  }

  getById(id: string): Observable<ReservationDto> {
    return this.http.get<ReservationDto>(`${this.baseUrl}/${id}`);
  }

  create(dto: ReservationDto): Observable<ReservationDto> {
    return this.http.post<ReservationDto>(`${this.baseUrl}/`, dto);
  }

  update(request: ReservationUpdateRequest): Observable<ReservationDto> {
    return this.http.put<ReservationDto>(`${this.baseUrl}/`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
