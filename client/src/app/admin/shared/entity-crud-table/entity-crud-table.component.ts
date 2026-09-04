import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, OnInit, signal, TemplateRef, viewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BaseCrudService } from '../../../core/api/base-crud.service';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { InputComponent } from '../../../shared/components/input/input.component';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { DialogComponent } from '../../../shared/components/dialog/dialog.component';
import { DataTableComponent, DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { SkeletonRowsComponent } from '../skeleton/skeleton-rows.component';
import { EmptyStateComponent } from '../empty-state/empty-state.component';

export interface EntityFieldConfig<T> {
  key: Extract<keyof T, string>;
  label: string;
  type?: 'text' | 'number' | 'textarea' | 'select';
  required?: boolean;
  /** e.g. the telephone regex — mirrors the DTO's server-side constraint so invalid input never round-trips. */
  pattern?: string;
  min?: number;
  hint?: string;
  /** Required when type is 'select' — e.g. the Batiment options for Etage.batiment (a UUID foreign key). */
  options?: SelectOption[];
}

/**
 * Generic CRUD table + create/edit dialog for any entity whose service
 * extends BaseCrudService (Batiment, Etage, Filiere, Service, Equipement —
 * five structurally identical flat-list entities, no pagination). Field
 * shape and validation are declared as data by the parent page, not
 * hand-rolled per entity.
 */
@Component({
  selector: 'app-entity-crud-table',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonComponent,
    InputComponent,
    SelectComponent,
    DialogComponent,
    DataTableComponent,
    SkeletonRowsComponent,
    EmptyStateComponent,
  ],
  template: `
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-lg font-semibold text-neutral-900 dark:text-white">{{ title() }}</h1>
        @if (subtitle()) {
          <p class="text-sm text-neutral-500 dark:text-neutral-400">{{ subtitle() }}</p>
        }
      </div>
      @if (canWrite()) {
        <app-button (click)="openCreate()">Ajouter {{ entityLabel() }}</app-button>
      }
    </div>

    <div class="mt-4">
      @if (loading()) {
        <app-skeleton-rows [columns]="columns().length + (canWrite() ? 1 : 0)"></app-skeleton-rows>
      } @else if (rows().length === 0) {
        <app-empty-state reason="no-data">
          @if (canWrite()) {
            <app-button size="sm" (click)="openCreate()">Ajouter {{ entityLabel() }}</app-button>
          }
        </app-empty-state>
      } @else {
        <app-data-table [columns]="tableColumns()" [rows]="rows()" [trackBy]="rowId"></app-data-table>
      }
    </div>

    <ng-template #actionsTpl let-row>
      <div class="flex gap-3">
        <button type="button" class="text-sm font-medium text-primary-600 hover:text-primary-700" (click)="openEdit(row)">
          Modifier
        </button>
        @if (deleteGuard()(row); as blockedReason) {
          <span class="text-sm text-neutral-400" [title]="blockedReason">Supprimer</span>
        } @else {
          <button type="button" class="text-sm font-medium text-danger-500 hover:text-danger-600" (click)="requestDelete(row)">
            Supprimer
          </button>
        }
      </div>
    </ng-template>

    <app-dialog [open]="dialogOpen()" [title]="dialogTitle()" (close)="closeDialog()">
      <form [formGroup]="form" class="flex flex-col gap-3" (ngSubmit)="save()">
        @for (field of fields(); track field.key) {
          @if (field.type === 'select') {
            <app-select [formControlName]="field.key" [label]="field.label" [options]="field.options ?? []"></app-select>
          } @else {
            <app-input
              [formControlName]="field.key"
              [label]="field.label"
              [type]="field.type === 'number' ? 'number' : 'text'"
              [hint]="field.hint ?? null"
              [errorText]="errorFor(field)"
            ></app-input>
          }
        }
      </form>
      <div footer class="flex gap-2">
        <app-button variant="secondary" (click)="closeDialog()">Annuler</app-button>
        <app-button [loading]="saving()" [disabled]="form.invalid" (click)="save()">Enregistrer</app-button>
      </div>
    </app-dialog>

    <app-dialog [open]="deleteTarget() !== null" title="Confirmer la suppression" (close)="deleteTarget.set(null)">
      <p class="text-sm text-neutral-600 dark:text-neutral-300">
        Supprimer définitivement cet élément&nbsp;? Cette action est irréversible.
      </p>
      <div footer class="flex gap-2">
        <app-button variant="secondary" (click)="deleteTarget.set(null)">Annuler</app-button>
        <app-button variant="danger" [loading]="deleting()" (click)="confirmDelete()">Supprimer</app-button>
      </div>
    </app-dialog>
  `,
})
export class EntityCrudTableComponent<T extends object, TUpdateRequest, TId extends string | number>
  implements OnInit
{
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);

  readonly service = input.required<BaseCrudService<T, TUpdateRequest, TId>>();
  readonly columns = input.required<DataTableColumn<T>[]>();
  readonly fields = input.required<EntityFieldConfig<T>[]>();
  readonly title = input.required<string>();
  readonly subtitle = input<string | null>(null);
  /** Singular label used in "Ajouter {entityLabel}" / dialog titles, e.g. "un bâtiment". */
  readonly entityLabel = input.required<string>();
  readonly idKey = input<Extract<keyof T, string>>('id' as Extract<keyof T, string>);
  readonly canWrite = input(true);
  readonly emptyDto = input.required<() => T>();
  readonly buildUpdateRequest = input.required<(id: TId, dto: T) => TUpdateRequest>();
  /** Optional row-level "delete disabled" reason, e.g. a room still referencing this floor. */
  readonly deleteGuard = input<(row: T) => string | null>(() => null);

  protected readonly rows = signal<T[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly deleting = signal(false);
  protected readonly dialogOpen = signal(false);
  protected readonly editingRow = signal<T | null>(null);
  protected readonly deleteTarget = signal<T | null>(null);

  form = this.fb.nonNullable.group({});

  protected readonly dialogTitle = computed(() =>
    this.editingRow() ? `Modifier ${this.entityLabel()}` : `Ajouter ${this.entityLabel()}`,
  );

  protected readonly rowId = (row: T): unknown => row[this.idKey()];

  private readonly actionsTpl = viewChild<TemplateRef<{ $implicit: T }>>('actionsTpl');

  protected readonly tableColumns = computed<DataTableColumn<T>[]>(() => {
    const cols = [...this.columns()];
    const tpl = this.actionsTpl();
    if (this.canWrite() && tpl) {
      cols.push({ key: '__actions', header: '', cellTemplate: tpl });
    }
    return cols;
  });

  ngOnInit(): void {
    this.rebuildForm();
    this.load();
  }

  private rebuildForm(): void {
    const group: Record<string, ReturnType<typeof this.fb.control>> = {};
    for (const field of this.fields()) {
      const validators = [];
      if (field.required) validators.push(Validators.required);
      if (field.pattern) validators.push(Validators.pattern(field.pattern));
      if (field.type === 'number') validators.push(Validators.pattern(/^-?\d+$/));
      if (field.min !== undefined) validators.push(Validators.min(field.min));
      group[field.key] = this.fb.control('', validators);
    }
    this.form = this.fb.nonNullable.group(group);
  }

  protected load(): void {
    this.loading.set(true);
    this.service()
      .getAll()
      .subscribe({
        next: (rows) => {
          this.rows.set(rows);
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
    this.rebuildForm();
    this.form.reset(this.toFormValue(this.emptyDto()()));
    this.dialogOpen.set(true);
  }

  protected openEdit(row: T): void {
    this.editingRow.set(row);
    this.rebuildForm();
    this.form.reset(this.toFormValue(row));
    this.dialogOpen.set(true);
  }

  protected closeDialog(): void {
    this.dialogOpen.set(false);
    this.editingRow.set(null);
  }

  private toFormValue(dto: T): Record<string, string> {
    const value: Record<string, string> = {};
    for (const field of this.fields()) {
      const raw = dto[field.key];
      value[field.key] = raw === null || raw === undefined ? '' : String(raw);
    }
    return value;
  }

  protected errorFor(field: EntityFieldConfig<T>): string | null {
    const control = this.form.get(field.key);
    if (!control || !control.touched || control.valid) return null;
    if (control.hasError('required')) return `${field.label} est requis.`;
    if (control.hasError('pattern')) return `${field.label} n'a pas le bon format.`;
    if (control.hasError('min')) return `${field.label} doit être au moins ${field.min}.`;
    return 'Valeur invalide.';
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const base = this.editingRow() ?? this.emptyDto()();
    const dto: T = { ...base };
    const formValue = this.form.getRawValue() as Record<string, string>;
    for (const field of this.fields()) {
      const raw = formValue[field.key];
      (dto as Record<string, unknown>)[field.key] = field.type === 'number' ? Number(raw) : raw;
    }

    this.saving.set(true);
    const editing = this.editingRow();
    const request$ = editing
      ? this.service().update(this.buildUpdateRequest()(editing[this.idKey()] as TId, dto))
      : this.service().create(dto);

    request$.subscribe({
      next: () => {
        this.saving.set(false);
        this.toast.show(editing ? 'Modifications enregistrées.' : 'Élément créé.', 'success');
        this.closeDialog();
        this.load();
      },
      error: (err: AppError) => {
        this.saving.set(false);
        this.toast.showError(err.message);
      },
    });
  }

  protected requestDelete(row: T): void {
    this.deleteTarget.set(row);
  }

  protected confirmDelete(): void {
    const target = this.deleteTarget();
    if (!target) return;
    this.deleting.set(true);
    this.service()
      .delete(target[this.idKey()] as TId)
      .subscribe({
        next: () => {
          this.deleting.set(false);
          this.deleteTarget.set(null);
          this.toast.show('Élément supprimé.', 'success');
          this.load();
        },
        error: (err: AppError) => {
          this.deleting.set(false);
          this.toast.showError(err.message);
        },
      });
  }
}
