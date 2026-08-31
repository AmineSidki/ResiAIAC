import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ChambreService } from '../../../core/services/chambre.service';
import { UtilisateurPromotionChambreService } from '../../../core/services/utilisateur-promotion-chambre.service';
import { EquipementUpcService } from '../../../core/services/equipement-upc.service';
import { EquipementService } from '../../../core/services/equipement.service';
import { ChambreDto, EquipementDto, UtilisateurPromotionChambreDto } from '../../../core/models/dtos';
import { UtilisateurPromotionChambreId } from '../../../core/models/ids';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../../core/auth/role.guard';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { DialogComponent } from '../../../shared/components/dialog/dialog.component';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';
import { UpcSelectorComponent } from '../../shared/upc-selector/upc-selector.component';

/**
 * Everything on this page beyond the room header is UPC/EquipementUpc data:
 * UPC reads (by-chambre, getById, save, update) are MANAGER-gated; UPC
 * DELETE is RESPONSABLE-only (confirmed against
 * UtilisateurPromotionChambreController.java — stricter than the other 4
 * ops on that controller). EquipementUpc's whole controller is
 * class-level MANAGER, including reads.
 */
@Component({
  selector: 'app-chambre-detail-page',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    StatusBadgeComponent,
    ButtonComponent,
    DialogComponent,
    SelectComponent,
    EmptyStateComponent,
    SkeletonRowsComponent,
    UpcSelectorComponent,
  ],
  template: `
    <a routerLink="/admin/chambres" class="text-sm text-primary-600 hover:text-primary-700">&larr; Retour aux chambres</a>

    @if (loadingChambre()) {
      <div class="mt-4"><app-skeleton-rows [rows]="2" [columns]="2"></app-skeleton-rows></div>
    } @else {
      @if (chambre(); as c) {
        <div class="mt-4 flex items-center justify-between rounded-lg border border-neutral-200 bg-white p-6">
          <div>
            <h1 class="text-lg font-semibold text-neutral-900">Chambre {{ c.matricule }}</h1>
            <p class="text-sm text-neutral-500">Capacité {{ c.capacite }}</p>
          </div>
          <app-status-badge kind="chambre" [value]="c.etat ?? 'LIBRE'"></app-status-badge>
        </div>
      }
    }

    <div class="mt-6 flex items-center justify-between">
      <h2 class="text-base font-semibold text-neutral-900">Occupants</h2>
      <app-button size="sm" (click)="openAssign()">Assigner un étudiant</app-button>
    </div>

    <div class="mt-2">
      @if (loadingUpcs()) {
        <app-skeleton-rows [rows]="3" [columns]="3"></app-skeleton-rows>
      } @else if (upcs().length === 0) {
        <app-empty-state reason="no-data">
          <app-button size="sm" (click)="openAssign()">Assigner un étudiant</app-button>
        </app-empty-state>
      } @else {
        <div class="flex flex-col gap-3">
          @for (upc of upcs(); track upcKey(upc)) {
            <div class="rounded-lg border border-neutral-200 bg-white p-4">
              <div class="flex items-start justify-between">
                <div>
                  <p class="text-sm font-medium text-neutral-900">Étudiant {{ upc.utilisateur }}</p>
                  <p class="text-xs text-neutral-500">
                    Promotion {{ upc.promotion }}
                    @if (upc.retard) {
                      <span class="ml-2 rounded-full bg-danger-500/10 px-2 py-0.5 text-danger-500">En retard</span>
                    }
                  </p>
                  @if (upc.note) {
                    <p class="mt-1 text-xs text-neutral-500">Note&nbsp;: {{ upc.note }}</p>
                  }
                </div>
                @if (canDeleteUpc()) {
                  <button
                    type="button"
                    class="text-sm font-medium text-danger-500 hover:text-danger-600"
                    (click)="deleteAssignment(upc)"
                  >
                    Retirer
                  </button>
                }
              </div>

              <div class="mt-3 border-t border-neutral-100 pt-3">
                <div class="flex items-center justify-between">
                  <p class="text-xs font-semibold uppercase tracking-wide text-neutral-400">
                    État des lieux — équipement
                  </p>
                  <button
                    type="button"
                    class="text-xs font-medium text-primary-600 hover:text-primary-700"
                    (click)="openEquipmentDialog(upc)"
                  >
                    + Ajouter
                  </button>
                </div>
                @if ((equipmentByUpc.get(upcKey(upc)) ?? []).length === 0) {
                  <p class="mt-1 text-xs text-neutral-400">Aucun équipement enregistré.</p>
                } @else {
                  <ul class="mt-1 flex flex-wrap gap-2">
                    @for (item of equipmentByUpc.get(upcKey(upc)) ?? []; track item.equipement) {
                      <li class="flex items-center gap-1.5 rounded-full bg-neutral-100 px-2.5 py-1 text-xs text-neutral-700">
                        {{ equipementNameById().get(item.equipement) ?? item.equipement }} × {{ item.quantite }}
                        <button
                          type="button"
                          class="text-neutral-400 hover:text-danger-500"
                          (click)="deleteEquipmentEntry(upc, item)"
                          aria-label="Retirer"
                        >
                          &#x2715;
                        </button>
                      </li>
                    }
                  </ul>
                }
              </div>
            </div>
          }
        </div>
      }
    </div>

    <app-dialog [open]="assignDialogOpen()" title="Assigner un étudiant à cette chambre" (close)="assignDialogOpen.set(false)">
      <app-upc-selector (selected)="onSelectorResolved($event)"></app-upc-selector>
      <div footer class="flex gap-2">
        <app-button variant="secondary" (click)="assignDialogOpen.set(false)">Annuler</app-button>
        <app-button [disabled]="!pendingSelection()" [loading]="assigning()" (click)="confirmAssign()">Assigner</app-button>
      </div>
    </app-dialog>

    <app-dialog [open]="equipmentDialogOpen()" title="Ajouter un équipement" (close)="equipmentDialogOpen.set(false)">
      <div class="flex flex-col gap-3">
        <app-select label="Équipement" placeholder="Choisir" [options]="equipementOptions()" [(ngModel)]="equipmentForm.equipementId"></app-select>
        <div class="flex flex-col gap-1">
          <label class="text-sm font-medium text-neutral-700">Quantité</label>
          <input
            type="number"
            min="1"
            [(ngModel)]="equipmentForm.quantite"
            class="rounded-md border border-neutral-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>
      </div>
      <div footer class="flex gap-2">
        <app-button variant="secondary" (click)="equipmentDialogOpen.set(false)">Annuler</app-button>
        <app-button [loading]="savingEquipment()" (click)="saveEquipmentEntry()">Ajouter</app-button>
      </div>
    </app-dialog>
  `,
})
export class ChambreDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly chambreService = inject(ChambreService);
  private readonly upcService = inject(UtilisateurPromotionChambreService);
  private readonly equipementUpcService = inject(EquipementUpcService);
  private readonly equipementService = inject(EquipementService);
  private readonly toast = inject(ToastService);
  private readonly currentUser = inject(CurrentUserService);

  private chambreId!: string;

  protected readonly chambre = signal<ChambreDto | null>(null);
  protected readonly loadingChambre = signal(true);
  protected readonly upcs = signal<UtilisateurPromotionChambreDto[]>([]);
  protected readonly loadingUpcs = signal(true);
  protected readonly equipmentByUpc = new Map<string, ReturnType<typeof this.emptyEquip>>();
  protected readonly equipementNameById = signal<Map<number, string>>(new Map());
  protected readonly equipementOptions = computed<SelectOption[]>(() =>
    Array.from(this.equipementNameById(), ([value, label]) => ({ value: String(value), label })),
  );

  protected readonly canDeleteUpc = computed(() => hasRoleAtLeast(this.currentUser.realmRoles(), 'RESPONSABLE'));

  protected readonly assignDialogOpen = signal(false);
  protected readonly assigning = signal(false);
  protected readonly pendingSelection = signal<UtilisateurPromotionChambreId | null>(null);

  protected readonly equipmentDialogOpen = signal(false);
  protected readonly savingEquipment = signal(false);
  protected equipmentForm = { equipementId: '', quantite: 1 };
  private equipmentTargetUpc: UtilisateurPromotionChambreDto | null = null;

  protected upcKey(upc: UtilisateurPromotionChambreDto): string {
    return `${upc.utilisateur}:${upc.promotion}:${upc.chambre}`;
  }

  private emptyEquip() {
    return [] as { equipement: number; quantite: number }[];
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.chambreId = id;

    this.chambreService.getById(id).subscribe({
      next: (c) => {
        this.chambre.set(c);
        this.loadingChambre.set(false);
      },
      error: (err: AppError) => {
        this.loadingChambre.set(false);
        this.toast.showError(err.message);
      },
    });

    this.equipementService.getAll().subscribe((list) => {
      this.equipementNameById.set(new Map(list.map((e) => [e.id as number, e.nom])));
    });

    this.loadUpcs();
  }

  private loadUpcs(): void {
    this.loadingUpcs.set(true);
    this.upcService.getAllByChambreId(this.chambreId).subscribe({
      next: (rows) => {
        this.upcs.set(rows);
        this.loadingUpcs.set(false);
        for (const upc of rows) this.loadEquipmentFor(upc);
      },
      error: (err: AppError) => {
        this.loadingUpcs.set(false);
        this.toast.showError(err.message);
      },
    });
  }

  private loadEquipmentFor(upc: UtilisateurPromotionChambreDto): void {
    this.equipementUpcService
      .getAllByUpc({
        utilisateur_id: upc.utilisateur,
        promotion_id: upc.promotion,
        chambre_id: upc.chambre,
      })
      .subscribe((entries) => {
        this.equipmentByUpc.set(
          this.upcKey(upc),
          entries.map((e) => ({ equipement: e.equipement, quantite: e.quantite })),
        );
      });
  }

  protected openAssign(): void {
    this.pendingSelection.set(null);
    this.assignDialogOpen.set(true);
  }

  protected onSelectorResolved(id: UtilisateurPromotionChambreId | null): void {
    this.pendingSelection.set(id);
  }

  protected confirmAssign(): void {
    const id = this.pendingSelection();
    if (!id) return;
    this.assigning.set(true);
    this.upcService
      .create({
        id: null,
        retard: false,
        note: null,
        utilisateur: id.utilisateur_id,
        promotion: id.promotion_id,
        chambre: id.chambre_id,
        equipementsEndommages: [],
      })
      .subscribe({
        next: () => {
          this.assigning.set(false);
          this.assignDialogOpen.set(false);
          this.toast.show('Étudiant assigné.', 'success');
          this.loadUpcs();
        },
        error: (err: AppError) => {
          this.assigning.set(false);
          this.toast.showError(err.message);
        },
      });
  }

  protected deleteAssignment(upc: UtilisateurPromotionChambreDto): void {
    this.upcService
      .delete({ utilisateur_id: upc.utilisateur, promotion_id: upc.promotion, chambre_id: upc.chambre })
      .subscribe({
        next: () => {
          this.toast.show('Attribution supprimée.', 'success');
          this.loadUpcs();
        },
        error: (err: AppError) => this.toast.showError(err.message),
      });
  }

  protected openEquipmentDialog(upc: UtilisateurPromotionChambreDto): void {
    this.equipmentTargetUpc = upc;
    this.equipmentForm = { equipementId: '', quantite: 1 };
    this.equipmentDialogOpen.set(true);
  }

  protected saveEquipmentEntry(): void {
    const upc = this.equipmentTargetUpc;
    if (!upc || !this.equipmentForm.equipementId) return;
    this.savingEquipment.set(true);
    this.equipementUpcService
      .create({
        id: null,
        quantite: Number(this.equipmentForm.quantite) || 1,
        equipement: Number(this.equipmentForm.equipementId),
        upc: { utilisateur_id: upc.utilisateur, promotion_id: upc.promotion, chambre_id: upc.chambre },
      })
      .subscribe({
        next: () => {
          this.savingEquipment.set(false);
          this.equipmentDialogOpen.set(false);
          this.toast.show('Équipement ajouté.', 'success');
          this.loadEquipmentFor(upc);
        },
        error: (err: AppError) => {
          this.savingEquipment.set(false);
          this.toast.showError(err.message);
        },
      });
  }

  protected deleteEquipmentEntry(upc: UtilisateurPromotionChambreDto, item: { equipement: number; quantite: number }): void {
    this.equipementUpcService
      .delete({
        equipement_id: item.equipement,
        utilisateurPromotionChambre_id: {
          utilisateur_id: upc.utilisateur,
          promotion_id: upc.promotion,
          chambre_id: upc.chambre,
        },
      })
      .subscribe({
        next: () => {
          this.toast.show('Équipement retiré.', 'success');
          this.loadEquipmentFor(upc);
        },
        error: (err: AppError) => this.toast.showError(err.message),
      });
  }
}
