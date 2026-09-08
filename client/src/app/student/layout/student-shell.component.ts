import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CurrentUserService } from '../../core/auth/current-user.service';
import { MyRoomStatusService } from '../../core/services/my-room-status.service';
import { ThemeToggleComponent } from '../../shared/components/theme-toggle/theme-toggle.component';

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
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ThemeToggleComponent],
  template: `
    <div class="min-h-screen bg-neutral-50 dark:bg-surface-dark">
      <header class="sticky top-0 z-10 border-b border-neutral-100 bg-surface dark:border-white/10 dark:bg-surface-dark">
        <div class="flex items-center justify-between px-4 py-3">
          <div class="min-w-0">
            <p class="truncate text-sm font-semibold text-neutral-900 dark:text-white">
              {{ currentUser.fullName() ?? 'Espace étudiant' }}
            </p>
            <p class="text-xs text-neutral-500 dark:text-neutral-400">ResiAIAC</p>
          </div>
          <div class="flex shrink-0 items-center gap-1">
            <app-theme-toggle></app-theme-toggle>
            <button
              type="button"
              (click)="currentUser.logout()"
              class="rounded-md px-3 py-1.5 text-xs font-medium text-neutral-500 hover:bg-neutral-100 hover:text-neutral-700 dark:text-neutral-400 dark:hover:bg-white/10 dark:hover:text-neutral-100"
            >
              Déconnexion
            </button>
          </div>
        </div>
        <nav class="flex gap-1 overflow-x-auto px-2 pb-2" aria-label="Navigation étudiant">
          @for (link of visibleNavLinks(); track link.path) {
            <a
              [routerLink]="link.path"
              routerLinkActive="bg-primary-50 text-primary-700 dark:bg-primary-900/40 dark:text-primary-300"
              class="shrink-0 whitespace-nowrap rounded-md px-3 py-1.5 text-sm font-medium text-neutral-500 hover:bg-neutral-100 dark:text-neutral-400 dark:hover:bg-white/10"
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
  private readonly roomStatus = inject(MyRoomStatusService);
  protected readonly navLinks = NAV_LINKS;

  /**
   * Hides "Réclamations" only in the unambiguous no-reservation-history
   * case (see MyRoomStatusService for why it isn't more aggressive than
   * that). Deliberately does NOT hide it while status is 'loading' —
   * showing it briefly then removing it is a smaller UX cost than the
   * reverse (a flash where a legitimate tab is briefly missing).
   */
  protected readonly visibleNavLinks = computed(() =>
    this.roomStatus.status() === 'no-history' ? NAV_LINKS.filter((l) => l.path !== 'reclamations') : NAV_LINKS,
  );
}
