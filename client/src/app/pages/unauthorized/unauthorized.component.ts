import { Component, inject } from '@angular/core';
import { CurrentUserService } from '../../core/auth/current-user.service';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  template: `
    <div class="flex min-h-screen items-center justify-center bg-surface dark:bg-surface-dark">
      <div class="text-center">
        <p class="text-sm font-semibold text-primary-600">401</p>
        <h1 class="mt-2 text-2xl font-semibold text-neutral-900">Sign in to continue</h1>
        <button
          type="button"
          (click)="currentUser.login()"
          class="mt-4 rounded-md bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700"
        >
          Log in
        </button>
      </div>
    </div>
  `,
})
export class UnauthorizedComponent {
  protected readonly currentUser = inject(CurrentUserService);
}
