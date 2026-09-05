import { inject, Injectable } from '@angular/core';
import { forkJoin, map, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UtilisateurService } from '../../../core/services/utilisateur.service';

/**
 * Resolves owner/utilisateur UUIDs to display names ("Amine Sidki") for the
 * admin Reservations/Documents/Réclamations list and detail pages — all
 * three only ever had the raw `utilisateur`/`proprietaire` UUID string to
 * show, since ReservationDto/DocumentDto/ReclamationDto only carry the id,
 * not a name. UtilisateurService.getById is MANAGER-gated, matching (or
 * looser than) every page this is used from.
 *
 * Caches resolved names for the lifetime of the app (providedIn: 'root') —
 * names change rarely enough that avoiding a re-fetch on every page
 * navigation is worth the staleness risk; call `.invalidate(id)` after an
 * edit that changes nom/prenom if that ever becomes visibly stale.
 */
@Injectable({ providedIn: 'root' })
export class OwnerNameService {
  private readonly utilisateurService = inject(UtilisateurService);
  private readonly cache = new Map<string, string>();

  /** Resolves a batch of (possibly-duplicate) ids, returns the full cache (including ids resolved on previous calls). */
  resolveMany(ids: string[]): Observable<Map<string, string>> {
    const unique = [...new Set(ids)].filter((id) => !this.cache.has(id));
    if (unique.length === 0) {
      return of(new Map(this.cache));
    }

    return forkJoin(
      unique.map((id) =>
        this.utilisateurService.getById(id).pipe(
          map((user) => ({ id, name: `${user.prenom} ${user.nom}` })),
          // A 404 here (deleted user, or a MANAGER without access to a
          // RESPONSABLE-only-visible record) shouldn't break the whole
          // table — fall back to a shortened id rather than erroring.
          catchError(() => of({ id, name: `Utilisateur #${id.slice(0, 8)}…` })),
        ),
      ),
    ).pipe(
      map((resolved) => {
        resolved.forEach(({ id, name }) => this.cache.set(id, name));
        return new Map(this.cache);
      }),
    );
  }

  /** Convenience for detail pages resolving a single id. */
  resolveOne(id: string): Observable<string> {
    return this.resolveMany([id]).pipe(map((names) => names.get(id) ?? `Utilisateur #${id.slice(0, 8)}…`));
  }

  invalidate(id: string): void {
    this.cache.delete(id);
  }
}
