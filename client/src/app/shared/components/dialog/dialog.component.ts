import { Component, input, output } from '@angular/core';

/**
 * Presentational modal shell. Purely template-driven (open/close state lives
 * in the parent) so it stays trivial to compose — parents pass content via
 * the default slot and the `footer` slot.
 */
@Component({
  selector: 'app-dialog',
  standalone: true,
  template: `
    @if (open()) {
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-neutral-900/40 p-4">
        <div
          class="w-full max-w-md rounded-lg bg-white p-6 shadow-xl"
          role="dialog"
          aria-modal="true"
          [attr.aria-labelledby]="title() ? 'app-dialog-title' : null"
        >
          <div class="flex items-start justify-between gap-4">
            @if (title()) {
              <h2 id="app-dialog-title" class="text-base font-semibold text-neutral-900">{{ title() }}</h2>
            }
            <button
              type="button"
              (click)="close.emit()"
              class="text-neutral-400 hover:text-neutral-600"
              aria-label="Close dialog"
            >
              &#x2715;
            </button>
          </div>
          <div class="mt-4">
            <ng-content></ng-content>
          </div>
          <div class="mt-6 flex justify-end gap-2">
            <ng-content select="[footer]"></ng-content>
          </div>
        </div>
      </div>
    }
  `,
})
export class DialogComponent {
  readonly open = input(false);
  readonly title = input<string | null>(null);
  readonly close = output<void>();
}
