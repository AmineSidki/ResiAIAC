import { Component, OnInit, TemplateRef, computed, inject, signal, viewChild } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { PromotionService } from '../../../core/services/promotion.service';
import { FiliereService } from '../../../core/services/filiere.service';
import { PromotionDto } from '../../../core/models/dtos';
import { AppError } from '../../../core/api/app-error';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../../core/auth/role.guard';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { InputComponent } from '../../../shared/components/input/input.component';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { DialogComponent } from '../../../shared/components/dialog/dialog.component';
import { DataTableComponent, DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

const CURRENT_YEAR = new Date().getFullYear();

/**
 * Promotions Management (Task B1) — the backend (PromotionController,
 * PromotionServiceImpl, PromotionDto) was already fully built; there was
 * simply no admin page wired up to it yet.
 *
 * GET /promotion is MANAGER-gated and paginated; POST/PUT/DELETE are
 * RESPONSABLE-gated (confirmed against PromotionController.java). Unlike
 * Batiment/Etage/Filiere/Service/Equipement — flat, unpaginated lists that
 * go through EntityCrudTableComponent — PromotionService is paginated and
 * doesn't extend BaseCrudService, so this page is hand-built the same way
 * user-list.page.ts is, plus a filter-by-filière (using the dedicated
 * by-filiere endpoint) with a working clear action, consistent with the
 * Filter Clearing fix applied to the other list pages.
 */
@Component({
  selector: 'app-promotion-page',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    ButtonComponent,
    InputComponent,
    SelectComponent,
    DialogComponent,
    DataTableComponent,
    PaginationComponent,
    SkeletonRowsComponent,
    EmptyStateComponent,
  ],
  template: `
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-lg font-semibold text-neutral-900 dark:text-white">Promotions</h1>
        <p class="text-sm text-neutral-500 dark:text-neutral-400">{{ totalElements() }} au total</p>
      </div>
      @if (canWrite()) {
        <app-button (click)="openCreate()">Ajouter une promotion</app-button>
      }
    </div>

    <div class="mt-3 flex items-end gap-2">
      <div class="w-64">
        <app-select
          placeholder="Toutes les filières"
          [clearable]="true"
          [options]="filiereOptions()"
          [ngModel]="filiereFilter() === null ? '' : String(filiereFilter())"
          (ngModelChange)="onFilterChange($event)"
        ></app-select>
      </div>
      @if (filiereFilter() !== null) {
        <button
          type="button"
          (click)="onFilterChange('')"
          class="h-[38px] rounded-md border border-neutral-300 px-3 text-sm font-medium text-neutral-600 hover:bg-neutral-50 dark:border-white/10 dark:text-neutral-300 dark:hover:bg-white/5"
        >
          Réinitialiser
        </button>
      }
    </div>

    <div class="mt-4">
      @if (loading()) {
        <app-skeleton-rows [columns]="3"></app-skeleton-rows>
      } @else if (rows().length === 0) {
        <app-empty-state [reason]="filiereFilter() !== null ? 'filtered' : 'no-data'">
          @if (canWrite() && filiereFilter() === null) {
            <app-button size="sm" (click)="openCreate()">Ajouter une promotion</app-button>
          }
        </app-empty-state>
      } @else {
        <app-data-table [columns]="columns()" [rows]="rows()" [trackBy]="rowId"></app-data-table>
        <app-pagination [currentPage]="page()" [totalPages]="totalPages()" [totalElements]="totalElements()" (pageChange)="goToPage($event)"></app-pagination>
      }
    </div>

    <ng-template #filiereTpl let-row>
      {{ filiereNameById().get(row.filiere) ?? ('Filière #' + row.filiere) }}
    </ng-template>

    <ng-template #actionsTpl let-row>
      @if (canWrite()) {
        <div class="flex gap-3">
          <button type="button" class="text-sm font-medium text-primary-600 hover:text-primary-700" (click)="openEdit(row)">Modifier</button>
          <button type="button" class="text-sm font-medium text-danger-500 hover:text-danger-600" (click)="deleteTarget.set(row)">Supprimer</button>
        </div>
      }
    </ng-template>

    <app-dialog [open]="dialogOpen()" [title]="editingRow() ? 'Modifier la promotion' : 'Ajouter une promotion'" (close)="closeDialog()">
      <form [formGroup]="form" class="flex flex-col gap-3">
        <app-select formControlName="filiere" label="Filière" [options]="filiereOptions()"></app-select>
        <app-input formControlName="anneeDeDepart" label="Année de départ" type="number" [errorText]="errorFor('anneeDeDepart')"></app-input>
        <app-input formControlName="anneeDeFin" label="Année de fin" type="number" [errorText]="errorFor('anneeDeFin')"></app-input>
        <app-input formControlName="niveau" label="Niveau" type="number" [errorText]="errorFor('niveau')"></app-input>
      </form>
      <div footer class="flex gap-2">
        <app-button variant="secondary" (click)="closeDialog()">Annuler</app-button>
        <app-button [loading]="saving()" [disabled]="form.invalid" (click)="save()">Enregistrer</app-button>
      </div>
    </app-dialog>

    <app-dialog [open]="deleteTarget() !== null" title="Confirmer la suppression" (close)="deleteTarget.set(null)">
      <p class="text-sm text-neutral-600 dark:text-neutral-300">Supprimer définitivement cette promotion&nbsp;? Cette action est irréversible.</p>
      <div footer class="flex gap-2">
        <app-button variant="secondary" (click)="deleteTarget.set(null)">Annuler</app-button>
        <app-button variant="danger" [loading]="deleting()" (click)="confirmDelete()">Supprimer</app-button>
      </div>
    </app-dialog>
  `,
})
export class PromotionPageComponent implements OnInit {
  private readonly promotionService = inject(PromotionService);
  private readonly filiereService = inject(FiliereService);
  private readonly currentUser = inject(CurrentUserService);
  private readonly toast = inject(ToastService);
  private readonly fb = inject(FormBuilder);

