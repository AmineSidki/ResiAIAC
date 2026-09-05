import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReclamationService } from '../../../core/services/reclamation.service';
import { MyRoomStatusService } from '../../../core/services/my-room-status.service';
import { ServiceEntityService } from '../../../core/services/service.service';
import { EquipementService } from '../../../core/services/equipement.service';
import { EquipementEntry, EquipementDto, ReclamationDto, ServiceDto } from '../../../core/models/dtos';
import { EtatReclamation, ETAT_RECLAMATION_VALUES } from '../../../core/models/enums';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { SkeletonComponent } from '../../../shared/components/skeleton/skeleton.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { DialogComponent } from '../../../shared/components/dialog/dialog.component';

interface EquipementRow {
  id: number;
  nom: string;
  selected: boolean;
  quantite: number;
}

const ETAT_FILTER_OPTIONS: SelectOption[] = [
  { value: '', label: 'Toutes' },
  ...ETAT_RECLAMATION_VALUES.map((etat) => ({ value: etat, label: etat })),
];

@Component({
  selector: 'app-student-reclamations',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DatePipe,
    ButtonComponent,
    SelectComponent,
    StatusBadgeComponent,
    SkeletonComponent,
    EmptyStateComponent,
    DialogComponent,
  ],
  template: `
    <div class="flex flex-col gap-4">
      <h1 class="text-lg font-semibold text-neutral-900">Mes réclamations</h1>

      <!-- Create form -->
      @if (roomStatus.status() === 'no-history') {
        <div class="rounded-lg border border-neutral-100 bg-surface p-4 text-sm text-neutral-600 shadow-sm dark:border-white/10 dark:bg-white/5 dark:text-neutral-300">
          Vous n'avez pas encore de chambre attribuée — vous pourrez déposer une réclamation une fois qu'une
          chambre vous aura été assignée par l'administration.
        </div>
      } @else {
        <form
          [formGroup]="form"
          (ngSubmit)="onSubmit()"
          class="flex flex-col gap-3 rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm"
        >
          <p class="text-sm font-medium text-neutral-900">Nouvelle réclamation</p>

          @if (loadingReferenceData()) {
            <app-skeleton variant="block" height="2.5rem" />
          } @else {
            <app-select label="Service concerné" placeholder="Choisir un service" formControlName="service" [options]="serviceOptions()" />
          }

          <div class="flex flex-col gap-1">
            <label for="reclamation-message" class="text-sm font-medium text-neutral-700">Message (optionnel)</label>
            <textarea
              id="reclamation-message"
              formControlName="message"
              rows="3"
              placeholder="Décrivez le problème..."
              class="rounded-md border border-neutral-300 px-3 py-2 text-sm text-neutral-900 shadow-sm placeholder:text-neutral-400 focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500"
            ></textarea>
          </div>

          @if (equipementRows().length > 0) {
            <div class="flex flex-col gap-2">
              <p class="text-sm font-medium text-neutral-700">Équipements concernés (optionnel)</p>
              @for (row of equipementRows(); track row.id) {
                <div class="flex items-center gap-3">
                  <input
                    type="checkbox"
                    [checked]="row.selected"
                    (change)="toggleEquipement(row.id)"
                    class="h-4 w-4 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
                  />
                  <span class="flex-1 text-sm text-neutral-700">{{ row.nom }}</span>
                  @if (row.selected) {
                    <input
                      type="number"
                      min="1"
                      [value]="row.quantite"
                      (input)="setQuantite(row.id, $event)"
                      class="w-16 rounded-md border border-neutral-300 px-2 py-1 text-sm"
                    />
                  }
                </div>
              }
            </div>
          }

          <app-button type="submit" [disabled]="form.invalid" [loading]="submitting()">Envoyer</app-button>
        </form>
      }

      <!-- List -->
      <div class="flex flex-col gap-2">
        <div class="flex items-center justify-between gap-3">
          <p class="text-sm font-medium text-neutral-900">Historique</p>
          <app-select [options]="etatFilterOptions" [formControl]="etatFilterControl" />
        </div>

        @if (loadingList()) {
          <app-skeleton variant="block" height="4rem" />
        } @else if (reclamations().length === 0) {
          <app-empty-state title="Aucune réclamation" description="Rien à afficher pour ce filtre." />
        } @else {
          @for (reclamation of reclamations(); track reclamation.id) {
            <button
              type="button"
              (click)="openDetail(reclamation)"
              class="flex items-center justify-between rounded-lg border border-neutral-100 bg-surface p-4 text-left shadow-sm hover:bg-neutral-50"
            >
              <div class="min-w-0">
                <p class="truncate text-sm font-medium text-neutral-900">{{ serviceLabel(reclamation.service) }}</p>
                @if (reclamation.createdAt) {
                  <p class="text-xs text-neutral-500">{{ reclamation.createdAt | date: 'mediumDate' }}</p>
                }
              </div>
              <app-status-badge kind="reclamation" [value]="reclamation.etat ?? 'EN_ATTENTE'" />
            </button>
          }
        }
      </div>
    </div>

    <app-dialog [open]="detailOpen()" [title]="detail() ? serviceLabel(detail()!.service) : null" (close)="closeDetail()">
      @if (detail(); as reclamation) {
        <div class="flex flex-col gap-2 text-sm">
          <app-status-badge kind="reclamation" [value]="reclamation.etat ?? 'EN_ATTENTE'" />
          @if (reclamation.message) {
            <p class="text-neutral-700">{{ reclamation.message }}</p>
          }
          @if (detailEquipementNames().length > 0) {
            <div>
              <p class="text-xs font-medium text-neutral-500">Équipements signalés</p>
              <ul class="list-inside list-disc text-neutral-700">
                @for (name of detailEquipementNames(); track name) {
                  <li>{{ name }}</li>
                }
              </ul>
            </div>
          }
        </div>
      }
    </app-dialog>
  `,
})
export class ReclamationsComponent implements OnInit {
  private readonly reclamationService = inject(ReclamationService);
  private readonly serviceEntityService = inject(ServiceEntityService);
  private readonly equipementService = inject(EquipementService);
  private readonly toastService = inject(ToastService);
  protected readonly roomStatus = inject(MyRoomStatusService);

