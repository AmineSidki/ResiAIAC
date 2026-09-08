import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrentUserService } from '../../core/auth/current-user.service';

/**
 * Reached when an authenticated user's role doesn't clear a route guard's
 * floor (see core/auth/role.guard.ts). Unlike /unauthorized this isn't a
 * "log in and you'll be fine" situation — the account itself lacks the
 * role — so instead of an auto-redirect loop risk, this just offers a way
 * back to whichever dashboard the user's own role actually grants.
 */
@Component({
  selector: 'app-forbidden',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="flex min-h-screen items-center justify-center bg-surface dark:bg-surface-dark">
      <div class="text-center">
        <p class="text-sm font-semibold text-danger-500">403</p>
        <h1 class="mt-2 text-2xl font-semibold text-neutral-900 dark:text-white">You don't have access to this page</h1>
        <p class="mt-2 text-neutral-500 dark:text-neutral-400">Your role doesn't allow this. Contact a manager if you think this is a mistake.</p>
        <a
          [routerLink]="homeLink()"
          class="mt-4 inline-block rounded-md bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700"
        >
          Retour à mon espace
        </a>
      </div>
    </div>
  `,
})
export class ForbiddenComponent {
  private readonly currentUser = inject(CurrentUserService);

  protected readonly homeLink = computed(() => {
    const role = this.currentUser.highestRole();
    if (!role) return '/';
    return role === 'ADMINISTRATEUR' || role === 'RESPONSABLE' || role === 'MANAGER' ? '/admin' : '/student';
  });
}
