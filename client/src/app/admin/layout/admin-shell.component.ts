import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CurrentUserService } from '../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../core/auth/role.guard';
import { AppRole } from '../../core/models/enums';

interface NavItem {
  label: string;
  path: string;
  /** Minimum role to even see this nav item — separate from write-gating inside the screen. */
  minRole: AppRole;
}

interface NavSection {
  heading: string;
  items: NavItem[];
}

/**
 * Desktop-first admin shell (target 1280px+): persistent left sidebar +
 * router-outlet content. Nav items are gated per-item against the user's
 * highest role, not just the blanket /admin route guard (requireManager) —
 * e.g. "Utilisateurs" and "Documents" are hidden from a pure MANAGER because
 * their list endpoints are RESPONSABLE-only server-side (confirmed against
 * UtilisateurController.java and DocumentController.java directly).
 */
@Component({
  selector: 'app-admin-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="flex min-h-screen bg-neutral-50">
      <aside class="flex w-64 shrink-0 flex-col border-r border-neutral-200 bg-white">
        <div class="flex h-16 items-center gap-2 border-b border-neutral-200 px-5">
          <span class="text-base font-semibold text-primary-700">ResiAIAC</span>
          <span class="rounded bg-primary-50 px-1.5 py-0.5 text-xs font-medium text-primary-600">Admin</span>
        </div>

        <nav class="flex-1 overflow-y-auto px-3 py-4">
          @for (section of visibleSections(); track section.heading) {
            <div class="mb-5">
              <p class="mb-1.5 px-2 text-xs font-semibold uppercase tracking-wide text-neutral-400">
                {{ section.heading }}
              </p>
              <ul class="space-y-0.5">
                @for (item of section.items; track item.path) {
                  <li>
                    <a
                      [routerLink]="item.path"
                      routerLinkActive="bg-primary-50 text-primary-700"
                      class="block rounded-md px-2.5 py-2 text-sm font-medium text-neutral-600 hover:bg-neutral-100"
                    >
                      {{ item.label }}
                    </a>
                  </li>
                }
              </ul>
            </div>
          }
        </nav>

        <div class="border-t border-neutral-200 p-4">
          <p class="text-sm font-medium text-neutral-900">{{ currentUser.fullName() ?? 'Signed in' }}</p>
          <p class="text-xs text-neutral-500">{{ currentUser.highestRole() }}</p>
          <button
            type="button"
            (click)="currentUser.logout()"
            class="mt-2 w-full rounded-md border border-neutral-300 px-3 py-1.5 text-sm text-neutral-700 hover:bg-neutral-50"
          >
            Log out
          </button>
        </div>
      </aside>

      <main class="min-w-0 flex-1 overflow-y-auto">
        <div class="mx-auto max-w-[1600px] px-8 py-6">
          <router-outlet></router-outlet>
        </div>
      </main>
    </div>
  `,
})
export class AdminShellComponent {
  protected readonly currentUser = inject(CurrentUserService);

  private readonly sections: NavSection[] = [
    {
      heading: 'Occupation',
      items: [{ label: 'Chambres', path: '/admin/chambres', minRole: 'MANAGER' }],
    },
    {
      heading: 'Suivi',
      items: [
        { label: 'Réclamations', path: '/admin/reclamations', minRole: 'MANAGER' },
        { label: 'Réservations', path: '/admin/reservations', minRole: 'MANAGER' },
        // getAll/getAllByStatus on DocumentController are RESPONSABLE-only —
        // confirmed against DocumentController.java, not just the brief.
        { label: 'Documents', path: '/admin/documents', minRole: 'RESPONSABLE' },
      ],
    },
    {
      heading: 'Référentiel',
      items: [
        { label: 'Bâtiments', path: '/admin/reference/batiments', minRole: 'MANAGER' },
        { label: 'Étages', path: '/admin/reference/etages', minRole: 'MANAGER' },
        { label: 'Filières', path: '/admin/reference/filieres', minRole: 'MANAGER' },
        { label: 'Services', path: '/admin/reference/services', minRole: 'MANAGER' },
        { label: 'Équipements', path: '/admin/reference/equipements', minRole: 'MANAGER' },
      ],
    },
    {
      heading: 'Administration',
      items: [
        // GET /api/v1/utilisateur/ (the list) is RESPONSABLE-only; getById is
        // MANAGER-accessible but there's no useful nav destination for
        // "look up one user by id" without the list first.
        { label: 'Utilisateurs', path: '/admin/utilisateurs', minRole: 'RESPONSABLE' },
      ],
    },
  ];

  protected readonly visibleSections = computed<NavSection[]>(() => {
    const roles = this.currentUser.realmRoles();
    return this.sections
      .map((section) => ({
        ...section,
        items: section.items.filter((item) => hasRoleAtLeast(roles, item.minRole)),
      }))
      .filter((section) => section.items.length > 0);
  });
}
