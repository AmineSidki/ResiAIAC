import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ReclamationService } from '../../../core/services/reclamation.service';
import { OwnerNameService } from '../../shared/owner-name/owner-name.service';
import { EquipementReclamationService } from '../../../core/services/equipement-reclamation.service';
import { EquipementService } from '../../../core/services/equipement.service';
import { ReclamationDto, EquipementReclamationDto } from '../../../core/models/dtos';
import { EtatReclamation } from '../../../core/models/enums';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { DialogComponent } from '../../../shared/components/dialog/dialog.component';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';

/**
 * Valid état transitions (EN_ATTENTE → EN_TRAITEMENT → FERME_TRAITE /
 * FERME_SANS_TRAITEMENT) are NOT enforced server-side (ReclamationServiceImpl.update
 * just persists whatever etat is sent) — this is a UI-level policy so staff
 * can't accidentally skip or reverse steps, not a backend guarantee.
 */
const NEXT_STATES: Record<EtatReclamation, EtatReclamation[]> = {
  EN_ATTENTE: ['EN_TRAITEMENT'],
  EN_TRAITEMENT: ['FERME_TRAITE', 'FERME_SANS_TRAITEMENT'],
  FERME_TRAITE: [],
  FERME_SANS_TRAITEMENT: [],
};

const TRANSITION_LABELS: Record<EtatReclamation, string> = {
  EN_ATTENTE: 'Remettre en attente',
  EN_TRAITEMENT: 'Prendre en traitement',
  FERME_TRAITE: 'Fermer (traitée)',
  FERME_SANS_TRAITEMENT: 'Fermer (sans traitement)',
};

