import { Component, OnInit, TemplateRef, computed, inject, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UtilisateurService } from '../../../core/services/utilisateur.service';
import { UtilisateurDto } from '../../../core/models/dtos';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { InputComponent } from '../../../shared/components/input/input.component';
import { DialogComponent } from '../../../shared/components/dialog/dialog.component';
import { DataTableComponent, DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

const TELEPHONE_PATTERN = '^\\+?[0-9]{8,15}$';

function emptyUser(): UtilisateurDto {
  return {
    id: null,
    email: '',
    nom: '',
    prenom: '',
    cin: '',
    adresse: '',
    telephone: '',
    reservations: [],
    reclamations: [],
    documents: [],
    combinaisonsUpc: [],
    createdAt: null,
    updatedAt: null,
  };
}

/**
 * GET /api/v1/utilisateur/ (the list) is RESPONSABLE-only — a pure MANAGER
 * never reaches this page (hidden from the sidebar, and the /admin/utilisateurs
 * route itself is guarded requireResponsable in admin.routes.ts). Individual
 * lookups (GET /{id}) are MANAGER-accessible via a separate detail route.
 */
@Component({
  selector: 'app-user-list-page',
  standalone: true,
  imports: [
    RouterLink,
    ReactiveFormsModule,
    ButtonComponent,
    InputComponent,
    DialogComponent,
    DataTableComponent,
    PaginationComponent,
    SkeletonRowsComponent,
    EmptyStateComponent,
  ],
  template: `
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-lg font-semibold text-neutral-900">Utilisateurs</h1>
        <p class="text-sm text-neutral-500">{{ totalElements() }} au total</p>
      </div>
      <app-button (click)="openCreate()">Ajouter un utilisateur</app-button>
    </div>

    <div class="mt-4">
      @if (loading()) {
        <app-skeleton-rows [columns]="5"></app-skeleton-rows>
      } @else if (rows().length === 0) {
        <app-empty-state reason="no-data">
          <app-button size="sm" (click)="openCreate()">Ajouter un utilisateur</app-button>
        </app-empty-state>
      } @else {
        <app-data-table [columns]="columns()" [rows]="rows()" [trackBy]="rowId"></app-data-table>
        <app-pagination [currentPage]="page()" [totalPages]="totalPages()" [totalElements]="totalElements()" (pageChange)="goToPage($event)"></app-pagination>
      }
    </div>

    <ng-template #nameTpl let-row>
      <a [routerLink]="['/admin/utilisateurs', row.id]" class="font-medium text-primary-600 hover:text-primary-700">
        {{ row.prenom }} {{ row.nom }}
      </a>
    </ng-template>

    <ng-template #actionsTpl let-row>
      <div class="flex gap-3">
        <button type="button" class="text-sm font-medium text-primary-600 hover:text-primary-700" (click)="openEdit(row)">Modifier</button>
        <button type="button" class="text-sm font-medium text-danger-500 hover:text-danger-600" (click)="deleteTarget.set(row)">Supprimer</button>
      </div>
    </ng-template>

    <app-dialog [open]="dialogOpen()" [title]="editingRow() ? 'Modifier utilisateur' : 'Ajouter un utilisateur'" (close)="closeDialog()">
      <form [formGroup]="form" class="flex flex-col gap-3">
        <app-input formControlName="prenom" label="Prénom" [errorText]="errorFor('prenom')"></app-input>
        <app-input formControlName="nom" label="Nom" [errorText]="errorFor('nom')"></app-input>
        <app-input formControlName="email" label="Email" type="email" [errorText]="errorFor('email')"></app-input>
        <app-input formControlName="cin" label="CIN" [errorText]="errorFor('cin')"></app-input>
        <app-input formControlName="telephone" label="Téléphone" type="tel" hint="Format: +212612345678" [errorText]="errorFor('telephone')"></app-input>
        <app-input formControlName="adresse" label="Adresse (optionnel)"></app-input>
      </form>
      @if (editingRow()) {
        <p class="mt-3 text-xs text-neutral-400">
          Note&nbsp;: modifier le nom/prénom ici ne met pas à jour le nom d'utilisateur Keycloak associé
          (lacune connue côté serveur — pas un bug frontend si l'affichage semble périmé).
        </p>
      }
      <div footer class="flex gap-2">
        <app-button variant="secondary" (click)="closeDialog()">Annuler</app-button>
        <app-button [loading]="saving()" [disabled]="form.invalid" (click)="save()">Enregistrer</app-button>
      </div>
    </app-dialog>

    <app-dialog [open]="deleteTarget() !== null" title="Confirmer la suppression" (close)="deleteTarget.set(null)">
      <p class="text-sm text-neutral-600">
        Supprimer cet utilisateur désactivera aussi son compte Keycloak. Cette action est irréversible.
      </p>
      <div footer class="flex gap-2">
        <app-button variant="secondary" (click)="deleteTarget.set(null)">Annuler</app-button>
        <app-button variant="danger" [loading]="deleting()" (click)="confirmDelete()">Supprimer</app-button>
      </div>
    </app-dialog>
  `,
})
export class UserListPageComponent implements OnInit {
  private readonly userService = inject(UtilisateurService);
  private readonly toast = inject(ToastService);
  private readonly fb = inject(FormBuilder);

  protected readonly rows = signal<UtilisateurDto[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly deleting = signal(false);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly totalElements = signal(0);

  protected readonly dialogOpen = signal(false);
  protected readonly editingRow = signal<UtilisateurDto | null>(null);
  protected readonly deleteTarget = signal<UtilisateurDto | null>(null);

  protected readonly rowId = (row: UtilisateurDto) => row.id;

  protected readonly form = this.fb.nonNullable.group({
    prenom: ['', Validators.required],
    nom: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    cin: ['', Validators.required],
    telephone: ['', [Validators.required, Validators.pattern(TELEPHONE_PATTERN)]],
    adresse: [''],
  });

  private readonly nameTpl = viewChild<TemplateRef<{ $implicit: UtilisateurDto }>>('nameTpl');
  private readonly actionsTpl = viewChild<TemplateRef<{ $implicit: UtilisateurDto }>>('actionsTpl');

  protected readonly columns = computed<DataTableColumn<UtilisateurDto>[]>(() => {
    const nameTpl = this.nameTpl();
    const actionsTpl = this.actionsTpl();
    const cols: DataTableColumn<UtilisateurDto>[] = [
      { key: 'name', header: 'Nom', cellTemplate: nameTpl, accessor: (r) => `${r.prenom} ${r.nom}` },
      { key: 'email', header: 'Email', accessor: (r) => r.email },
      { key: 'cin', header: 'CIN', accessor: (r) => r.cin },
      { key: 'telephone', header: 'Téléphone', accessor: (r) => r.telephone },
    ];
    if (actionsTpl) {
      cols.push({ key: 'actions', header: '', cellTemplate: actionsTpl });
    }
    return cols;
  });

  ngOnInit(): void {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    this.userService.getAll({ page: this.page() }).subscribe({
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

  protected goToPage(page: number): void {
    this.page.set(page);
    this.load();
  }

  protected openCreate(): void {
    this.editingRow.set(null);
    this.form.reset({ prenom: '', nom: '', email: '', cin: '', telephone: '', adresse: '' });
    this.dialogOpen.set(true);
  }

  protected openEdit(row: UtilisateurDto): void {
    this.editingRow.set(row);
    this.form.reset({
      prenom: row.prenom,
      nom: row.nom,
      email: row.email,
      cin: row.cin,
      telephone: row.telephone,
      adresse: row.adresse ?? '',
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
    if (control.hasError('email')) return "Format d'email invalide.";
    if (control.hasError('pattern')) return 'Format invalide (ex: +212612345678).';
    return 'Valeur invalide.';
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const editing = this.editingRow();
    const dto: UtilisateurDto = {
      ...(editing ?? emptyUser()),
      prenom: value.prenom,
      nom: value.nom,
      email: value.email,
      cin: value.cin,
      telephone: value.telephone,
      adresse: value.adresse || null,
    };

    this.saving.set(true);
    const request$ = editing
      ? this.userService.update({ id: editing.id as string, dto })
      : this.userService.create(dto);

    request$.subscribe({
      next: () => {
        this.saving.set(false);
        this.toast.show(editing ? 'Utilisateur mis à jour.' : 'Utilisateur créé.', 'success');
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
    this.userService.delete(target.id).subscribe({
      next: () => {
        this.deleting.set(false);
        this.deleteTarget.set(null);
        this.toast.show('Utilisateur supprimé.', 'success');
        this.load();
      },
      error: (err: AppError) => {
        this.deleting.set(false);
        this.toast.showError(err.message);
      },
    });
  }
}
