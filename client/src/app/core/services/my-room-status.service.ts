import { inject, Injectable, signal } from '@angular/core';
import { ReservationService } from './reservation.service';

export type MyRoomStatus = 'loading' | 'has-history' | 'no-history';

/**
 * Best-effort signal for "does this student have a chambre yet" — used to
 * hide the Réclamations tab (and disable its create form) for students who
 * can't actually use it. There is no self-service endpoint that answers
 * this directly: ReclamationServiceImpl.saveMy resolves the student's room
 * via getCurrentRoomByUser, which reads UtilisateurPromotionChambre (UPC)
 * records — and UPC has no "/me" route (every UtilisateurPromotionChambreController
 * route is MANAGER-gated). GET /reservation/me is the closest self-service
 * signal available.
 *
 * This only gates the unambiguous case: zero reservations at all definitely
 * means no chambre yet (a brand-new student who hasn't even self-reserved).
 * It deliberately does NOT try to infer "has a chambre" from a TERMINEE
 * reservation's presence — staff can also mark a reservation TERMINEE/FERMEE
 * for reasons unrelated to a UPC assignment (see reservation-detail.page.ts's
 * manual état transitions), so that would be guessing at a business rule
 * this class has no reliable way to confirm. For that ambiguous case
 * (some reservation history, but still no real UPC row), the accurate
 * check is left to the server: reclamations.component.ts still catches the
 * 404 from getCurrentRoomByUser at submit time and shows a clear message
 * there instead of silently failing.
 */
@Injectable({ providedIn: 'root' })
export class MyRoomStatusService {
  private readonly reservationService = inject(ReservationService);

  private readonly statusSignal = signal<MyRoomStatus>('loading');
  readonly status = this.statusSignal.asReadonly();

  constructor() {
    this.refresh();
  }

  refresh(): void {
    this.statusSignal.set('loading');
    this.reservationService.getAllMy({ size: 1 }).subscribe({
      next: (page) => this.statusSignal.set(page.totalElements > 0 ? 'has-history' : 'no-history'),
      // Fail open on error (network blip, etc.) — an unreliable check should
      // never be the reason a legitimate student loses access to the tab.
      error: () => this.statusSignal.set('has-history'),
    });
  }
}