@Component({
  selector: 'app-reclamation-detail-page',
  standalone: true,
  imports: [RouterLink, FormsModule, StatusBadgeComponent, ButtonComponent, DialogComponent, SelectComponent, SkeletonRowsComponent],
  template: `
    <a routerLink="/admin/reclamations" class="text-sm text-primary-600 hover:text-primary-700 dark:text-primary-400">&larr; Retour aux réclamations</a>

    @if (loading()) {
      <div class="mt-4"><app-skeleton-rows [rows]="3" [columns]="2"></app-skeleton-rows></div>
    } @else {
      @if (reclamation(); as r) {
        <div class="mt-4 rounded-lg border border-neutral-200 bg-white p-6 dark:border-white/10 dark:bg-white/5">
          <div class="flex items-start justify-between">
            <div>
              <h1 class="text-lg font-semibold text-neutral-900 dark:text-white">Réclamation {{ r.id?.slice(0, 8) }}…</h1>
              <p class="text-sm text-neutral-500 dark:text-neutral-400">Étudiant {{ ownerName() ?? '…' }} · Chambre {{ r.chambre }} · Service {{ r.service }}</p>
            </div>
            <app-status-badge kind="reclamation" [value]="r.etat ?? 'EN_ATTENTE'"></app-status-badge>
          </div>

          @if (r.message) {
            <p class="mt-4 rounded-md bg-neutral-50 p-3 text-sm text-neutral-700 dark:bg-white/5 dark:text-neutral-200">{{ r.message }}</p>
          }

          @if (nextStates(r.etat).length > 0) {
            <div class="mt-4 flex gap-2">
              @for (next of nextStates(r.etat); track next) {
                <app-button size="sm" [variant]="next.startsWith('FERME') ? 'secondary' : 'primary'" [loading]="transitioning()" (click)="transition(r, next)">
                  {{ transitionLabel(next) }}
                </app-button>
              }
            </div>
          }
        </div>

        <div class="mt-6">
          <div class="flex items-center justify-between">
            <h2 class="text-base font-semibold text-neutral-900 dark:text-white">Équipement signalé</h2>
            <app-button size="sm" variant="secondary" (click)="openAddEquipment()">+ Ajouter</app-button>
          </div>
          @if (loadingEquipment()) {
            <div class="mt-2"><app-skeleton-rows [rows]="2" [columns]="2"></app-skeleton-rows></div>
          } @else if (equipmentEntries().length === 0) {
            <p class="mt-2 text-sm text-neutral-400">Aucun équipement associé à cette réclamation.</p>
          } @else {
            <ul class="mt-2 flex flex-wrap gap-2">
              @for (item of equipmentEntries(); track item.equipement) {
                <li class="rounded-full bg-neutral-100 px-3 py-1 text-sm text-neutral-700 dark:bg-white/10 dark:text-neutral-200">
                  {{ equipementNameById().get(item.equipement) ?? item.equipement }} × {{ item.quantite }}
                </li>
              }
            </ul>
          }
        </div>
      }
    }

    <app-dialog [open]="addEquipmentOpen()" title="Ajouter un équipement" (close)="addEquipmentOpen.set(false)">
      <div class="flex flex-col gap-3">
        <app-select label="Équipement" placeholder="Choisir" [options]="equipementOptions()" [(ngModel)]="form.equipementId"></app-select>
        <div class="flex flex-col gap-1">
          <label class="text-sm font-medium text-neutral-700">Quantité</label>
          <input
            type="number"
            min="1"
            [(ngModel)]="form.quantite"
            class="rounded-md border border-neutral-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>
      </div>
      <div footer class="flex gap-2">
        <app-button variant="secondary" (click)="addEquipmentOpen.set(false)">Annuler</app-button>
        <app-button [loading]="savingEquipment()" (click)="saveEquipment()">Ajouter</app-button>
      </div>
    </app-dialog>
  `,
})
export class ReclamationDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly reclamationService = inject(ReclamationService);
  private readonly ownerNameService = inject(OwnerNameService);
  private readonly equipementReclamationService = inject(EquipementReclamationService);
  private readonly equipementService = inject(EquipementService);
  private readonly toast = inject(ToastService);

  private reclamationId!: string;

  protected readonly reclamation = signal<ReclamationDto | null>(null);
  protected readonly ownerName = signal<string | null>(null);
  protected readonly loading = signal(true);
  protected readonly transitioning = signal(false);

  protected readonly equipmentEntries = signal<EquipementReclamationDto[]>([]);
  protected readonly loadingEquipment = signal(true);
  protected readonly equipementNameById = signal<Map<number, string>>(new Map());
  protected readonly equipementOptions = computed<SelectOption[]>(() =>
    Array.from(this.equipementNameById(), ([value, label]) => ({ value: String(value), label })),
  );

  protected readonly addEquipmentOpen = signal(false);
  protected readonly savingEquipment = signal(false);
  protected form = { equipementId: '', quantite: 1 };

  protected readonly nextStates = (etat: EtatReclamation | null) => (etat ? NEXT_STATES[etat] : []);
  protected readonly transitionLabel = (etat: EtatReclamation) => TRANSITION_LABELS[etat];

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.reclamationId = id;

    this.reclamationService.getById(id).subscribe({
      next: (r) => {
        this.reclamation.set(r);
        this.loading.set(false);
        this.ownerNameService.resolveOne(r.utilisateur).subscribe((name) => this.ownerName.set(name));
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.toast.showError(err.message);
      },
    });

    this.equipementService.getAll().subscribe((list) => {
      this.equipementNameById.set(new Map(list.map((e) => [e.id as number, e.nom])));
    });

    this.loadEquipment();
  }

  private loadEquipment(): void {
    this.loadingEquipment.set(true);
    this.equipementReclamationService.getAllByReclamationId(this.reclamationId).subscribe({
      next: (entries) => {
        this.equipmentEntries.set(entries);
        this.loadingEquipment.set(false);
      },
      error: (err: AppError) => {
        this.loadingEquipment.set(false);
        this.toast.showError(err.message);
      },
    });
  }

  protected transition(r: ReclamationDto, next: EtatReclamation): void {
    this.transitioning.set(true);
    this.reclamationService.update({ id: r.id as string, dto: { ...r, etat: next } }).subscribe({
      next: (updated) => {
        this.transitioning.set(false);
        this.reclamation.set(updated);
        this.toast.show('État mis à jour.', 'success');
      },
      error: (err: AppError) => {
        this.transitioning.set(false);
        this.toast.showError(err.message);
      },
    });
  }

  protected openAddEquipment(): void {
    this.form = { equipementId: '', quantite: 1 };
    this.addEquipmentOpen.set(true);
  }

  protected saveEquipment(): void {
    if (!this.form.equipementId) return;
    this.savingEquipment.set(true);
    this.equipementReclamationService
      .create({
        id: null,
        quantite: Number(this.form.quantite) || 1,
        equipement: Number(this.form.equipementId),
        reclamation: this.reclamationId,
      })
      .subscribe({
        next: () => {
          this.savingEquipment.set(false);
          this.addEquipmentOpen.set(false);
          this.toast.show('Équipement ajouté.', 'success');
          this.loadEquipment();
        },
        error: (err: AppError) => {
          this.savingEquipment.set(false);
          this.toast.showError(err.message);
        },
      });
  }
}
