import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { UtilisateurService } from '../../../core/services/utilisateur.service';
import { UtilisateurDto } from '../../../core/models/dtos';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../../core/auth/role.guard';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';

/**
 * GET /api/v1/utilisateur/{id} is MANAGER-accessible even though the list
 * (GET /) is RESPONSABLE-only — a plain MANAGER can reach this page (e.g.
 * from a UPC assignment) without ever seeing the "Utilisateurs" nav item or
 * the list itself. Read-only for MANAGER; edit/delete only shown to
 * RESPONSABLE+, which route back through the list page's dialogs.
 */
@Component({
  selector: 'app-user-detail-page',
  standalone: true,
  imports: [RouterLink, SkeletonRowsComponent],
  template: `
    <a routerLink="/admin/utilisateurs" class="text-sm text-primary-600 hover:text-primary-700">&larr; Retour à la liste</a>

    @if (loading()) {
      <div class="mt-4">
        <app-skeleton-rows [rows]="3" [columns]="2"></app-skeleton-rows>
      </div>
    } @else {
      @if (user(); as u) {
        <div class="mt-4 rounded-lg border border-neutral-200 bg-white p-6">
          <h1 class="text-lg font-semibold text-neutral-900">{{ u.prenom }} {{ u.nom }}</h1>
          <dl class="mt-4 grid grid-cols-2 gap-x-6 gap-y-3 text-sm">
            <div><dt class="text-neutral-500">Email</dt><dd class="text-neutral-900">{{ u.email }}</dd></div>
            <div><dt class="text-neutral-500">CIN</dt><dd class="text-neutral-900">{{ u.cin }}</dd></div>
            <div><dt class="text-neutral-500">Téléphone</dt><dd class="text-neutral-900">{{ u.telephone }}</dd></div>
            <div><dt class="text-neutral-500">Adresse</dt><dd class="text-neutral-900">{{ u.adresse ?? '—' }}</dd></div>
            <div><dt class="text-neutral-500">Réservations</dt><dd class="text-neutral-900">{{ u.reservations.length }}</dd></div>
            <div><dt class="text-neutral-500">Réclamations</dt><dd class="text-neutral-900">{{ u.reclamations.length }}</dd></div>
            <div><dt class="text-neutral-500">Documents</dt><dd class="text-neutral-900">{{ u.documents.length }}</dd></div>
            <div><dt class="text-neutral-500">Attributions de chambre</dt><dd class="text-neutral-900">{{ u.combinaisonsUpc.length }}</dd></div>
          </dl>
          @if (canEdit()) {
            <p class="mt-4 text-xs text-neutral-400">
              Pour modifier ou supprimer, utilisez la liste des utilisateurs (nécessite le rôle RESPONSABLE).
            </p>
          }
        </div>
      }
    }
  `,
})
export class UserDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly userService = inject(UtilisateurService);
  private readonly toast = inject(ToastService);
  private readonly currentUser = inject(CurrentUserService);

  protected readonly user = signal<UtilisateurDto | null>(null);
  protected readonly loading = signal(true);
  protected readonly canEdit = computed(() => hasRoleAtLeast(this.currentUser.realmRoles(), 'RESPONSABLE'));

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.userService.getById(id).subscribe({
      next: (u) => {
        this.user.set(u);
        this.loading.set(false);
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.toast.showError(err.message);
      },
    });
  }
}
