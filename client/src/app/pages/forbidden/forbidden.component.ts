import { Component } from '@angular/core';

@Component({
  selector: 'app-forbidden',
  standalone: true,
  template: `
    <div class="flex min-h-screen items-center justify-center bg-surface dark:bg-surface-dark">
      <div class="text-center">
        <p class="text-sm font-semibold text-danger-500">403</p>
        <h1 class="mt-2 text-2xl font-semibold text-neutral-900">You don't have access to this page</h1>
        <p class="mt-2 text-neutral-500">Your role doesn't allow this. Contact a manager if you think this is a mistake.</p>
      </div>
    </div>
  `,
})
export class ForbiddenComponent {}
