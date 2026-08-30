import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrentUserService } from '../../core/auth/current-user.service';

/**
 * Scaffold landing page — proves the auth wiring end-to-end by reflecting
 * the logged-in user's name and highest role. Student and admin shells
 * replace this with real routed views.
 */
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="flex min-h-screen flex-col items-center justify-center gap-3 bg-surface dark:bg-surface-dark">
      @if (currentUser.authenticated()) {
        <p class="text-lg font-medium text-neutral-900">Welcome, {{ currentUser.fullName() ?? 'there' }}</p>
        <p class="text-sm text-neutral-500">Role: {{ currentUser.highestRole() ?? 'none assigned' }}</p>
        <a
          routerLink="/student"
          class="rounded-md bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700"
        >
          Espace étudiant
        </a>
        <button
          type="button"
          (click)="currentUser.logout()"
          class="mt-2 rounded-md border border-neutral-300 px-4 py-2 text-sm font-medium text-neutral-700 hover:bg-neutral-50"
        >
          Log out
        </button>
      } @else {
        <p class="text-neutral-500">Not signed in.</p>
        <button
          type="button"
          (click)="currentUser.login()"
          class="rounded-md bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700"
        >
          Log in
        </button>
      }
    </div>
  `,
})
export class HomeComponent {
  protected readonly currentUser = inject(CurrentUserService);
}
