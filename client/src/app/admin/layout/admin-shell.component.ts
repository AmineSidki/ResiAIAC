import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CurrentUserService } from '../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../core/auth/role.guard';
import { AppRole } from '../../core/models/enums';
import { ThemeToggleComponent } from '../../shared/components/theme-toggle/theme-toggle.component';
import { RoleBadgeComponent } from '../../shared/components/role-badge/role-badge.component';

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

const SIDEBAR_COLLAPSED_KEY = 'resiaiac.admin-sidebar-collapsed';

/**
 * Desktop-first admin shell (target 1280px+): persistent left sidebar +
 * router-outlet content. Nav items are gated per-item against the user's
 * highest role, not just the blanket /admin route guard (requireManager) —
 * e.g. "Utilisateurs" and "Documents" are hidden from a pure MANAGER because
 * their list endpoints are RESPONSABLE-only server-side (confirmed against
 * UtilisateurController.java and DocumentController.java directly).
 *
 * The outer container is `h-screen overflow-hidden` and the sidebar is a
 * fixed-height flex column of its own — only `<main>` scrolls. Previously
 * the container was `min-h-screen`, so on any content taller than the
 * viewport the whole page (sidebar included) scrolled together, pushing the
 * user's name/logout button out of view. The collapse toggle persists to
 * localStorage the same way the theme does, so it survives a reload.
 */
@Component({
  selector: 'app-admin-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ThemeToggleComponent, RoleBadgeComponent],
  template: `
    <div class="flex h-screen overflow-hidden bg-neutral-50 dark:bg-surface-dark">
      <aside
        class="flex h-screen shrink-0 flex-col overflow-hidden border-r border-neutral-200 bg-white transition-[width] duration-150 dark:border-white/10 dark:bg-white/[0.03]"
        [class.w-64]="!collapsed()"
        [class.w-[72px]]="collapsed()"
      >
        <div class="flex h-16 shrink-0 items-center gap-2 border-b border-neutral-200 px-5 dark:border-white/10">
          @if (!collapsed()) {
            <span class="text-base font-semibold text-primary-700 dark:text-primary-300">ResiAIAC</span>
            <span class="rounded bg-primary-50 px-1.5 py-0.5 text-xs font-medium text-primary-600 dark:bg-primary-900/40 dark:text-primary-300">Admin</span>
          } @else {
            <span class="mx-auto text-base font-semibold text-primary-700 dark:text-primary-300">R</span>
          }
        </div>

        <nav class="flex-1 overflow-y-auto px-3 py-4">
          @for (section of visibleSections(); track section.heading) {
            <div class="mb-5">
              @if (!collapsed()) {
                <p class="mb-1.5 px-2 text-xs font-semibold uppercase tracking-wide text-neutral-400 dark:text-neutral-500">
                  {{ section.heading }}
                </p>
              }
              <ul class="space-y-0.5">
                @for (item of section.items; track item.path) {
                  <li>
                    <a
                      [routerLink]="item.path"
                      routerLinkActive="bg-primary-50 text-primary-700 dark:bg-primary-900/40 dark:text-primary-300"
                      [title]="item.label"
                      class="block truncate rounded-md px-2.5 py-2 text-sm font-medium text-neutral-600 hover:bg-neutral-100 dark:text-neutral-300 dark:hover:bg-white/5"
                    >
                      {{ collapsed() ? item.label.charAt(0) : item.label }}
                    </a>
                  </li>
                }
              </ul>
            </div>
          }
        </nav>

        <div class="shrink-0 border-t border-neutral-200 p-4 dark:border-white/10">
          <button
            type="button"
            (click)="toggleCollapsed()"
            class="mb-3 flex w-full items-center gap-2 rounded-md border border-neutral-200 px-2.5 py-1.5 text-xs font-medium text-neutral-500 hover:bg-neutral-50 dark:border-white/10 dark:text-neutral-400 dark:hover:bg-white/5"
            [attr.aria-label]="collapsed() ? 'Déplier le menu' : 'Réduire le menu'"
          >
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" class="h-3.5 w-3.5 shrink-0" [class.rotate-180]="collapsed()">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
            </svg>
            @if (!collapsed()) {
              <span>Réduire</span>
            }
          </button>

          @if (!collapsed()) {
            <div class="flex items-center justify-between gap-2">
              <div class="min-w-0">
                <p class="truncate text-sm font-medium text-neutral-900 dark:text-white">{{ currentUser.fullName() ?? 'Signed in' }}</p>
                <app-role-badge [role]="currentUser.highestRole()"></app-role-badge>
              </div>
              <app-theme-toggle></app-theme-toggle>
            </div>
            <button
              type="button"
              (click)="currentUser.logout()"
              class="mt-2 w-full rounded-md border border-neutral-300 px-3 py-1.5 text-sm text-neutral-700 hover:bg-neutral-50 dark:border-white/10 dark:text-neutral-200 dark:hover:bg-white/5"
            >
              Log out
            </button>
          } @else {
            <div class="flex flex-col items-center gap-2">
              <app-theme-toggle></app-theme-toggle>
              <button
                type="button"
                (click)="currentUser.logout()"
                title="Log out"
                class="w-full rounded-md border border-neutral-300 px-2 py-1.5 text-xs text-neutral-700 hover:bg-neutral-50 dark:border-white/10 dark:text-neutral-200 dark:hover:bg-white/5"
              >
                ⏻
              </button>
            </div>
          }
        </div>
      </aside>

      <main class="h-screen min-w-0 flex-1 overflow-y-auto">
        <div class="mx-auto max-w-[1600px] px-8 py-6">
          <router-outlet></router-outlet>
        </div>
      </main>
    </div>
  `,
})
export class AdminShellComponent {
  protected readonly currentUser = inject(CurrentUserService);
  protected readonly collapsed = signal(this.readInitialCollapsed());

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
        // Promotion GET is MANAGER-gated, POST/PUT/DELETE RESPONSABLE-gated
        // server-side (confirmed against PromotionController.java) — same
        // floor as the rest of this section, write-gating happens in-page.
        { label: 'Promotions', path: '/admin/reference/promotions', minRole: 'MANAGER' },
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

  protected toggleCollapsed(): void {
    const next = !this.collapsed();
    this.collapsed.set(next);
    try {
      localStorage.setItem(SIDEBAR_COLLAPSED_KEY, String(next));
    } catch {
      // Storage unavailable — collapse state just won't persist across reloads.
    }
  }

  private readInitialCollapsed(): boolean {
    try {
      return localStorage.getItem(SIDEBAR_COLLAPSED_KEY) === 'true';
    } catch {
      return false;
    }
  }
}