  protected readonly loadingReferenceData = signal(true);
  protected readonly loadingList = signal(true);
  protected readonly submitting = signal(false);

  protected readonly services = signal<ServiceDto[]>([]);
  protected readonly serviceOptions = signal<SelectOption[]>([]);
  protected readonly equipements = signal<EquipementDto[]>([]);
  protected readonly equipementRows = signal<EquipementRow[]>([]);
  protected readonly reclamations = signal<ReclamationDto[]>([]);

  protected readonly etatFilterOptions = ETAT_FILTER_OPTIONS;
  protected readonly etatFilterControl = new FormControl('', { nonNullable: true });
  private etatFilter: EtatReclamation | '' = '';

  protected readonly detailOpen = signal(false);
  protected readonly detail = signal<ReclamationDto | null>(null);
  protected readonly detailEquipementNames = signal<string[]>([]);

  protected readonly form = new FormGroup({
    service: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    message: new FormControl('', { nonNullable: true }),
  });

  ngOnInit(): void {
    this.serviceEntityService.getAll().subscribe({
      next: (services) => {
        this.services.set(services);
        this.serviceOptions.set(services.map((s) => ({ value: String(s.id), label: s.nom })));
        this.maybeFinishLoadingReference();
      },
      error: (err: AppError) => {
        this.loadingReferenceData.set(false);
        this.toastService.showError(err.message);
      },
    });

    this.equipementService.getAll().subscribe({
      next: (equipements) => {
        this.equipements.set(equipements);
        this.equipementRows.set(
          equipements.map((e) => ({ id: e.id ?? 0, nom: e.nom, selected: false, quantite: 1 })),
        );
        this.maybeFinishLoadingReference();
      },
      error: (err: AppError) => {
        this.loadingReferenceData.set(false);
        this.toastService.showError(err.message);
      },
    });

    this.etatFilterControl.valueChanges.subscribe((value) => {
      this.etatFilter = value as EtatReclamation | '';
      this.refreshList();
    });

    this.refreshList();
  }

