import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReservationService } from '../../../core/services/reservation.service';
import { ChambreService } from '../../../core/services/chambre.service';
import { ReservationDto, ChambreDto } from '../../../core/models/dtos';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { SkeletonComponent } from '../../../shared/components/skeleton/skeleton.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';

/** Rooms a student could plausibly reserve — MAINTENANCE/OCCUPEE excluded from the picker. */
const RESERVABLE_ETATS = new Set(['LIBRE', 'PARTIELLEMENT_LIBRE']);

@Component({
  selector: 'app-student-reservation',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DatePipe,
    ButtonComponent,
    SelectComponent,
    StatusBadgeComponent,
    SkeletonComponent,
    EmptyStateComponent,
  ],
  template: `
    <div class="flex flex-col gap-4">
      <h1 class="text-lg font-semibold text-neutral-900">Ma chambre</h1>

      <form
        [formGroup]="form"
        (ngSubmit)="onSubmit()"
        class="flex flex-col gap-3 rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm"
      >
        <p class="text-sm font-medium text-neutral-900">Réserver une chambre</p>
        @if (loadingChambres()) {
          <app-skeleton variant="block" height="2.5rem" />
        } @else {
          <app-select label="Chambre disponible" placeholder="Choisir une chambre" formControlName="chambre" [options]="chambreOptions()" />
        }

        @if (submitError(); as message) {
          <p class="rounded-md bg-danger-500/10 px-3 py-2 text-sm text-danger-500">{{ message }}</p>
        }

        <app-button type="submit" [disabled]="form.invalid" [loading]="submitting()">Réserver</app-button>
      </form>

      <div class="flex flex-col gap-2">
        <p class="text-sm font-medium text-neutral-900">Mes réservations</p>
        @if (loadingReservations()) {
          <app-skeleton variant="block" height="4rem" />
        } @else if (reservations().length === 0) {
          <app-empty-state title="Aucune réservation" description="Vous n'avez pas encore réservé de chambre." />
        } @else {
          @for (reservation of reservations(); track reservation.id) {
            <div class="flex items-center justify-between rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm">
              <div>
                <p class="text-sm font-medium text-neutral-900">{{ chambreLabel(reservation.chambre) }}</p>
                @if (reservation.createdAt) {
                  <p class="text-xs text-neutral-500">Depuis le {{ reservation.createdAt | date: 'mediumDate' }}</p>
                }
              </div>
              <app-status-badge kind="reservation" [value]="reservation.etat ?? 'ACTIVE'" />
            </div>
          }
        }
      </div>
    </div>
  `,
})
export class ReservationComponent implements OnInit {
  private readonly reservationService = inject(ReservationService);
  private readonly chambreService = inject(ChambreService);
  private readonly toastService = inject(ToastService);

  protected readonly loadingChambres = signal(true);
  protected readonly loadingReservations = signal(true);
  protected readonly submitting = signal(false);
  protected readonly submitError = signal<string | null>(null);
  protected readonly reservations = signal<ReservationDto[]>([]);
  protected readonly chambres = signal<ChambreDto[]>([]);

  protected readonly chambreOptions = signal<SelectOption[]>([]);

  protected readonly form = new FormGroup({
    chambre: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  ngOnInit(): void {
    this.chambreService.getAll().subscribe({
      next: (chambres) => {
        this.chambres.set(chambres);
        this.chambreOptions.set(
          chambres
            .filter((c) => RESERVABLE_ETATS.has(c.etat ?? ''))
            .map((c) => ({ value: c.id ?? '', label: c.matricule })),
        );
        this.loadingChambres.set(false);
      },
      error: (err: AppError) => {
        this.loadingChambres.set(false);
        this.toastService.showError(err.message);
      },
    });

    this.refreshReservations();
  }

  private refreshReservations(): void {
    this.loadingReservations.set(true);
    this.reservationService.getAllMy().subscribe({
      next: (page) => {
        this.reservations.set(page.content);
        this.loadingReservations.set(false);
      },
      error: (err: AppError) => {
        this.loadingReservations.set(false);
        this.toastService.showError(err.message);
      },
    });
  }

  protected chambreLabel(chambreId: string): string {
    return this.chambres().find((c) => c.id === chambreId)?.matricule ?? chambreId;
  }

  protected onSubmit(): void {
    const chambre = this.form.controls.chambre.value;
    if (!chambre) return;

    this.submitError.set(null);
    this.submitting.set(true);
    this.reservationService.createMy({ chambre }).subscribe({
      next: () => {
        this.submitting.set(false);
        this.form.reset({ chambre: '' });
        this.toastService.show('Réservation créée.', 'success');
        this.refreshReservations();
      },
      error: (err: AppError) => {
        this.submitting.set(false);
        // A 400 here is almost always RoomFullException — an expected,
        // normal outcome, not a system failure — so it's surfaced inline
        // rather than as a generic toast. presentation === 'expected'
        // covers this and other real 4xx messages (see AppError docs);
        // there's no further discriminator on the wire to single out
        // RoomFullException specifically.
        if (err.presentation === 'expected') {
          this.submitError.set(err.message);
        } else {
          this.toastService.showError(err.message);
        }
      },
    });
  }
}
