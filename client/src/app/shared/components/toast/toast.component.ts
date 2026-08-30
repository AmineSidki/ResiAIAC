import { Component, inject } from '@angular/core';
import { ToastService, ToastTone } from './toast.service';

const TONE_CLASSES: Record<ToastTone, string> = {
  info: 'bg-neutral-900 text-white',
  success: 'bg-success-500 text-white',
  danger: 'bg-danger-500 text-white',
};

/** Mount once near the root (e.g. in AppComponent) — a single global toast stack. */
@Component({
  selector: 'app-toast-host',
  standalone: true,
  template: `
    <div class="pointer-events-none fixed bottom-4 right-4 z-50 flex flex-col gap-2">
      @for (toast of toastService.toasts(); track toast.id) {
        <div
          class="pointer-events-auto flex items-center gap-3 rounded-md px-4 py-2.5 text-sm shadow-lg"
          [class]="toneClasses(toast.tone)"
          role="status"
        >
          <span>{{ toast.message }}</span>
          <button
            type="button"
            (click)="toastService.dismiss(toast.id)"
            class="text-white/70 hover:text-white"
            aria-label="Dismiss notification"
          >
            &#x2715;
          </button>
        </div>
      }
    </div>
  `,
})
export class ToastComponent {
  protected readonly toastService = inject(ToastService);

  protected toneClasses(tone: ToastTone): string {
    return TONE_CLASSES[tone];
  }
}