  private maybeFinishLoadingReference(): void {
    if (this.services().length > 0 || this.equipements().length > 0) {
      this.loadingReferenceData.set(false);
    }
  }

  private refreshList(): void {
    this.loadingList.set(true);
    const request = this.etatFilter
      ? this.reclamationService.getAllMyByStatus(this.etatFilter)
      : this.reclamationService.getAllMy();

    request.subscribe({
      next: (page) => {
        this.reclamations.set(page.content);
        this.loadingList.set(false);
      },
      error: (err: AppError) => {
        this.loadingList.set(false);
        this.toastService.showError(err.message);
      },
    });
  }

  protected serviceLabel(serviceId: number): string {
    return this.services().find((s) => s.id === serviceId)?.nom ?? `Service #${serviceId}`;
  }

  protected toggleEquipement(id: number): void {
    this.equipementRows.update((rows) =>
      rows.map((row) => (row.id === id ? { ...row, selected: !row.selected } : row)),
    );
  }

  protected setQuantite(id: number, event: Event): void {
    const value = Math.max(1, Number((event.target as HTMLInputElement).value) || 1);
    this.equipementRows.update((rows) => rows.map((row) => (row.id === id ? { ...row, quantite: value } : row)));
  }

  protected onSubmit(): void {
    const { service, message } = this.form.getRawValue();
    if (!service) return;

    const equipements: EquipementEntry[] = this.equipementRows()
      .filter((row) => row.selected)
      .map((row) => ({ id: row.id, quantite: row.quantite }));

    this.submitting.set(true);
    this.reclamationService
      .createMy({ service: Number(service), message: message.trim() === '' ? null : message.trim(), equipements })
      .subscribe({
        next: () => {
          this.submitting.set(false);
          this.form.reset({ service: '', message: '' });
          this.equipementRows.update((rows) => rows.map((row) => ({ ...row, selected: false, quantite: 1 })));
          this.toastService.show('Réclamation envoyée.', 'success');
          this.refreshList();
        },
        error: (err: AppError) => {
          this.submitting.set(false);
          // The proactive notice above already covers the common case (zero
          // reservation history); this covers the narrower one it can't
          // safely detect — some reservation exists, but the student still
          // has no real UPC-based room, so getCurrentRoomByUser (server
          // side) 404s. Same underlying cause, just caught reactively here.
          if (err.status === 404) {
            this.toastService.showError(
              "Vous n'avez pas encore de chambre attribuée — la réclamation n'a pas pu être envoyée.",
            );
            this.roomStatus.refresh();
            return;
          }
          this.toastService.showError(err.message);
        },
      });
  }

  /**
   * The equipment list isn't reliably populated on the object returned by
   * `saveMy` — so this always re-fetches via getMyById rather than reusing
   * the row already in `reclamations()`, which may be stale for the same
   * reason right after a create.
   */
  protected openDetail(reclamation: ReclamationDto): void {
    if (!reclamation.id) return;
    this.detailOpen.set(true);
    this.detail.set(reclamation);
    this.detailEquipementNames.set([]);

    this.reclamationService.getMyById(reclamation.id).subscribe({
      next: (fresh) => {
        this.detail.set(fresh);
        const equipementMap = new Map(this.equipements().map((e) => [e.id, e.nom]));
        this.detailEquipementNames.set(
          fresh.equipements.map((entry) => equipementMap.get(entry.equipement_id) ?? `Équipement #${entry.equipement_id}`),
        );
      },
      error: (err: AppError) => this.toastService.showError(err.message),
    });
  }

  protected closeDetail(): void {
    this.detailOpen.set(false);
    this.detail.set(null);
  }
}
