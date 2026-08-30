import { HttpParams } from '@angular/common/http';

/**
 * Mirrors Spring's Pageable/@PageableDefault(sort = "createdAt", direction = DESC).
 * Server enforces a hard max-page-size of 20 — this client treats it as a
 * ceiling too, never requesting a larger page.
 */
export interface PageableParams {
  page?: number;
  size?: number;
  /** Spring sort syntax, e.g. "createdAt,desc" or "createdAt,asc". */
  sort?: string;
}

export const MAX_PAGE_SIZE = 20;
export const DEFAULT_SORT = 'createdAt,desc';

export function buildPageableParams(pageable?: PageableParams): HttpParams {
  const page = pageable?.page ?? 0;
  const requestedSize = pageable?.size ?? MAX_PAGE_SIZE;
  const size = Math.min(requestedSize, MAX_PAGE_SIZE);
  const sort = pageable?.sort ?? DEFAULT_SORT;

  return new HttpParams().set('page', page).set('size', size).set('sort', sort);
}
