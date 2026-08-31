import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ReservationService } from '../../../core/services/reservation.service';
import { ReservationDto } from '../../../core/models/dtos';
import { EtatReservation } from '../../../core/models/enums';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';

/**
 * No server-side state machine exists for EtatReservation (ReservationServiceImpl.update
 * persists whatever etat is sent) — ACTIVE → TERMINEE/FERMEE, both terminal
 * in the UI, is a defensive client-side policy so staff have a sane
 * workflow, not a guarantee the backend enforces.
 */
const NEXT_STATES: Record<EtatReservation, { state: EtatReservation; label: string }[]> = {
  ACTIVE: [
    { state: 'TERMINEE', label: 'Marquer terminée' },
    { state: 'FERMEE', label: 'Fermer' },
  ],
  TERMINEE: [],
  FERMEE: [],
};

@Component({
  selector: 'app-reservation-detail-page',
  standalone: true,
  imports: [RouterLink, DatePipe, StatusBadgeComponent, ButtonComponent, SkeletonRowsComponent],
  template: `
    <a routerLink="/admin/reservations" class="text-sm text-primary-600 hover:text-primary-700">&larr; Retour aux réservations</a>

    @if (loading()) {
      <div class="mt-4"><app-skeleton-rows [rows]="3" [columns]="2"></app-skeleton-rows></div>
    } @else {
      @if (reservation(); as r) {
        <div class="mt-4 rounded-lg border border-neutral-200 bg-white p-6">
          <div class="flex items-start justify-between">
            <div>
              <h1 class="text-lg font-semibold text-neutral-900">Réservation {{ r.id?.slice(0, 8) }}…</h1>
              <p class="text-sm text-neutral-500">Chambre {{ r.chambre }} · Utilisateur {{ r.utilisateur }}</p>
              @if (r.createdAt) {
                <p class="mt-1 text-xs text-neutral-400">Créée le {{ r.createdAt | date: 'dd/MM/yyyy HH:mm' }}</p>
              }
            </div>
            <app-status-badge kind="reservation" [value]="r.etat ?? 'ACTIVE'"></app-status-badge>
          </div>

          @if (nextStates(r.etat).length > 0) {
            <div class="mt-4 flex gap-2">
              @for (next of nextStates(r.etat); track next.state) {
                <app-button size="sm" variant="secondary" [loading]="transitioning()" (click)="transition(r, next.state)">
                  {{ next.label }}
                </app-button>
              }
            </div>
          }
        </div>
      }
    }
  `,
})
export class ReservationDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly reservationService = inject(ReservationService);
  private readonly toast = inject(ToastService);

  protected readonly reservation = signal<ReservationDto | null>(null);
  protected readonly loading = signal(true);
  protected readonly transitioning = signal(false);

  protected readonly nextStates = (etat: EtatReservation | null) => (etat ? NEXT_STATES[etat] : []);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.reservationService.getById(id).subscribe({
      next: (r) => {
        this.reservation.set(r);
        this.loading.set(false);
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.toast.showError(err.message);
      },
    });
  }

  protected transition(r: ReservationDto, next: EtatReservation): void {
    this.transitioning.set(true);
    this.reservationService.update({ id: r.id as string, dto: { ...r, etat: next } }).subscribe({
      next: (updated) => {
        this.transitioning.set(false);
        this.reservation.set(updated);
        this.toast.show('État mis à jour.', 'success');
      },
      error: (err: AppError) => {
        this.transitioning.set(false);
        this.toast.showError(err.message);
      },
    });
  }
}
