import { Component, effect, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { CurrentUserService } from '../../core/auth/current-user.service';

/**
 * Reached when an anonymous visitor hits a guarded route (see
 * core/auth/role.guard.ts). The guard attaches `?redirect=<attemptedUrl>` —
 * this component threads that value through `login()`'s redirectUri so
 * Keycloak sends the browser back to the originally-requested page instead
 * of back to /unauthorized, and also auto-navigates there the moment
 * `authenticated()` flips true (covers the case where the user was already
 * mid-login in another tab, or the auto-refresh silently re-authenticates).
 * This is the fix for the "stuck on 401/403 forever after logging in" loop.
 */
@Component({
  selector: 'app-unauthorized',
  standalone: true,
  template: `
    <div class="flex min-h-screen items-center justify-center bg-surface dark:bg-surface-dark">
      <div class="text-center">
        <p class="text-sm font-semibold text-primary-600 dark:text-primary-400">401</p>
        <h1 class="mt-2 text-2xl font-semibold text-neutral-900 dark:text-white">Sign in to continue</h1>
        <p class="mt-2 text-sm text-neutral-500 dark:text-neutral-400">
          You'll be taken back to where you were headed once you're signed in.
        </p>
        <button
          type="button"
          (click)="login()"
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
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly redirectTarget = toSignal(
    this.route.queryParamMap.pipe(map((params) => params.get('redirect'))),
    { initialValue: null },
  );

  constructor() {
    effect(() => {
      if (this.currentUser.authenticated()) {
        this.router.navigateByUrl(this.redirectTarget() ?? '/');
      }
    });
  }

  protected login(): void {
    const target = this.redirectTarget();
    const redirectUri = target ? `${window.location.origin}${target}` : undefined;
    this.currentUser.login(redirectUri);
  }
}
