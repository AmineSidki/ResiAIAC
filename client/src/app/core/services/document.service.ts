import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { buildPageableParams, PageableParams } from '../api/pageable';
import { DocumentDto, DocumentUpdateRequest, Page } from '../models/dtos';
import { EtatDocument } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class DocumentService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/document`;

  // --- self-service (/me) — open to any authenticated user ---

  getAllMy(pageable?: PageableParams): Observable<Page<DocumentDto>> {
    return this.http.get<Page<DocumentDto>>(`${this.baseUrl}/me`, {
      params: buildPageableParams(pageable),
    });
  }

  getAllMyByStatus(etat: EtatDocument, pageable?: PageableParams): Observable<Page<DocumentDto>> {
    return this.http.get<Page<DocumentDto>>(`${this.baseUrl}/me/by-etat/${etat}`, {
      params: buildPageableParams(pageable),
    });
  }

  /** 404 here means ResourceOwnershipMismatchException, not "no such document" — see AppError.presentation. */
  getMyById(id: string): Observable<DocumentDto> {
    return this.http.get<DocumentDto>(`${this.baseUrl}/me/${id}`);
  }

  /** See getUrlById above — same text/plain-vs-JSON fix, same endpoint shape. */
  getMyUrlById(id: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/me/${id}/url`, { responseType: 'text' });
  }

  /**
   * Convenience for the profile page: current student's profile-picture
   * document, if any. A single page is enough — uploadMy('pfp', ...)
   * always replaces rather than accumulates, so there's at most one.
   *
   * nomSceau on the wire is literally FileType.bucketName ('images' for
   * pfp, not the short 'pfp' slot key) — see DocumentServiceImpl.uploadMyDocument.
   */
  getMyProfileImage(): Observable<DocumentDto | null> {
    return this.getAllMy({ size: 20 }).pipe(
      map((page) => page.content.find((d) => d.nomSceau === 'images') ?? null),
    );
  }

  /** Replaces the student's existing document of this type rather than accumulating history. */
  uploadProfileImage(file: File): Observable<DocumentDto> {
    return this.uploadMy('pfp', file);
  }

  uploadCin(file: File): Observable<DocumentDto> {
    return this.uploadMy('cin', file);
  }

  uploadDiploma(file: File): Observable<DocumentDto> {
    return this.uploadMy('dip', file);
  }

  private uploadMy(segment: 'pfp' | 'cin' | 'dip', file: File): Observable<DocumentDto> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<DocumentDto>(`${this.baseUrl}/me/upload/${segment}`, formData);
  }

  // --- admin/manager side ---

  /** MANAGER-gated. */
  /**
   * Backend returns this as a bare String body — Spring's
   * StringHttpMessageConverter writes that as unquoted text/plain, not
   * JSON. `responseType: 'text'` tells HttpClient to read the body as-is
   * instead of running it through JSON.parse (which was silently throwing
   * on the raw URL string and breaking every document preview).
   */
  getUrlById(id: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/${id}/url`, { responseType: 'text' });
  }

  /** MANAGER-gated. */
  getById(id: string): Observable<DocumentDto> {
    return this.http.get<DocumentDto>(`${this.baseUrl}/${id}`);
  }

  /** RESPONSABLE-gated, paginated. */
  getAll(pageable?: PageableParams): Observable<Page<DocumentDto>> {
    return this.http.get<Page<DocumentDto>>(`${this.baseUrl}/`, {
      params: buildPageableParams(pageable),
    });
  }

  /** RESPONSABLE-gated, paginated — e.g. the review queue for EN_ATTENTE documents. */
  getAllByStatus(etat: EtatDocument, pageable?: PageableParams): Observable<Page<DocumentDto>> {
    return this.http.get<Page<DocumentDto>>(`${this.baseUrl}/by-etat/${etat}`, {
      params: buildPageableParams(pageable),
    });
  }

  /**
   * MANAGER-gated — metadata only (etat, noteSurValidite). There is no
   * admin-side create: Document creation only happens via the self-service
   * upload routes above.
   */
  update(request: DocumentUpdateRequest): Observable<DocumentDto> {
    return this.http.put<DocumentDto>(`${this.baseUrl}/`, request);
  }

  /** RESPONSABLE-gated. */
  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