  protected readonly String = String;

  protected readonly canWrite = computed(() => hasRoleAtLeast(this.currentUser.realmRoles(), 'RESPONSABLE'));

  protected readonly rows = signal<PromotionDto[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly deleting = signal(false);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly totalElements = signal(0);
  protected readonly filiereFilter = signal<number | null>(null);

  protected readonly dialogOpen = signal(false);
  protected readonly editingRow = signal<PromotionDto | null>(null);
  protected readonly deleteTarget = signal<PromotionDto | null>(null);

  protected readonly filiereOptions = signal<SelectOption[]>([]);
  protected readonly filiereNameById = signal<Map<number, string>>(new Map());

  protected readonly rowId = (row: PromotionDto) => row.id;

  protected readonly form = this.fb.nonNullable.group({
    filiere: ['', Validators.required],
    anneeDeDepart: [String(CURRENT_YEAR), [Validators.required, Validators.pattern(/^\d+$/)]],
    anneeDeFin: [String(CURRENT_YEAR + 1), [Validators.required, Validators.pattern(/^\d+$/)]],
    niveau: ['1', [Validators.required, Validators.pattern(/^\d+$/)]],
  });

  private readonly filiereTpl = viewChild<TemplateRef<{ $implicit: PromotionDto }>>('filiereTpl');
  private readonly actionsTpl = viewChild<TemplateRef<{ $implicit: PromotionDto }>>('actionsTpl');

  protected readonly columns = computed<DataTableColumn<PromotionDto>[]>(() => {
    const actionsTpl = this.actionsTpl();
    const cols: DataTableColumn<PromotionDto>[] = [
      { key: 'periode', header: 'Période', accessor: (r) => `${r.anneeDeDepart} - ${r.anneeDeFin}` },
      { key: 'niveau', header: 'Niveau', accessor: (r) => String(r.niveau) },
      { key: 'filiere', header: 'Filière', cellTemplate: this.filiereTpl() },
    ];
    if (this.canWrite() && actionsTpl) {
      cols.push({ key: 'actions', header: '', cellTemplate: actionsTpl });
    }
    return cols;
  });

  ngOnInit(): void {
    this.filiereService.getAll().subscribe((filieres) => {
      this.filiereOptions.set(filieres.map((f) => ({ value: String(f.id), label: f.nom })));
      this.filiereNameById.set(new Map(filieres.map((f) => [f.id as number, f.nom])));
    });
    this.load();
  }

  protected onFilterChange(value: string): void {
    this.filiereFilter.set(value ? Number(value) : null);
    this.page.set(0);
    this.load();
  }

  protected goToPage(page: number): void {
    this.page.set(page);
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    const filiere = this.filiereFilter();
    const request$ = filiere !== null
      ? this.promotionService.getAllByFiliere(filiere, { page: this.page() })
      : this.promotionService.getAll({ page: this.page() });

    request$.subscribe({
      next: (result) => {
        this.rows.set(result.content);
        this.totalPages.set(result.totalPages);
        this.totalElements.set(result.totalElements);
        this.loading.set(false);
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.toast.showError(err.message);
      },
    });
  }

  protected openCreate(): void {
    this.editingRow.set(null);
    this.form.reset({
      filiere: this.filiereOptions()[0]?.value ?? '',
      anneeDeDepart: String(CURRENT_YEAR),
      anneeDeFin: String(CURRENT_YEAR + 1),
      niveau: '1',
    });
    this.dialogOpen.set(true);
  }

  protected openEdit(row: PromotionDto): void {
    this.editingRow.set(row);
    this.form.reset({
      filiere: String(row.filiere),
      anneeDeDepart: String(row.anneeDeDepart),
      anneeDeFin: String(row.anneeDeFin),
      niveau: String(row.niveau),
    });
    this.dialogOpen.set(true);
  }

  protected closeDialog(): void {
    this.dialogOpen.set(false);
    this.editingRow.set(null);
  }

  protected errorFor(key: keyof typeof this.form.controls): string | null {
    const control = this.form.get(key);
    if (!control || !control.touched || control.valid) return null;
    if (control.hasError('required')) return 'Ce champ est requis.';
    if (control.hasError('pattern')) return 'Doit être un nombre entier.';
    return 'Valeur invalide.';
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const editing = this.editingRow();
    const dto: PromotionDto = {
      id: editing?.id ?? null,
      filiere: Number(value.filiere),
      anneeDeDepart: Number(value.anneeDeDepart),
      anneeDeFin: Number(value.anneeDeFin),
      niveau: Number(value.niveau),
      combinaisonsUpc: editing?.combinaisonsUpc ?? [],
    };

    this.saving.set(true);
    const request$ = editing
      ? this.promotionService.update({ id: editing.id as string, dto })
      : this.promotionService.create(dto);

    request$.subscribe({
      next: () => {
        this.saving.set(false);
        this.toast.show(editing ? 'Promotion mise à jour.' : 'Promotion créée.', 'success');
        this.closeDialog();
        this.load();
      },
      error: (err: AppError) => {
        this.saving.set(false);
        this.toast.showError(err.message);
      },
    });
  }

  protected confirmDelete(): void {
    const target = this.deleteTarget();
    if (!target?.id) return;
    this.deleting.set(true);
    this.promotionService.delete(target.id).subscribe({
      next: () => {
        this.deleting.set(false);
        this.deleteTarget.set(null);
        this.toast.show('Promotion supprimée.', 'success');
        this.load();
      },
      error: (err: AppError) => {
        this.deleting.set(false);
        this.toast.showError(err.message);
      },
    });
  }
}
