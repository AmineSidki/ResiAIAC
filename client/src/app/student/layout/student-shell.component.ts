import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CurrentUserService } from '../../core/auth/current-user.service';

interface StudentNavLink {
  path: string;
  label: string;
}

const NAV_LINKS: StudentNavLink[] = [
  { path: 'profile', label: 'Profil' },
  { path: 'documents', label: 'Documents' },
  { path: 'reservation', label: 'Chambre' },
  { path: 'reclamations', label: 'Réclamations' },
];

/**
 * Root layout for the ETUDIANT self-service shell — distinct from the
 * admin/manager shell (Track B owns that one). Mobile-first: a simple top
 * app bar plus a horizontally-scrollable tab strip (comfortable at 375px,
 * no wasted vertical space for a persistent sidebar), and a card-based
 * content area that every child page renders into via router-outlet.
 *
 * Wired under the existing `/student` route in app.routes.ts, replacing the
 * scaffold's `pages/home` rather than extending it, per Task A1.
 */
@Component({
  selector: 'app-student-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="min-h-screen bg-neutral-50">
      <header class="sticky top-0 z-10 border-b border-neutral-100 bg-surface">
        <div class="flex items-center justify-between px-4 py-3">
          <div class="min-w-0">
            <p class="truncate text-sm font-semibold text-neutral-900">
              {{ currentUser.fullName() ?? 'Espace étudiant' }}
            </p>
            <p class="text-xs text-neutral-500">ResiAIAC</p>
          </div>
          <button
            type="button"
            (click)="currentUser.logout()"
            class="shrink-0 rounded-md px-3 py-1.5 text-xs font-medium text-neutral-500 hover:bg-neutral-100 hover:text-neutral-700"
          >
            Déconnexion
          </button>
        </div>
        <nav class="flex gap-1 overflow-x-auto px-2 pb-2" aria-label="Navigation étudiant">
          @for (link of navLinks; track link.path) {
            <a
              [routerLink]="link.path"
              routerLinkActive="bg-primary-50 text-primary-700"
              class="shrink-0 whitespace-nowrap rounded-md px-3 py-1.5 text-sm font-medium text-neutral-500 hover:bg-neutral-100"
            >
              {{ link.label }}
            </a>
          }
        </nav>
      </header>

      <main class="mx-auto max-w-2xl px-4 py-4">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
})
export class StudentShellComponent {
  protected readonly currentUser = inject(CurrentUserService);
  protected readonly navLinks = NAV_LINKS;
}
