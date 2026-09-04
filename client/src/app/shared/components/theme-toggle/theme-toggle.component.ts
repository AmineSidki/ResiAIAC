import { Component, inject } from '@angular/core';
import { ThemeService } from '../../../core/theme/theme.service';

/**
 * Small icon toggle, meant to sit in a shell header/sidebar footer. Uses
 * inline SVGs (sun/moon) instead of an icon font so it has zero extra
 * dependency footprint.
 */
@Component({
  selector: 'app-theme-toggle',
  standalone: true,
  template: `
    <button
      type="button"
      (click)="theme.toggle()"
      class="inline-flex h-8 w-8 items-center justify-center rounded-md text-neutral-500 hover:bg-neutral-100 hover:text-neutral-700 dark:text-neutral-400 dark:hover:bg-white/10 dark:hover:text-neutral-100"
      [attr.aria-label]="theme.theme() === 'dark' ? 'Passer en mode clair' : 'Passer en mode sombre'"
      [title]="theme.theme() === 'dark' ? 'Mode clair' : 'Mode sombre'"
    >
      @if (theme.theme() === 'dark') {
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" class="h-4 w-4">
          <circle cx="12" cy="12" r="4"></circle>
          <path stroke-linecap="round" d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41" />
        </svg>
      } @else {
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="h-4 w-4">
          <path d="M20.354 15.354A9 9 0 018.646 3.646a9.003 9.003 0 1011.708 11.708z" />
        </svg>
      }
    </button>
  `,
})
export class ThemeToggleComponent {
  protected readonly theme = inject(ThemeService);
}
