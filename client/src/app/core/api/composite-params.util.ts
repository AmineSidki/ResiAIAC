import { HttpParams } from '@angular/common/http';

/**
 * Composite-key entities (UPC, EquipementUpc, EquipementReclamation) are
 * addressed via query params rather than a single path {id} segment.
 * This is the single shared helper every composite-key service should use
 * instead of hand-rolling HttpParams per call.
 *
 * Example: buildCompositeParams({ utilisateurId: u, promotionId: p, chambreId: c })
 *   -> ?utilisateurId=..&promotionId=..&chambreId=..
 *
 * Falsy-but-valid values (0) are kept; only null/undefined are skipped, so a
 * partial key lookup (e.g. equipementId only) is possible where the backend
 * route allows it.
 */
export function buildCompositeParams(
  parts: Record<string, string | number | null | undefined>,
): HttpParams {
  let params = new HttpParams();
  for (const [key, value] of Object.entries(parts)) {
    if (value !== null && value !== undefined) {
      params = params.set(key, value);
    }
  }
  return params;
}
