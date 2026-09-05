import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { UtilisateurService } from '../../../core/services/utilisateur.service';
import { FiliereService } from '../../../core/services/filiere.service';
import { PromotionService } from '../../../core/services/promotion.service';
import { UtilisateurPromotionChambreService } from '../../../core/services/utilisateur-promotion-chambre.service';
import { UtilisateurDto, UtilisateurPromotionChambreDto } from '../../../core/models/dtos';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../../core/auth/role.guard';
import { RoleBadgeComponent } from '../../../shared/components/role-badge/role-badge.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';

/**
 * GET /api/v1/utilisateur/{id} is MANAGER-accessible even though the list
 * (GET /) is RESPONSABLE-only — a plain MANAGER can reach this page (e.g.
 * from a UPC assignment) without ever seeing the "Utilisateurs" nav item or
 * the list itself. Read-only for MANAGER; edit/delete only shown to
 * RESPONSABLE+, which route back through the list page's dialogs.
 *
 * UtilisateurDto used to carry `reservations`/`reclamations`/`documents`/
 * `combinaisonsUpc` id-list fields that this page rendered as bare counts —
 * all four were dropped from the backend DTO (server no longer serializes
 * them at all), so those counts silently went from real numbers to
 * TypeScript compile errors. Rather than re-adding placeholder counts, this
 * now fetches the one that's actually meaningful here — room/promotion
 * history — from its real source, GET /upc/by-utilisateur/{id}, which this
 * page's MANAGER floor already has access to. This is also, in effect, the
 * per-user view of "utilisateur-promotion-chambre" data that had no
 * dedicated tab anywhere in the admin UI.
 */
@Component({
  selector: 'app-user-detail-page',
  standalone: true,
  imports: [RouterLink, RoleBadgeComponent, SkeletonRowsComponent],
  template: `
    <a routerLink="/admin/utilisateurs" class="text-sm text-primary-600 hover:text-primary-700 dark:text-primary-400">&larr; Retour à la liste</a>

    @if (loading()) {
      <div class="mt-4">
        <app-skeleton-rows [rows]="3" [columns]="2"></app-skeleton-rows>
      </div>
    } @else {
      @if (user(); as u) {
        <div class="mt-4 rounded-lg border border-neutral-200 bg-white p-6 dark:border-white/10 dark:bg-white/5">
          <div class="flex items-center gap-2">
            <h1 class="text-lg font-semibold text-neutral-900 dark:text-white">{{ u.prenom }} {{ u.nom }}</h1>
            <app-role-badge [role]="u.role"></app-role-badge>
          </div>
          <dl class="mt-4 grid grid-cols-2 gap-x-6 gap-y-3 text-sm">
            <div><dt class="text-neutral-500 dark:text-neutral-400">Email</dt><dd class="text-neutral-900 dark:text-white">{{ u.email }}</dd></div>
            <div><dt class="text-neutral-500 dark:text-neutral-400">CIN</dt><dd class="text-neutral-900 dark:text-white">{{ u.cin }}</dd></div>
            <div><dt class="text-neutral-500 dark:text-neutral-400">Téléphone</dt><dd class="text-neutral-900 dark:text-white">{{ u.telephone }}</dd></div>
            <div><dt class="text-neutral-500 dark:text-neutral-400">Adresse</dt><dd class="text-neutral-900 dark:text-white">{{ u.adresse ?? '—' }}</dd></div>
            <div><dt class="text-neutral-500 dark:text-neutral-400">Filière</dt><dd class="text-neutral-900 dark:text-white">{{ filiereName() ?? '—' }}</dd></div>
          </dl>
          @if (canEdit()) {
            <p class="mt-4 text-xs text-neutral-400">
              Pour modifier, supprimer ou assigner une promotion, utilisez la liste des utilisateurs (nécessite le rôle RESPONSABLE pour modifier/supprimer).
            </p>
          }
        </div>

        <div class="mt-4 rounded-lg border border-neutral-200 bg-white p-6 dark:border-white/10 dark:bg-white/5">
          <h2 class="text-sm font-semibold text-neutral-900 dark:text-white">Historique chambre / promotion</h2>
          @if (loadingUpc()) {
            <div class="mt-3"><app-skeleton-rows [rows]="2" [columns]="3"></app-skeleton-rows></div>
          } @else if (upcRecords().length === 0) {
            <p class="mt-2 text-sm text-neutral-500 dark:text-neutral-400">Aucune chambre attribuée pour l'instant.</p>
          } @else {
            <ul class="mt-3 flex flex-col gap-2">
              @for (record of upcRecords(); track record.id?.chambre_id) {
                <li class="flex items-center justify-between rounded-md border border-neutral-100 px-3 py-2 text-sm dark:border-white/10">
                  <span class="text-neutral-700 dark:text-neutral-200">
                    Chambre {{ record.chambre.slice(0, 8) }}… — {{ promotionLabel(record.promotion) }}
                  </span>
                  @if (record.retard) {
                    <span class="rounded-full bg-danger-500/10 px-2 py-0.5 text-xs font-medium text-danger-500">Retard</span>
                  }
                </li>
              }
            </ul>
          }
        </div>
      }
    }
  `,
})
export class UserDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly userService = inject(UtilisateurService);
  private readonly filiereService = inject(FiliereService);
  private readonly promotionService = inject(PromotionService);
  private readonly upcService = inject(UtilisateurPromotionChambreService);
  private readonly toast = inject(ToastService);
  private readonly currentUser = inject(CurrentUserService);

  protected readonly user = signal<UtilisateurDto | null>(null);
  protected readonly loading = signal(true);
  protected readonly canEdit = computed(() => hasRoleAtLeast(this.currentUser.realmRoles(), 'RESPONSABLE'));

  protected readonly filiereName = signal<string | null>(null);
  protected readonly upcRecords = signal<UtilisateurPromotionChambreDto[]>([]);
  protected readonly loadingUpc = signal(true);
  private readonly promotionLabelById = signal<Map<string, string>>(new Map());

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    this.userService.getById(id).subscribe({
      next: (u) => {
        this.user.set(u);
        this.loading.set(false);
        if (u.filiere != null) {
          this.filiereService.getById(u.filiere).subscribe({
            next: (f) => this.filiereName.set(f.nom),
            error: () => this.filiereName.set(null),
          });
        }
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.toast.showError(err.message);
      },
    });

    this.upcService.getAllByUtilisateurId(id).subscribe({
      next: (records) => {
        this.upcRecords.set(records);
        this.loadingUpc.set(false);
        // Best-effort label resolution — a UUID promotion id on its own
        // ("Promotion #a1b2c3d4…") isn't meaningful to a human, so this
        // resolves each distinct promotion referenced in the history to its
        // "2024 - 2025 · Niveau 3" form. Uses getById one at a time (small
        // dataset per user — a handful of UPC rows at most) rather than
        // pulling in the whole paginated promotion list for this.
        const uniqueIds = [...new Set(records.map((r) => r.promotion))];
        uniqueIds.forEach((promotionId) => {
          this.promotionService.getById(promotionId).subscribe({
            next: (p) =>
              this.promotionLabelById.update((map) => {
                const next = new Map(map);
                next.set(promotionId, `${p.anneeDeDepart} - ${p.anneeDeFin} · Niveau ${p.niveau}`);
                return next;
              }),
            error: () => undefined,
          });
        });
      },
      error: () => this.loadingUpc.set(false),
    });
  }

  protected promotionLabel(promotionId: string): string {
    return this.promotionLabelById().get(promotionId) ?? `Promotion #${promotionId.slice(0, 8)}…`;
  }
}
