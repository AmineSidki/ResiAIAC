import { Component, OnInit, TemplateRef, computed, inject, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UtilisateurService } from '../../../core/services/utilisateur.service';
import { UtilisateurDto } from '../../../core/models/dtos';
import { AppRole } from '../../../core/models/enums';
import { AppError } from '../../../core/api/app-error';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { InputComponent } from '../../../shared/components/input/input.component';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { DialogComponent } from '../../../shared/components/dialog/dialog.component';
import { DataTableComponent, DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { RoleBadgeComponent } from '../../../shared/components/role-badge/role-badge.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

const TELEPHONE_PATTERN = '^\\+?[0-9]{8,15}$';

const ROLE_LABELS: Record<AppRole, string> = {
  ETUDIANT: 'Étudiant',
  MANAGER: 'Manager',
  RESPONSABLE: 'Responsable',
  ADMINISTRATEUR: 'Administrateur',
};

function emptyUser(): UtilisateurDto {
  return {
    id: null,
    role: 'ETUDIANT',
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
 *
 * Custom-role creation: POST /utilisateur/ (RESPONSABLE-gated) rejects
 * role=RESPONSABLE/ADMINISTRATEUR server-side (BadRouteException) — only
 * POST /utilisateur/admin/ (ADMINISTRATEUR-only) can hand out those two
 * roles. The role dropdown below is scoped to what the signed-in user is
 * actually allowed to grant, and `save()` picks the matching endpoint.
 */
@Component({
  selector: 'app-user-list-page',
  standalone: true,
  imports: [
    RouterLink,
    ReactiveFormsModule,
    ButtonComponent,
    InputComponent,
    SelectComponent,
    DialogComponent,
    DataTableComponent,
    PaginationComponent,
    RoleBadgeComponent,
    SkeletonRowsComponent,
    EmptyStateComponent,
  ],
  template: `
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-lg font-semibold text-neutral-900 dark:text-white">Utilisateurs</h1>
        <p class="text-sm text-neutral-500 dark:text-neutral-400">{{ totalElements() }} au total</p>
      </div>
      <app-button (click)="openCreate()">Ajouter un utilisateur</app-button>
    </div>

    <div class="mt-4">
      @if (loading()) {
        <app-skeleton-rows [columns]="6"></app-skeleton-rows>
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
      <a [routerLink]="['/admin/utilisateurs', row.id]" class="font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400">
        {{ row.prenom }} {{ row.nom }}
      </a>
    </ng-template>

    <ng-template #roleTpl let-row>
      <app-role-badge [role]="row.role"></app-role-badge>
    </ng-template>

    <ng-template #actionsTpl let-row>
      <div class="flex gap-3">
        <button type="button" class="text-sm font-medium text-primary-600 hover:text-primary-700" (click)="openEdit(row)">Modifier</button>
        <button type="button" class="text-sm font-medium text-danger-500 hover:text-danger-600" (click)="deleteTarget.set(row)">Supprimer</button>
      </div>
    </ng-template>

    <app-dialog [open]="dialogOpen()" [title]="editingRow() ? 'Modifier utilisateur' : 'Ajouter un utilisateur'" (close)="closeDialog()">
      <form [formGroup]="form" class="flex flex-col gap-3">
        @if (!editingRow()) {
          <app-select
            formControlName="role"
            label="Rôle"
            [options]="roleOptions()"
          ></app-select>
        } @else {
          <div class="flex flex-col gap-1">
            <span class="text-sm font-medium text-neutral-700 dark:text-neutral-300">Rôle</span>
            <app-role-badge [role]="editingRow()!.role"></app-role-badge>
            <p class="text-xs text-neutral-400">Le rôle ne se modifie pas depuis ce formulaire.</p>
          </div>
        }
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
      <p class="text-sm text-neutral-600 dark:text-neutral-300">
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
  private readonly currentUser = inject(CurrentUserService);
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

  /**
   * A RESPONSABLE can only hand out ETUDIANT/MANAGER via the normal create
   * route (the server rejects the rest with BadRouteException); an
   * ADMINISTRATEUR can grant any role via the admin-only route. Scoping the
   * dropdown avoids offering a choice that would just come back as a 400.
   */
  protected readonly roleOptions = computed<SelectOption<AppRole>[]>(() => {
    const allowed: AppRole[] =
      this.currentUser.highestRole() === 'ADMINISTRATEUR'
        ? ['ETUDIANT', 'MANAGER', 'RESPONSABLE', 'ADMINISTRATEUR']
        : ['ETUDIANT', 'MANAGER'];
    return allowed.map((role) => ({ value: role, label: ROLE_LABELS[role] }));
  });

  protected readonly form = this.fb.nonNullable.group({
    role: ['ETUDIANT' as AppRole, Validators.required],
    prenom: ['', Validators.required],
    nom: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    cin: ['', Validators.required],
    telephone: ['', [Validators.required, Validators.pattern(TELEPHONE_PATTERN)]],
    adresse: [''],
  });

  private readonly nameTpl = viewChild<TemplateRef<{ $implicit: UtilisateurDto }>>('nameTpl');
  private readonly roleTpl = viewChild<TemplateRef<{ $implicit: UtilisateurDto }>>('roleTpl');
  private readonly actionsTpl = viewChild<TemplateRef<{ $implicit: UtilisateurDto }>>('actionsTpl');

  protected readonly columns = computed<DataTableColumn<UtilisateurDto>[]>(() => {
    const nameTpl = this.nameTpl();
    const roleTpl = this.roleTpl();
    const actionsTpl = this.actionsTpl();
    const cols: DataTableColumn<UtilisateurDto>[] = [
      { key: 'name', header: 'Nom', cellTemplate: nameTpl, accessor: (r) => `${r.prenom} ${r.nom}` },
      { key: 'role', header: 'Rôle', cellTemplate: roleTpl, accessor: (r) => r.role },
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
    this.form.reset({ role: 'ETUDIANT', prenom: '', nom: '', email: '', cin: '', telephone: '', adresse: '' });
    this.dialogOpen.set(true);
  }

  protected openEdit(row: UtilisateurDto): void {
    this.editingRow.set(row);
    this.form.reset({
      role: row.role,
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
      role: editing ? editing.role : value.role,
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
      : dto.role === 'RESPONSABLE' || dto.role === 'ADMINISTRATEUR'
        ? this.userService.createAdmin(dto)
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
